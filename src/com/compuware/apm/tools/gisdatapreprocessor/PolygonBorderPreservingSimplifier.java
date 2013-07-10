/*
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
 * Simplifies the geometries of a set of features, ensuring that the borders of
 * the geometries still align without any holes or gapsafter the simplification.
 * For the simplification of lines the <code>TopologyPreservingSimplifier</code>
 * from the Java Topology Suite is used.
 * The implementation depends both on the JTS library as well as some GeoTools
 * library. JTS is also distributed as part of the GeoTools download package:
 *  - JTS: http://www.vividsolutions.com/jts/JTSHome.htm
 *  - GeoTools: http://www.geotools.org/
 *
 * To simplify the polygons without breaking up the shared borders the geometries
 * are split into line fragments and then each line fragment simplified separately.
 *
 * Afterwards the polygons are recreated from the new simplified lines.
 *
 * The source code for this class is available on GitHub so in case there are
 * more detailed questions about the implementation please refer to it. I made
 * an effort to document it as best as I could, which admittedly is still
 * somewhat lacking. ;)
 *
 * @author markus@oehler.at
 * @version 0.1 (10.7.2012)
 *
 * TODO: does replacing the simplifier has any effect at all?
 * TODO: change package
 * TODO: change github project
 * TODO: Replace list to array conversion with better pattern
 * TODO: Should implementation be changed to use coordinates instead of points
 * 		 ?? (more lightweight?)
 */
public class PolygonBorderPreservingSimplifier
{

	/**
	 * Geometry type name for multi polygon geometry
	 */
	protected static final String GEOMETRY_TYPE_MULTIPOLYGON = "MultiPolygon";


	/**
	 * Geometry type name for polygon geometry
	 */
	protected static final String GEOMETRY_TYPE_POLYGON = "Polygon";


	/**
	 * Number of places after komma used to generate a key for the. Tests showed
	 * that 8 is playing it save and the risk of duplicated keys is incredibly
	 * low for everything but the most detailed of data.
	 */
	private static final int KEY_PRECISION = 8;


	/**
	 * Stores how many connections each point of every geometry has to other
	 * points in the set of geometries.
	 *
	 * Note: The key for a point has to be generated using the
	 * 	<code>generateStringIdForPoint</code> function.
	 */
	protected Map<String, MutableInt> numberOfConnectedPoints;


	/**
	 * Stores the already simplified line fragments. The keys under which the
	 * line fragments are stored is generated based on the points of the NON
	 * simplified line by the <code></code>
	 *
	 * Note: The key for the simplified line has to be generated using the
	 * 	<code>generateStringIdForLine</code> function.
	 */
	protected Map<String, LineString> simplifiedLines;


	/**
	 * List of points that are already fixed and cannot be removed or simplified.
	 * This are the end points of the already existing simplified line fragments.
	 */
	protected List<String> pivotPoints;


	/**
	 * Used for creating new geometries (lines, polygons, multipolygons, ...)
	 */
	protected GeometryFactory geometryFactory;


	/**
	 * Used to duplicate the features passed to this class as we do not modify
	 * the input shapes but return a copy of the features with simplified shapes.
	 */
	protected SimpleFeatureBuilder simpleFeatureBuilder;


	/**
	 * The degree of simplification, i.e. the maximum distance a point can be
	 * from his original location after simplification.
	 */
	protected double distanceTolerance = 0.01;


	/**
	 * Whether to preserve the topology of the original objects or not.
	 * TODO: still needs to be implemented I think. (the current approach is
	 * 	lacking)
	 */
	protected boolean preserveTopology = false;


	/**
	 * The collection of features that should be simplified.
	 */
	protected DefaultFeatureCollection features;


	/**
	 * Static convenience method for ease of use. Instantiates an <code>
	 * PolygonBorderPreservingSimplifier</code> internally.
	 * The tolerane value must be non-negative. A tolerance value of zero is
	 * effectively a no-op.
	 *
	 * @param features The collection of features to simplify
	 * @param distanceTolerance the approximation tolerance to use
	 * @param preserveTopology if the topology should be preserved or not
	 * @return a new feature collection containing the features with simplified
	 * 	shapes.
	 */
	public static DefaultFeatureCollection simplify(
			DefaultFeatureCollection features,
			double distanceTolerance,
			boolean preserveTopology)
	{
		PolygonBorderPreservingSimplifier simplifier
			= new PolygonBorderPreservingSimplifier(features);
		simplifier.setPreserveTopology(preserveTopology);
		simplifier.setDistanceTolerance(distanceTolerance);
		return simplifier.getResultFeatureCollection();
	}


	/**
	 * Creates a new instance of the simplifier with the given features
	 * @param features
	 */
	public PolygonBorderPreservingSimplifier(DefaultFeatureCollection features)
	{
		this.features = features;
		this.geometryFactory = new GeometryFactory(new PrecisionModel());
	}

	/**
	 * Set the approximation tolerance to use. The tolerane value must be non-
	 * negative. A tolerance value of zero is effectively a no-op.
	 *
	 * TODO: verify input parameter
	 * @param distanceTolerance the approximation tolerance to use
	 */
	public void setDistanceTolerance(double distanceTolerance)
	{
		this.distanceTolerance = distanceTolerance;
	}

	/**
	 * Set if the topology should be preserved or not
	 * @param preserveTopology
	 */
	public void setPreserveTopology(boolean preserveTopology)
	{
		this.preserveTopology = preserveTopology;
	}


	public DefaultFeatureCollection getResultFeatureCollection()
	{
		this.simplifiedLines = new HashMap<String, LineString>();
		this.pivotPoints = new ArrayList<String>();

		return simplify();
	}

	public void setPrecisionModel(PrecisionModel model) {
		this.geometryFactory = new GeometryFactory(model);
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
		numberOfConnectedPoints = new HashMap<String, MutableInt>();

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
			// Now we iterate over all line segments (meaning each two neighbouring points)
			// in the line and enter the connection between these two points in the list
			// of connections.
			//
			// NOTE: We ignore the last point in the line since it is the same point
			//       as the first one due to the  definition of a LinearRing in GeoTools
			for (int i = 0; i < (line.getNumPoints() - 1); i++) {

				int indexFrom = i;
				Point pointFrom = line.getPointN(indexFrom);
				String pointFromString = generateStringIdForPoint(pointFrom);

				int indexTo = i + 1;
				Point pointTo = line.getPointN(indexTo);
				String pointToString = generateStringIdForPoint(pointTo);

				// if the points are identical then skip them (precaution)
				if (pointFromString.equals(pointToString)) {
					continue;
				}

				// if no entry for this point in the connections map then create new entry
				// with an empty list of connecting points
				MutableInt counter = numberOfConnectedPoints.get(pointFromString);
				if (counter == null) {
					numberOfConnectedPoints.put(pointFromString, new MutableInt());
				} else {
					// increment the counter
					counter.increment();
				}

				// if A is connected to B then B is also connected to a, therefore we
				// also need to enter the reverse connection in the array of connections

				// if no entry for this point in the connections map then create new entry
				// with an empty list of connecting points
				counter = numberOfConnectedPoints.get(pointToString);
				if (counter == null) {
					numberOfConnectedPoints.put(pointToString, new MutableInt());
				} else {
					// increment the counter
					counter.increment();
				}
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
		List<LinearRing> simplifiedInternalRings = new ArrayList<LinearRing>();
		if (polygon.getNumInteriorRing() > 0) {
			for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
				LineString simplifiedInnerLine = simplifyRing(polygon.getInteriorRingN(i));
				if (simplifiedInnerLine != null) {
					simplifiedInternalRings.add(convertLineStringToLinearRing(simplifiedInnerLine));
				}
			}
		}
		LinearRing[] internalLines = new LinearRing[simplifiedInternalRings.size()];
		for (int i = 0; i < simplifiedInternalRings.size(); i++) {
			internalLines[i] = new LinearRing(simplifiedInternalRings.get(i).getCoordinateSequence(), geometryFactory);
		}

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
		for (int i = 0; i < ring.getNumPoints() - 1; i++) {
			points.add(ring.getPointN(i));
		}
		boolean isPivot = false;
		int pointIndex = 0;

		while (!isPivot && pointIndex < points.size()) {
			String pointStr = generateStringIdForPoint(points.get(pointIndex));
			pointIndex++;
			MutableInt numberOfConnections = numberOfConnectedPoints.get(pointStr);
			isPivot = (numberOfConnections != null && numberOfConnections.get() > 2) || pivotPoints.contains(pointStr);
		}
		pointIndex--;

		// If the line has no common segments with any other line then we can just simplify it
		// without further ado and add it to the list of simplified lines.
		if (!isPivot) {
			LineString line = geometryFactory.createLineString(pointToCoordinates(points));
			LineString simplifiedLine = (LineString) TopologyPreservingSimplifier.simplify(line, distanceTolerance);
			if (simplifiedLine.getNumPoints() <= 2) {
				return null;
			}
			String simpleLineKey = generateStringIdForLine(line);
			// TODO: is this necessary to add it to the list of recordeds strings?
			// We already know that there are no intersections ... so what is the point?
			simplifiedLines.put(simpleLineKey, simplifiedLine);
			pivotPoints.add(generateStringIdForPoint(points.get(0)));
			pivotPoints.add(generateStringIdForPoint(points.get(points.size() - 1)));
			return simplifiedLine;
		}

		// Now the more complicated case ... at least some segments of the line overlap with other segments:
		//
		// If the line has one or more common segments with other lines then we need to identify this segments
		// and use already existing segments whenever possible. For segments of the line that do not exist yet
		// new simplified line segments are created.
		List<Point> simplifiedLinePoints = new ArrayList<Point>();
		List<Point> reorderedPoints = new ArrayList<Point>();

		// this will put a known pivot point to the first position of the array of points
		reorderedPoints.addAll(points.subList(pointIndex, points.size()));
		reorderedPoints.addAll(points.subList(0, pointIndex + 1)); // NB: the first point in the list is the same point as the last

		int iFrom = 0;
		MutableInt numberOfConnections = numberOfConnectedPoints.get(generateStringIdForPoint(reorderedPoints.get(1)));
		int iConnections = numberOfConnections.get();

		for (int i = 1; i < reorderedPoints.size(); i++) {
			String pointStr = generateStringIdForPoint(reorderedPoints.get(i));
			numberOfConnections = numberOfConnectedPoints.get(pointStr);

			if ((numberOfConnections != null && numberOfConnections.get() != iConnections)
					|| pivotPoints.contains(pointStr)
					|| reorderedPoints.size() == i + 1) {

				List<Point> pointsForLine = reorderedPoints.subList(iFrom, i + 1);
				String lineKey = generateStringIdForPoint(pointsForLine.get(
						pointsForLine.size() - 1),
						pointsForLine.get(pointsForLine.size() - 2),
						pointsForLine.get(0)
				);
				if (simplifiedLines.containsKey(lineKey)) {
					LineString line = simplifiedLines.get(lineKey);
					pointsForLine = new ArrayList<Point>();
					for (int j = line.getNumPoints() - 1; j > 0; j--) {
						//NB: we skip the last point
						pointsForLine.add(line.getPointN(j));
					}
				} else {
					LineString line = geometryFactory.createLineString(pointToCoordinates(pointsForLine));
					LineString simplifiedLine = (LineString) TopologyPreservingSimplifier.simplify(line, distanceTolerance);
					lineKey = buildLineKey(pointsForLine);
					simplifiedLines.put(lineKey, simplifiedLine);
					pivotPoints.add(generateStringIdForPoint(pointsForLine.get(0)));
					pivotPoints.add(generateStringIdForPoint(pointsForLine.get(pointsForLine.size()-1)));
					pointsForLine = new ArrayList<Point>();
					for (int j = 0; j < simplifiedLine.getNumPoints() - 1; j++) {
						// NB we skip the last point
						pointsForLine.add(simplifiedLine.getPointN(j));
					}
				}
				simplifiedLinePoints.addAll(pointsForLine);
				iFrom = i;
				if (i +1 < reorderedPoints.size()) {
					Point nextPoint = reorderedPoints.get(i+1);
					iConnections = numberOfConnectedPoints.get(generateStringIdForPoint(nextPoint)).get();
				}
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
		return generateStringIdForPoint(points.get(0), points.get(1), points.get(points.size() - 1));
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

	protected static String generateStringIdForLine(LineString line)
	{
		return generateStringIdForPoint(line.getPointN(0), line.getPointN(1),
				line.getPointN(line.getNumPoints() - 1));
	}

	protected static String generateStringIdForPoint(Point point1, Point point2,
			Point point3)
	{
		return generateStringIdForPoint(point1) + " "
				+ generateStringIdForPoint(point2) + " "
				+ generateStringIdForPoint(point3);
	}

	protected static String generateStringIdForPoint(Point p)
	{
		return String.format("%." + KEY_PRECISION + "f %." + KEY_PRECISION + "f", p.getX(), p.getY());
	}


	protected static Coordinate[] pointToCoordinates(List<Point> points) {
		Coordinate[] coordinates = new Coordinate[points.size()];
		int i = 0;
		for (Point point: points) {
			coordinates[i++] = point.getCoordinate();
		}
		return coordinates;
	}

	/**
	 *
	 *
	 * @param original
	 * @return a shallow copy of the feature given as input
	 *
	 * TODO: should we use deep copy here ??
	 */
	protected SimpleFeature cloneSimpleFeature(SimpleFeature original)
	{
		if (simpleFeatureBuilder == null) {
			simpleFeatureBuilder = new SimpleFeatureBuilder(original.getFeatureType());
		}

		// NB: shallow copy
		simpleFeatureBuilder.init(original);
		return simpleFeatureBuilder.buildFeature(original.getID());
	}


	/**
	 * Small helper utility class to simplify counting with the counters stored in a hashmap.
	 * Reduces object churn when used instead of immutable integers
	 *
	 * see http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
	 */
	class MutableInt {
		private int value = 1; // note that we start at 1 since we're counting

		public void increment () {
			++value;
		}

		public int get() {
			return value;
		}
	}

}
