package com.compuware.apm.tools.gisdatapreprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

public class ShapeFileFixer {

	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3_FIXED_UP = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind" + System.currentTimeMillis() + ".shp";

	private static SimpleFeatureSource naturalEarthAdmin1RegionsFeatureSource;
//	private static SimpleFeatureSource naturalEarthAdmin1RegionsForMaxmindFeatureSource;

	private static Map<String, String> isoToFipsMappingForCountries;
	private static Map<String, Region>  unassignedMaxmindRegions;
	private static Map<String, Region>  maxmindRegions;
	private static Map<String, Region>  maxmindRegionsByFIPSCode;
	private static Map<String, Map<String, Region>> maxmindRegionsByCountry;

	private static Map<String, MutableInt> fipsCodeFrequency;
	private static Map<String, MutableInt> nameFrequency;


	private static final List<String> attributesToCopy = new ArrayList<String>();

	static {
		attributesToCopy.add("the_geom");
		attributesToCopy.add("adm1_code");
		attributesToCopy.add("iso_a2");
		attributesToCopy.add("name");
		attributesToCopy.add("name_alt");
		attributesToCopy.add("fips");
		attributesToCopy.add("fips_alt");
		attributesToCopy.add("woe_label");
		attributesToCopy.add("woe_name");
		attributesToCopy.add("gn_name");
		attributesToCopy.add("iso_3166_2");
	}


	public static void main(String[] args) throws IOException {

		initShapeFileDataSources();
		loadAndPrepareMaxmindData();

		// create modified feature type
		SimpleFeatureType modifiedFeatureType = createModifiedFeatureType();

		// analyze feature source for exclusion criteria
		analyzeNaturalEarthData();

		// create iterator over features from the original natural earth data
		SimpleFeatureIterator iterator = naturalEarthAdmin1RegionsFeatureSource.getFeatures().features();

		// create feature collection that should store the result
		DefaultFeatureCollection result = fixupFeatures(modifiedFeatureType, iterator);
		analyzeOutputData(result);

		SHPFileUtils.writeResultToFile(modifiedFeatureType, result, NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3_FIXED_UP);

		System.out.println();
		System.out.println(unassignedMaxmindRegions.size() + " maxmind regions were not assigned a geographic feature!");
		System.out.println();

		Map<String, MutableInt> unassignedByCountry = new HashMap<String, MutableInt>();
		for(String key: unassignedMaxmindRegions.keySet()) {
			Region region = unassignedMaxmindRegions.get(key);
			System.out.println(region.getCode() + " - " + region.getName());

			MutableInt counter = unassignedByCountry.get(region.getCountryCode());
			if (counter == null) {
				unassignedByCountry.put(region.getCountryCode(), new MutableInt());
			} else {
				counter.increment();
			}
		}

		System.out.println();
		System.out.println("Unassigned regions by country: ");
		System.out.println();
		for (String key: unassignedByCountry.keySet()) {
			System.out.println("  " + key + " " + unassignedByCountry.get(key).get());
		}
	}

	private static void analyzeNaturalEarthData() throws IOException
	{
		fipsCodeFrequency = new HashMap<String, MutableInt>();
		nameFrequency = new HashMap<String, MutableInt>();
		SimpleFeatureIterator iterator = naturalEarthAdmin1RegionsFeatureSource.getFeatures().features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				String fipsCode = feature.getAttribute("fips").toString();

				MutableInt counter = fipsCodeFrequency.get(fipsCode);
				if (counter == null) {
					fipsCodeFrequency.put(fipsCode, new MutableInt());
				} else {
					counter.increment();
				}

				String name = feature.getAttribute("name").toString();
				counter = nameFrequency.get(name);
				if (counter == null) {
					nameFrequency.put(name, new MutableInt());
				} else {
					counter.increment();
				}

			}
		} finally {
			iterator.close();
		}

		// Print analyzer results:
		/*
		System.out.println("FIPS Codes present multiple times:");
		for (String key: fipsCodeFrequency.keySet()) {
			int frequency = fipsCodeFrequency.get(key).get();
			if (frequency > 1) {
				System.out.println(key + ": " + frequency);
			}
		}
		System.out.println();
		System.out.println("Names present multiple times:");
		for (String key: nameFrequency.keySet()) {
			int frequency = nameFrequency.get(key).get();
			if (frequency > 1) {
				System.out.println(key + ": " + frequency);
			}
		}
		*/

	}

	private static void analyzeOutputData(DefaultFeatureCollection result)
	{
		Map<String, MutableInt> fipsCodeFrequency = new HashMap<String, MutableInt>();

		SimpleFeatureIterator iterator = result.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				String fipsCode = feature.getAttribute("fips").toString();

				MutableInt counter = fipsCodeFrequency.get(fipsCode);
				if (counter == null) {
					fipsCodeFrequency.put(fipsCode, new MutableInt());
				} else {
					counter.increment();
				}

			}
		} finally {
			iterator.close();
		}
		System.out.println();
		System.out.println("FIPS Codes present multiple times:");
		for (String key: fipsCodeFrequency.keySet()) {
			int frequency = fipsCodeFrequency.get(key).get();
			if (frequency > 1) {
				System.out.println(key + ": " + frequency);
			}
		}
	}

	private static void loadAndPrepareMaxmindData() throws IOException {
		isoToFipsMappingForCountries = MaxMindCSVFileReader.getIsoCountryCodeToFIPSCountryCodeMapping();
		maxmindRegions = MaxMindCSVFileReader.readMaxmindRegions();

		maxmindRegionsByFIPSCode = new HashMap<String, Region>();
		maxmindRegionsByCountry = new HashMap<String, Map<String, Region>>();

		Set<String> uniqueRegionCodes = maxmindRegions.keySet();
		for (String uniqueRegionCode: uniqueRegionCodes) {
			Region region = maxmindRegions.get(uniqueRegionCode);

			if (!maxmindRegions.containsKey(region.getCountryCode())) {
				maxmindRegionsByCountry.put(region.getCountryCode(), new HashMap<String, Region>());
			}
			maxmindRegionsByCountry.get(region.getCountryCode()).put(uniqueRegionCode, region);
		}

		// to simplify things we also store each region a second time with a FIPS code (as opposed
		// to the combination of ISO and FIPS maxmind uses).
		for (String uniqueRegionCode: uniqueRegionCodes) {
			Region region = maxmindRegions.get(uniqueRegionCode);
			String fipsCountryCode = isoToFipsMappingForCountries.get(region.getCountryCode());
			String fipsCode = fipsCountryCode + uniqueRegionCode.substring(3);

			maxmindRegionsByFIPSCode.put(fipsCode, region);
		}
		unassignedMaxmindRegions = new HashMap<String, Region>(maxmindRegions);

		for (String key: MaxmindSpecialCaseThreatment.regionsThatAreMappedToOtherRegions.keySet()) {
			if (unassignedMaxmindRegions.containsKey(key)) {
				unassignedMaxmindRegions.remove(key);
			} else {
				System.out.println("ERROR: No region with key " + key + " but it was expected to exist!");
			}
		}

	}
/*
	private static void writeResultToFile(DefaultFeatureCollection result,
		ShapefileDataStore newDataStore) throws IOException {
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

	private static ShapefileDataStore createOutputFile(SimpleFeatureType featureType)
			throws MalformedURLException, IOException {
		File newFile = new File(NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3_FIXED_UP);
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();


		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(featureType);
		return newDataStore;
	}
*/
	private static DefaultFeatureCollection fixupFeatures(SimpleFeatureType modifiedFeatureType,
			SimpleFeatureIterator iterator) {
		DefaultFeatureCollection result = new DefaultFeatureCollection();

		try {
			while (iterator.hasNext()) {
				SimpleFeature original = iterator.next();

				SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(modifiedFeatureType);
				SimpleFeature retypedFeature = SimpleFeatureBuilder.retype(original, featureBuilder);

				fixupFeature(retypedFeature);

				result.add(retypedFeature);
			}
		} finally {
			iterator.close();
		}

		return result;
	}

	private static void fixupFeature(SimpleFeature retypedFeature)
	{
		String name = retypedFeature.getAttribute("name").toString();
		String fipsCode = retypedFeature.getAttribute("fips").toString();
		String isoCountryCode = retypedFeature.getAttribute("iso_a2").toString();
		String admin1Code = retypedFeature.getAttribute("adm1_code").toString();

		if ("GB".equals(isoCountryCode)) {
			fixupFeatureByAlternativeFips(retypedFeature);
		} else if (MaxmindSpecialCaseThreatment.regionsThatAreIgnored.contains(admin1Code)) {
			fixupIgnoredRegion(retypedFeature);
		} else if (MaxmindSpecialCaseThreatment.staticMappingsFromAdmin1CodeToMaxMindCode.containsKey(admin1Code)) {
			fixupRegionWithStaticOverride(retypedFeature);
		} else if (MaxmindSpecialCaseThreatment.countriesWithoutRegions.contains(isoCountryCode)) {
			fixupRegionOfCountryThatIsntSplitIntoRegionsInMaxmind(retypedFeature, isoCountryCode);
		} else if ("US".equals(isoCountryCode) || "CA".equals(isoCountryCode)) {
			fixupFeatureByIso3166_2(retypedFeature);
		} else if (MaxmindSpecialCaseThreatment.fixedFIPSCodesForNaturalEarth.containsKey(admin1Code)) {
			fixupNaturalEarthFipsCode(retypedFeature);
		} else if (fipsCode != null && !fipsCode.isEmpty() && fipsCodeFrequency.get(fipsCode).get() == 1
				&& maxmindRegionsByFIPSCode.containsKey(fipsCode)) {
			fixupRegionByFIPS(retypedFeature, fipsCode);
		} else if (name != null && !name.isEmpty() && nameFrequency.get(name).get() == 1) {
			fixupFeatureByName(retypedFeature);
		}

	}

	private static void fixupRegionWithStaticOverride(SimpleFeature feature) {
		String admin1Code = feature.getAttribute("adm1_code").toString();
		String maxmindCode = MaxmindSpecialCaseThreatment.staticMappingsFromAdmin1CodeToMaxMindCode.get(admin1Code);

		Region region = maxmindRegions.get(maxmindCode);
		if (region != null) {
			feature.setAttribute("geoip_code", region.getCode());
			feature.setAttribute("geoip_name", region.getName());
			feature.setAttribute("geoip_alg", "Static override of region assignment");
			unassignedMaxmindRegions.remove(region.getCode());
		} else {
			System.out.println("Found no region altough override code was specified for " + admin1Code);
		}

	}

	private static void fixupFeatureByAlternativeFips(SimpleFeature feature) {
		String alternativeFips = feature.getAttribute("fips_alt").toString();
		// TODO: add check for duplicates
		if (maxmindRegionsByFIPSCode.get(alternativeFips) != null) {
			Region region = maxmindRegionsByFIPSCode.get(alternativeFips);
			if (region != null) {
				feature.setAttribute("geoip_code", region.getCode());
				feature.setAttribute("geoip_name", region.getName());
				feature.setAttribute("geoip_alg", "Fixed using alternative FIPS Code!");
				feature.setAttribute("fips", alternativeFips);
				unassignedMaxmindRegions.remove(region.getCode());
			}
		}
	}

	private static void fixupNaturalEarthFipsCode(SimpleFeature feature) {
		// get fixed fips code
		String fipsCode = MaxmindSpecialCaseThreatment.fixedFIPSCodesForNaturalEarth.get(feature.getAttribute("adm1_code").toString());

		Region region = maxmindRegionsByFIPSCode.get(fipsCode);
		if (region != null) {
			feature.setAttribute("geoip_code", region.getCode());
			feature.setAttribute("geoip_name", region.getName());
			feature.setAttribute("geoip_alg", "Fixed natural earth FIPS code");
			feature.setAttribute("fips", fipsCode);
			unassignedMaxmindRegions.remove(region.getCode());
		}
	}


	private static void fixupFeatureByIso3166_2(SimpleFeature feature) {
		String iso3166Code = feature.getAttribute("iso_3166_2").toString();
		if (maxmindRegions.containsKey(iso3166Code)) {
			Region region = maxmindRegions.get(iso3166Code);
			feature.setAttribute("geoip_code", region.getCode());
			feature.setAttribute("geoip_name", region.getName());
			feature.setAttribute("geoip_alg", "Identified by ISO 3166-2 code");

			unassignedMaxmindRegions.remove(iso3166Code);
		} else {
			System.out.println("Didn't find US/CA region for " + feature.getAttribute("adm1_code"));
		}
	}

	private static void fixupIgnoredRegion(SimpleFeature feature) {
		feature.setAttribute("geoip_code", "-");
		feature.setAttribute("geoip_name", "-");
		feature.setAttribute("geoip_alg", "Region so unimportant that it is ignored!");
	}

	private static void fixupRegionOfCountryThatIsntSplitIntoRegionsInMaxmind(SimpleFeature retypedFeature,
			String isoCountryCode)
	{
		retypedFeature.setAttribute("geoip_code", isoCountryCode);
		retypedFeature.setAttribute("geoip_name", "-");
		retypedFeature.setAttribute("geoip_alg", "Country has no regions in Maxmind");
	}

	private static void fixupRegionByFIPS(SimpleFeature retypedFeature,
			String fipsCode) {
		Region region = maxmindRegionsByFIPSCode.get(fipsCode);

		retypedFeature.setAttribute("geoip_code", region.getCode());
		retypedFeature.setAttribute("geoip_name", region.getName());
		retypedFeature.setAttribute("geoip_alg", "Identical Fips code");

		unassignedMaxmindRegions.remove(region.getCode());
	}

	private static void fixupFeatureByName(SimpleFeature retypedFeature)
	{
		String admin1Code = retypedFeature.getAttribute("adm1_code").toString();
		String name = retypedFeature.getAttribute("name").toString();
		String[] alternativeNames = retypedFeature.getAttribute("name_alt").toString().split("|");
		String woeLabel = retypedFeature.getAttribute("woe_label").toString().split(",")[0];
		String woeName = retypedFeature.getAttribute("woe_name").toString();
		String gnName = retypedFeature.getAttribute("gn_name").toString();
		String isoTwoLetterCode = retypedFeature.getAttribute("iso_a2").toString();

		Map<String, Region> regions = maxmindRegionsByCountry.get(isoTwoLetterCode);

		if ("-1".equals(isoTwoLetterCode)) {
			return;
		}
		if (regions == null || regions.size() == 0) {
			System.out.println("No regions for " + isoTwoLetterCode + " (" + admin1Code + " " + name + ")");
			return;
		}
		List<Region> matchingRegion = new ArrayList<Region>();
		for(String regionCode: regions.keySet()) {
			Region region = regions.get(regionCode);
			String geoIpName = region.getName();
			if (geoIpName.equals(name) || geoIpName.equals(woeLabel) || geoIpName.equals(woeName) || geoIpName.equals(gnName)) {
				matchingRegion.add(region);
			} else {
				for (String alternativeName: alternativeNames) {
					if (geoIpName.equals(alternativeName)) {
						matchingRegion.add(region);
					}
				}
			}
		}

		if (matchingRegion.size() == 1) {
			retypedFeature.setAttribute("geoip_code", matchingRegion.get(0).getCode());
			retypedFeature.setAttribute("geoip_name", matchingRegion.get(0).getName());
			retypedFeature.setAttribute("geoip_alg", "Identical name");

			unassignedMaxmindRegions.remove(matchingRegion.get(0).getCode());
		} else if (matchingRegion.size() > 1) {
			System.out.println("More than one region with matching name for " + admin1Code + " (" + name + ")");
		}
	}

	private static SimpleFeatureType createModifiedFeatureType() {
		SimpleFeatureType featureType = naturalEarthAdmin1RegionsFeatureSource.getSchema();
		SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
		featureTypeBuilder.setName(featureType.getName());
		featureTypeBuilder.setCRS(featureType.getCoordinateReferenceSystem());
		for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
			/* System.out.println("Attribute: " + ad.getName().toString()); */
			if (attributesToCopy.contains(ad.getName().toString())) {
				featureTypeBuilder.add(ad);
			}
		}

		featureTypeBuilder.add("geoip_code", String.class);
		featureTypeBuilder.add("geoip_name", String.class);
		featureTypeBuilder.add("geoip_alg", String.class);
		SimpleFeatureType modifiedFeatureType = featureTypeBuilder.buildFeatureType();
		return modifiedFeatureType;
	}

	private static void initShapeFileDataSources() throws IOException
	{
		File file = new File(NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3);
		ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
		naturalEarthAdmin1RegionsFeatureSource = store.getFeatureSource();
	}

}