package maxmind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.portable.RemarshalException;

public class MaxmindSpecialCaseThreatment {

	public static final List<String> countriesWithoutRegions = new ArrayList<String>();
	public static final List<String> regionsThatAreIgnored = new ArrayList<String>();
	public static final List<String> duplicateFipsCodesThatAreOkay = new ArrayList<String>();
	public static final List<String> unmappableRegions = new ArrayList<String>();

	public static final Map<String, String> fixedFIPSCodesForNaturalEarth = new HashMap<String, String>();
	public static final Map<String, String> staticMappingsFromAdmin1CodeToMaxMindCode = new HashMap<String, String>();
	public static final Map<String, String> regionsThatAreMappedToOtherRegions = new HashMap<String, String>();



	static {
		// TODO: Add names
		countriesWithoutRegions.add("AI");
		countriesWithoutRegions.add("AQ");
		countriesWithoutRegions.add("AS");
		countriesWithoutRegions.add("AW");
		countriesWithoutRegions.add("AX");
		countriesWithoutRegions.add("BL");
		countriesWithoutRegions.add("CK");
		countriesWithoutRegions.add("CW");
		countriesWithoutRegions.add("EH");
		countriesWithoutRegions.add("FK");
		countriesWithoutRegions.add("FO");
		countriesWithoutRegions.add("GG");
		countriesWithoutRegions.add("GI");
		countriesWithoutRegions.add("GS");
		countriesWithoutRegions.add("GU");
		countriesWithoutRegions.add("HK");
		countriesWithoutRegions.add("HM");
		countriesWithoutRegions.add("IM");
		countriesWithoutRegions.add("IO");
		countriesWithoutRegions.add("JE");
		countriesWithoutRegions.add("ME");
		countriesWithoutRegions.add("MF");
		countriesWithoutRegions.add("MH");
		countriesWithoutRegions.add("MP");
		countriesWithoutRegions.add("MT");
		countriesWithoutRegions.add("NC");
		countriesWithoutRegions.add("NU");
		countriesWithoutRegions.add("PF");
		countriesWithoutRegions.add("PM");
		countriesWithoutRegions.add("PN");
		countriesWithoutRegions.add("PR");
		countriesWithoutRegions.add("PW");
		countriesWithoutRegions.add("SG");
		countriesWithoutRegions.add("SX");
		countriesWithoutRegions.add("TC");
		countriesWithoutRegions.add("TF");
		countriesWithoutRegions.add("TL");
		countriesWithoutRegions.add("TV");
		countriesWithoutRegions.add("UM");
		countriesWithoutRegions.add("VA");
		countriesWithoutRegions.add("VG");
		countriesWithoutRegions.add("VI");
		countriesWithoutRegions.add("WF");

		// Fix FIPS codes in natural earth
		// Afghanistan:
		//   - AF18 appeared 2 times
		fixedFIPSCodesForNaturalEarth.put("AFG-3413", "AF36"); // Paktia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("AFG-3413", "AF-36"); // Paktia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("AFG-1764", "AF-18"); // Nangarhar

		// Albania
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALB-1498","AL-49"); // FIPS correct but a few regions in montenegro have the same fips

		// Algeria
		fixedFIPSCodesForNaturalEarth.put("DZA-2217", "AG04"); // Constantine
		staticMappingsFromAdmin1CodeToMaxMindCode.put("DZA-2191", "DZ-43"); // El Oued
		staticMappingsFromAdmin1CodeToMaxMindCode.put("DZA-2220", "DZ-48"); // Mila
		staticMappingsFromAdmin1CodeToMaxMindCode.put("DZA-2214", "DZ-27"); // M'sila

		// Angola
		fixedFIPSCodesForNaturalEarth.put("AGO-1880", "AO20"); // Luanda

		// Argentinia
		fixedFIPSCodesForNaturalEarth.put("ARG-1275", "AR13"); // Mendoza
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ARG-1309", "AR-08"); // Entre Rios

		// Armenia - duplicate FIPS in countries that aren't armenia
		fixedFIPSCodesForNaturalEarth.put("ARM-1733", "AM10"); // Vayots' Dzor
		fixedFIPSCodesForNaturalEarth.put("ARM-1732", "AM08"); // Syunik'
		fixedFIPSCodesForNaturalEarth.put("ARM-1670", "AM09"); // Tavush
		fixedFIPSCodesForNaturalEarth.put("ARM-1671", "AM02"); // Ararat

		// Australia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("AUS-2654", "AU-02");

		// Brazil
		fixedFIPSCodesForNaturalEarth.put("BRA-1311", "BR27"); // Sao Paulo

		// Bangladesh
		fixedFIPSCodesForNaturalEarth.put("BGD-2476", "BG84"); //
		fixedFIPSCodesForNaturalEarth.put("BGD-3255", "BG83"); //
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BGD-5492", "BD-83"); // Rangpur

		// Belarus
		fixedFIPSCodesForNaturalEarth.put("BLR-2340", "BO06"); // Mogilev
		fixedFIPSCodesForNaturalEarth.put("BLR-2345", "BO05"); // Minskaya voblasc
		fixedFIPSCodesForNaturalEarth.put("BLR-4825", "BO04"); // Minks city

		// Congo
		duplicateFipsCodesThatAreOkay.add("CF04"); // Kouilou
		fixedFIPSCodesForNaturalEarth.put("COG-2185", "CF14"); // Cuvette-Ouest
		staticMappingsFromAdmin1CodeToMaxMindCode.put("COG-2880", "CG-10"); // Sangha =>FIPS is okay but duplicate in Cuvette-Ouest

		fixedFIPSCodesForNaturalEarth.put("COD-1895", "CG10"); // Maniema

		// China
		fixedFIPSCodesForNaturalEarth.put("CHN-1151", "CH06"); //Qinghai

		// Cyprus
		staticMappingsFromAdmin1CodeToMaxMindCode.put("CYN+00?", "CY-02"); // not the best solution,recheck

		// Cape Verde
		staticMappingsFromAdmin1CodeToMaxMindCode.put("CPV-5058", "CV-10"); // Sao Nicolau

		// Bosnia and Herzegovina
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2224", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2225", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2226", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2227", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2228", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2887", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2889", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2890", "BA-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-2891", "BA-01");
		// Republika Srpska
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-3153", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4801", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4802", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4803", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4804", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4805", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4806", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4807", "BA-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BIH-4808", "BA-02");

		// Botswana
		fixedFIPSCodesForNaturalEarth.put("BWA-2629", "BC11");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BWA-4853" ,"BW-08");

		// Burundi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BDI-3364", "BI-02"); // Bujumbura Mairi => Bujumbura
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BDI-4854", "BI-02"); // Bujumbura Rural => Bujumbura

		// Chad
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-5578", "TD-03"); //Borkou-Ennedi-Tibesti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-5577", "TD-03"); //Borkou-Ennedi-Tibesti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-5579", "TD-03"); //Borkou-Ennedi-Tibesti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-1464", "TD-04"); // Chari-Baguirmi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-5580", "TD-04"); // Chari-Baguirmi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-1486", "TD-10"); // Mayo-Kebbi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TCD-4858", "TD-10"); // Mayo-Kebbi

		fixedFIPSCodesForNaturalEarth.put("CUB-1364", "CU05"); // Camagüey
		fixedFIPSCodesForNaturalEarth.put("CUB-1363", "CU07"); // Ciego de Avila
		regionsThatAreMappedToOtherRegions.put("CU-11", "CU-02"); // La Habana

		// Denmark
		fixedFIPSCodesForNaturalEarth.put("DNK-3416", "DA18"); // Midtjylland
		fixedFIPSCodesForNaturalEarth.put("DNK-3417", "DA21"); // Syddanmark
		fixedFIPSCodesForNaturalEarth.put("DNK-3418", "DA19"); // Nordjylland
		fixedFIPSCodesForNaturalEarth.put("DNK-3419", "DA17"); // Hovedstaden
		fixedFIPSCodesForNaturalEarth.put("DNK-3420", "DA20"); // Sjaælland

		// Djibouti
		fixedFIPSCodesForNaturalEarth.put("DJI-1568", "DJ08"); // Arta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("DJI-1570", "DJ-04"); // Obock - region from another country has same code

		// Ecuador
		fixedFIPSCodesForNaturalEarth.put("ECU-1287", "EC18"); // Pinchina
		fixedFIPSCodesForNaturalEarth.put("ECU-1289", "EC19"); // Tungurahua

		// Eritrea
		fixedFIPSCodesForNaturalEarth.put("ERI-1565", "ER03"); // Debubawi Keyih Bahri
		regionsThatAreIgnored.add("ERI+99?");

		// Egypt
		fixedFIPSCodesForNaturalEarth.put("EGY-1551", "EG23"); // Qina
		fixedFIPSCodesForNaturalEarth.put("EGY-1556", "EG02"); // Al Bahr al Ahmar
		fixedFIPSCodesForNaturalEarth.put("EGY-1558", "EG27"); // Shamal Sina'


		// Greenland
		unmappableRegions.add("GL-01"); // 3 Regions in Greenland, but that do not longer match the
		unmappableRegions.add("GL-02"); // admin1 regions in Greenland
		unmappableRegions.add("GL-03");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2738", "GL");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2739", "GL");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2740", "GL");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2741", "GL");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2742", "GL");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GRL-2743", "GL");

		// Guinea-Bissau
		fixedFIPSCodesForNaturalEarth.put("GNB-5500", "PU12"); // Biombo
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GNB-770", "GW-05"); // Bolama

		// Haiti
		fixedFIPSCodesForNaturalEarth.put("HTI-1359", "HA14"); // Grand' Anse

		// Honduras
		fixedFIPSCodesForNaturalEarth.put("HND-641", "HO11"); // Islas de la Bahia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("HND-637", "HN-01"); // Atlantida

		// Hungary
		fixedFIPSCodesForNaturalEarth.put("HUN-4916", "HU19"); // Szeged
		fixedFIPSCodesForNaturalEarth.put("HUN-3173", "HU10"); // Hajdu-Bihar
		fixedFIPSCodesForNaturalEarth.put("HUN-4924", "HU07"); // Debrecen
		fixedFIPSCodesForNaturalEarth.put("HUN-3138", "HU22"); // Vas
		fixedFIPSCodesForNaturalEarth.put("HUN-4904", "HU34"); // Sopron

		// Indonesia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IDN-554", "ID-28"); // FIPS code okay but another region from another country has the same entry

		// Ireland
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-5575", "IE-07"); // Dublin
		fixedFIPSCodesForNaturalEarth.put("IRL-3412", "EI22"); // Monaghan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-5572", "IE-26"); // Tipperary
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-724", "IE-26"); // Tipperary
		fixedFIPSCodesForNaturalEarth.put("IRL-1444", "EI27"); // Waterford
		fixedFIPSCodesForNaturalEarth.put("IRL-5569", "EI32"); // Cork

		// Japan
		fixedFIPSCodesForNaturalEarth.put("JPN-3500", "JA27"); // Nagasaki
		fixedFIPSCodesForNaturalEarth.put("JPN-1852", "JA32"); // Osaka
		fixedFIPSCodesForNaturalEarth.put("JPN-1860", "JA40"); // Tokyo
		fixedFIPSCodesForNaturalEarth.put("JPN-3502", "JA47"); // Okinawa

		// Jordania
		fixedFIPSCodesForNaturalEarth.put("JOR-860", "JO17");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("JOR-860", "JO-17");

		// Kazachstan
		regionsThatAreMappedToOtherRegions.put("KZ-08", "KZ-14"); // Baikonur - used to be a separate region but no longer

		// Kirgistan
		fixedFIPSCodesForNaturalEarth.put("KGZ-1120", "KG03"); // Jalal-Abad
		fixedFIPSCodesForNaturalEarth.put("KGZ-1116", "KG02"); // Chuy
		fixedFIPSCodesForNaturalEarth.put("KGZ-1117", "KG07"); // Ysyk-Kol
		fixedFIPSCodesForNaturalEarth.put("KGZ-1118", "KG04"); // Naryn
		fixedFIPSCodesForNaturalEarth.put("KGZ-1121", "KG06"); // Talas
		fixedFIPSCodesForNaturalEarth.put("KGZ-1122", "KG08"); // Talas
		regionsThatAreMappedToOtherRegions.put("KG-05", "KG-08"); // Osh

		// Kuwait
		fixedFIPSCodesForNaturalEarth.put("KWT-1666", "KU09"); // Mubarak Al-Kabir
		fixedFIPSCodesForNaturalEarth.put("KWT-3506", "KU08"); // Hawalli
		fixedFIPSCodesForNaturalEarth.put("KWT-1664", "KU07"); // Al Farwaniyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KWT-1665", "KW-01");

		// Kiribati
		regionsThatAreMappedToOtherRegions.put("KI-01", "KI");
		regionsThatAreMappedToOtherRegions.put("KI-02", "KI");
		regionsThatAreMappedToOtherRegions.put("KI-03", "KI");

		// Palaestine
		fixedFIPSCodesForNaturalEarth.put("GAZ+00?", "GZ");
		fixedFIPSCodesForNaturalEarth.put("WEB+00?", "WE");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GAZ+00?", "PS-GZ");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("WEB+00?", "PS-WE");

		//Peru
		fixedFIPSCodesForNaturalEarth.put("PER-586", "PE05"); // Ayacucho
		fixedFIPSCodesForNaturalEarth.put("PER-588", "PE09"); // Huancavelica
		fixedFIPSCodesForNaturalEarth.put("PER-573", "PE07"); // Callao
		fixedFIPSCodesForNaturalEarth.put("PER-3505", "PE21"); // Puno

		// Rumainia - correct fips codes but regions from another country have the same
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ROU-278", "RO-12"); // Caras-Severin
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ROU-124", "RO-26" ); // Mehedinti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ROU-129", "RO-41"); // Calarasi

		// Saint Kitts & Nevins
		fixedFIPSCodesForNaturalEarth.put("KNA-5112", "SC15");

		// Saudi Arabia
		fixedFIPSCodesForNaturalEarth.put("SAU-862", "SA20"); // Al Jawf


		// Serbia - Vojvodina
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-1059", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-1060", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-1061", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-273", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-274", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-275", "RS-02");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SRB-281", "RS-02");

		// Serbia, Kosovo
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5886", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5887", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5888", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5889", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5890", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5891", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5892", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5893", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5894", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5895", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5896", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5897", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5898", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5899", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5900", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5901", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5902", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5903", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5904", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5905", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5906", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5907", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5908", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5909", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5910", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5911", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5912", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5913", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5914", "RS-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KOS-5915", "RS-01");

		// Senegal
		fixedFIPSCodesForNaturalEarth.put("SEN-779", "SG05");  // Tambacounda
		fixedFIPSCodesForNaturalEarth.put("SEN-765", "SG10");  // Kaolack
		fixedFIPSCodesForNaturalEarth.put("SEN-5516", "SG16"); // Kaffrine
		fixedFIPSCodesForNaturalEarth.put("SEN-774", "SG11");  // Kolda
		fixedFIPSCodesForNaturalEarth.put("SEN-5517", "SG14"); // Saint-Louis
		fixedFIPSCodesForNaturalEarth.put("SEN-767", "SG15");  // Matam

		// Slovakia
		fixedFIPSCodesForNaturalEarth.put("SVK-1054", "LO04"); // Nitra
		fixedFIPSCodesForNaturalEarth.put("SVK-1055", "LO06"); // Trencin
		fixedFIPSCodesForNaturalEarth.put("SVK-1056", "LO07"); // Trnava

		// South Africa
		fixedFIPSCodesForNaturalEarth.put("ZAF-1189", "SF11"); // Western Cape
		fixedFIPSCodesForNaturalEarth.put("ZAF-1201", "SF10"); // North-West
		fixedFIPSCodesForNaturalEarth.put("ZAF-1208", "SF06"); // Gauteng
		regionsThatAreMappedToOtherRegions.put("ZA-01", "ZA-10"); // North-Western Province
		fixedFIPSCodesForNaturalEarth.put("ZAF-1210", "SF09"); // Limpopo
		fixedFIPSCodesForNaturalEarth.put("ZAF-1188", "SF08"); // Northern Cape
		fixedFIPSCodesForNaturalEarth.put("ZAF-1926", "SF05"); // Eastern Cape

		// Sudan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-880", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-875", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-879", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-874", "SD-29"); // Al Khartum
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-878", "SD-30"); // Ash Shamaliyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-876", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-884", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-149", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-881", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-811", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-797", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-882", "SD-34"); // Kurdufan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-883", "SD-34"); // Kurdufan

		// Suriname
		fixedFIPSCodesForNaturalEarth.put("SUR-67", "NS13"); // Marowijne
		fixedFIPSCodesForNaturalEarth.put("SUR-70", "NS11"); // Commewijne

		// South Sudan
		// TODO: create separate region and remap data
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-865", "SD-44"); // Central Equatoria State
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-892", "SD-28"); // Al Istiwa'iyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-864", "SD-28"); // Al Istiwa'iyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-871", "SD-40"); // Al Wahadah State
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-869", "SD-35"); // Upper Nile
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-870", "SD-35"); // Upper Nile
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-873", "SD-32"); // Bahr al Ghazal
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-872", "SD-32"); // Bahr al Ghazal
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-147", "SD-32"); // Bahr al Ghazal
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDS-863", "SD-32"); // Bahr al Ghazal

		// Sweden
		fixedFIPSCodesForNaturalEarth.put("SWE-193", "SW05"); // Gotlands Lan
		fixedFIPSCodesForNaturalEarth.put("SWE-185", "SW18"); // Sodermanlands Lan
		fixedFIPSCodesForNaturalEarth.put("SWE-3429", "SW27"); // Skane Lan
		fixedFIPSCodesForNaturalEarth.put("SWE-3428", "SW28"); // Vastra Gotaland

		// Switzerland
		fixedFIPSCodesForNaturalEarth.put("CHE-3422", "SZ23"); // Vaud
		fixedFIPSCodesForNaturalEarth.put("CHE-3421", "SZ06"); // Fribourg
		fixedFIPSCodesForNaturalEarth.put("CHE-3423", "SZ03"); // Basel Landschaft
		fixedFIPSCodesForNaturalEarth.put("CHE-3425", "SZ04"); // Basel-Stadt
		fixedFIPSCodesForNaturalEarth.put("CHE-3424", "SZ05"); // Bern

		// Taiwan - geoip is messed up and only gives us 4 areas,so we need to map all the 22 regions from NE to this 4
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1156", "TW-02"); // Kaohsiung City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1158", "TW-04"); // Pingtung
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1160", "TW-04"); // Tainan City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1161", "TW-04"); // Hsinchu City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1162", "TW-04"); // Hsinchu
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1163", "TW-04"); // Yilan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1164", "TW-04"); // Keelung City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1165", "TW-04"); // Miaoli
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1166", "TW-03"); // Taipei City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1167", "TW-03"); // New Taipei City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1168", "TW-04"); // Taoyuan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1169", "TW-04"); // Changhua
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1170", "TW-04"); // Chiayi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1171", "TW-04"); // Chiayi City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1172", "TW-04"); // Hualien
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1173", "TW-04"); // Nantou
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1174", "TW-04"); // Taichung City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1176", "TW-04"); // Yunlin
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-1177", "TW-04"); // Taitung
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-3414", "TW-04"); // Penghu
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-3415", "TW-01"); // Kinmen
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TWN-5128", "TW-01"); // Lienchiang

		// Tunisia
		fixedFIPSCodesForNaturalEarth.put("TUN-104", "TS27"); // Ben Arous
		fixedFIPSCodesForNaturalEarth.put("TUN-114", "TS32"); // Sfax
		regionsThatAreMappedToOtherRegions.put("TN-39", "TN-36"); // Manouba

		// Canada
		regionsThatAreIgnored.add("CAN+99?");

		// Liberia
		regionsThatAreMappedToOtherRegions.put("LR-05", "LR-20"); // Lofa
		regionsThatAreMappedToOtherRegions.put("LR-06", "LR-13"); // Maryland
		regionsThatAreMappedToOtherRegions.put("LR-07", "LR-14"); // Monrovia
		regionsThatAreMappedToOtherRegions.put("LR-04", "LR-12"); // Grand Cape Mount

		// Liechtenstein

		// Lybia
		regionsThatAreMappedToOtherRegions.put("LY-41", "LY-50"); // Tarhunah
		regionsThatAreMappedToOtherRegions.put("LY-45", "LY-58"); // Zlitan
		regionsThatAreMappedToOtherRegions.put("LY-59", "LY-58"); // Sawfajjin
		regionsThatAreMappedToOtherRegions.put("LY-62", "LY-53");// Yafran


		// Madagascar
		// provinces where desolved and the previous admin2 regions became
		// admin1 regions but they all have the FIPS code of their former province assigned.
		// Maxmind still gives the codes for the former provinces so we need to merge those
		// geometries;
		duplicateFipsCodesThatAreOkay.add("MA01");
		duplicateFipsCodesThatAreOkay.add("MA02");
		duplicateFipsCodesThatAreOkay.add("MA03");
		duplicateFipsCodesThatAreOkay.add("MA04");
		duplicateFipsCodesThatAreOkay.add("MA05");
		duplicateFipsCodesThatAreOkay.add("MA06");

		// Macau ?? is that a bugin NaturalEarth
		fixedFIPSCodesForNaturalEarth.put("MAC+00?", "MC02");
		regionsThatAreMappedToOtherRegions.put("MO-01", "MO-02");

		// Malawi
		fixedFIPSCodesForNaturalEarth.put("MWI-1901", "MI17"); // Nkhata Bay
		fixedFIPSCodesForNaturalEarth.put("MWI-5509", "MI27"); // Likoma

		// Mexico
		fixedFIPSCodesForNaturalEarth.put("MEX-2725", "MX27"); // Tabasco
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MEX-2713", "MX-32"); // Zacatecas

		// Monaco
		unmappableRegions.add("MC-01");
		unmappableRegions.add("MC-02");
		unmappableRegions.add("MC-03");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MCO+00?", "MC");

		// Mozambique
		fixedFIPSCodesForNaturalEarth.put("MOZ-1927", "MZ04"); // Maputo - region
		fixedFIPSCodesForNaturalEarth.put("MOZ-5854", "MZ11"); // Maputo - city

		// Myanmar
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MMR-3276", "MM-07"); // Magwe
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MMR-3268", "MM-09"); // Pegu
		regionsThatAreMappedToOtherRegions.put("MM-14", "MM-17"); // Rangoon

		// New Zealand
		fixedFIPSCodesForNaturalEarth.put("NZL-5468", "NZG1"); // Waikato
		fixedFIPSCodesForNaturalEarth.put("NZL-5469", "NZG2"); // Wellington
		fixedFIPSCodesForNaturalEarth.put("NZL-3402", "NZF5"); // Nelson

		// Nicaragua
		unmappableRegions.add("NI-16"); // Zelaya - former region - now Region autonome del atlantico norte + sur
		fixedFIPSCodesForNaturalEarth.put("NIC-24", "NU15"); // Rivas
		fixedFIPSCodesForNaturalEarth.put("NIC-4800", "NU14"); // Rio San Juan

		// Nigeria
		fixedFIPSCodesForNaturalEarth.put("NGA-2854" ,"NI54"); // Ekiti
		fixedFIPSCodesForNaturalEarth.put("NGA-2871", "NI51"); // Sokoto

		// Paraguay
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PRY-4837", "PY-21"); // Nueva Asuncion
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PRY-598", "PY-03"); // Bocqueron
		unmappableRegions.add("PY-20"); // Chaco
		fixedFIPSCodesForNaturalEarth.put("PRY-597", "PA23"); // Alto Paraguay

		// Philippines
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5541", "PH-B9"); // Dagupan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5528", "PH-C3"); // Davao City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5526", "PH-C9"); // Iloilo City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5530", "PH-C8"); // Iligan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5524", "PH-D4"); // Lapu-Lapu
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5560", "PH-D7"); // Lucena
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5549", "PH-H2"); // Quezon / PH-F2 - Quezon City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5533", "PH-F1"); // Puerto Princesa
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5540", "PH-E3"); // Olongapo
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5554", "PH-E9"); // Pasay" +
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5523", "PH-D8"); // Mandaue
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5529", "PH-C6"); // General Santos
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5543", "PH-D9"); // Manila

		regionsThatAreMappedToOtherRegions.put("PH-C2", "PH-65"); // Dapitan
		regionsThatAreMappedToOtherRegions.put("PH-C5", "PH-46"); // Dumaguete
		regionsThatAreMappedToOtherRegions.put("PH-C4", "PH-65"); // Dipolog
		regionsThatAreMappedToOtherRegions.put("PH-C1", "PH-21"); // Danao
		regionsThatAreMappedToOtherRegions.put("PH-C7", "PH-43"); // Gingoog
		regionsThatAreMappedToOtherRegions.put("PH-D6", "PH-09"); // Lipa
		regionsThatAreMappedToOtherRegions.put("PH-D5", "PH-05"); // Legaspi
		regionsThatAreMappedToOtherRegions.put("PH-D3", "PH-28"); // Laoag
		regionsThatAreMappedToOtherRegions.put("PH-D2", "PH-45"); // La Carlota
		regionsThatAreMappedToOtherRegions.put("PH-D1", "PH-16"); // Iriga
		regionsThatAreMappedToOtherRegions.put("PH-E8", "PH-47"); // Palayan
		regionsThatAreMappedToOtherRegions.put("PH-E1", "PH-35"); // Marawi
		regionsThatAreMappedToOtherRegions.put("PH-E2", "PH-21"); // Naga
		regionsThatAreMappedToOtherRegions.put("PH-E4", "PH-37"); // Ormoc
		regionsThatAreMappedToOtherRegions.put("PH-E5", "PH-42"); // Oroquieta
		regionsThatAreMappedToOtherRegions.put("PH-E6", "PH-42"); // Ozamis
		regionsThatAreMappedToOtherRegions.put("PH-E7", "PH-66"); // Pagadian
		regionsThatAreMappedToOtherRegions.put("PH-F9", "PH-D9"); // Surigao
		regionsThatAreMappedToOtherRegions.put("PH-F3", "PH-18"); // Roxas
		regionsThatAreMappedToOtherRegions.put("PH-F4", "PH-45"); // San Carlos
		regionsThatAreMappedToOtherRegions.put("PH-F7", "PH-33"); // San Pablo
		regionsThatAreMappedToOtherRegions.put("PH-F8", "PH-45"); // Silay
		regionsThatAreMappedToOtherRegions.put("PH-F5", "PH-51"); // San Carlos
		regionsThatAreMappedToOtherRegions.put("PH-F6", "PH-47"); // San Jose
		regionsThatAreMappedToOtherRegions.put("PH-G6", "PH-20"); // Trece Martires
		regionsThatAreMappedToOtherRegions.put("PH-G7", "PH-66"); // Zamboanga
		regionsThatAreMappedToOtherRegions.put("PH-G2", "PH-20"); // Tagaytay
		regionsThatAreMappedToOtherRegions.put("PH-G3", "PH-11"); // Tagbilaran
		regionsThatAreMappedToOtherRegions.put("PH-G4", "PH-42"); // Tangub
		regionsThatAreMappedToOtherRegions.put("PH-G5", "PH-21"); // Toledo

		// Mali
		fixedFIPSCodesForNaturalEarth.put("MLI-2809", "ML04"); // Mopti

		// Mauritania
		// this is the fix for natural earth
		fixedFIPSCodesForNaturalEarth.put("MRT-2787", "MR13"); // Noukachott - Captial district
		// but we have to override the codes for GEOIP manually as it does not know about MR-13
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MRT-2787", "MR-06");	 // Noukachott - Captial district
		staticMappingsFromAdmin1CodeToMaxMindCode.put("MRT-2788", "MR-06");  // Trarza

		// Niger
		// GeoIP still uses old codes so we need a separate fix for Natural earth and GEOIP
		// "NE05" old code for niamey when it was still a region
		// "NE08" new code for niamey now that it is the capital district
		staticMappingsFromAdmin1CodeToMaxMindCode.put("NER-4859", "NE-05"); // region
		staticMappingsFromAdmin1CodeToMaxMindCode.put("NER-94", "NE-08"); // capital district
		// fix for natural earth
		fixedFIPSCodesForNaturalEarth.put("NER-4859", "NG05");

		// Norway
		fixedFIPSCodesForNaturalEarth.put("NOR-75", "NO09");

		// Panama
		fixedFIPSCodesForNaturalEarth.put("PAN-1958", "PM01"); // Bocas del Toro
		fixedFIPSCodesForNaturalEarth.put("PAN-1419", "PM09"); // Guna Yala / San blas
		fixedFIPSCodesForNaturalEarth.put("PAN-3454", "PM12"); // Ngäbe Buglé
		fixedFIPSCodesForNaturalEarth.put("PAN-3455", "PM11"); // Embera
		fixedFIPSCodesForNaturalEarth.put("PAN-1418", "PM05"); // Darien
		regionsThatAreMappedToOtherRegions.put("PA-12", "PA-09"); // San Blas Guna Yala

		// Solomon islands
		duplicateFipsCodesThatAreOkay.add("BP06"); // Guadacanal + Capital Territory
		fixedFIPSCodesForNaturalEarth.put("SLB-3508", "BP12"); // Choiseul
		fixedFIPSCodesForNaturalEarth.put("SLB-1261", "BP13"); // Rennell and Bellona
		// fixes for duplicates
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SLB-1262", "SB-10"); // Central
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SLB-3507", "SB-11"); // Western

		// Saint Helena
		fixedFIPSCodesForNaturalEarth.put("SHN-4863", "SH01"); // Ascension
		fixedFIPSCodesForNaturalEarth.put("SHN-4864", "SH02"); // Saint Helena
		fixedFIPSCodesForNaturalEarth.put("SHN-4865", "SH03"); // Tristan da Cunha

		// St. Lucia
		fixedFIPSCodesForNaturalEarth.put("LCA-5079", "ST02");
		fixedFIPSCodesForNaturalEarth.put("LCA-5084", "ST11");

		// Uzbekistan
		fixedFIPSCodesForNaturalEarth.put("UZB-365", "UZ06");

		// Tonga
		fixedFIPSCodesForNaturalEarth.put("TON-4945", "TN02"); // Tongatapu
		fixedFIPSCodesForNaturalEarth.put("TON-4946", "TN03"); // Vava'u
		fixedFIPSCodesForNaturalEarth.put("TON-4948", "TN01"); // Ha'apai
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TON-4947", "TO-02"); // Niuas
		staticMappingsFromAdmin1CodeToMaxMindCode.put("TON-4949", "TO-02"); // 'Eua

		// Vanuatu
		// distributed in 6 provinces, but maxmind returns some more islands
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-560", "VU-07"); // Torba
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-561", "VU-13"); // Sanma
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-562", "VU-15"); // Tafea
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-563", "VU-10|VU-17|VU-11|VU-05|VU-16"); // Malampa
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-564", "VU-12|VU-06"); // Penema
		staticMappingsFromAdmin1CodeToMaxMindCode.put("VUT-565", "VU-08|VU-14|VU-18|VU-09");  // Shefa

		// Venezuela
		fixedFIPSCodesForNaturalEarth.put("VEN-44", "VE24"); // Dependencias Federales
		fixedFIPSCodesForNaturalEarth.put("VEN-43", "VE25"); // Distrito Federal
		fixedFIPSCodesForNaturalEarth.put("VEN-42", "VE26"); // Vargas
		fixedFIPSCodesForNaturalEarth.put("VEN-48", "VE17"); // Nueva Esparta

		// Zambia
		fixedFIPSCodesForNaturalEarth.put("ZMB-520", "ZA09"); // Lusaka
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ZMB-516","ZM-02"); // Central
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ZMB-5511", "ZM-05"); // Muchinga
	}
}
