package at.localhost.vectorworldmap.deprecated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.util.ProgressListener;

import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;

import com.vividsolutions.jts.algorithm.distance.DiscreteHausdorffDistance;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;


public class GISDataPreprocessor {

	private static final String MAXMIND_COUNTRIES = "resources/maxmind-countries.csv";
	private static final String MAXMIND_REGIONS = "resources/maxmind-regions.csv";
	private static final String MAXMIND_COUNTRIES_TO_CONTINENTS = "resources/maxmind-countries-to-continents.csv";
	private static final String FIPS_TO_ISO_COUNTRY_CODE_MAPPING = "resources/fips10-4_to_iso3166_countrycodes.csv";

	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";
	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V14 = "resources/naturalearth/v14/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";

	private static final Map<String, String> countryIdToContinentIdMap = new HashMap<String, String>();
	private static final Map<String, String> maxmindContinentIdToCompuwareContinentIdMap = new HashMap<String, String>();
	private static final Map<String, String> isoCountryCodeToFipsCountryCode = new HashMap<String, String>();

	private static final List<String> unmappableRegions = new ArrayList<String>();
	private static final Map<String, String> specialCasesWhereRegionsAreThreatedAsCountries = new HashMap<String, String>();
	private static final Map<String, String> specialCasesWhereCountriesAreThreatedAsRegions = new HashMap<String, String>();
	private static final Map<String, String[]> manualMappingOfRegionIdsToAdm1Ids = new HashMap<String, String[]>();
	private static final Map<String, String> remappedRegionIds = new HashMap<String, String>();
	private static final Map<String, DefaultFeatureCollection> featureCollections = new HashMap<String, DefaultFeatureCollection>();

	private static SimpleFeatureSource countryFeatureSource;
	private static SimpleFeatureSource regionFeatureSource;
	private static SimpleFeatureSource regionFeatureSourcev14;

	private static int failureCount = 0;
	private static int warningsCount = 0;
	private static Map<String, Integer> failuresByCountry = new HashMap<String, Integer>();

	private static List<String> logOfCurrentRecord;
	private static int currentRecordStatus;

	private static final int STATUS_ERROR = 3;
	private static final int STATUS_WARNING = 2;
	private static final int STATUS_OK = 1;

	static {
		maxmindContinentIdToCompuwareContinentIdMap.put("AF", "F"); // Africa
		maxmindContinentIdToCompuwareContinentIdMap.put("AS", "A"); // Asia
		maxmindContinentIdToCompuwareContinentIdMap.put("EU", "E"); // Europe
		maxmindContinentIdToCompuwareContinentIdMap.put("NA", "N"); // North America
		maxmindContinentIdToCompuwareContinentIdMap.put("SA", "S"); // South America
		maxmindContinentIdToCompuwareContinentIdMap.put("OC", "O"); // Oceanica
		maxmindContinentIdToCompuwareContinentIdMap.put("--", "Z"); // No continent (e.g. A1(Anonymous Proxy), A2(Satellite Provider), O1(other countries))
		maxmindContinentIdToCompuwareContinentIdMap.put("AN", "Z"); // Antarctica (since we don't display antarctica we map it to no continent)

		// cases where the country to continent file mapping does not contain a valid mapping
		countryIdToContinentIdMap.put("BQ", "SA"); // Bonaire, Saint Eustatius and Saba
		countryIdToContinentIdMap.put("CW", "SA"); // Curacao
		countryIdToContinentIdMap.put("SS", "AF"); // South Sudan
		countryIdToContinentIdMap.put("SX", "NA"); // Sint Marteen

		unmappableRegions.add("A1");     //
		unmappableRegions.add("A2");     //
		unmappableRegions.add("O1");     // Other countries
		unmappableRegions.add("AP");     // Asia Pacific - not assignable to country
		unmappableRegions.add("EU");     // Europe - not assignable to country
		unmappableRegions.add("US-AA");  // Armed Forces America
		unmappableRegions.add("US-AP");  // Armed Forces Asia Pacific
		unmappableRegions.add("US-AE");  // Armed Forces Canada, Europe & Africa
		unmappableRegions.add("KN-01");  // Saint Kitts - 15 regions where we don't have features
		unmappableRegions.add("KN-02");
		unmappableRegions.add("KN-03");
		unmappableRegions.add("KN-04");
		unmappableRegions.add("KN-05");
		unmappableRegions.add("KN-06");
		unmappableRegions.add("KN-07");
		unmappableRegions.add("KN-08");
		unmappableRegions.add("KN-09");
		unmappableRegions.add("KN-10");
		unmappableRegions.add("KN-11");
		unmappableRegions.add("KN-12");
		unmappableRegions.add("KN-13");
		unmappableRegions.add("KN-14");
		unmappableRegions.add("KN-15");
		unmappableRegions.add("LC-01"); // Saint Lucia - 11 Regions
		unmappableRegions.add("LC-02");
		unmappableRegions.add("LC-03");
		unmappableRegions.add("LC-04");
		unmappableRegions.add("LC-05");
		unmappableRegions.add("LC-06");
		unmappableRegions.add("LC-07");
		unmappableRegions.add("LC-08");
		unmappableRegions.add("LC-09");
		unmappableRegions.add("LC-10");
		unmappableRegions.add("LC-11");
		unmappableRegions.add("GD-01"); // Grenada - 6 Regions
		unmappableRegions.add("GD-02");
		unmappableRegions.add("GD-03");
		unmappableRegions.add("GD-04");
		unmappableRegions.add("GD-05");
		unmappableRegions.add("GD-06");
		unmappableRegions.add("GL-01"); // 3 Regions in Greenland, but that do not longer match the
		unmappableRegions.add("GL-02"); // admin1 regions in Greenland
		unmappableRegions.add("GL-03");
		unmappableRegions.add("AG-01"); // Antigua and Barbuda - 8 regions
		unmappableRegions.add("AG-03");
		unmappableRegions.add("AG-04");
		unmappableRegions.add("AG-05");
		unmappableRegions.add("AG-06");
		unmappableRegions.add("AG-07");
		unmappableRegions.add("AG-08");
		unmappableRegions.add("AG-09");
		unmappableRegions.add("BS-05"); // Bahamas - 21 regions
		unmappableRegions.add("BS-06");
		unmappableRegions.add("BS-10");
		unmappableRegions.add("BS-13");
		unmappableRegions.add("BS-15");
		unmappableRegions.add("BS-16");
		unmappableRegions.add("BS-18");
		unmappableRegions.add("BS-22");
		unmappableRegions.add("BS-23");
		unmappableRegions.add("BS-24");
		unmappableRegions.add("BS-25");
		unmappableRegions.add("BS-26");
		unmappableRegions.add("BS-27");
		unmappableRegions.add("BS-28");
		unmappableRegions.add("BS-29");
		unmappableRegions.add("BS-30");
		unmappableRegions.add("BS-31");
		unmappableRegions.add("BS-32");
		unmappableRegions.add("BS-33");
		unmappableRegions.add("BS-34");
		unmappableRegions.add("BS-35");
		unmappableRegions.add("BS-35");
		unmappableRegions.add("KY-01");  // Cayman - 8 regions
		unmappableRegions.add("KY-02");
		unmappableRegions.add("KY-03");
		unmappableRegions.add("KY-04");
		unmappableRegions.add("KY-05");
		unmappableRegions.add("KY-06");
		unmappableRegions.add("KY-07");
		unmappableRegions.add("KY-08");
		unmappableRegions.add("BM-01");  // Bermuda - 11 regions
		unmappableRegions.add("BM-02");
		unmappableRegions.add("BM-03");
		unmappableRegions.add("BM-04");
		unmappableRegions.add("BM-05");
		unmappableRegions.add("BM-06");
		unmappableRegions.add("BM-07");
		unmappableRegions.add("BM-08");
		unmappableRegions.add("BM-09");
		unmappableRegions.add("BM-10");
		unmappableRegions.add("BM-11");
		unmappableRegions.add("BM-11");
		unmappableRegions.add("TT-01");  // Trinidad & Tobago -- 12 regions
		unmappableRegions.add("TT-02");  // Trinidad tobago has no fips codes and mapping does not work because the number and names are totally different;
		unmappableRegions.add("TT-03");
		unmappableRegions.add("TT-04");
		unmappableRegions.add("TT-05");
		unmappableRegions.add("TT-06");
		unmappableRegions.add("TT-07");
		unmappableRegions.add("TT-08");
		unmappableRegions.add("TT-09");
		unmappableRegions.add("TT-10");
		unmappableRegions.add("TT-11");
		unmappableRegions.add("TT-12");
		unmappableRegions.add("MS-01");  // Montserrat -- 3 regions
		unmappableRegions.add("MS-02");
		unmappableRegions.add("MS-03");
		unmappableRegions.add("VC-01"); // Saint Vincent and the Grenadines - 6 regions
		unmappableRegions.add("VC-02");
		unmappableRegions.add("VC-03");
		unmappableRegions.add("VC-04");
		unmappableRegions.add("VC-05");
		unmappableRegions.add("VC-06");
		unmappableRegions.add("FM-01"); // Federated Staes of Micronesia - 4 regions
		unmappableRegions.add("FM-02");
		unmappableRegions.add("FM-03");
		unmappableRegions.add("FM-04");
		unmappableRegions.add("NR-01"); // Nauru aggregation
		unmappableRegions.add("NR-02");
		unmappableRegions.add("NR-03");
		unmappableRegions.add("NR-04");
		unmappableRegions.add("NR-05");
		unmappableRegions.add("NR-06");
		unmappableRegions.add("NR-07");
		unmappableRegions.add("NR-08");
		unmappableRegions.add("NR-09");
		unmappableRegions.add("NR-10");
		unmappableRegions.add("NR-11");
		unmappableRegions.add("NR-12");
		unmappableRegions.add("NR-13");
		unmappableRegions.add("NR-14");
		// unmappableRegions.add("WS-02"); no such region
		unmappableRegions.add("WS-02");  // Samoa - 10 regions
		unmappableRegions.add("WS-03");
		unmappableRegions.add("WS-04");
		unmappableRegions.add("WS-05");
		unmappableRegions.add("WS-06");
		unmappableRegions.add("WS-07");
		unmappableRegions.add("WS-08");
		unmappableRegions.add("WS-09");
		unmappableRegions.add("WS-10");
		unmappableRegions.add("WS-11");
		unmappableRegions.add("TO-01"); // Tonga - 3 Regions
		unmappableRegions.add("TO-02");
		unmappableRegions.add("TO-03");

		unmappableRegions.add("VU-05"); // Vanuatu - 14 regions
		unmappableRegions.add("VU-06"); // some of this regions are actually in natural earth but some aren't
		unmappableRegions.add("VU-07"); // to simplify things i just ignore those that i could map
		unmappableRegions.add("VU-08");
		unmappableRegions.add("VU-09");
		unmappableRegions.add("VU-10");
		unmappableRegions.add("VU-11");
		unmappableRegions.add("VU-12");
		unmappableRegions.add("VU-13");
		unmappableRegions.add("VU-14");
		unmappableRegions.add("VU-15");
		unmappableRegions.add("VU-16");
		unmappableRegions.add("VU-17");
		unmappableRegions.add("VU-18");
		unmappableRegions.add("SB-03"); // Solomon Islands - 9 regions
		unmappableRegions.add("SB-06"); // some of this reagions are actually mapped in natural earth but some arent
		unmappableRegions.add("SB-07");
		unmappableRegions.add("SB-08");
		unmappableRegions.add("SB-09");
		unmappableRegions.add("SB-10");
		unmappableRegions.add("SB-11");
		unmappableRegions.add("SB-12");
		unmappableRegions.add("SB-13");
		unmappableRegions.add("KI-01"); // Kiribati - 3 regions
		unmappableRegions.add("KI-02");
		unmappableRegions.add("KI-03");
		unmappableRegions.add("SM-01"); // San Marino - 9 regions
		unmappableRegions.add("SM-02");
		unmappableRegions.add("SM-03");
		unmappableRegions.add("SM-04");
		unmappableRegions.add("SM-05");
		unmappableRegions.add("SM-06");
		unmappableRegions.add("SM-07");
		unmappableRegions.add("SM-08");
		unmappableRegions.add("SM-09");
		unmappableRegions.add("LI-01"); // Lichtenstein - 13 regions
		unmappableRegions.add("LI-02");
		unmappableRegions.add("LI-03");
		unmappableRegions.add("LI-04");
		unmappableRegions.add("LI-05");
		unmappableRegions.add("LI-06");
		unmappableRegions.add("LI-07");
		unmappableRegions.add("LI-08");
		unmappableRegions.add("LI-09");
		unmappableRegions.add("LI-10");
		unmappableRegions.add("LI-11");
		unmappableRegions.add("LI-21");
		unmappableRegions.add("LI-22");
		unmappableRegions.add("SC-01"); // Seychellen - 23 regions
		unmappableRegions.add("SC-02");
		unmappableRegions.add("SC-03");
		unmappableRegions.add("SC-04");
		unmappableRegions.add("SC-05");
		unmappableRegions.add("SC-06");
		unmappableRegions.add("SC-07");
		unmappableRegions.add("SC-08");
		unmappableRegions.add("SC-09");
		unmappableRegions.add("SC-10");
		unmappableRegions.add("SC-11");
		unmappableRegions.add("SC-12");
		unmappableRegions.add("SC-13");
		unmappableRegions.add("SC-14");
		unmappableRegions.add("SC-15");
		unmappableRegions.add("SC-16");
		unmappableRegions.add("SC-17");
		unmappableRegions.add("SC-18");
		unmappableRegions.add("SC-19");
		unmappableRegions.add("SC-20");
		unmappableRegions.add("SC-21");
		unmappableRegions.add("SC-22");
		unmappableRegions.add("SC-23");
		unmappableRegions.add("MU-12"); // Mauritius - 12 regions
		unmappableRegions.add("MU-13");
		unmappableRegions.add("MU-14");
		unmappableRegions.add("MU-15");
		unmappableRegions.add("MU-16");
		unmappableRegions.add("MU-17");
		unmappableRegions.add("MU-18");
		unmappableRegions.add("MU-19");
		unmappableRegions.add("MU-20");
		unmappableRegions.add("MU-21");
		unmappableRegions.add("MU-22");
		unmappableRegions.add("MU-23");

		unmappableRegions.add("CV-01"); //Cape Verde - 16 regions
		unmappableRegions.add("CV-02");
		unmappableRegions.add("CV-04");
		unmappableRegions.add("CV-05");
		unmappableRegions.add("CV-07");
		unmappableRegions.add("CV-08");
		unmappableRegions.add("CV-10");
		unmappableRegions.add("CV-11");
		unmappableRegions.add("CV-13");
		unmappableRegions.add("CV-14");
		unmappableRegions.add("CV-15");
		unmappableRegions.add("CV-16");
		unmappableRegions.add("CV-17");
		unmappableRegions.add("CV-18");
		unmappableRegions.add("CV-19");

		unmappableRegions.add("MV-01"); // Maldives - 20 regions
		unmappableRegions.add("MV-05");
		unmappableRegions.add("MV-30");
		unmappableRegions.add("MV-31");
		unmappableRegions.add("MV-32");
		unmappableRegions.add("MV-33");
		unmappableRegions.add("MV-34");
		unmappableRegions.add("MV-35");
		unmappableRegions.add("MV-36");
		unmappableRegions.add("MV-37");
		unmappableRegions.add("MV-38");
		unmappableRegions.add("MV-39");
		unmappableRegions.add("MV-40");
		unmappableRegions.add("MV-41");
		unmappableRegions.add("MV-42");
		unmappableRegions.add("MV-43");
		unmappableRegions.add("MV-44");
		unmappableRegions.add("MV-45");
		unmappableRegions.add("MV-46");
		unmappableRegions.add("MV-47");

		unmappableRegions.add("BH-01"); // Bahrain 16 regions
		unmappableRegions.add("BH-02");
		unmappableRegions.add("BH-05");
		unmappableRegions.add("BH-06");
		unmappableRegions.add("BH-08");
		unmappableRegions.add("BH-09");
		unmappableRegions.add("BH-10");
		unmappableRegions.add("BH-11");
		unmappableRegions.add("BH-12");
		unmappableRegions.add("BH-13");
		unmappableRegions.add("BH-14");
		unmappableRegions.add("BH-15");
		unmappableRegions.add("BH-16");
		unmappableRegions.add("BH-17");
		unmappableRegions.add("BH-18");
		unmappableRegions.add("BH-19");
		unmappableRegions.add("ST-01"); // ST-1 Sao Tome & Principe
		unmappableRegions.add("ST-02");


		unmappableRegions.add("GM-07"); // Gambia - North Bank => no such region in map
		unmappableRegions.add("BI-23"); // Burundi - Mwaro => no such region in map

		// maxmind says it's a country, for naturalearth this are more like regions - so we use regions
		specialCasesWhereRegionsAreThreatedAsCountries.put("GF", "FRA-2000"); // French Guyana
		specialCasesWhereRegionsAreThreatedAsCountries.put("GP", "GLP+00?");  // Guadeloupe
		specialCasesWhereRegionsAreThreatedAsCountries.put("BV", "BVT+00?");  // Bouvet
		specialCasesWhereRegionsAreThreatedAsCountries.put("BQ", "NLD+11?");  // Besondere Gemeinden Niederlande
		specialCasesWhereRegionsAreThreatedAsCountries.put("CX", "IOA-2652"); // Christmas Islands
		specialCasesWhereRegionsAreThreatedAsCountries.put("CC", "IOA-1928"); // Cocos Islands
		specialCasesWhereRegionsAreThreatedAsCountries.put("MQ", "FRA-1442"); // Martinique
		specialCasesWhereRegionsAreThreatedAsCountries.put("RE", "REU+00?");  // Reunion
		specialCasesWhereRegionsAreThreatedAsCountries.put("TK", "TKL+00?");  // Tokelau
		specialCasesWhereRegionsAreThreatedAsCountries.put("SJ", "NOR-901");  // Svalbad & Jan Mayen (problem here is that Jan Mayen is assigned to province nordland)
		specialCasesWhereRegionsAreThreatedAsCountries.put("YT", "MYT+00?");  // Mayotte

		// TODO - create regions for that
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-FM", "FM"); // Federated States of Micronesia
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-GU", "GU"); // Guam
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-PW", "PW"); // Palau
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-MH", "MH"); // Marshall Islands
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-MP", "MP"); // Northern Marian Islands
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-AS", "AS"); // American-Samoa
		specialCasesWhereCountriesAreThreatedAsRegions.put("US-VI", "VI"); // American Virgin Islands

		// Armenia
		manualMappingOfRegionIdsToAdm1Ids.put("AM-02", new String[] {"ARM-1671"});  // Ararat
		manualMappingOfRegionIdsToAdm1Ids.put("AM-08", new String[] {"ARM-1732"});  // Syunik
		manualMappingOfRegionIdsToAdm1Ids.put("AM-10", new String[] {"ARM-1733"});  // Vayots' Dzor

		// Malaysia
		manualMappingOfRegionIdsToAdm1Ids.put("MY-08", new String[] {"MYS-1141", "MYS-1142"});  // Perlis
		manualMappingOfRegionIdsToAdm1Ids.put("MY-09", new String[] {"MYS-1138", "MYS-1139"});  // Pulau Pinang
		manualMappingOfRegionIdsToAdm1Ids.put("MY-16", new String[] {"MYS-1186"});  // Sabah
		remappedRegionIds.put("MY-17", "MY-12");
		remappedRegionIds.put("MY-14", "MY-05");

		// Vietnam
		manualMappingOfRegionIdsToAdm1Ids.put("VN-72", new String[] {"VNM-470"});  // Bac Kan
		manualMappingOfRegionIdsToAdm1Ids.put("VN-81", new String[] {"VNM-461"});  // Hung Yen
		manualMappingOfRegionIdsToAdm1Ids.put("VN-84", new String[] {"VNM-487"});  // Quang Nam
		manualMappingOfRegionIdsToAdm1Ids.put("VN-85", new String[] {"VNM-452"});  // Thai Nguyen
		manualMappingOfRegionIdsToAdm1Ids.put("VN-88", new String[] {"VNM-477"});  // Dac Lak (seems to be merged with dak nong)
		manualMappingOfRegionIdsToAdm1Ids.put("VN-87", new String[] {"VNM-505"});  // Can Tho (seems to be merged with hau glang)
		manualMappingOfRegionIdsToAdm1Ids.put("VN-89", new String[] {"VNM-453"});  // Lai Chau
		manualMappingOfRegionIdsToAdm1Ids.put("VN-93", new String[] {"VNM-512"});  // Hau Giang
		manualMappingOfRegionIdsToAdm1Ids.put("VN-91", new String[] {"VNM-491"});  // Dak Nong
		manualMappingOfRegionIdsToAdm1Ids.put("VN-60", new String[] {"VNM-481"});  // Ninh Thuan
		manualMappingOfRegionIdsToAdm1Ids.put("VN-37", new String[] {"VNM-457"});  // Tien Giang
		manualMappingOfRegionIdsToAdm1Ids.put("VN-44", new String[] {"VNM-465"});  // Ha Noi
		manualMappingOfRegionIdsToAdm1Ids.put("VN-43", new String[] {"VNM-497"});  // Dong Nai
		manualMappingOfRegionIdsToAdm1Ids.put("VN-50", new String[] {"VNM-512"});  // Ha Giang
		manualMappingOfRegionIdsToAdm1Ids.put("VN-09", new String[] {"VNM-500", "VNM-499"});  // Dong Thap

		// Honduras
		manualMappingOfRegionIdsToAdm1Ids.put("HN-01", new String[] {"HND-637"});  // Atlantida
		manualMappingOfRegionIdsToAdm1Ids.put("HN-02", new String[] {"HND-661"});
		manualMappingOfRegionIdsToAdm1Ids.put("HN-05", new String[] {"HND-651"});  // Copan
		manualMappingOfRegionIdsToAdm1Ids.put("HN-10", new String[] {"HND-648"});  // Intibuca
		manualMappingOfRegionIdsToAdm1Ids.put("HN-11", new String[] {"HND-641"});
		manualMappingOfRegionIdsToAdm1Ids.put("HN-12", new String[] {"HND-649"});  // La Paz

		//
		manualMappingOfRegionIdsToAdm1Ids.put("HT-14", new String[] {"HTI-1359"});

		// Chile
		manualMappingOfRegionIdsToAdm1Ids.put("CL-04", new String[] {"CHL-2700"}); // Araucania
		manualMappingOfRegionIdsToAdm1Ids.put("CL-06", new String[] {"CHL-2702"}); // Bio Bio
		manualMappingOfRegionIdsToAdm1Ids.put("CL-14", new String[] {"CHL-2704"}); // Los Lagos
		manualMappingOfRegionIdsToAdm1Ids.put("CL-15", new String[] {"CHL-2693"}); // Tarapaca
		manualMappingOfRegionIdsToAdm1Ids.put("CL-17", new String[] {"CHL-2701"}); // Los Rios
		remappedRegionIds.put("CL-13", "CL-15"); // Two entries for Tarapaca
		remappedRegionIds.put("CL-09", "CL-14"); // Two entries for Lagos

		// Venezuela
		manualMappingOfRegionIdsToAdm1Ids.put("VE-21", new String[] {"VEN-29", "VEN-30"}); // Trujillo
		manualMappingOfRegionIdsToAdm1Ids.put("VE-23", new String[] {"VEN-31"}); // Zulia
		manualMappingOfRegionIdsToAdm1Ids.put("VE-24", new String[] {"VEN-44"}); // Dependencias Federales
		manualMappingOfRegionIdsToAdm1Ids.put("VE-25", new String[] {"VEN-43"}); // Distrito Federal
		manualMappingOfRegionIdsToAdm1Ids.put("VE-18", new String[] {"VEN-35"}); // Portuguesa
		manualMappingOfRegionIdsToAdm1Ids.put("VE-13", new String[] {"VEN-34"}); // Lara
		manualMappingOfRegionIdsToAdm1Ids.put("VE-26", new String[] {"VEN-42"}); // Vargas

		// Brazil
		manualMappingOfRegionIdsToAdm1Ids.put("BR-27", new String[] {"BRA-1311"}); // Sao Paulo
		manualMappingOfRegionIdsToAdm1Ids.put("BR-01", new String[] {"BRA-576"}); // Acre
		manualMappingOfRegionIdsToAdm1Ids.put("BR-13", new String[] {"BRA-593"}); // Morankao
		manualMappingOfRegionIdsToAdm1Ids.put("BR-31", new String[] {"BRA-596"}); // Tocaitis
		manualMappingOfRegionIdsToAdm1Ids.put("BR-24", new String[] {"BRA-595"}); // Bondonia
		manualMappingOfRegionIdsToAdm1Ids.put("BR-21", new String[] {"BRA-627"}); // Bondonia

		// Paraguay
		unmappableRegions.add("PY-20"); // Chaco => region spanning mutliple administrative reasons, what are they thinking?!
		manualMappingOfRegionIdsToAdm1Ids.put("PY-01", new String[] {"PRY-616"});  // Alto Parana
		manualMappingOfRegionIdsToAdm1Ids.put("PY-02", new String[] {"PRY-615"});  // Amambay
		manualMappingOfRegionIdsToAdm1Ids.put("PY-03", new String[] {"PRY-598"});  // Bocquerion
		manualMappingOfRegionIdsToAdm1Ids.put("PY-11", new String[] {"PRY-620"});  // Itapa
		manualMappingOfRegionIdsToAdm1Ids.put("PY-17", new String[] {"PRY-606"});  // San Pedro
		manualMappingOfRegionIdsToAdm1Ids.put("PY-23", new String[] {"PRY-597"});  // Alto Paraquay
		remappedRegionIds.put("PY-21", "PY-06"); // Neuva Asuncion => Central

		// Argentina
		manualMappingOfRegionIdsToAdm1Ids.put("AR-07", new String[] {"ARG-1295"}); // Ciudad de Buenos Aires
		manualMappingOfRegionIdsToAdm1Ids.put("AR-08", new String[] {"ARG-1309"}); // Entre Rios
		manualMappingOfRegionIdsToAdm1Ids.put("AR-13", new String[] {"ARG-1275"}); // Mendoza
		manualMappingOfRegionIdsToAdm1Ids.put("AR-15", new String[] {"ARG-1276"}); // Neequem
		manualMappingOfRegionIdsToAdm1Ids.put("AR-16", new String[] {"ARG-1297"}); // Rio Negro
		remappedRegionIds.put("AR-01", "AR-07"); // Buenos Aires => Ciudad de Benuos Aires

		// Mexico
		manualMappingOfRegionIdsToAdm1Ids.put("MX-05", new String[] {"MEX-2735"});  // Chiapas
		manualMappingOfRegionIdsToAdm1Ids.put("MX-32", new String[] {"MEX-2713"});  // Zacatecas

		// Fiji Islands
		manualMappingOfRegionIdsToAdm1Ids.put("FJ-01", new String[] {"FJI-2617"});  // Fiji - Central
		manualMappingOfRegionIdsToAdm1Ids.put("FJ-02", new String[] {"FJI-2618"});  // Fiji - Easter
		manualMappingOfRegionIdsToAdm1Ids.put("FJ-03", new String[] {"FJI-2619"});  // Fiji - Norther
		manualMappingOfRegionIdsToAdm1Ids.put("FJ-04", new String[] {"FJI-2658"});  // Fiji - Rotuma
		manualMappingOfRegionIdsToAdm1Ids.put("FJ-05", new String[] {"FJI-2620"});  // Fiji - Western

		// Australia
		manualMappingOfRegionIdsToAdm1Ids.put("AU-01", new String[] {"AUS-2653"});  // Australia - Captial Territory
		manualMappingOfRegionIdsToAdm1Ids.put("AU-02", new String[] {"AUS-2654"});  // New South Wales
		manualMappingOfRegionIdsToAdm1Ids.put("NZ-10", new String[] {"NZC+00?"});   // Chatham Islands

		// Papa Neuginea
		manualMappingOfRegionIdsToAdm1Ids.put("PG-01", new String[] {"PNG-1249"});  // Papa Neuguinea - Central
		manualMappingOfRegionIdsToAdm1Ids.put("PG-06", new String[] {"PNG-1248"});  // Papa Neuguinea - Western
		manualMappingOfRegionIdsToAdm1Ids.put("PG-20", new String[] {"PNG-1254"});  // Papa Neuguinea - National Captial

		// Marocco
		manualMappingOfRegionIdsToAdm1Ids.put("MA-59", new String[] {"MAR-3469"});  // Marocco - Layoune-Boujadour-Sakia El Hamra

		// Egypt
		manualMappingOfRegionIdsToAdm1Ids.put("EG-02", new String[] {"EGY-1556"});  // Egypt - Al Bahr al Ahmar
		manualMappingOfRegionIdsToAdm1Ids.put("EG-13", new String[] {"EGY-1550"});  // Egypt - Al Wadi al Jahid

		// Austria
		manualMappingOfRegionIdsToAdm1Ids.put("AT-05", new String[] {"AUT-2327"});  // Austria - Salzburg
		manualMappingOfRegionIdsToAdm1Ids.put("AT-07", new String[] {"AUT-2329", "AUT-2328"});  // Austria - Tirol (merge with Osttirol)

		// Ethipia
		manualMappingOfRegionIdsToAdm1Ids.put("ET-44", new String[] {"ETH-3131", "ETH-3133"}); // merge ETH-3131 + ETH-3133 ?? Addis abeba ethopia
		manualMappingOfRegionIdsToAdm1Ids.put("ET-48", new String[] {"ETH-3135"});
		unmappableRegions.add("ET-51");

		// Barbados
		manualMappingOfRegionIdsToAdm1Ids.put("BB-04", new String[] {"BRB-1992"}); // Barbados - Saint James
		manualMappingOfRegionIdsToAdm1Ids.put("BB-11", new String[] {"BRB-1999"}); // Barbados - Saint Thomas

		// Bolivia
		manualMappingOfRegionIdsToAdm1Ids.put("BO-05", new String[] {"BOL-1937"}); // Orero
		manualMappingOfRegionIdsToAdm1Ids.put("BO-07", new String[] {"BOL-1939"}); // Potosi


		// Costa Rica
		manualMappingOfRegionIdsToAdm1Ids.put("CR-01", new String[] {"CRI-1333"}); // Costa Rica - Alajuela
		manualMappingOfRegionIdsToAdm1Ids.put("CR-07", new String[] {"CRI-1330"}); // Costa Rica - Puntarenas

		// Colombia
		manualMappingOfRegionIdsToAdm1Ids.put("CO-17", new String[] {"COL-1318"}); //
		manualMappingOfRegionIdsToAdm1Ids.put("CO-25", new String[] {"COL-1342"}); // Saint Andres
		manualMappingOfRegionIdsToAdm1Ids.put("CO-11", new String[] {"COL-1415"}); // Choco
		manualMappingOfRegionIdsToAdm1Ids.put("CO-09", new String[] {"COL-1404"}); // Cauca
		manualMappingOfRegionIdsToAdm1Ids.put("CO-29", new String[] {"COL-1408"}); // Valle de Cauca
		manualMappingOfRegionIdsToAdm1Ids.put("CO-33", new String[] {"COL-1398"}); // Cundinamoera
		manualMappingOfRegionIdsToAdm1Ids.put("CO-34", new String[] {"COL-1399"}); // Bogota

		// Cuba
		manualMappingOfRegionIdsToAdm1Ids.put("CU-07", new String[] {"CUB-1363"});
		manualMappingOfRegionIdsToAdm1Ids.put("CU-05", new String[] {"CUB-1364"}); // Cuba - Camaguey
		manualMappingOfRegionIdsToAdm1Ids.put("CU-13", new String[] {"CUB-1368"}); // Cuba - Las Tunas

		// Dominican Republic
		manualMappingOfRegionIdsToAdm1Ids.put("DO-36", new String[] {"DOM-1978"});
		manualMappingOfRegionIdsToAdm1Ids.put("DO-17", new String[] {"DOM-1977"}); // Dominican Republic - Peravia
		manualMappingOfRegionIdsToAdm1Ids.put("DO-37", new String[] {"DOM-1394"}); // Dominican Republic - Santa Domingo
		manualMappingOfRegionIdsToAdm1Ids.put("DO-19", new String[] {"DOM-1968"}); // Dominican Republic - Hermanas (mapped main city

		// Ecuador
		manualMappingOfRegionIdsToAdm1Ids.put("EC-20", new String[] {"ECU-1289"}); // Napo
		manualMappingOfRegionIdsToAdm1Ids.put("EC-23", new String[] {"ECU-1269"}); // Zamora Chinipe
		manualMappingOfRegionIdsToAdm1Ids.put("EC-24", new String[] {"ECU-1286"}); // Orellona

		// Sudan
		manualMappingOfRegionIdsToAdm1Ids.put("SD-27", new String[] {"SDN-880"}); // Al Wusta
		manualMappingOfRegionIdsToAdm1Ids.put("SD-28", new String[] {"SDN-1286"}); // Al Istiwa'iyah
		manualMappingOfRegionIdsToAdm1Ids.put("SD-30", new String[] {"SDN-878"}); // Ash Shamaliyah
		manualMappingOfRegionIdsToAdm1Ids.put("SD-31", new String[] {"SDN-1286"}); // Ash Sharqiyah
		manualMappingOfRegionIdsToAdm1Ids.put("SD-32", new String[] {"SDN-1286"}); // Bahr al Ghazal
		manualMappingOfRegionIdsToAdm1Ids.put("SD-33", new String[] {"SDN-881", "SDN-811", "SDN-797"}); // Darfur
		manualMappingOfRegionIdsToAdm1Ids.put("SD-34", new String[] {"SDN-1286"}); // Kurdufan
		manualMappingOfRegionIdsToAdm1Ids.put("SD-44", new String[] {"SDN-1286"}); // Central Equatoria State

		// Panama
		manualMappingOfRegionIdsToAdm1Ids.put("PA-01", new String[] {"PAN-1958"}); // Panama - Bocas del Toro
		manualMappingOfRegionIdsToAdm1Ids.put("PA-05", new String[] {"PAN-1418", "PAN-3455"}); // Panama - Darien
		manualMappingOfRegionIdsToAdm1Ids.put("PA-08", new String[] {"PAN-1340"}); // Panama - Panama (same ID as San Blas, therefore no unique mapping)
		manualMappingOfRegionIdsToAdm1Ids.put("PA-09", new String[] {"PAN-1419"}); // Panama - San Blas / Kuna Yala
		manualMappingOfRegionIdsToAdm1Ids.put("PA-07", new String[] {"PAN-1339"}); // Panama - Los Santos
		manualMappingOfRegionIdsToAdm1Ids.put("PA-12", new String[] {"PAN-3454"}); // Panama - Ngobe Bugle
		// manualMappingOfRegionIdsToAdm1Ids.put("PA-11", new String[] {"PAN-3455"}); // Panama - Embera (merge with Darien)
		unmappableRegions.add("PA-11");

		// Peru
		manualMappingOfRegionIdsToAdm1Ids.put("PE-06", new String[] {"PER-578"}); // Cajamarca
		manualMappingOfRegionIdsToAdm1Ids.put("PE-21", new String[] {"PER-573"}); // Puno
		manualMappingOfRegionIdsToAdm1Ids.put("PE-09", new String[] {"PER-588"}); // Huancavelica
		manualMappingOfRegionIdsToAdm1Ids.put("PE-07", new String[] {"PER-3505"}); // Callao
		manualMappingOfRegionIdsToAdm1Ids.put("PE-15", new String[] {"PER-591", "PER-587"}); // Lima
		manualMappingOfRegionIdsToAdm1Ids.put("PE-18", new String[] {"PER-574"}); // Moquegia
		manualMappingOfRegionIdsToAdm1Ids.put("PE-23", new String[] {"PER-575"}); // Tacna

		// Suriname
		manualMappingOfRegionIdsToAdm1Ids.put("SR-13", new String[] {"SUR-67"}); // Marowijne

		// El Salvador
		manualMappingOfRegionIdsToAdm1Ids.put("SV-07", new String[] {"SLV-1350"}); // La Union (region with same key and name in Honduras)
		manualMappingOfRegionIdsToAdm1Ids.put("SV-03", new String[] {"SLV-1357"}); // Chalatenango
		manualMappingOfRegionIdsToAdm1Ids.put("SV-04", new String[] {"SLV-1345"}); // Cuscatan

		// Nicaragua
		manualMappingOfRegionIdsToAdm1Ids.put("NI-07", new String[] {"NIC-657"}); // Atlantico Norte
		manualMappingOfRegionIdsToAdm1Ids.put("NI-17", new String[] {"NIC-656"}); // Jinotega
		manualMappingOfRegionIdsToAdm1Ids.put("NI-15", new String[] {"NIC-24"}); // Rivas and Rio San Juan mapped to Nicaragua region
		remappedRegionIds.put("NI-14","NI-15");
		// unmappableRegions.add("NI-15"); // Rio San Juan (together with Rivas mappend as "nicaragua" (NIC-24)
		// unmappableRegions.add("NI-14"); // Rivas
		unmappableRegions.add("NI-16"); // Zelaya - not to be found on any map

		// Kirgistan
		manualMappingOfRegionIdsToAdm1Ids.put("KG-01", new String[] {"KGZ-1115"}); // Bishkek
		manualMappingOfRegionIdsToAdm1Ids.put("KG-02", new String[] {"KGZ-1116"}); // Chuy

		// Yemen
		manualMappingOfRegionIdsToAdm1Ids.put("YE-07", new String[] {"YEM-333"}); // Al Bayda'
		manualMappingOfRegionIdsToAdm1Ids.put("YE-18", new String[] {"YEM-334"}); // Ad Dali

		// Taiwan
		// manualMappingOfRegionIdsToAdm1Ids.put("TW-01", new String[] {"TWN-333", }); // Fu-chien
		// manualMappingOfRegionIdsToAdm1Ids.put("TW-02", new String[] {"TWN-334", }); // Kao-hsiung
		// manualMappingOfRegionIdsToAdm1Ids.put("TW-03", new String[] {"TWN-334", }); // T'ai-pei
		// manualMappingOfRegionIdsToAdm1Ids.put("TW-04", new String[] {"TWN-334", }); // T'ai-wan

		// United Arab Emirates
		manualMappingOfRegionIdsToAdm1Ids.put("AE-02", new String[] {"ARE-346"}); // Ajman
		manualMappingOfRegionIdsToAdm1Ids.put("AE-03", new String[] {"ARE-350"}); // Dubai
		manualMappingOfRegionIdsToAdm1Ids.put("AE-04", new String[] {"ARE-341", "ARE-342"}); // Fujairah

		// Japan
		manualMappingOfRegionIdsToAdm1Ids.put("JP-06", new String[] {"JPN-1848"}); // Fukui
		manualMappingOfRegionIdsToAdm1Ids.put("JP-10", new String[] {"JPN-3503"}); // Gumma

		// China
		manualMappingOfRegionIdsToAdm1Ids.put("CN-22", new String[] {"CHN-1155"}); // Beijing

		// Zypern
		manualMappingOfRegionIdsToAdm1Ids.put("CY-01", new String[] {"CYP-3466"});  // Famagusta
		manualMappingOfRegionIdsToAdm1Ids.put("CY-02", new String[] {"CYN+00?"});  // Kyrenia
		manualMappingOfRegionIdsToAdm1Ids.put("CY-04", new String[] {"CYP-3464"});  // Nicosia

		// Iran
		manualMappingOfRegionIdsToAdm1Ids.put("IR-01", new String[] {"IRN-3237"});  // Azarbayjan-e Bakhtari
		manualMappingOfRegionIdsToAdm1Ids.put("IR-12", new String[] {"IRN-3248"});  // Kerman
		manualMappingOfRegionIdsToAdm1Ids.put("IR-24", new String[] {"IRN-3231"});  // Markazi
		manualMappingOfRegionIdsToAdm1Ids.put("IR-11", new String[] {"IRN-3228"});  // Hormozgan
		// manualMappingOfRegionIdsToAdm1Ids.put("IR-44", new String[] {"IRN-"});  // Alborz
		// manualMappingOfRegionIdsToAdm1Ids.put("IR-30", new String[] {"IRN-"});  // Khorasan
		manualMappingOfRegionIdsToAdm1Ids.put("IR-27", new String[] {"IRN-3234"});  // Zanjan
		manualMappingOfRegionIdsToAdm1Ids.put("IR-18", new String[] {"IRN-3215"});  // Semnan Province
		remappedRegionIds.put("IR-44", "IR-38"); // Alborz not in map
		unmappableRegions.add("IR-30"); // unclear if visitors mapped to Khorasa are from south or north khorasan

		// Laos
		manualMappingOfRegionIdsToAdm1Ids.put("LA-11", new String[] {"LAO-3283", "LAO-3284"});  // Vientiane

		// Mongolia
		manualMappingOfRegionIdsToAdm1Ids.put("MN-20", new String[] {"MNG-3331"});  // Ulaanbaatar

		// Quatar
		unmappableRegions.add("QA-12");

		// India
		manualMappingOfRegionIdsToAdm1Ids.put("IN-32", new String[] {"IND-3504"}); // Daman and Diu

		// Saudi Arabia
		manualMappingOfRegionIdsToAdm1Ids.put("SA-19", new String[] {"SAU-848"});  // Tabuk

		// Kuwait
		manualMappingOfRegionIdsToAdm1Ids.put("KW-09", new String[] {"KWT-1666"});  // Mubarak al Kabir

		// Macau
		// TODO: Maybe only map land leven and ignore regions as there is only 1 region
		remappedRegionIds.put("MO-01", "MO-02"); // no region for Ilhas

		// Uganda
		unmappableRegions.add("RW-01");
		unmappableRegions.add("RW-06");
		unmappableRegions.add("RW-07");

		// Swatziland
		unmappableRegions.add("SZ-05");

		// Kongo
		manualMappingOfRegionIdsToAdm1Ids.put("CG-10", new String[] {"CG-2880"});
		unmappableRegions.add("CG-12"); // Brazzaville /Hauptstadt

		// map maxmind ids to different region ids
		// regions ids that need to be remapped from the maxmind ids because we do not have the necessary detail level
		// to display it on the map

		// Georgia
		remappedRegionIds.put("GE-01","GE-SZ");
		remappedRegionIds.put("GE-02","GE-AB");
		remappedRegionIds.put("GE-03","GE-SJ");
		remappedRegionIds.put("GE-04","GE-AJ");
		remappedRegionIds.put("GE-05","GE-MM");
		remappedRegionIds.put("GE-06","GE-SJ");
		remappedRegionIds.put("GE-07","GE-SJ");
		remappedRegionIds.put("GE-08","GE-KA");
		remappedRegionIds.put("GE-09","GE-RL");
		remappedRegionIds.put("GE-10","GE-SJ");
		remappedRegionIds.put("GE-11","GE-IM");
		remappedRegionIds.put("GE-12","GE-KK");
		remappedRegionIds.put("GE-13","GE-SJ");
		remappedRegionIds.put("GE-14","GE-IM");
		remappedRegionIds.put("GE-15","GE-SZ");
		remappedRegionIds.put("GE-16","GE-GU");
		remappedRegionIds.put("GE-17","GE-KA");
		remappedRegionIds.put("GE-18","GE-KK");
		remappedRegionIds.put("GE-19","GE-MM");
		remappedRegionIds.put("GE-20","GE-KK");
		remappedRegionIds.put("GE-21","GE-SK");
		remappedRegionIds.put("GE-22","GE-SK");
		remappedRegionIds.put("GE-23","GE-KA");
		remappedRegionIds.put("GE-24","GE-SK");
		remappedRegionIds.put("GE-25","GE-SK");
		remappedRegionIds.put("GE-26","GE-SK");
		remappedRegionIds.put("GE-27","GE-IM");
		remappedRegionIds.put("GE-28","GE-SK");
		remappedRegionIds.put("GE-29","GE-SZ");
		remappedRegionIds.put("GE-30","GE-IM");
		remappedRegionIds.put("GE-31","GE-IM");
		remappedRegionIds.put("GE-32","GE-KA");
		remappedRegionIds.put("GE-33","GE-GU");
		remappedRegionIds.put("GE-34","GE-RL");
		remappedRegionIds.put("GE-35","GE-KK");
		remappedRegionIds.put("GE-36","GE-SZ");
		remappedRegionIds.put("GE-37","GE-SZ");
		remappedRegionIds.put("GE-38","GE-MM");
		remappedRegionIds.put("GE-39","GE-SJ");
		remappedRegionIds.put("GE-40","GE-RL");
		remappedRegionIds.put("GE-41","GE-GU");
		remappedRegionIds.put("GE-42","GE-SZ");
		remappedRegionIds.put("GE-43","GE-MM");
		remappedRegionIds.put("GE-44","GE-KA");
		remappedRegionIds.put("GE-45","GE-KK");
		remappedRegionIds.put("GE-46","GE-IM");
		remappedRegionIds.put("GE-47","GE-KA");
		remappedRegionIds.put("GE-48","GE-IM");
		remappedRegionIds.put("GE-49","GE-SZ");
		remappedRegionIds.put("GE-50","GE-KA");
		remappedRegionIds.put("GE-51","GE-TB");
		remappedRegionIds.put("GE-52","GE-KA");
		remappedRegionIds.put("GE-53","GE-IM");
		remappedRegionIds.put("GE-54","GE-KK");
		remappedRegionIds.put("GE-55","GE-MM");
		remappedRegionIds.put("GE-56","GE-IM");
		remappedRegionIds.put("GE-57","GE-RL");
		remappedRegionIds.put("GE-58","GE-SZ");
		remappedRegionIds.put("GE-59","GE-KK");
		remappedRegionIds.put("GE-60","GE-IM");
		remappedRegionIds.put("GE-61","GE-IM");
		remappedRegionIds.put("GE-62","GE-IM");
		remappedRegionIds.put("GE-63","GE-SZ");
		remappedRegionIds.put("GE-64","GE-SZ");

		// dominican republic
		remappedRegionIds.put("DO-05","DO-34"); // DO-5 is the old code for the Districto Nacional, DO-34 the new code

		// philippines
		manualMappingOfRegionIdsToAdm1Ids.put("PH-01", new String[] { "PHL-2542" }); // Abra
		manualMappingOfRegionIdsToAdm1Ids.put("PH-02", new String[] { "PHL-2587" }); // Agusan del Norte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-03", new String[] { "PHL-2588" }); // Agusan del Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-04", new String[] { "PHL-2525" }); // Aklan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-05", new String[] { "PHL-2536" }); // Albay
		manualMappingOfRegionIdsToAdm1Ids.put("PH-06", new String[] { "PHL-2526", "PHL-2527" }); // Antique
		manualMappingOfRegionIdsToAdm1Ids.put("PH-G8", new String[] { "PHL-2549" }); // Aurora
		manualMappingOfRegionIdsToAdm1Ids.put("PH-07", new String[] { "PHL-2555" }); // Bataan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-08", new String[] { "PHL-2543" }); // Batanes
		manualMappingOfRegionIdsToAdm1Ids.put("PH-09", new String[] { "PHL-2564" }); // Batangas
		manualMappingOfRegionIdsToAdm1Ids.put("PH-10", new String[] { "PHL-2559" }); // Benguet
		manualMappingOfRegionIdsToAdm1Ids.put("PH-11", new String[] { "PHL-2513" }); // Bohol
		manualMappingOfRegionIdsToAdm1Ids.put("PH-12", new String[] { "PHL-2589" }); // Bukidnon
		manualMappingOfRegionIdsToAdm1Ids.put("PH-13", new String[] { "PHL-2565" }); // Bulacan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-14", new String[] { "PHL-2545" }); // Cagayan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-15", new String[] { "PHL-2537" }); // Camarines Norte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-16", new String[] { "PHL-2538" }); // Camarines Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-17", new String[] { "PHL-2590" }); // Camiguin
		manualMappingOfRegionIdsToAdm1Ids.put("PH-18", new String[] { "PHL-2528" }); // Capiz
		manualMappingOfRegionIdsToAdm1Ids.put("PH-19", new String[] { "PHL-2539" }); // Catanduanes
		manualMappingOfRegionIdsToAdm1Ids.put("PH-20", new String[] { "PHL-2563" }); // Cavite
		manualMappingOfRegionIdsToAdm1Ids.put("PH-21", new String[] { "PHL-2514" }); // Cebu
		manualMappingOfRegionIdsToAdm1Ids.put("PH-22", new String[] { "PHL-2519" }); // Basilan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-23", new String[] { "PHL-2582" }); // Eastern Samar
		manualMappingOfRegionIdsToAdm1Ids.put("PH-I7", new String[] { "PHL-2591" }); // Davao del Norte"
		manualMappingOfRegionIdsToAdm1Ids.put("PH-25", new String[] { "PHL-2596" }); // Davao del Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-26", new String[] { "PHL-2597" }); // Davao Oriental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-I6", new String[] { "PHL-2592" }); // Compostela Valley
		manualMappingOfRegionIdsToAdm1Ids.put("PH-27", new String[] { "PHL-2551" }); // Ifugao
		manualMappingOfRegionIdsToAdm1Ids.put("PH-28", new String[] { "PHL-2547" }); // Ilocos Norte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-29", new String[] { "PHL-2548" }); // Ilocos Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-30", new String[] { "PHL-2529", "PHL-2530", "PHL-2515" }); // Iloilo
		manualMappingOfRegionIdsToAdm1Ids.put("PH-31", new String[] { "PHL-2550" }); // Isabela
		manualMappingOfRegionIdsToAdm1Ids.put("PH-32", new String[] { "PHL-2546" }); // Kalinga-Apayao
		manualMappingOfRegionIdsToAdm1Ids.put("PH-33", new String[] { "PHL-2566" }); // Laguna
		manualMappingOfRegionIdsToAdm1Ids.put("PH-34", new String[] { "PHL-2573" }); // Lanao del Norte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-35", new String[] { "PHL-2574", "PHL-2575" }); // Lanao del Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-36", new String[] { "PHL-2561" }); // La Union
		manualMappingOfRegionIdsToAdm1Ids.put("PH-37", new String[] { "PHL-2583", "PHL-2581" }); // Leyte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-38", new String[] { "PHL-2569" }); // Marinduque
		manualMappingOfRegionIdsToAdm1Ids.put("PH-39", new String[] { "PHL-2540" }); // Masbate
		manualMappingOfRegionIdsToAdm1Ids.put("PH-40", new String[] { "PHL-2531", "PHL-2532", "PHL-2533", "PHL-2570" }); // Mindoro Occidental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-41", new String[] { "PHL-2571" }); // Mindoro Oriental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-42", new String[] { "PHL-2523" }); // Misamis Occidental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-43", new String[] { "PHL-2595" }); // Misamis Oriental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-44", new String[] { "PHL-2552" }); // Mountain
		manualMappingOfRegionIdsToAdm1Ids.put("PH-45", new String[] { "PHL-2518" }); // Negros Occidental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-46", new String[] { "PHL-2516" }); // Negros Oriental
		manualMappingOfRegionIdsToAdm1Ids.put("PH-47", new String[] { "PHL-2557" }); // Nueva Ecija
		manualMappingOfRegionIdsToAdm1Ids.put("PH-48", new String[] { "PHL-2553" }); // Nueva Vizcaya
		manualMappingOfRegionIdsToAdm1Ids.put("PH-49", new String[] { "PHL-2534" }); // Palawan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-50", new String[] { "PHL-2558" }); // Pampanga
		manualMappingOfRegionIdsToAdm1Ids.put("PH-51", new String[] { "PHL-2562" }); // Pangasinan
		manualMappingOfRegionIdsToAdm1Ids.put("PH-53", new String[] { "PHL-2567" }); // Rizal
		manualMappingOfRegionIdsToAdm1Ids.put("PH-54", new String[] { "PHL-2535" }); // Romblon
		manualMappingOfRegionIdsToAdm1Ids.put("PH-55", new String[] { "PHL-2584" }); // Samar
		manualMappingOfRegionIdsToAdm1Ids.put("PH-56", new String[] { "PHL-2576", "PHL-2577" }); // Maguindanao
		manualMappingOfRegionIdsToAdm1Ids.put("PH-57", new String[] { "PHL-2579" }); // North Cotabato
		manualMappingOfRegionIdsToAdm1Ids.put("PH-58", new String[] { "PHL-2541" }); // Sorsogon
		manualMappingOfRegionIdsToAdm1Ids.put("PH-59", new String[] { "PHL-2585" }); // Southern Leyte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-60", new String[] { "PHL-2524" }); // Sulu
		manualMappingOfRegionIdsToAdm1Ids.put("PH-62", new String[] { "PHL-2594" }); // Surigao del Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-63", new String[] { "PHL-2556" }); // Tarlac
		manualMappingOfRegionIdsToAdm1Ids.put("PH-64", new String[] { "PHL-2560" }); // Zambales
		manualMappingOfRegionIdsToAdm1Ids.put("PH-65", new String[] { "PHL-2520" }); // Zamboanga del Norte
		manualMappingOfRegionIdsToAdm1Ids.put("PH-66", new String[] { "PHL-2521", "PHL-2522" }); // Zamboanga del Sur
		manualMappingOfRegionIdsToAdm1Ids.put("PH-67", new String[] { "PHL-2586" }); // Northern Samar
		manualMappingOfRegionIdsToAdm1Ids.put("PH-68", new String[] { "PHL-2554" }); // Quirino
		manualMappingOfRegionIdsToAdm1Ids.put("PH-69", new String[] { "PHL-2517" }); // Siquijor
		manualMappingOfRegionIdsToAdm1Ids.put("PH-70", new String[] { "PHL-2599" }); // South Cotabato
		manualMappingOfRegionIdsToAdm1Ids.put("PH-71", new String[] { "PHL-2580", "PHL-2600" }); // Sultan Kudarat
		manualMappingOfRegionIdsToAdm1Ids.put("PH-72", new String[] { "PHL-2511" }); // Tawitawi
		manualMappingOfRegionIdsToAdm1Ids.put("PH-D9", new String[] { "PHL-2568" }); // Manila" => Metro Manila
		manualMappingOfRegionIdsToAdm1Ids.put("PH-M9", new String[] { "PHL-2598" }); // Sarangani
		manualMappingOfRegionIdsToAdm1Ids.put("PH-H2", new String[] { "PHL-2572" }); // Quezon
		manualMappingOfRegionIdsToAdm1Ids.put("PH-61", new String[] { "PHL-2593" }); // Surigao del Norte = Dinagat Islands


		remappedRegionIds.put("PH-J7", "PH-32"); // Kalinga => Apalayo
		remappedRegionIds.put("PH-24", "PH-I7"); // Davao => Davao
		// cities in the philippines
		remappedRegionIds.put("PH-A1", "PH-50");
		remappedRegionIds.put("PH-A2", "PH-44");
		remappedRegionIds.put("PH-A3", "PH-45");
		remappedRegionIds.put("PH-A4", "PH-10");
		remappedRegionIds.put("PH-A5", "PH-46");
		remappedRegionIds.put("PH-A6", "PH-22");
		remappedRegionIds.put("PH-A7", "PH-09");
		remappedRegionIds.put("PH-H3", "PH-45");
		remappedRegionIds.put("PH-A8", "PH-02");
		remappedRegionIds.put("PH-A9", "PH-47");
		remappedRegionIds.put("PH-B1", "PH-45");
		remappedRegionIds.put("PH-B2", "PH-43");
		remappedRegionIds.put("PH-B3", "PH-55");
		remappedRegionIds.put("PH-B4", "PH-D9");
		remappedRegionIds.put("PH-B5", "PH-46");
		remappedRegionIds.put("PH-B6", "PH-20");
		remappedRegionIds.put("PH-B7", "PH-21");
		remappedRegionIds.put("PH-B8", "PH-56");
		remappedRegionIds.put("PH-B9", "PH-51");
		remappedRegionIds.put("PH-C1", "PH-21");
		remappedRegionIds.put("PH-C2", "PH-65");
		remappedRegionIds.put("PH-C3", "PH-25");
		remappedRegionIds.put("PH-C4", "PH-65");
		remappedRegionIds.put("PH-C5", "PH-46");
		remappedRegionIds.put("PH-C6", "PH-70");
		remappedRegionIds.put("PH-C7", "PH-43");
		remappedRegionIds.put("PH-C8", "PH-34");
		remappedRegionIds.put("PH-C9", "PH-30");
		remappedRegionIds.put("PH-D1", "PH-16");
		remappedRegionIds.put("PH-D2", "PH-45");
		remappedRegionIds.put("PH-D3", "PH-28");
		remappedRegionIds.put("PH-D4", "PH-21");
		remappedRegionIds.put("PH-D5", "PH-05");
		remappedRegionIds.put("PH-D6", "PH-09");
		remappedRegionIds.put("PH-D7", "PH-H2");
		remappedRegionIds.put("PH-D8", "PH-21");
		remappedRegionIds.put("PH-E1", "PH-35");
		remappedRegionIds.put("PH-E2", "PH-21");
		remappedRegionIds.put("PH-E3", "PH-64");
		remappedRegionIds.put("PH-E4", "PH-37");
		remappedRegionIds.put("PH-E5", "PH-42");
		remappedRegionIds.put("PH-E6", "PH-42");
		remappedRegionIds.put("PH-E7", "PH-66");
		remappedRegionIds.put("PH-E8", "PH-47");
		remappedRegionIds.put("PH-E9", "PH-D9");
		remappedRegionIds.put("PH-F1", "PH-49");
		remappedRegionIds.put("PH-F2", "PH-H2");
		remappedRegionIds.put("PH-F3", "PH-18");
		remappedRegionIds.put("PH-F4", "PH-45");
		remappedRegionIds.put("PH-F5", "PH-51");
		remappedRegionIds.put("PH-F6", "PH-47");
		remappedRegionIds.put("PH-F7", "PH-33");
		remappedRegionIds.put("PH-F8", "PH-45");
		remappedRegionIds.put("PH-F9", "PH-61");
		remappedRegionIds.put("PH-G1", "PH-37");
		remappedRegionIds.put("PH-G2", "PH-20");
		remappedRegionIds.put("PH-G3", "PH-11");
		remappedRegionIds.put("PH-G4", "PH-42");
		remappedRegionIds.put("PH-G5", "PH-21");
		remappedRegionIds.put("PH-G6", "PH-20");
		remappedRegionIds.put("PH-G7", "PH-66");
		remappedRegionIds.put("PH-J7", "PH-32");
		remappedRegionIds.put("PH-K6", "PH-12");
		remappedRegionIds.put("PH-M5", "PH-13");
		remappedRegionIds.put("PH-M6", "PH-D9");
		remappedRegionIds.put("PH-M8", "PH-31");
		remappedRegionIds.put("PH-N1", "PH-45");
		remappedRegionIds.put("PH-N3", "PH-61");
		remappedRegionIds.put("PH-P2", "PH-66");
		remappedRegionIds.put("PH-P1", "PH-64");

		// Indonesia
		manualMappingOfRegionIdsToAdm1Ids.put("ID-15", new String[] { "IDN-1229" }); // Lampung
		manualMappingOfRegionIdsToAdm1Ids.put("ID-28", new String[] { "IDN-554" }); // Maluku
		manualMappingOfRegionIdsToAdm1Ids.put("ID-29", new String[] { "IDN-538" }); // Maluku Utara

		// Kazachstan
		remappedRegionIds.put("KZ-02", "KZ-01"); // Almaty => City
		remappedRegionIds.put("KZ-05", "KZ-03"); // Astana => City
		remappedRegionIds.put("KZ-08", "KZ-14"); // Bayqonyr => City
		manualMappingOfRegionIdsToAdm1Ids.put("KZ-14", new String[] { "KAZ-3197" }); // Qyzylorda

		// Sri Lanka
		manualMappingOfRegionIdsToAdm1Ids.put("LK-29", new String[] { "LKA-2448", "LKA-2449", "LKA-2450" }); // Central Province
		manualMappingOfRegionIdsToAdm1Ids.put("LK-30", new String[] { "LKA-2453", "LKA-2455" }); // North Central
		manualMappingOfRegionIdsToAdm1Ids.put("LK-32", new String[] { "LKA-2462", "LKA-2461" });  // North Western
		manualMappingOfRegionIdsToAdm1Ids.put("LK-33", new String[] { "LKA-2469", "LKA-2469" });  // Sabaragamuwa
		manualMappingOfRegionIdsToAdm1Ids.put("LK-34", new String[] { "LKA-2464", "LKA-2466", "LKA-2465" }); // Southern
		manualMappingOfRegionIdsToAdm1Ids.put("LK-35", new String[] { "LKA-2467", "LKA-2468" }); // Uva
		manualMappingOfRegionIdsToAdm1Ids.put("LK-36", new String[] { "LKA-2470", "LKA-2471", "LKA-2472" }); // Western
		manualMappingOfRegionIdsToAdm1Ids.put("LK-37", new String[] { "LKA-2451", "LKA-2452", "LKA-2454" }); // Eastern
		manualMappingOfRegionIdsToAdm1Ids.put("LK-38", new String[] { "LKA-2459", "LKA-2460", "LKA-2457", "LKA-2456", "LKA-2458" }); // Northern

		// North Korea
		manualMappingOfRegionIdsToAdm1Ids.put("KP-08", new String[] { "PRK-3309" }); // Kaesong-si
		manualMappingOfRegionIdsToAdm1Ids.put("KP-09", new String[] { "PRK-3308" }); // Kangwon-do
		manualMappingOfRegionIdsToAdm1Ids.put("KP-12", new String[] { "PRK-3305" }); // P'yongyang-si
		manualMappingOfRegionIdsToAdm1Ids.put("KP-14", new String[] { "PRK-3306" }); // Namp'o-si
		remappedRegionIds.put("KP-18", "KP-17");

		// Thailand
		manualMappingOfRegionIdsToAdm1Ids.put("TH-59", new String[] { "THA-379" }); // Ranong
		manualMappingOfRegionIdsToAdm1Ids.put("TH-68", new String[] { "THA-384", "THA-386" }); // Songkhla
		manualMappingOfRegionIdsToAdm1Ids.put("TH-21", new String[] { "THA-472" }); // Nakhon Phanom
		manualMappingOfRegionIdsToAdm1Ids.put("TH-32", new String[] { "THA-407" }); // Chai Nat
		manualMappingOfRegionIdsToAdm1Ids.put("TH-71", new String[] { "THA-426" }); // Ubon Ratchathani

		// South Korea
		manualMappingOfRegionIdsToAdm1Ids.put("KR-06", new String[] { "KOR-2496"}); // Kangwon-do
		manualMappingOfRegionIdsToAdm1Ids.put("KR-10", new String[] { "KOR-2507"}); // Pusan-jikhalsi
		manualMappingOfRegionIdsToAdm1Ids.put("KR-14", new String[] { "KOR-2501"}); // Kyongsang-bukto
		manualMappingOfRegionIdsToAdm1Ids.put("KR-15", new String[] { "KOR-2509"}); // Taegu-jikhalsi
		manualMappingOfRegionIdsToAdm1Ids.put("KR-16", new String[] { "KOR-2499"}); // Cholla-namdo
		manualMappingOfRegionIdsToAdm1Ids.put("KR-18", new String[] { "KOR-2506"}); // Kwangju-jikhalsi
		manualMappingOfRegionIdsToAdm1Ids.put("KR-20", new String[] { "KOR-2501"}); // Kyongsang-namdo

		// Nepal
		manualMappingOfRegionIdsToAdm1Ids.put("NP-01", new String[] { "NPL-1130"}); // Bagmati
		manualMappingOfRegionIdsToAdm1Ids.put("NP-02", new String[] { "NPL-1124"}); // Bheri
		manualMappingOfRegionIdsToAdm1Ids.put("NP-03", new String[] { "NPL-1125"}); // Dhawalagiri
		manualMappingOfRegionIdsToAdm1Ids.put("NP-04", new String[] { "NPL-1095"}); // Gandaki
		manualMappingOfRegionIdsToAdm1Ids.put("NP-05", new String[] { "NPL-1131"}); // Janakpur
		manualMappingOfRegionIdsToAdm1Ids.put("NP-06", new String[] { "NPL-1126"}); // Karnali
		manualMappingOfRegionIdsToAdm1Ids.put("NP-07", new String[] { "NPL-1134"}); // Kosi
		manualMappingOfRegionIdsToAdm1Ids.put("NP-08", new String[] { "NPL-1127"}); // Lumbini
		manualMappingOfRegionIdsToAdm1Ids.put("NP-09", new String[] { "NPL-1128"}); // Mahakali
		manualMappingOfRegionIdsToAdm1Ids.put("NP-10", new String[] { "NPL-1135"}); // Mechi
		manualMappingOfRegionIdsToAdm1Ids.put("NP-11", new String[] { "NPL-1132"}); // Narayani
		manualMappingOfRegionIdsToAdm1Ids.put("NP-12", new String[] { "NPL-373"}); // Rapti
		manualMappingOfRegionIdsToAdm1Ids.put("NP-13", new String[] { "NPL-1133"}); // Sagarmatha
		manualMappingOfRegionIdsToAdm1Ids.put("NP-14", new String[] { "NPL-1129"}); // Seti
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			// read the mapping of countries to continents in a global accessible map for further reference
			readMaxmindCountryToContinentsMappings();

			// create a map for storing information about the region objects created so far
			Map<String, Region> regions = new HashMap<String, Region>();

			// create mapping from iso to fips codes, we will need that later
			readFIPSToISOMapping();

			// read countries and create region objects
			readMaxmindCountryIds(regions);
			// read regions and create region objects
			readMaxmindRegionIds(regions);

			// open feature source
			initShapeFileDataSources();

			//
			findFeaturesForCountries(regions);
			findFeaturesForRegions(regions);

			createFeatureCollections(regions);
			testConsistencyOfFeatureCollections();
			simplifyShapesForFeatureCollections();
			generateGeoJSONFiles();

		} catch (IOException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		} catch (CQLException e) {
			System.out.println("IOException during preprocessing of GIS Data!");
			e.printStackTrace();
		}

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

	private static void findFeaturesForCountries(Map<String, Region> regions) throws IOException, CQLException
	{

		for (Region region : regions.values()) {
			if (region.getType() != RegionType.COUNTRY) {
				continue;
			} else if (!region.isMappable()) {
				continue;
			}

			SimpleFeature feature = null;
			if (specialCasesWhereRegionsAreThreatedAsCountries.containsKey(region.getCode())) {
				feature = fetchSimpleFeatureFromRegionDataSourceV2("adm1_code_",  specialCasesWhereRegionsAreThreatedAsCountries.get(region.getCode()));
			} else {
				feature = fetchFeatureFromCountryDataSource(region.getCode());
			}

			if (feature == null) {
				System.out.println("Did not find feature for " + region.getCode());
			} else {
				region.addFeature(feature);
			}

		}
		System.out.println("Completed processing of countries");
	}

	private static void findFeaturesForRegions(Map<String, Region> regions) throws IOException, CQLException
	{

		for (Region region : regions.values()) {
			if (region.getType() != RegionType.REGION) {
				continue;
			} else if (!region.isMappable()) {
				continue;
			}

			SimpleFeature featureV14 = null;
			SimpleFeature featureV2 = null;

			logOfCurrentRecord = new ArrayList<String>();
			currentRecordStatus = 0;

			String countryCode = region.getCountryCode();
			String regionCode = region.getCode();

			if (specialCasesWhereCountriesAreThreatedAsRegions.containsKey(regionCode)) {
				// Features are taken from country layer because some of these have multiple regions
				// at the admin 1 level (e.g. Micronesia and Marshall Islands)
				featureV2 = fetchFeatureFromCountryDataSource(specialCasesWhereCountriesAreThreatedAsRegions.get(regionCode));
				if (featureV2 != null) {
					region.setType(RegionType.COUNTRY);
					// TODO: set different continent for region
					// System.out.println("Successfully found feature for special case " + regionCode);
				} else {
					failureCount++;
					logOfCurrentRecord.add("Failed to find feature for " + regionCode);
					currentRecordStatus = STATUS_ERROR;
				}
			} else if ("US".equals(countryCode) || "CA".equals(countryCode)) {
				continue;
				/*featureV2 = fetchSimpleFeatureFromRegionDataSourceV2("iso_3166_2", regionCode);
				if (featureV2 != null) {
					logOfCurrentRecord.add("Successfully found feature for " + regionCode);
					currentRecordStatus = Math.max(currentRecordStatus, STATUS_OK);
				} else {
					failureCount++;
					logOfCurrentRecord.add("Failed to find feature for " + regionCode);
					currentRecordStatus = STATUS_ERROR;
				}*/
			} else {
				// all other countries
				if (!region.getContinentCode().equals("A") /*&& region.getContinentCode() != "S" && region.getContinentCode() != "E"*/) {
					continue;
				}
				/*
				if (!region.getCountryCode().equals("SD")) {
					continue;
				}*/
				// System.out.println("\nTrying to find feature for " + region.getCode());
				/*boolean successfull = */fetchSimpleFeatureForRegion(region);
			}

			if (featureV2 != null) {
				region.addFeature(featureV2);
			}

			String status = "Ok";
			if (currentRecordStatus == STATUS_ERROR) {
				status ="ERROR";
				if (!failuresByCountry.containsKey(region.getCountryCode())) {
					failuresByCountry.put(region.getCountryCode(), 0);
				}
				int value = failuresByCountry.get(region.getCountryCode()) + 1;
				failuresByCountry.put(countryCode, value);
			} else if (currentRecordStatus == STATUS_WARNING) {
				status = "WARNING";

			}
			if (currentRecordStatus > 1) {
				System.out.println();
				System.out.println("Status of record: " + status);
				System.out.println("Log: ");
				for (String line:logOfCurrentRecord) {
					System.out.println(" - " + line);
				}
			}

		}
		System.out.println("Completed processing of regions");
		System.out.println("Number of failures: " + failureCount);
		System.out.println("Number of warnings: " + warningsCount);
		for (String country: failuresByCountry.keySet()) {
			System.out.println("Errors for " + country + ": " + failuresByCountry.get(country));
		}
	}

	private static SimpleFeature fetchFeatureFromCountryDataSource(String code) {
		try {
			Filter filter = CQL.toFilter("iso_a2 LIKE '" + code + "'");
			SimpleFeatureCollection features = countryFeatureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = features.features();
			if (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				if (iterator.hasNext()) {
					logOfCurrentRecord.add("Found more than one feature for country " + code);
					warningsCount++;
					currentRecordStatus = Math.max(currentRecordStatus, STATUS_WARNING);
				}
				iterator.close();
				return feature;
			} else {
				iterator.close();
				logOfCurrentRecord.add("Did not find feature for country " + code);
				currentRecordStatus = STATUS_ERROR;
				failureCount++;
			}
		} catch (Exception e) {
			logOfCurrentRecord.add("Exception while looking for Feature for country " + code);
			currentRecordStatus = STATUS_ERROR;
			failureCount++;
		}
		return null;
	}

	private static boolean fetchSimpleFeatureForRegion(Region region) throws CQLException {

		if (manualMappingOfRegionIdsToAdm1Ids.containsKey(region.getCode())) {
			String[] ids = manualMappingOfRegionIdsToAdm1Ids.get(region.getCode());

			for (String id : ids) {
				SimpleFeature feature = fetchSimpleFeatureFromRegionDataSourceV2("adm1_code_", id);
				if (feature == null) {
					currentRecordStatus = STATUS_ERROR;
					logOfCurrentRecord.add("Did not found feature altough override code was specified!");
					return false;
				}
				region.addFeature(feature);
			}

			return true;
		}
		String fipsRegionCode = generateFipsRegionCodeForRegionCode(region.getCode());

		SimpleFeature featureV14 = fetchSimpleFeatureFromRegionDataSourceV14("FIRST_FIPS", fipsRegionCode);
		if (featureV14 == null) {
			logOfCurrentRecord.add("First Fips did not yield a record, trying FIPS_1 next");
			featureV14 = fetchSimpleFeatureFromRegionDataSourceV14("FIPS_1", fipsRegionCode);
		}

		if (featureV14 != null) {
			String hascCodeForRegion = featureV14.getProperty("HASC_1").getValue().toString();
			if (hascCodeForRegion == null || hascCodeForRegion.isEmpty()) {
				logOfCurrentRecord.add("Did not find hasc code based on region code " + region.getCode());

			} else {
				logOfCurrentRecord.add("Found hasc code " + hascCodeForRegion + " based on region code " + region.getCode());

				SimpleFeature featureV20 = fetchSimpleFeatureFromRegionDataSourceV2("code_hasc", hascCodeForRegion);

				if (featureV20 == null) {
					logOfCurrentRecord.add("Did not find feature based on hasc code!");
					// we are not at the end yet so we don't report an error
					/* currentRecordStatus = STATUS_ERROR;
					failureCount++; */
				} else {
					logOfCurrentRecord.add("Successfully find feature for region " + region.getCode());
					currentRecordStatus = Math.max(currentRecordStatus, STATUS_OK);
					region.addFeature(featureV20);
					return true;
				}
			}
		}

		logOfCurrentRecord.add("Did not find feature based on FIPS code, trying to find feature based on name and countryCode");
		logOfCurrentRecord.add("Name: " + region.getName() + " Code: " + region.getCode());

		if (region.getName().contains("'")) {
			logOfCurrentRecord.add("Region name contains invalid characters!");
			currentRecordStatus = STATUS_ERROR;
			failureCount++;
			return false;
		}
		Filter filter = CQL.toFilter("name" + " LIKE '%" + region.getName() + "%' AND iso_a2 LIKE '" + region.getCountryCode() + "'");
		SimpleFeature featureV20 = fetchSimpleFeatureFromRegionDataSourceV2(filter, region.getCode());

		if (featureV20 == null) {
			logOfCurrentRecord.add("Did not find region name in name column, trying column name_alt");
			filter = CQL.toFilter("name_alt" + " LIKE '%" + region.getName() + "%' AND iso_a2 LIKE '" + region.getCountryCode() + "'");
			featureV20 = fetchSimpleFeatureFromRegionDataSourceV2(filter, region.getCode());
		}

		if (featureV20 != null) {
			logOfCurrentRecord.add("Found feature based on name!");
			region.addFeature(featureV20);
			return true;
		}
		logOfCurrentRecord.add("Did not find feature based on name and iso code!");
		currentRecordStatus = STATUS_ERROR;
		failureCount++;

		return false;
	}

	private static String generateFipsRegionCodeForRegionCode(String regionCode)
	{
		String countryCode = regionCode.substring(0,2);
		return isoCountryCodeToFipsCountryCode.get(countryCode) + regionCode.substring(3);
	}

	private static SimpleFeature fetchSimpleFeatureFromRegionDataSourceV2(String field, String code) throws CQLException
	{
		Filter filter = CQL.toFilter(field + " LIKE '" + code + "'");
		return fetchSimpleFeatureFromRegionDataSource("v2", filter, code);
	}

	private static SimpleFeature fetchSimpleFeatureFromRegionDataSourceV2(Filter filter, String code)
	{
		return fetchSimpleFeatureFromRegionDataSource("v2", filter, code);
	}

	private static SimpleFeature fetchSimpleFeatureFromRegionDataSourceV14(String field, String code) throws CQLException
	{
		Filter filter = CQL.toFilter(field + " LIKE '%" + code + "%'");
		return fetchSimpleFeatureFromRegionDataSource("v14", filter, code);
	}

	private static SimpleFeature fetchSimpleFeatureFromRegionDataSource(String version, Filter filter, String regionCode) {
		try {

			SimpleFeatureCollection features = null;
			if (version == "v2") {
				features = regionFeatureSource.getFeatures(filter);
			} else {
				features = regionFeatureSourcev14.getFeatures(filter);
			}
			SimpleFeatureIterator iterator = features.features();
			if (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				if (iterator.hasNext()) {
					logOfCurrentRecord.add("Found more than one feature for region " + regionCode);
					warningsCount++;
					currentRecordStatus = Math.max(currentRecordStatus, STATUS_WARNING);
				}
				iterator.close();
				return feature;
			} else {
				iterator.close();
				logOfCurrentRecord.add("Did not find feature for region " + regionCode);
			}
		} catch (Exception e) {
			logOfCurrentRecord.add("Exception while looking for Feature for region " + regionCode);
		}
		return null;
	}


	private static String createUniqueRegionCode(String countryCode, String regionCode)
	{
		return countryCode + "-" + regionCode;
	}

	private static String getContinentCode(String countryCode) {
		return countryIdToContinentIdMap.get(countryCode);
	}

	private static boolean isAToZ(char character)
	{
		int intValueOfCharacter = character;
		int intValueOfA = 'A';
		int intValueOfZ = 'Z';

		return (intValueOfA <= intValueOfCharacter && intValueOfCharacter <= intValueOfZ);
	}

	private static void testConsistencyOfFeatureCollections() throws IOException {

		for (String key: featureCollections.keySet()) {
			if (key.length() != 2) {
				// we only look at consistency of countries now
				continue;
			}

			DefaultFeatureCollection features = featureCollections.get(key);
			SimpleFeatureIterator iterator = featureCollections.get(key).features();
			Geometry countryGeometry = null;
			List<Geometry> regionGeometries = new ArrayList<Geometry>();
			int i = 0;
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				if (feature.getAttribute("region_type") == "region") {
					regionGeometries.add(((Geometry) feature.getDefaultGeometry()).buffer(0));
				} else if (feature.getAttribute("region_type") == "country") {
					countryGeometry = ((Geometry) feature.getDefaultGeometry()).buffer(0);
				}
			}
			iterator.close();

			GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
			GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(regionGeometries);

			for (int j = 0; j < regionGeometries.size(); j++) {
				for (int k = j + 1; k < regionGeometries.size(); k++) {
					Geometry geometry1 = regionGeometries.get(j);
					Geometry geometry2 = regionGeometries.get(k);

					if (geometry1.intersects(geometry2)) {
						if (geometry1.intersection(geometry2).getArea() > 0.01) {
							System.out.println("Found intersection geometries for " + key);
							System.out.println("Intersecting area: " + geometry1.intersection(geometry2).getArea());
							System.out.println();
						}
					}
				}
			}


			Geometry unifiedCountriesGeometries = geometryCollection.union().buffer(0);
			if (unifiedCountriesGeometries == null || countryGeometry == null) {
				System.out.println("Could not calculate distance!");
				continue;
			}
			double distance = DiscreteHausdorffDistance.distance(unifiedCountriesGeometries, countryGeometry);

			if (distance > 0.01
					&& !("MX".equals(key) && distance < 2)
					&& !("CR".equals(key) && distance < 5)
					&& !("VE".equals(key) && distance < 4)
					&& !("CO".equals(key) && distance < 4)
					&& !("PA".equals(key) && distance < 1)
					&& !("HN".equals(key) && distance < 2)) {
				System.out.println("Unified regions do not match country for " + key);
				System.out.println("Number of regions: " +  regionGeometries.size());
				System.out.println("Haussdorff Distance: " + distance);
				System.out.println("Area country: " + countryGeometry.getArea());
				System.out.println("Area regions: " + unifiedCountriesGeometries.getArea());

				DefaultFeatureCollection collection = new DefaultFeatureCollection();

				SimpleFeatureBuilder builder = new SimpleFeatureBuilder(createFeatureType());
				builder.set("id", "Country");
				builder.set("parent_id", "none");
				builder.set("region_type", "Country");
				builder.set("name", "Country");
				builder.add(countryGeometry);

				collection.add(builder.buildFeature("Country"));

				builder.set("id", "Regions");
				builder.set("parent_id", "none");
				builder.set("region_type", "Regions");
				builder.set("name", "Regions");
				builder.add(unifiedCountriesGeometries);

				collection.add(builder.buildFeature("Regions"));

				builder.set("id", "Difference");
				builder.set("parent_id", "none");
				builder.set("region_type", "Difference");
				builder.set("name", "Difference");
				builder.add(countryGeometry.symDifference(unifiedCountriesGeometries));

				collection.add(builder.buildFeature("Difference"));

				generateGeoJSONFile(key + "-difference", collection);
			}

		}

	}

	private static void simplifyShapesForFeatureCollections() {


		for (String key: featureCollections.keySet()) {
			DefaultFeatureCollection collection = featureCollections.get(key);
			Geometry[] geometries = new Geometry[collection.size()];

			SimpleFeatureIterator iterator = collection.features();
			int i = 0;
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				geometries[i++] = geometry;
			}
			iterator.close();

			GeometryFactory factory = new GeometryFactory();
			GeometryCollection geoCollection = new GeometryCollection(geometries, factory);
			geoCollection = (GeometryCollection) TopologyPreservingSimplifier.simplify(geoCollection, 0.01);

			iterator = collection.features();
			i = 0;
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				feature.setDefaultGeometry(geoCollection.getGeometryN(i++));
			}
			iterator.close();

		}

	}

	private static void generateGeoJSONFiles() throws IOException
	{


		for (String key: featureCollections.keySet()) {
			generateGeoJSONFile(key, featureCollections.get(key));
		}

	}

	private static void generateGeoJSONFile(String filename, DefaultFeatureCollection features) throws IOException
	{
		FeatureJSON feature = new FeatureJSON(new GeometryJSON(6));
		FileWriter writer = new FileWriter("out/" + filename + ".json");
		feature.writeFeatureCollection(features, writer);
	}



	private static void createFeatureCollections(Map<String, Region> regions)
	{

		for (String key: regions.keySet()) {
			Region region = regions.get(key);
			if (region != null && region.isMappable() && region.getFeatures().size() >= 1) {
				if (!featureCollections.containsKey(region.getCountryCode())) {
					featureCollections.put(region.getCountryCode(), new DefaultFeatureCollection());
				}
				if (!featureCollections.containsKey(region.getContinentCode())) {
					featureCollections.put(region.getContinentCode(), new DefaultFeatureCollection());
				}
				SimpleFeature feature = createSimpleFeature(region);
				featureCollections.get(region.getCountryCode()).add(feature);
				featureCollections.get(region.getContinentCode()).add(feature);
			}
		}

	}

	private static SimpleFeature createSimpleFeature(Region region)
	{
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(createFeatureType());
		builder.set("id", region.getCode());
		if (region.getType() == RegionType.COUNTRY) {
			builder.set("parent_id", region.getContinentCode());
		} else {
			builder.set("parent_id", region.getCountryCode());
		}
		builder.set("region_type", region.getType().toString());
		builder.set("name", region.getName());

		List<SimpleFeature> features = region.getFeatures();
		if (features.size() == 1) {
			// builder.set("geometry", features.get(0).getDefaultGeometry());
			Geometry geometry = ((Geometry) features.get(0).getDefaultGeometry()).buffer(0);
			// geometry = TopologyPreservingSimplifier.simplify(geometry, 0.5);
			builder.add(geometry);
		} else {
			// builder.set("geometry", features.get(0).getDefaultGeometry());
			Geometry geometry = combineGeometries(features);
			// geometry = TopologyPreservingSimplifier.simplify(geometry, 0.5);
			builder.add(geometry);
		}

		SimpleFeature feature = builder.buildFeature(region.getCode());

		return feature;
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

}