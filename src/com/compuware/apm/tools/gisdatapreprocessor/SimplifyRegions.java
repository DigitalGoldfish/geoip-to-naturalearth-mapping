package com.compuware.apm.tools.gisdatapreprocessor;

import java.io.File;
import java.io.IOException;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;


public class SimplifyRegions {



	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";

	private static final String OUTPUT_DIRECTORY = "out/shp/";
	private static final String ADMIN1_FILENAME = "simplified_features_admin1_";
	private static final String FILE_EXTENSION = ".shp";

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {

			// open feature source
			initShapeFileDataSources();

			simplify(0.05);
			// combineGeometries(regions);


		} catch (IOException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		}
	}


	private static void simplify(double distanceTolerance) throws IOException
	{
		SimpleFeatureCollection features = regionFeatureSource.getFeatures();
		PolygonBorderPreservingSimplifier simplifier = new PolygonBorderPreservingSimplifier(features);
		simplifier.setDistanceTolerance(distanceTolerance);
		SimpleFeatureCollection result = simplifier.getResultFeatureCollection();

		SHPFileUtils.writeResultToFile(regionFeatureSource.getSchema(), result, generateFilename(distanceTolerance));
	}


	private static String generateFilename(double distanceTolerance) {
		return OUTPUT_DIRECTORY + ADMIN1_FILENAME + Double.toString(distanceTolerance).replace(".", "_") + FILE_EXTENSION;
	}


	private static void initShapeFileDataSources() throws IOException
	{
		File file = new File(NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2);
		ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
		countryFeatureSource = store.getFeatureSource();

		file = new File(NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3);
		store = new ShapefileDataStore(file.toURI().toURL());
		regionFeatureSource = store.getFeatureSource();
	}

}
