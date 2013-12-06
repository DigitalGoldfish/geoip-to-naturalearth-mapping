package at.localhost.vectorworldmap.tools;

import java.io.File;
import java.io.IOException;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

import at.localhost.vectorworldmap.gis.PolygonBorderPreservingSimplifier;
import at.localhost.vectorworldmap.util.SHPFileUtils;

public class SimplifyRegions
{
	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";

	private static final String OUTPUT_DIRECTORY = "out/shp/";
	private static final String ADMIN0_FILENAME = "simplified_features_admin0_";
	private static final String ADMIN1_FILENAME = "simplified_features_admin1_";
	private static final String FILE_EXTENSION = ".shp";

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;

	private static int countOmmitedFeatures = 0;

	private static double[] distanceTolerances = {0.1, /*0.05 , 0.01, 0.005, 0.001*/};

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			System.out.println("start processing");
			// open feature source
			initShapeFileDataSources();
			simplify(distanceTolerances);

		} catch (IOException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		}
	}


	private static void simplify(double[] distanceTolerances) throws IOException
	{
		SimpleFeatureCollection features = countryFeatureSource.getFeatures();
		PolygonBorderPreservingSimplifier simplifier = new PolygonBorderPreservingSimplifier(features);
		for(double distanceTolerance:distanceTolerances) {
			simplifier.setDistanceTolerance(distanceTolerance);
			SimpleFeatureCollection result = simplifier.getResultFeatureCollection();
			SHPFileUtils.writeResultToFile(countryFeatureSource.getSchema(), result, generateFilename(ADMIN0_FILENAME, distanceTolerance));
		}

		features = regionFeatureSource.getFeatures();
		simplifier = new PolygonBorderPreservingSimplifier(features);
		for(double distanceTolerance:distanceTolerances) {
			simplifier.setDistanceTolerance(distanceTolerance);
			SimpleFeatureCollection result = simplifier.getResultFeatureCollection();
			SHPFileUtils.writeResultToFile(regionFeatureSource.getSchema(), result, generateFilename(ADMIN1_FILENAME, distanceTolerance));
		}

	}

	private static String generateFilename(String filename, double distanceTolerance) {
		return OUTPUT_DIRECTORY + filename + "tolerance_" + Double.toString(distanceTolerance).replace(".", "_") + FILE_EXTENSION;
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
