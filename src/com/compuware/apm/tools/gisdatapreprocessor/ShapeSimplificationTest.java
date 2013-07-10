package com.compuware.apm.tools.gisdatapreprocessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;


public class ShapeSimplificationTest {

	private static final String MAXMIND_COUNTRIES = "resources/maxmind-countries.csv";
	private static final String MAXMIND_REGIONS = "resources/maxmind-regions.csv";
	private static final String MAXMIND_COUNTRIES_TO_CONTINENTS = "resources/maxmind-countries-to-continents.csv";
	private static final String FIPS_TO_ISO_COUNTRY_CODE_MAPPING = "resources/fips10-4_to_iso3166_countrycodes.csv";

	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V14 = "resources/naturalearth/v14/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;
	private static SimpleFeatureSource regionFeatureSourcev14;



	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {

			// open feature source
			initShapeFileDataSources();
			SimpleFeature country = fetchFeatureFromCountryDataSource("AT");
			List<SimpleFeature> regions = fetchFeatureFromRegionDataSource("AT");
			List<SimpleFeature> regionsForSimplification = regions.subList(0, 3);
			DefaultFeatureCollection original = new DefaultFeatureCollection();
			int i = 0;
			for (SimpleFeature feature:regions) {
				original.add(feature);
			}

			DefaultFeatureCollection simplifiedFeatures = simplifyShapes(regions);
			// combineGeometries(regions);
			generateGeoJSONFile("original", original);
			generateGeoJSONFile("simplification_0_1", simplifiedFeatures);

		} catch (IOException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		} /* catch (CQLException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		} */

	}

	private static DefaultFeatureCollection simplifyShapes(List <SimpleFeature> features)
	{

		/* Geometry[] geometries = new Geometry[features.size()];

		int i = 0;
		for (SimpleFeature feature: features) {
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			for (int j = 0; j < geometry.getNumGeometries(); j++) {
				geometries[i++] = geometry.getGeometryN(j);
			}
		}

		GeometryFactory factory = new GeometryFactory();
		GeometryCollection geoCollection = new GeometryCollection(geometries, factory);


		geoCollection = (GeometryCollection) TopologyPreservingSimplifier.simplify(geoCollection, 0.05); */

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		for (SimpleFeature feature: features) {
			collection.add(createSimpleFeature(feature));
		}

		DefaultFeatureCollection simplifiedFeatures = PolygonBorderPreservingSimplifier.simplify(collection, 0.01, true);
		return simplifiedFeatures;
		// i = 0;
		/* for (SimpleFeature feature: features) {
			feature.setDefaultGeometry(geoCollection.getGeometryN(i++));
		} */

	}

	private static void initShapeFileDataSources() throws IOException
	{
		File file = new File(NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2);
		ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
		countryFeatureSource = store.getFeatureSource();

		file = new File(NATURAL_EARTH_ADMIN_1_BOUNDARIES_V2);
		store = new ShapefileDataStore(file.toURI().toURL());
		regionFeatureSource = store.getFeatureSource();

		file = new File(NATURAL_EARTH_ADMIN_1_BOUNDARIES_V14);
		store = new ShapefileDataStore(file.toURI().toURL());
		regionFeatureSourcev14 = store.getFeatureSource();
	}

	private static SimpleFeature fetchFeatureFromCountryDataSource(String code) {
		try {
			Filter filter = CQL.toFilter("iso_a2 LIKE '" + code + "'");
			SimpleFeatureCollection features = countryFeatureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = features.features();
			if (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				iterator.close();
				return feature;
			} else {
				iterator.close();
			}
		} catch (Exception e) {
			System.out.println("Exception!");
		}
		return null;
	}

	private static List<SimpleFeature> fetchFeatureFromRegionDataSource(String code) {
		List<SimpleFeature> collectedFeatures = new ArrayList<SimpleFeature>();
		try {
			Filter filter = CQL.toFilter("iso_a2 LIKE '" + code + "'");
			SimpleFeatureCollection features = regionFeatureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = features.features();

			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				collectedFeatures.add(feature);
			}
			iterator.close();
		} catch (Exception e) {
			System.out.println("Exception!");
		}
		return collectedFeatures;
	}

	private static void generateGeoJSONFile(String filename, DefaultFeatureCollection features) throws IOException
	{
		FeatureJSON feature = new FeatureJSON(new GeometryJSON(4));
		FileWriter writer = new FileWriter("out/" + filename + ".json");
		feature.writeFeatureCollection(features, writer);
	}

	private static Geometry combineGeometries(List<SimpleFeature> features)
	{
		Collection<Geometry> geoCollection = new ArrayList<Geometry>();

		for (SimpleFeature feature: features) {
			geoCollection.add((Geometry) feature.getDefaultGeometry());
		}
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
		GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(geoCollection);

		return geometryCollection.union().buffer(0);
	}


	static SimpleFeatureType regionFeatureType = null;

	/**
	 * Here is how you can use a SimpleFeatureType builder to create the schema for your shapefile
	 * dynamically.
	 * <p>
	 * This method is an improvement on the code used in the main method above (where we used
	 * DataUtilities.createFeatureType) because we can set a Coordinate Reference System for the
	 * FeatureType and a a maximum field length for the 'name' field dddd
	 */
	private static SimpleFeatureType createFeatureType() {

		if (regionFeatureType == null) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("Region");
			builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

			// add attributes in order

			builder.add("geometry", Geometry.class);
			builder.add("id", String.class);
			builder.add("parent_id", String.class);
			builder.add("region_type", String.class);
			builder.add("name", String.class);
			builder.setDefaultGeometry("geometry");

			// build the type
			regionFeatureType = builder.buildFeatureType();

		}

		return regionFeatureType;
	}

	private static SimpleFeature createSimpleFeature(SimpleFeature feature)
	{
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(createFeatureType());
		builder.set("id", feature.getAttribute("code_hasc"));
		builder.set("parent_id", "AT");
		builder.set("region_type", "region" );
		builder.set("name", feature.getAttribute("name"));
		builder.add(feature.getDefaultGeometry());
/*
		List<SimpleFeature> features = region.getFeatures();
		if (features.size() == 1) {
			// builder.set("geometry", features.get(0).getDefaultGeometry());
			Geometry geometry = ((Geometry) features.get(0).getDefaultGeometry()).buffer(0);
			// geometry = TopologyPreservingSimplifier.simplify(geometry, 0.5);
			builder.add(geometry);
		} else {
			Geometry geometry = combineGeometries(features);
			builder.add(geometry);
		}
*/
		SimpleFeature newFeature = builder.buildFeature(feature.getAttribute("code_hasc").toString());

		return newFeature;
	}

}
