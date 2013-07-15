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
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;

public class GeoJSONGenerator {

	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;

	private static SimpleFeatureType outputFeatureType;

	private static final Map<String, List<Region>> subregionsByContinents = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> countriesByContinents = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> countriesBySubregions = new HashMap<String, List<Region>>();
	private static final Map<String, List<Region>> regionsByCountry   = new HashMap<String, List<Region>>();
	private static final Map<String, Region> countries   = new HashMap<String, Region>();
	private static final Map<String, Region> continents = new HashMap<String, Region>();
	private static final Map<String, Region> subregions = new HashMap<String, Region>();

	private static final Map<String, String> subregionToContinent = new HashMap<String, String>();
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
			Geometry geometry = mergeRegions(countriesForContinent);
			if (geometry != null) {
				Region continent = new Region(continentCode, continentCode, RegionType.CONTINENT, continentCode);
				continent.setGeometry(geometry);
				continents.put(continentCode, continent);
			} else {
				System.out.println("Error generating shape for " + continentCode);
			}
		}

		for (String subregionCode: countriesBySubregions.keySet()) {
			List<Region> countriesForSubregion = countriesBySubregions.get(subregionCode);
			if (countriesForSubregion != null && countriesForSubregion.size() > 0) {
				Geometry geometry = mergeRegions(countriesForSubregion);
				if (geometry != null) {
					String continentCode = subregionToContinent.get(subregionCode);
					Region subregion = new Region(subregionCode, subregionCode, RegionType.SUBREGION, continentCode);
					subregion.setGeometry(geometry);
					subregions.put(subregionCode, subregion);
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

	private static Geometry mergeRegions(List<Region> regions) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		for (Region region : regions) {
			geometries.add(region.getGeometry().buffer(0));
		}

		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
		GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(geometries);
		try {
			return geometryCollection.union();
		} catch (Exception e) {
			System.out.println("Error generating unioned shape");
			return null;
		}
	}

	private static void generateGeoJSONFiles() throws IOException {

		Collection<Region> continents = GeoJSONGenerator.continents.values();
		SimpleFeatureCollection continentFeatures = createFeatures(continents);
		generateGeoJSONFile("continents-world", continentFeatures);

		Collection<Region> subregions = GeoJSONGenerator.subregions.values();
		SimpleFeatureCollection subregionFeatures = createFeatures(subregions);
		generateGeoJSONFile("subregions-world", subregionFeatures);

		for (String continentCode: subregionsByContinents.keySet()) {
			List<Region> subregionsForContinent = subregionsByContinents.get(continentCode);
			SimpleFeatureCollection features = createFeatures(subregionsForContinent);
			generateGeoJSONFile("subregions-continent-" + continentCode, features);
		}

		for (String continentCode: countriesByContinents.keySet()) {
			List<Region> countriesForContinent = countriesByContinents.get(continentCode);
			SimpleFeatureCollection features = createFeatures(countriesForContinent);
			generateGeoJSONFile("countries-continent-" + continentCode, features);
		}

		for (String subregionCode: countriesBySubregions.keySet()) {
			List<Region> countriesForSubregion = countriesBySubregions.get(subregionCode);
			SimpleFeatureCollection features = createFeatures(countriesForSubregion);
			generateGeoJSONFile("countries-subregion-" + subregionCode, features);
		}

		for (String countryCode: regionsByCountry.keySet()) {
			List<Region> regionsForCountry = regionsByCountry.get(countryCode);
			SimpleFeatureCollection features = createFeatures(regionsForCountry);
			generateGeoJSONFile("regions-" + countryCode, features);
		}

	}

	private static void generateGeoJSONFile(String filename, SimpleFeatureCollection features) throws IOException
	{
		FeatureJSON feature = new FeatureJSON(new GeometryJSON(4));
		FileWriter writer = new FileWriter("out/" + filename + ".json");
		feature.writeFeatureCollection(features, writer);
	}

	private static SimpleFeatureCollection createFeatures(Collection<Region> regions) {
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
				String continent = country.getAttribute("continent").toString();
				String subregion = country.getAttribute("subregion").toString();
				String name = country.getAttribute("name").toString();
				String code = country.getAttribute("iso_a2").toString();
				// I don't care if it is already in the map since i just overwrite
				// it with the same values anyway - no need for a check
				subregionToContinent.put(subregion, continent);

				Region region = new Region(code, name, RegionType.COUNTRY, continent);
				region.setGeometry((Geometry)country.getDefaultGeometry());

				if (!countriesByContinents.containsKey(continent)) {
					countriesByContinents.put(continent, new ArrayList<Region>());
				}
				countriesByContinents.get(continent).add(region);

				if (!countriesBySubregions.containsKey(subregion)) {
					countriesBySubregions.put(subregion, new ArrayList<Region>());
				}
				countriesBySubregions.get(subregion).add(region);
				GeoJSONGenerator.countries.put(code, region);

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
					Region region = new Region(code, name, RegionType.COUNTRY, countryCode, country.getContinentCode());
					region.setGeometry((Geometry) admin1Region.getDefaultGeometry());

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
//		builder.set("id", region.getCode());
		builder.set("parent_id", region.getCountryCode());
		builder.set("region_type", region.getType().toString() );
		builder.set("name", region.getName());
		builder.set("geometry", region.getGeometry());


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

}
