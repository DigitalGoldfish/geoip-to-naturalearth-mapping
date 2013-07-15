/**
 *
 */
package at.localhost.vectorworldmap.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.shapefile.ng.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author cwat-moehler
 *
 */
public class SHPFileUtils {

	public static ShapefileDataStore createOutputFile(
			SimpleFeatureType featureType,
			String pathAndFileName)
			throws MalformedURLException, IOException
	{
		File newFile = new File(pathAndFileName);
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(featureType);
		return newDataStore;
	}


	public static void writeResultToFile(
			final SimpleFeatureType featureType,
			final SimpleFeatureCollection result,
			final String pathAndFileName)
					throws IOException
	{
		ShapefileDataStore datastore = createOutputFile(featureType, pathAndFileName);
		writeResultToFile(result, datastore);
	}


	public static void writeResultToFile(
			final SimpleFeatureCollection result,
			final ShapefileDataStore newDataStore)
					throws IOException
	{
		Transaction transaction = new DefaultTransaction("create");
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			try {
				featureStore.setTransaction(transaction);
				featureStore.addFeatures(result);
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
		} else {
			System.out.println(typeName + " does not support read/write access.");
			System.exit(1);
		}
	}

}
