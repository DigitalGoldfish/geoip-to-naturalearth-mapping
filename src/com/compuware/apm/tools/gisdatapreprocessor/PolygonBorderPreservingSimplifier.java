/**
 *
 */
package com.compuware.apm.tools.gisdatapreprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * @author cwat-moehler
 *
 */
public class PolygonBorderPreservingSimplifier
{

	protected static final String GEOMETRY_TYPE_MULTIPOLYGON = "MultiPolygon";

    protected static final String GEOMETRY_TYPE_POLYGON = "Polygon";

    protected static final int PRECISION = 8;

    protected Map<String, List<String>> connections;

    protected Map<String, LineString> simplifiedLines;

    protected List<String> pivotPoints; // TODO - check if a list isn't enough


    protected GeometryFactory geometryFactory;

    protected double distanceTolerance = 0.01;
    protected boolean preserveTopology = false;
    protected DefaultFeatureCollection features;


    public PolygonBorderPreservingSimplifier(DefaultFeatureCollection features) {
    	this.features = features;
    	this.geometryFactory = new GeometryFactory(new PrecisionModel(10));
    	this.simplifiedLines = new HashMap<String, LineString>();
    	this.pivotPoints = new ArrayList<String>();
    }

    public static DefaultFeatureCollection simplify(DefaultFeatureCollection features, double distanceTolerance, boolean preserveTopology)
    {
    	PolygonBorderPreservingSimplifier simplifier = new PolygonBorderPreservingSimplifier(features);
    	simplifier.setPreserveTopology(preserveTopology);
    	simplifier.setDistanceTolerance(distanceTolerance);
    	return simplifier.getResultFeatureCollection();
    }

    public void setDistanceTolerance(double distanceTolerance) {
    	this.distanceTolerance = distanceTolerance;
    }

    public void setPreserveTopology(boolean preserveTopology) {
    	this.preserveTopology = preserveTopology;
    }

    public DefaultFeatureCollection getResultFeatureCollection() {
    	return simplify();
    }

    protected DefaultFeatureCollection simplify()
    {
    	// To simplify the geometric shapes without loosing the borders we need the information
    	// which points &line fragments are shared between two or more shapes. This method creates
    	// that info and stores it in the global map "connections".s
        determineConnections(features);

        // Create featureCollection to store copies of the features with simplified geometry
        // (as we do not modify the input parameters)
        DefaultFeatureCollection result = new DefaultFeatureCollection();

        SimpleFeatureIterator featuresIterator = features.features();
        try {
	        while (featuresIterator.hasNext()) {
	        	SimpleFeature feature = featuresIterator.next();

	        	// get all polygons of the currently processed feature
	        	List<Polygon> polygons = extractPolygons(feature);
	        	List<Polygon> simplifiedPolygons = new ArrayList<Polygon>(polygons.size());

	        	// iterate trough polygons of feature and simplify each polygon
	        	for (Polygon polygon: polygons) {
	        		Polygon simplifiedPolygon = simplifyPolygon(polygon);
	        		if (simplifiedPolygon != null) {
	        			simplifiedPolygons.add(simplifiedPolygon);
	        		}
	        	}

	        	if (simplifiedPolygons.size() > 0) {
		        	// create feature for simplified shape and append it to result
	        		Polygon[] polys = new Polygon[(simplifiedPolygons.size())]; // TODO - find better name
	        		for (int i = 0; i < simplifiedPolygons.size(); i++) {
	        			polys[i] = simplifiedPolygons.get(i);
	        		}

	        		MultiPolygon shape = geometryFactory.createMultiPolygon(polys);
	        		SimpleFeature simplifiedFeature = cloneSimpleFeature(feature);
	        		simplifiedFeature.setDefaultGeometry(shape);
	        		result.add(simplifiedFeature);
	        	} else {
	        		// For this feature no shape is left after simplification (this can happen if topology isn't preserved).
	        		// In this case the feature is not included in the output.
	        		System.out.println("Feature is ommited because it is too small!");
	        	}
	        }
        } finally {
        	featuresIterator.close();
        }

        return result;
    }

	protected void determineConnections(DefaultFeatureCollection features) {
        connections = new HashMap<String, List<String>>();

    	List<Polygon> polygons = extractPolygons(features);
    	for (Polygon polygon: polygons) {
    		if (polygon.getArea() > 0) {
    			analyzePointsOfPolygon(polygon);
    		} else {
    			System.out.println("Polygon has an area 0; ignoring polygon");
    		}
    	}
	}

	/**
	 * @param lines
	 */
	protected void analyzePointsOfPolygon(Polygon polygon)
	{
		List<LineString> lines = extractLinesFromPolygon(polygon);
		for (LineString line: lines) {
//			System.out.println(buildLineKey(line));

			// Now we iterate over all line segments (meaning each two neighbouring points)
			// in the line and enter the connection between these two points in the list
			// of connections.
			//
			// NOTE: We ignore the last point in the line since it is the same point
			//       as the first one due to the  definition of a LinearRing in GeoTools
			for (int i = 0; i < (line.getNumPoints() - 1); i++) {

				int indexFrom = i;
				Point pointFrom = line.getPointN(indexFrom);
				String pointFromString = pointToString(pointFrom);

				int indexTo = i + 1;
				Point pointTo = line.getPointN(indexTo);
				String pointToString = pointToString(pointTo);

				// if the points are identical then skip them (precaution)
				if (pointFromString.equals(pointToString)) {
					continue;
				}

				// if no entry for this point in the connections map then create new entry
				// with an empty list of connecting points
				if (!connections.containsKey(pointFromString)) {
					connections.put(pointFromString, new ArrayList<String>());
				}
				// add the new connection
				connections.get(pointFromString).add(pointToString);

				// if A is connected to B then B is also connected to a, therefore we
				// also need to enter the reverse connection in the array of connections

				// if no entry for this point in the connections map then create new entry
				// with an empty list of connecting points
				if (!connections.containsKey(pointToString)) {
					connections.put(pointToString, new ArrayList<String>());
				}
				// add the new connection
				connections.get(pointToString).add(pointFromString);
			}
		}
	}

	protected Polygon simplifyPolygon(Polygon polygon)
	{
		// Start by simplifying the exterior ring of the polygon. There always exists
		// exactly one exterior ring, no more  - no less.
		LineString simplifiedExternalLine = simplifyRing(polygon.getExteriorRing());

		// Test if returned geometry is valid
		if (simplifiedExternalLine == null) {
			// Exterior ring is null because the shape was too small and will disappear at this level
			// if topology isn't preserved.
			System.out.println("Simplified Polygon does not exist anymore");
			return null;
		}

		// simplifyRing returns a LineString, but what we need to recreate the polygone is a LineRing
		LinearRing simplifiedExternalRing = convertLineStringToLinearRing(simplifiedExternalLine);

		// Simplify internal rings, there can be any finite number of internal rings.
		// There also can be none which is actually the most common case for maps.
		// TODO: ignore internal rings for now, we only have 1 - in niederösterreich around vienna
		// and for testing we want to keep it as simple as possible
		/* List<LinearRing> simplifiedInternalRings = new ArrayList<LinearRing>();
		if (polygon.getNumInteriorRing() > 0) {
			for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
				LineString simplifiedInnerLine = simplifyRing(polygon.getInteriorRingN(i));
				if (simplifiedInnerLine != null) {
					simplifiedInternalRings.add(new LinearRing(simplifiedInnerLine.getCoordinateSequence(), geometryFactory));
				}
			}
		}*/
		LinearRing[] internalLines = new LinearRing[0/*simplifiedInternalRings.size()*/];
		/* for (int i = 0; i < simplifiedInternalRings.size(); i++) {
			internalLines[i] = new LinearRing(simplifiedInternalRings.get(i).getCoordinateSequence(), geometryFactory);
		} */

		return new Polygon(simplifiedExternalRing, internalLines, geometryFactory);
	}

	protected LinearRing convertLineStringToLinearRing(LineString line) {
		List<Point> points = new ArrayList<Point>(line.getNumPoints() + 1);
		for (int i = 0; i < line.getNumPoints(); i++) {
			points.add(line.getPointN(i));
		}
		// add first point again as last point so that the circle is closed
		points.add(line.getPointN(0));

		LineString closedLine = geometryFactory.createLineString(pointToCoordinates(points));
		return new LinearRing(closedLine.getCoordinateSequence(), geometryFactory);
	}

    protected LineString simplifyRing(LineString ring)
    {
    	List<Point> points = new ArrayList<Point>();
    	System.out.println(ring);
    	for (int i = 0; i < ring.getNumPoints() - 1; i++) {
    		points.add(ring.getPointN(i));
    	}
    	boolean isPivot = false;
    	int pointIndex = 0;

    	while (!isPivot && pointIndex < points.size()) {
    		String pointStr = pointToString(points.get(pointIndex));
    		pointIndex++;
    		isPivot = (connections.containsKey(pointStr) && connections.get(pointStr).size() > 2) || pivotPoints.contains(pointStr);
    	}
    	pointIndex--;

    	// if the line has no common segments with any line that was already simplified it can just
    	// be simplified without further considerations. However after simplifications we need to
    	// track it as a line that was already simplified.
    	if (!isPivot) {
    		System.out.println("Simple case - no shared borders");
    		LineString line = geometryFactory.createLineString(pointToCoordinates(points));
    		LineString simplifiedLine = (LineString) TopologyPreservingSimplifier.simplify(line, distanceTolerance);
    		if (simplifiedLine.getNumPoints() <= 2) {
    			return null;
    		}
			String simpleLineKey = buildLineKey(line);
			simplifiedLines.put(simpleLineKey, simplifiedLine);
			pivotPoints.add(pointToString(points.get(0)));
			pivotPoints.add(pointToString(points.get(points.size() - 1)));
			return simplifiedLine;
    	}

    	System.out.println("Complicated case - shared borders");
    	// If the line has one or more common segments with other lines then we need to identify this segments
    	// and use already existing segments whenever possible. For segments of the line that do not exist yet
    	// new simplified line segments are created.
    	List<Point> simplifiedLinePoints = new ArrayList<Point>();
    	List<Point> reorderedPoints = new ArrayList<Point>();
    	reorderedPoints.addAll(points.subList(pointIndex, points.size()));
    	reorderedPoints.addAll(points.subList(0, pointIndex + 1)); // NB: the first point in the list is the same point as the last

    	int iFrom = 0;
    	for (int i = 1; i < reorderedPoints.size(); i++) {

    		String pointStr = pointToString(reorderedPoints.get(i));
    		if ((connections.containsKey(pointStr) && connections.get(pointStr).size() > 2) || pivotPoints.contains(pointStr)) {

    			System.out.println();
    			List<Point> pointsForLine = reorderedPoints.subList(iFrom, i + 1);
    			String lineKey = buildLineKey(pointsForLine.get(
    					pointsForLine.size() - 1),
    					pointsForLine.get(pointsForLine.size() - 2),
    					pointsForLine.get(0)
    			);
    			if (simplifiedLines.containsKey(lineKey)) {
    				System.out.println();
    				System.out.println("Use existing simplified line");
    				LineString line = simplifiedLines.get(lineKey);
    				System.out.println(line);
    				pointsForLine = new ArrayList<Point>();
    				for (int j = /* 0 */ line.getNumPoints() - 1/**/ ; j > 0 /* line.getNumPoints() - 1 */; j--) {
    					//NB: we skip the last point
    					System.out.println("Adding point " + line.getPointN(j) + " to line!");
    					pointsForLine.add(line.getPointN(j));
    				}
    			} else {
    				System.out.println();
    				System.out.println("New simplified line");
    	    		LineString line = geometryFactory.createLineString(pointToCoordinates(pointsForLine));
    	    		System.out.println(line);
    	    		LineString simplifiedLine = (LineString) TopologyPreservingSimplifier.simplify(line, distanceTolerance);
    	    		System.out.println(simplifiedLine);
    	    		lineKey = buildLineKey(pointsForLine);
    	    		System.out.println(simplifiedLine);
    	    		simplifiedLines.put(lineKey, simplifiedLine);
    	    		pointsForLine = new ArrayList<Point>();
    	    		for (int j = 0; j < simplifiedLine.getNumPoints() - 1; j++) {
    	    			// NB we skip the last point
    	    			System.out.println("Adding point " + simplifiedLine.getPointN(j) + " to line!");
    	    			pointsForLine.add(simplifiedLine.getPointN(j));
    	    		}
    			}
    			simplifiedLinePoints.addAll(pointsForLine);
    			System.out.println(simplifiedLinePoints);
    			iFrom = i;
    		} // end if
    	} // end for
    	if (simplifiedLinePoints.size() <= 2) {
    		return null;
    	}
    	// close the ring
    	// simplifiedLinePoints.add(simplifiedLinePoints.get(0));

    	return geometryFactory.createLineString(pointToCoordinates(simplifiedLinePoints));
    }

    protected static String buildLineKey(List<Point> points) {
    	return buildLineKey(points.get(0), points.get(1), points.get(points.size() - 1));
    }


	protected static List<LineString> extractLinesFromPolygon(Polygon polygon)
	{
		List<LineString> lines = new ArrayList<LineString>();
		lines.add(polygon.getExteriorRing());
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			lines.add(polygon.getInteriorRingN(i));
		}
		return lines;
	}

	protected List<Polygon> extractPolygons(DefaultFeatureCollection features)
	{
		SimpleFeatureIterator iterator = features.features();
		List<Polygon> result = new ArrayList<Polygon>();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				String geometryType = geometry.getGeometryType();
				if (GEOMETRY_TYPE_MULTIPOLYGON.equals(geometryType) || GEOMETRY_TYPE_POLYGON.equals(geometryType)) {
					result.addAll(extractPolygonsOfGeometry(geometry));
				} else {
					System.out.println("Unsupported Type:" + geometryType + " Ignoring Feature");
				}
			}
		} finally {
			iterator.close();
		}
		return result;
	}

	protected List<Polygon> extractPolygons(SimpleFeature feature)
	{
		return extractPolygonsOfGeometry((Geometry) feature.getDefaultGeometry());
	}

	protected List<Polygon> extractPolygonsOfGeometry(Geometry geometry) {
		List<Polygon> polygons = new ArrayList<Polygon>();
		if (GEOMETRY_TYPE_POLYGON.equals(geometry.getGeometryType())) {
			polygons.add((Polygon) geometry);
		} else if (GEOMETRY_TYPE_MULTIPOLYGON.equals(geometry.getGeometryType())) {
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				polygons.add((Polygon) geometry.getGeometryN(i));
			}
		}
		return polygons;
	}

    protected static String buildLineKey(LineString line) {
    	return buildLineKey(line.getPointN(0), line.getPointN(1), line.getPointN(line.getNumPoints() - 1));
    	/* return pointToString(line.getPointN(0)) + " "
    			+ pointToString(line.getPointN(1)) + " "
    			+ pointToString(line.getPointN(line.getNumPoints() - 1)); */
    	// TODO: check if line.getEndPoint() can be used instead;
    }

    protected static String buildLineKey(Point point1, Point point2, Point point3) {
    	return pointToString(point1) + " "
    			+ pointToString(point2) + " "
    			+ pointToString(point3);
    	// TODO: check if line.getEndPoint() can be used instead;
    }

    protected static String pointToString(Point p) {
    	return String.format("%." + PRECISION + "f %." + PRECISION + "f", p.getX(), p.getY());
    }

    protected static Coordinate[] pointToCoordinates(List<Point> points) {
    	Coordinate[] coordinates = new Coordinate[points.size()];
    	int i = 0;
    	for (Point point: points) {
    		coordinates[i++] = point.getCoordinate();
    	}
    	return coordinates;
    }

    protected static SimpleFeature cloneSimpleFeature(SimpleFeature original)
    {
    	// TODO: instantiate and reuse a copy of SimpleFeatureBuilder (better performance)
    	return SimpleFeatureBuilder.copy(original);
    }

}
