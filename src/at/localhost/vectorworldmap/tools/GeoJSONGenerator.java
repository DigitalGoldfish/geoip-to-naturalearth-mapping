package at.localhost.vectorworldmap.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import at.localhost.vectorworldmap.util.FeatureJSON;
import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

public class GeoJSONGenerator {

	 /**
	 *
	 */
	//private static final String OUTPUT_PATH = "c:/workspaces/apm/apm/ui/components/com.compuware.apm.webui.gwt.monitoring/src/main/resources/assets/map/data/";
	private static final String OUTPUT_PATH = "d:/geojson/";

	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";

	// private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "out/shp/simplified_features_admin0_tolerance_0_05.shp";
	//private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "out/shp/simplified_features_admin1_tolerance_0_05.shp";

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;

	private static SimpleFeatureType outputFeatureType;

	private static final Map<String, String> continentNameToContinentCode = new HashMap<String, String>();
	private static final Map<String, String> continentCodeToContinentName = new HashMap<String, String>();

	private static final Map<String, String> subregionNameToSubregionCode = new HashMap<String, String>();
	private static final Map<String, String> subregionCodeToSubregionName = new HashMap<String, String>();

	private static final Map<String, List<Region>> subregionsByContinents = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> countriesByContinents = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> countriesBySubregions = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> regionsByCountry   = new HashMap<String, List<Region>>();
	private static final Map<String, Region> countries   = new HashMap<String, Region>();
	private static final Map<String, Region> continents = new HashMap<String, Region>();
	private static final Map<String, Region> subregions = new HashMap<String, Region>();

	private static final Map<String, String> subregionToContinent = new HashMap<String, String>();

	static {
		continentNameToContinentCode.put("Africa", "CONT_AF");
		continentNameToContinentCode.put("Asia", "CONT_AS");
		continentNameToContinentCode.put("Antarctica", null); // we ignore Antarctica
		continentNameToContinentCode.put("Europe", "CONT_EU");
		continentNameToContinentCode.put("North America", "CONT_NA");
		continentNameToContinentCode.put("South America", "CONT_SA");
		continentNameToContinentCode.put("Oceania", "CONT_OC");
		continentNameToContinentCode.put("Seven seas (open ocean)", /*"P"*/ null);

		subregionNameToSubregionCode.put("Middle Africa", "MAF");
		subregionNameToSubregionCode.put("Western Europe", "WEU");
		subregionNameToSubregionCode.put("Western Asia", "WAS");
		subregionNameToSubregionCode.put("Caribbean", "CAR");
		subregionNameToSubregionCode.put("Eastern Africa", "EAF");
		subregionNameToSubregionCode.put("Eastern Europe", "EEU");
		subregionNameToSubregionCode.put("Central Asia", "CAS");
		subregionNameToSubregionCode.put("Southern Europe", "SEU");
		subregionNameToSubregionCode.put("Seven seas (open ocean)", "OCE");
		subregionNameToSubregionCode.put("Eastern Asia", "EAS");
		subregionNameToSubregionCode.put("Northern America", "NAM");
		subregionNameToSubregionCode.put("South America", "SAM");
		subregionNameToSubregionCode.put("Australia and New Zealand", "ANZ");
		subregionNameToSubregionCode.put("Southern Africa", "SAF");
		subregionNameToSubregionCode.put("Northern Africa", "NAF");
		subregionNameToSubregionCode.put("South-Eastern Asia", "SEA");
		subregionNameToSubregionCode.put("Northern Europe", "NEU");
		subregionNameToSubregionCode.put("Micronesia", "MIC");
		subregionNameToSubregionCode.put("Melanesia", "MEL");
		subregionNameToSubregionCode.put("Polynesia", "POL");
		subregionNameToSubregionCode.put("Western Africa", "WAF");
		subregionNameToSubregionCode.put("Central America", "CAM");
		subregionNameToSubregionCode.put("Southern Asia", "SAS");
	}


	public static void main(String[] args) throws IOException {

		initShapeFileDataSources();
		loadCountries();
		loadRegions();

		generateAdditionalShapes();

		// generate GEOJson Files
		generateGeoJSONFiles();
	}

	private static void generateAdditionalShapes()
	{
		for (String continentCode: countriesByContinents.keySet()) {
			List<Region> countriesForContinent = countriesByContinents.get(continentCode);
			Geometry geometry = mergeRegions(countriesForContinent, continentCode);
			if (geometry != null) {
				Region continent = new Region(continentCode, continentCodeToContinentName.get(continentCode), RegionType.CONTINENT, continentCode);
				continent.setGeometry(geometry);
				continent.setParentId("WORLD");
				continents.put(continentCode, continent);
			} else {
				System.out.println("Error generating shape for " + continentCode);
			}
		}

		for (String subregionCode: countriesBySubregions.keySet()) {
			List<Region> countriesForSubregion = countriesBySubregions.get(subregionCode);
			if (countriesForSubregion != null && countriesForSubregion.size() > 0) {
				Geometry geometry = mergeRegions(countriesForSubregion, subregionCode);
				if (geometry != null) {
					String continentCode = subregionToContinent.get(subregionCode);
					Region subregion = new Region(subregionCode,  subregionCodeToSubregionName.get(subregionCode), RegionType.SUBREGION, continentCode);
					subregion.setGeometry(geometry);
					subregion.setParentId(continentCode);
					subregions.put(subregionCode, subregion);
					System.out.println("Subregion: " + subregionCode);
					if (!subregionsByContinents.containsKey(continentCode)) {
						subregionsByContinents.put(continentCode, new ArrayList<Region>());
					}
					subregionsByContinents.get(continentCode).add(subregion);
				} else {
					System.out.println("Error generating shape for " + subregionCode);
				}
			} else {
				System.out.println("No regions for Subregion " + subregionCode);
			}
		}
	}

	private static Geometry mergeRegions(List<Region> regions, String code) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		for (Region region : regions) {
			Geometry geometry = region.getGeometry().buffer(0);
			geometries.add(geometry);
			validateGeometry(geometry, region.getCode());
		}

		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
		Geometry geometry = factory.buildGeometry(geometries);

		/*if (geometry.getNumGeometries() > 1) {
			GeometryCollection geometryCollection = (GeometryCollection) geometry;
			Geometry unionedGeometry = geometryCollection.union();
			validateGeometry(unionedGeometry, code);
			return unionedGeometry;
		}*/
		System.out.println("No region collection for " + code);
		return geometry;
		/*try {
			return geometryCollection.union().buffer(0.005);
		} catch (Exception e) {
			System.out.println("Error generating unioned shape");
			return null;
		}*/
	}

	private static void generateGeoJSONFiles() throws IOException {

		/*
		Collection<Region> continents = GeoJSONGenerator.continents.values();
		SimpleFeatureCollection continentFeatures = createFeatures(continents);
		Collection<Region> subregions = GeoJSONGenerator.subregions.values();
		SimpleFeatureCollection subregionFeatures = createFeatures(subregions);

		DefaultFeatureCollection unifiedFeatures = new DefaultFeatureCollection();
		unifiedFeatures.addAll(continentFeatures);
		// unifiedFeatures.addAll(subregionFeatures);

		generateGeoJSONFile("WORLD", unifiedFeatures, 0.2);
		*/
/*
		Collection<Region> subregions = GeoJSONGenerator.subregions.values();
		SimpleFeatureCollection subregionFeatures = createFeatures(subregions);
		generateGeoJSONFile("subregions-world", subregionFeatures);
 */
		/*
		for (String continentCode: subregionsByContinents.keySet()) {
			List<Region> subregionsForContinent = subregionsByContinents.get(continentCode);
			SimpleFeatureCollection features = createFeatures(subregionsForContinent);
			generateGeoJSONFile("subregions-continent-" + continentCode, features);
		} */

		/*
		for (String continentCode: countriesByContinents.keySet()) {
			List<Region> countriesForContinent = countriesByContinents.get(continentCode);
			SimpleFeatureCollection features = createFeatures(countriesForContinent);
			generateGeoJSONFile(continentCode, features, 0.1);
		}
		*/

		/*
		for (String subregionCode: countriesBySubregions.keySet()) {
			List<Region> countriesForSubregion = countriesBySubregions.get(subregionCode);
			SimpleFeatureCollection features = createFeatures(countriesForSubregion);
			generateGeoJSONFile("countries-subregion-" + subregionCode, features);
		} */



		for (String countryCode: regionsByCountry.keySet()) {

			List<Region> regionsForCountry = regionsByCountry.get(countryCode);
			SimpleFeatureCollection features = createFeatures(regionsForCountry);
			generateGeoJSONFile(countryCode, features, 0.05);

			/*
			System.out.println(" ");
			System.out.println("Creating json file for " + countryCode);
			System.out.println("========================== ");
			List<Region> regionsForCountry = regionsByCountry.get(countryCode);
			DefaultFeatureCollection features = createFeatures(regionsForCountry);
			ReferencedEnvelope bounds = features.getBounds();

			bounds.expandBy(bounds.getWidth() * 0.15, bounds.getHeight() * 0.15);

			DefaultFeatureCollection countryFeatures = new DefaultFeatureCollection();
			try {
				for (String continentCode: countriesByContinents.keySet()) {
					for (Region country: countriesByContinents.get(continentCode)) {
						//if (!country.getCountryCode().equals(countryCode)) {
							System.out.println("Country " + country.getName() + " is close to " + countryCode);
							SimpleFeature feature = createSimpleFeature(country);
							if (bounds.intersects(feature.getBounds())) {
								SimpleFeature clippedFeature = createSimpleClippedFeature(country, bounds);
								countryFeatures.add(clippedFeature);
							}
						//}
					}
				}

				generateGeoJSONFile(countryCode, countryFeatures, 0.05);
			} catch (Exception e) {
				System.out.println("Exception " + e.getMessage() + " while processing " + countryCode);
			}*/

		}

	}

	private static void generateGeoJSONFile(String filename, SimpleFeatureCollection features, double distanceTolerance) throws IOException
	{
		FeatureJSON feature = new FeatureJSON(new GeometryJSON(8));
		feature.setEncodeFeatureCollectionCRS(true);
		feature.setEncodeFeatureCollectionBounds(true);
		feature.setEncodeDistanceTolerance(true);
		feature.setDistanceTolerance(distanceTolerance);

		FileWriter writer = new FileWriter(OUTPUT_PATH + filename + ".json");
		feature.writeFeatureCollection(features, writer);
	}

	private static DefaultFeatureCollection createFeatures(Collection<Region> regions) {
		DefaultFeatureCollection features = new DefaultFeatureCollection();
		for (Region region : regions) {
			SimpleFeature feature = createSimpleFeature(region);
			features.add(feature);
		}
		return features;
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

	private static void loadCountries() throws IOException {
		SimpleFeatureCollection countries = countryFeatureSource.getFeatures();
		SimpleFeatureIterator iterator = countries.features();

		try {
			while(iterator.hasNext()) {
				SimpleFeature country = iterator.next();

				String continentName = country.getAttribute("continent").toString();
				String continentCode = continentNameToContinentCode.get(continentName);
				continentCodeToContinentName.put(continentCode, continentName);

				String subregionName = country.getAttribute("subregion").toString();
				String subregionCode = subregionNameToSubregionCode.get(subregionName);
				subregionCodeToSubregionName.put(subregionCode, subregionName);

				String name = country.getAttribute("name").toString();
				String code = country.getAttribute("iso_a2").toString();

				validateGeometry((Geometry) country.getDefaultGeometry(), country.getAttribute("iso_a2").toString());

				if (continentCode != null) {
					// I don't care if it is already in the map since i just overwrite
					// it with the same values anyway - no need for a check
					subregionToContinent.put(subregionCode, continentCode);

					Region region = new Region(code, name, RegionType.COUNTRY, continentCode);
					region.setGeometry((Geometry)country.getDefaultGeometry());
					region.setParentId(continentCode);

					if (!countriesByContinents.containsKey(continentCode)) {
						countriesByContinents.put(continentCode, new ArrayList<Region>());
					}
					countriesByContinents.get(continentCode).add(region);

					if (!countriesBySubregions.containsKey(subregionCode)) {
						countriesBySubregions.put(subregionCode, new ArrayList<Region>());
					}
					countriesBySubregions.get(subregionCode).add(region);
					GeoJSONGenerator.countries.put(code, region);
				}
			}
		} finally {
			iterator.close();
		}
	}

	private static void loadRegions() throws IOException {
		SimpleFeatureCollection regions = regionFeatureSource.getFeatures();
		SimpleFeatureIterator iterator = regions.features();

		try {
			while(iterator.hasNext()) {
				SimpleFeature admin1Region = iterator.next();

				String name = admin1Region.getAttribute("name").toString();
				String code = admin1Region.getAttribute("adm1_code").toString();
				String countryCode = admin1Region.getAttribute("iso_a2").toString();

				Region country = countries.get(countryCode);
				if (country == null) {
					System.out.println("no country found for " + countryCode);
				} else {
					Region region = new Region(code, name, RegionType.REGION, countryCode, country.getContinentCode());
					region.setGeometry((Geometry) admin1Region.getDefaultGeometry());
					region.setParentId(countryCode);

					if (!regionsByCountry.containsKey(countryCode)) {
						regionsByCountry.put(countryCode, new ArrayList<Region>());
					}
					regionsByCountry.get(countryCode).add(region);
				}
			}
		} finally {
			iterator.close();
		}
	}

	private static SimpleFeature createSimpleFeature(Region region)
	{
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(getFeatureType());
		builder.set("parent_id", region.getParentId());
		builder.set("region_type", region.getType().toString() );
		builder.set("name", region.getName());
		builder.set("geometry", region.getGeometry());


		SimpleFeature newFeature = builder.buildFeature(region.getCode());

		return newFeature;
	}

	private static SimpleFeature createSimpleClippedFeature(Region region, com.vividsolutions.jts.geom.Envelope envelope)
	{

		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(getFeatureType());
		builder.set("parent_id", region.getParentId());
		builder.set("region_type", region.getType().toString() );
		builder.set("name", region.getName());

		GeometryFactory factory = new GeometryFactory();
		Geometry envelopeGeometry = factory.toGeometry(envelope);

		Geometry intersection = envelopeGeometry.intersection(region.getGeometry());

		builder.set("geometry", intersection);

		SimpleFeature newFeature = builder.buildFeature(region.getCode());

		return newFeature;
	}

	private static SimpleFeatureType getFeatureType() {

		if (outputFeatureType == null) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("Region");
			builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

			// add attributes in order
			builder.add("geometry", Geometry.class);
//			builder.add("id", String.class);
			builder.add("parent_id", String.class);
			builder.add("region_type", String.class);
			builder.add("name", String.class);
			builder.setDefaultGeometry("geometry");

			// build the type
			outputFeatureType = builder.buildFeatureType();

		}

		return outputFeatureType;
	}

	private static void validateGeometry(Geometry geometry, String code) {
		IsValidOp validityOperation = new IsValidOp(geometry);
		TopologyValidationError err = validityOperation.getValidationError();
		if (err != null) {
			System.out.println("Error found for region " + code);
			System.out.println(err);
		}
	}

}
