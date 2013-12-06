package maxmind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		countriesWithoutRegions.add("KY");

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

		// Cayman island
		staticMappingsFromAdmin1CodeToMaxMindCode.put("CYM+00?", "KY-01|KY-02|KY-03|KY-04|KY-05|KY-06|KY-07|KY-08");

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

		// Bermuda
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5134", "BD03"); // City of Hamilton
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5135", "BD06"); // City of Saint George
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5136", "BD01"); // Devonshire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5137", "BD02"); // Hamilton
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5138", "BD04"); // Paget
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5139", "BD05"); // Pembroke
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5140", "BD07"); // Saint George's
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5141", "BD08"); // Sandys
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5142", "BD09"); // Smith's
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5143", "BD10"); // Southampton
		staticMappingsFromAdmin1CodeToMaxMindCode.put("BMU-5144", "BD11"); // Warwick

		// Cambodia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KHM-1795", "KH-15"); // Ratanakiri Kiri
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KHM-1781", "KH-16"); // Siem Reap
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KHM-1789", "KH-11"); // Phnum Penh
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KHM-1778", "KH-01"); // Batdambang
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KHM-1797", "KH-06"); // Kampot
		fixedFIPSCodesForNaturalEarth.put("KHM-1778", "CB29"); // Batdambang

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

		// Croatia
		fixedFIPSCodesForNaturalEarth.put("HRV-1582","HR07"); // Krapinsko-Zagorska
		fixedFIPSCodesForNaturalEarth.put("HRV-1490","HR03"); // Dubrovacko-Neretvanska
		fixedFIPSCodesForNaturalEarth.put("HRV-1605","HR18"); // Vukovarsko-Srijemska
		fixedFIPSCodesForNaturalEarth.put("HRV-1493","HR19"); // Zadarska
		fixedFIPSCodesForNaturalEarth.put("HRV-1603","HR10"); // Osjecko-Baranjska

		// Colombia
		fixedFIPSCodesForNaturalEarth.put("COL-1342", "CO25"); // San Andres y Providencia
		fixedFIPSCodesForNaturalEarth.put("COL-1408", "CO29"); // Valle del Cauca
		fixedFIPSCodesForNaturalEarth.put("COL-1398", "CO33"); // Cundinamarca
		fixedFIPSCodesForNaturalEarth.put("COL-1416", "CO38"); // Magdalena
		fixedFIPSCodesForNaturalEarth.put("COL-1318", "CO17"); // La Guajira
		fixedFIPSCodesForNaturalEarth.put("COL-1415", "CO11"); // Choco

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

		// Estonia
		regionsThatAreMappedToOtherRegions.put("EE-06", "EN-03"); // Kohtla-Jarve
		regionsThatAreMappedToOtherRegions.put("EE-09", "EN-03"); // Narva
		regionsThatAreMappedToOtherRegions.put("EE-15", "EN-03"); // Sillamae
		regionsThatAreMappedToOtherRegions.put("EE-10", "EE-11"); // Parnu
		regionsThatAreMappedToOtherRegions.put("EE-17", "EE-18"); // Tartu
		regionsThatAreMappedToOtherRegions.put("EE-16", "EE-06"); // Tallinn

		// Ethiopia
		fixedFIPSCodesForNaturalEarth.put("ETH-3135", "ET48"); // Dire Dawa
		fixedFIPSCodesForNaturalEarth.put("ETH-3110", "ET53"); // Tigray
		fixedFIPSCodesForNaturalEarth.put("ETH-3134", "ET52"); // Sumale
		fixedFIPSCodesForNaturalEarth.put("ETH-3131", "ET51"); // Oromiya
		fixedFIPSCodesForNaturalEarth.put("ETH-3111", "ET45"); // Afar
		fixedFIPSCodesForNaturalEarth.put("ETH-3133", "ET44"); // Adis Abeba

		// Finnland
		fixedFIPSCodesForNaturalEarth.put("FIN-3176", "FI26"); // Lapland
		fixedFIPSCodesForNaturalEarth.put("FIN-3177", "FI24"); // Central Finland
		fixedFIPSCodesForNaturalEarth.put("FIN-3178", "FI33"); // Northern Savonia
		fixedFIPSCodesForNaturalEarth.put("FIN-3179", "FI22"); // Kainuu
		fixedFIPSCodesForNaturalEarth.put("FIN-3180", "FI31"); // Northern Ostrobothnia
		fixedFIPSCodesForNaturalEarth.put("FIN-3181", "FI23"); // Central Ostrobothnia
		fixedFIPSCodesForNaturalEarth.put("FIN-3182", "FI29"); // Ostrobothnia
		fixedFIPSCodesForNaturalEarth.put("FIN-3183", "FI18"); // Southern Ostrobothnia
		fixedFIPSCodesForNaturalEarth.put("FIN-3184", "FI27"); // Päijät-Häme
		fixedFIPSCodesForNaturalEarth.put("FIN-3185", "FI20"); // Tavastia Proper
		fixedFIPSCodesForNaturalEarth.put("FIN-3186", "FI28"); // Pirkanmaa
		fixedFIPSCodesForNaturalEarth.put("FIN-3187", "FI25"); // Kymenlaakso
		fixedFIPSCodesForNaturalEarth.put("FIN-3188", "FI17"); // South Karelia
		fixedFIPSCodesForNaturalEarth.put("FIN-3189", "FI19"); // Southern Savonia
		fixedFIPSCodesForNaturalEarth.put("FIN-3190", "FI30"); // North Karelia
		fixedFIPSCodesForNaturalEarth.put("FIN-3191", "FI34"); // Finland Proper
		fixedFIPSCodesForNaturalEarth.put("FIN-3192", "FI32"); // Satakunta
		fixedFIPSCodesForNaturalEarth.put("FIN-3193", "FI35"); // Uusimaa

		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3176", "FI-06"); // Lapland
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3177", "FI-15"); // Central Finland
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3178", "FI-14"); // Northern Savonia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3179", "FI-08"); // Kainuu
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3180", "FI-08"); // Northern Ostrobothnia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3181", "FI-15"); // Central Ostrobothnia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3182", "FI-15"); // Ostrobothnia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3183", "FI-15"); // Southern Ostrobothnia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3184", "FI-13"); // Päijät-Häme
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3185", "FI-13"); // Tavastia Proper
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3186", "FI-15"); // Pirkanmaa
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3187", "FI-13"); // Kymenlaakso
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3188", "FI-13"); // South Karelia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3189", "FI-14"); // Southern Savonia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3190", "FI-14"); // North Karelia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3191", "FI-15"); // Finland Proper
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3192", "FI-15"); // Satakunta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FIN-3193", "FI-13"); // Uusimaa

		// France
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5262", "FR-B9"); //	Ain
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5263", "FR-B6");  //	Aisne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5264", "FR-98");  //	Allier
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5265", "FR-B8");  //	Alpes-de-Haute-Provence
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5266", "FR-B8");  //	Alpes-Maritimes
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5267", "FR-B9");  //	Ardeche
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5268", "FR-A4");  //	Ardennes
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5269", "FR-B3");  //	Ariege
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5270", "FR-A4");  //	Aube
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5271", "FR-A9");  //	Aude
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5272", "FR-B3");  //	Aveyron
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5273", "FR-C1");  //	Bas-Rhin
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5274", "FR-B8");  //	Bouches-du-Rhone
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5275", "FR-99");  //	Calvados
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5276", "FR-98");  //	Cantal
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5277", "FR-B7");  //	Charente
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5278", "FR-B7");  //	Charente-Maritime
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5279", "FR-A3");  //	Cher
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5280", "FR-B1");  //	Corraze
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5281", "FR-A5");  //	Corse-du-Sud
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5282", "FR-A1");  //	Cote-d'Or
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5283", "FR-A2");  //	Cotes-d'Armor
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5284", "FR-B1");  //	Creuse
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5285", "FR-B7");  //	Deux-Savres
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5286", "FR-97");  //	Dordogne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5287", "FR-A6");  //	Doubs
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5288", "FR-B9");  //	Drame
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5289", "FR-A8");  //	Essonne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5290", "FR-A7");  //	Eure
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5291", "FR-A3");  //	Eure-et-Loir
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5292", "FR-A2");  //	Finistere
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5293", "FR-A9");  //	Gard
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5294", "FR-B3");  //	Gers
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5295", "FR-97");  //	Gironde
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5296", "FR-C1");  //	Haute-Rhin
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5297", "FR-A5");  //	Haute-Corse
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5298", "FR-B3");  //	Haute-Garonne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5299", "FR-98");  //	Haute-Loire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5300", "FR-A4");  //	Haute-Marne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5301", "FR-A6"); //	Haute-SaÃ´ne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5302", "FR-B9");  //	Haute-Savoie
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5303", "FR-B1");  //	Haute-Vienne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5304", "FR-B8");  //	Hautes-Alpes
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5305", "FR-B3");  //	Hautes-Pyranaes
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5306", "FR-A8");  //	Hauts-de-Seine
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5307", "FR-A9");  //	Herault
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5308", "FR-A2");  //	Ille-et-Vilaine
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5309", "FR-A3");  //	Indre
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5310", "FR-A3");  //	Indre-et-Loire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5311", "FR-B9");  //	Isre
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5312", "FR-A6");  //	Jura
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5313", "FR-97");  //	Landes
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5314", "FR-A3");  //	Loir-et-Cher
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5315", "FR-B9");  //	Loire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5316", "FR-B5");  //	Loire-Atlantique
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5317", "FR-A3");  //	Loiret
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5318", "FR-B3");  //	Lot
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5319", "FR-97");  //	Lot-et-Garonne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5320", "FR-A9");  //	Lozare
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5321", "FR-B5");  //	Maine-et-Loire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5322", "FR-99");  //	Manche
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5323", "FR-A4");  //	Marne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5324", "FR-B5");  //	Mayenne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5325", "FR-B2");  //	Meurhe-et-Moselle
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5326", "FR-B2");  //	Meuse
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5327", "FR-A2");  //	Morbihan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5328", "FR-B2");  //	Moselle
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5329", "FR-A1");  //	Nievre
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5330", "FR-B4");  //	Nord
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5331", "FR-B6");  //	Oise
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5332", "FR-99");  //	Orne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5333", "FR-A8");  //	Paris
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5334", "FR-B4");  //	Pas-de-Calais
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5335", "FR-98");  //	Puy-de-Dome
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5336", "FR-97");  //	Pyranaes-Atlantiques
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5337", "FR-A9");  //	Pyranaes-Orientales
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5338", "FR-B9");  //	Rhone
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5339", "FR-A1");  //	Saone-et-Loire
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5340", "FR-B5");  //	Sarthe
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5341", "FR-B9");  //	Savoie
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5342", "FR-A8");  //	Seien-et-Marne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5343", "FR-A7");  //	Seine-Maritime
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5344", "FR-A8");  //	Seine-Saint-Denis
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5345", "FR-B6");  //	Somme
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5346", "FR-B3");  //	Tarn
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5347", "FR-B3");  //	Tarn-et-Garonne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5348", "FR-A6");  //	Territoire de Belfort
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5349", "FR-A8");  //	Val-d'Oise
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5350", "FR-A8");  //	Val-de-Marne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5351", "FR-B8");  //	Var
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5352", "FR-B8");  //	Vaucluse
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5353", "FR-B5");  //	Vendre
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5354", "FR-B7");  //	Vienne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5355", "FR-B2");  //	Vosges
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5356", "FR-A1");  //	Yonne
		staticMappingsFromAdmin1CodeToMaxMindCode.put("FRA-5357", "FR-A8");  // Yvelines



		// FI-01 - Aland - separate state with separate admin 1 regions
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-3137", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4810", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4811", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4812", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4813", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4814", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4815", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4816", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4817", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4818", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4819", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4820", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4821", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4822", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4823", "FI-01");
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ALD-4824", "FI-01");

		// Georgia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3015", "GE-02"); // Abkhazia
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3027", "GE-04"); // Ajaria
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3028", "GE-65"); // Guria
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3029", "GE-71"); // Samegrelo-Zemo Svaneti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3030", "GE-66"); // Imereti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3033", "GE-67"); // Kakheti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3034", "GE-69"); //  Mtskheta-Mtianeti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3035", "GE-70"); // Racha-Lechkhumi-Kvemo Svaneti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3036", "GE-51"); // Tbilisi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3037", "GE-68"); // Kvemo Kartli
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3038", "GE-72"); // Samtskhe-Javakheti
		staticMappingsFromAdmin1CodeToMaxMindCode.put("GEO-3039", "GE-73"); // Shida Kartli
		regionsThatAreMappedToOtherRegions.put("GE-01","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-02","GE-02");
		regionsThatAreMappedToOtherRegions.put("GE-03","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-04","GE-04");
		regionsThatAreMappedToOtherRegions.put("GE-05","GE-69");
		regionsThatAreMappedToOtherRegions.put("GE-06","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-07","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-08","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-09","GE-70");
		regionsThatAreMappedToOtherRegions.put("GE-10","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-11","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-12","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-13","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-14","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-15","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-16","GE-65");
		regionsThatAreMappedToOtherRegions.put("GE-17","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-18","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-19","GE-69");
		regionsThatAreMappedToOtherRegions.put("GE-20","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-21","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-22","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-23","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-24","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-25","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-26","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-27","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-28","GE-73");
		regionsThatAreMappedToOtherRegions.put("GE-29","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-30","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-31","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-32","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-33","GE-65");
		regionsThatAreMappedToOtherRegions.put("GE-34","GE-70");
		regionsThatAreMappedToOtherRegions.put("GE-35","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-36","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-37","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-38","GE-69");
		regionsThatAreMappedToOtherRegions.put("GE-39","GE-72");
		regionsThatAreMappedToOtherRegions.put("GE-40","GE-70");
		regionsThatAreMappedToOtherRegions.put("GE-41","GE-65");
		regionsThatAreMappedToOtherRegions.put("GE-42","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-43","GE-69");
		regionsThatAreMappedToOtherRegions.put("GE-44","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-45","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-46","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-47","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-48","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-49","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-50","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-51","GE-51");
		regionsThatAreMappedToOtherRegions.put("GE-52","GE-67");
		regionsThatAreMappedToOtherRegions.put("GE-53","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-54","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-55","GE-69");
		regionsThatAreMappedToOtherRegions.put("GE-56","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-57","GE-70");
		regionsThatAreMappedToOtherRegions.put("GE-58","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-59","GE-68");
		regionsThatAreMappedToOtherRegions.put("GE-60","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-61","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-62","GE-66");
		regionsThatAreMappedToOtherRegions.put("GE-63","GE-71");
		regionsThatAreMappedToOtherRegions.put("GE-64","GE-71");

		// Greece
		fixedFIPSCodesForNaturalEarth.put("GRC-2883", "GR57"); // Ionioi Nisoi
		fixedFIPSCodesForNaturalEarth.put("GRC-2884", "GR54"); // Attiki
		fixedFIPSCodesForNaturalEarth.put("GRC-2885", "GR62"); // Peloponnisos
		fixedFIPSCodesForNaturalEarth.put("GRC-2886", "GR55"); // Dytiki Ellada
		fixedFIPSCodesForNaturalEarth.put("GRC-2892", "GR56"); // Dytiki Makedonia
		fixedFIPSCodesForNaturalEarth.put("GRC-2900", "GR64"); // Thessalia
		fixedFIPSCodesForNaturalEarth.put("GRC-2949", "GR58"); // Ipeiros
		fixedFIPSCodesForNaturalEarth.put("GRC-2988", "GR65"); // Voreio Aigaio
		fixedFIPSCodesForNaturalEarth.put("GRC-2989", "GR63"); // Stereá Elláda
		fixedFIPSCodesForNaturalEarth.put("GRC-2990", "GR60"); // Kriti
		fixedFIPSCodesForNaturalEarth.put("GRC-2991", "GR59"); // Kentriki Makedonia
		fixedFIPSCodesForNaturalEarth.put("GRC-2992", "GR52"); // Ayion Oros
		fixedFIPSCodesForNaturalEarth.put("GRC-3001", "GR53"); // Anatoliki Makedonia kai Thraki
		fixedFIPSCodesForNaturalEarth.put("GRC-3013", "GR61"); // Notio Aigaio

		regionsThatAreMappedToOtherRegions.put("GR-01", "GR-53"); // Evros
		regionsThatAreMappedToOtherRegions.put("GR-02", "GR-53"); // Rodhopi
		regionsThatAreMappedToOtherRegions.put("GR-03", "GR-53"); // Xanthi
		regionsThatAreMappedToOtherRegions.put("GR-04", "GR-53"); // Drama
		regionsThatAreMappedToOtherRegions.put("GR-05", "GR-59"); // Serrai
		regionsThatAreMappedToOtherRegions.put("GR-06", "GR-59"); // Kilkis
		regionsThatAreMappedToOtherRegions.put("GR-07", "GR-59"); // Pella
		regionsThatAreMappedToOtherRegions.put("GR-08", "GR-56"); // Florina
		regionsThatAreMappedToOtherRegions.put("GR-09", "GR-56"); // Kastoria
		regionsThatAreMappedToOtherRegions.put("GR-10", "GR-56"); // Grevena
		regionsThatAreMappedToOtherRegions.put("GR-11", "GR-56"); // Kozani
		regionsThatAreMappedToOtherRegions.put("GR-12", "GR-59"); // Imathia
		regionsThatAreMappedToOtherRegions.put("GR-13", "GR-59"); // Thessaloniki
		regionsThatAreMappedToOtherRegions.put("GR-14", "GR-53"); // Kavala
		regionsThatAreMappedToOtherRegions.put("GR-15", "GR-59"); // Khalkidhiki
		regionsThatAreMappedToOtherRegions.put("GR-16", "GR-59"); // Pieria
		regionsThatAreMappedToOtherRegions.put("GR-17", "GR-58"); // Ioannina
		regionsThatAreMappedToOtherRegions.put("GR-18", "GR-58"); // Thesprotia
		regionsThatAreMappedToOtherRegions.put("GR-19", "GR-58"); // Preveza
		regionsThatAreMappedToOtherRegions.put("GR-20", "GR-58"); // Arta
		regionsThatAreMappedToOtherRegions.put("GR-21", "GR-64"); // Larisa
		regionsThatAreMappedToOtherRegions.put("GR-22", "GR-64"); // Trikala
		regionsThatAreMappedToOtherRegions.put("GR-23", "GR-64"); // Kardhitsa
		regionsThatAreMappedToOtherRegions.put("GR-24", "GR-64"); // Magnisia
		regionsThatAreMappedToOtherRegions.put("GR-25", "GR-57"); // Kerkira
		regionsThatAreMappedToOtherRegions.put("GR-26", "GR-57"); // Levkas
		regionsThatAreMappedToOtherRegions.put("GR-27", "GR-57"); // Kefallinia
		regionsThatAreMappedToOtherRegions.put("GR-28", "GR-57"); // Zakinthos
		regionsThatAreMappedToOtherRegions.put("GR-29", "GR-63"); // Fthiotis
		regionsThatAreMappedToOtherRegions.put("GR-30", "GR-63"); // Evritania
		regionsThatAreMappedToOtherRegions.put("GR-31", "GR-55"); // Aitolia kai Akarnania
		regionsThatAreMappedToOtherRegions.put("GR-32", "GR-63"); // Fokis
		regionsThatAreMappedToOtherRegions.put("GR-33", "GR-63"); // Voiotia
		regionsThatAreMappedToOtherRegions.put("GR-34", "GR-63"); // Evvoia
		regionsThatAreMappedToOtherRegions.put("GR-35", "GR-54"); // Attiki
		regionsThatAreMappedToOtherRegions.put("GR-36", "GR-62"); // Argolis
		regionsThatAreMappedToOtherRegions.put("GR-37", "GR-62"); // Korinthia
		regionsThatAreMappedToOtherRegions.put("GR-38", "GR-55"); // Akhaia
		regionsThatAreMappedToOtherRegions.put("GR-39", "GR-55"); // Ilia
		regionsThatAreMappedToOtherRegions.put("GR-40", "GR-62"); // Messinia
		regionsThatAreMappedToOtherRegions.put("GR-41", "GR-62"); // Arkadhia
		regionsThatAreMappedToOtherRegions.put("GR-42", "GR-62"); // Lakonia
		regionsThatAreMappedToOtherRegions.put("GR-43", "GR-60"); // Khania
		regionsThatAreMappedToOtherRegions.put("GR-44", "GR-60"); // Rethimni
		regionsThatAreMappedToOtherRegions.put("GR-45", "GR-60"); // Iraklion
		regionsThatAreMappedToOtherRegions.put("GR-46", "GR-60"); // Lasithi
		regionsThatAreMappedToOtherRegions.put("GR-47", "GR-63"); // Dhodhekanisos
		regionsThatAreMappedToOtherRegions.put("GR-48", "GR-65"); // Samos
		regionsThatAreMappedToOtherRegions.put("GR-49", "GR-61"); // Kikladhes
		regionsThatAreMappedToOtherRegions.put("GR-50", "GR-65"); // Khios
		regionsThatAreMappedToOtherRegions.put("GR-51", "GR-65"); // Lesvos

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

		// India
		fixedFIPSCodesForNaturalEarth.put("IND-3264", "IN09"); // Gujarat
		fixedFIPSCodesForNaturalEarth.put("IND-3263", "IN25"); // Tamil Nadu
		fixedFIPSCodesForNaturalEarth.put("IND-3262", "IN22"); // Puducherry
		fixedFIPSCodesForNaturalEarth.put("IND-3504", "IN32"); // Daman and Diu
		fixedFIPSCodesForNaturalEarth.put("IND-3265", "IN33"); // Goa

		// Ireland
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-5575", "IE-07"); // Dublin
		fixedFIPSCodesForNaturalEarth.put("IRL-3412", "EI22"); // Monaghan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-5572", "IE-26"); // Tipperary
		staticMappingsFromAdmin1CodeToMaxMindCode.put("IRL-724", "IE-26"); // Tipperary
		fixedFIPSCodesForNaturalEarth.put("IRL-1444", "EI27"); // Waterford
		fixedFIPSCodesForNaturalEarth.put("IRL-5569", "EI32"); // Cork

		// Italy - 107 provinces but we track regions
		duplicateFipsCodesThatAreOkay.add("IT01");
		duplicateFipsCodesThatAreOkay.add("IT02");
		duplicateFipsCodesThatAreOkay.add("IT03");
		duplicateFipsCodesThatAreOkay.add("IT04");
		duplicateFipsCodesThatAreOkay.add("IT05");
		duplicateFipsCodesThatAreOkay.add("IT06");
		duplicateFipsCodesThatAreOkay.add("IT07");
		duplicateFipsCodesThatAreOkay.add("IT08");
		duplicateFipsCodesThatAreOkay.add("IT09");
		duplicateFipsCodesThatAreOkay.add("IT10");
		duplicateFipsCodesThatAreOkay.add("IT11");
		duplicateFipsCodesThatAreOkay.add("IT12");
		duplicateFipsCodesThatAreOkay.add("IT13");
		duplicateFipsCodesThatAreOkay.add("IT14");
		duplicateFipsCodesThatAreOkay.add("IT15");
		duplicateFipsCodesThatAreOkay.add("IT16");
		duplicateFipsCodesThatAreOkay.add("IT17");
		duplicateFipsCodesThatAreOkay.add("IT18");
		duplicateFipsCodesThatAreOkay.add("IT19");
		duplicateFipsCodesThatAreOkay.add("IT20");

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

		// Laos
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3281", "LA-09"); // Saravan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3291", "LA-08"); // Phongsali
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3284", "LA-11"); // Vientiane
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3282", "LA-10"); // Savannakhet
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3293", "LA-04"); // Khammouan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("LAO-3272", "LA-05"); // Louang Namtha

		// Kuwait
		fixedFIPSCodesForNaturalEarth.put("KWT-1666", "KU09"); // Mubarak Al-Kabir
		fixedFIPSCodesForNaturalEarth.put("KWT-3506", "KU08"); // Hawalli
		fixedFIPSCodesForNaturalEarth.put("KWT-1664", "KU07"); // Al Farwaniyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("KWT-1665", "KW-01");

		// Kiribati
		regionsThatAreMappedToOtherRegions.put("KI-01", "KI");
		regionsThatAreMappedToOtherRegions.put("KI-02", "KI");
		regionsThatAreMappedToOtherRegions.put("KI-03", "KI");

		// Oman
		fixedFIPSCodesForNaturalEarth.put("OMN-2405", "MU03"); // Al Wusta
		fixedFIPSCodesForNaturalEarth.put("OMN-2404", "MU01"); // Ad Dakhiliyah
		fixedFIPSCodesForNaturalEarth.put("OMN-2411", "MU08"); // Zufar
		fixedFIPSCodesForNaturalEarth.put("OMN-2414", "MU09"); // Az Zahirah
		fixedFIPSCodesForNaturalEarth.put("OMN-4836", "MU10"); // Al Buraymi
		staticMappingsFromAdmin1CodeToMaxMindCode.put("OMN-2414", "OM-05"); // Az Zahirah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("OMN-5497", "OM-02"); // Al Batinah South
		staticMappingsFromAdmin1CodeToMaxMindCode.put("OMN-2424", "OM-02"); // Al Batinah North
		staticMappingsFromAdmin1CodeToMaxMindCode.put("OMN-2406", "OM-04"); // Al Sharqiyah South
		staticMappingsFromAdmin1CodeToMaxMindCode.put("OMN-2424", "OM-04"); // Al Sharqiyah North

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

		// Slovenia
		fixedFIPSCodesForNaturalEarth.put("SVN-939", "SI54"); // Krsko
		fixedFIPSCodesForNaturalEarth.put("SVN-198", "SIF9"); // Cerkvenjak
		fixedFIPSCodesForNaturalEarth.put("SVN-199", "SIF4"); // Benedikt
		fixedFIPSCodesForNaturalEarth.put("SVN-200", "SIM3"); // Sveta Ana
		fixedFIPSCodesForNaturalEarth.put("SVN-201", "SIH2"); // Hodoš
		fixedFIPSCodesForNaturalEarth.put("SVN-202", "SIG8"); // Grad
		fixedFIPSCodesForNaturalEarth.put("SVN-227", "SIH8"); // Komenda
		fixedFIPSCodesForNaturalEarth.put("SVN-228", "SIJ8"); // Oplotnica
		fixedFIPSCodesForNaturalEarth.put("SVN-229", "SIG9"); // Hajdina
		fixedFIPSCodesForNaturalEarth.put("SVN-230", "SIJ4"); // Miklavž na Dravskem polju
		fixedFIPSCodesForNaturalEarth.put("SVN-231", "SIH1"); // Hoce-Slivnica
		fixedFIPSCodesForNaturalEarth.put("SVN-232", "SIM7"); // Trnovska vas
		fixedFIPSCodesForNaturalEarth.put("SVN-233", "SIN6"); // Žetale
		fixedFIPSCodesForNaturalEarth.put("SVN-234", "SIK1"); // Podlehnik
		fixedFIPSCodesForNaturalEarth.put("SVN-235", "SIK6"); // Prevalje
		fixedFIPSCodesForNaturalEarth.put("SVN-236", "SIL5"); // Selnica ob Dravi
		fixedFIPSCodesForNaturalEarth.put("SVN-237", "SII8"); // Lovrenc na Pohorju
		fixedFIPSCodesForNaturalEarth.put("SVN-238", "SIM8"); // Trzin
		fixedFIPSCodesForNaturalEarth.put("SVN-239", "SIH5"); // Jezersko
		fixedFIPSCodesForNaturalEarth.put("SVN-240", "SIN8"); // Žužemberk
		fixedFIPSCodesForNaturalEarth.put("SVN-241", "SIG3"); // Dobrna
		fixedFIPSCodesForNaturalEarth.put("SVN-242", "SIM5"); // Tabor
		fixedFIPSCodesForNaturalEarth.put("SVN-243", "SIN4"); // Vransko
		fixedFIPSCodesForNaturalEarth.put("SVN-244", "SIJ6"); // Mirna Pec
		fixedFIPSCodesForNaturalEarth.put("SVN-245", "SIK4"); // Prebold
		fixedFIPSCodesForNaturalEarth.put("SVN-247", "SIK3"); // Polzela
		fixedFIPSCodesForNaturalEarth.put("SVN-248", "SIH9"); // Kostel
		fixedFIPSCodesForNaturalEarth.put("SVN-249", "SIF7"); // Braslovce
		fixedFIPSCodesForNaturalEarth.put("SVN-250", "SIM1"); // Sodražica
		fixedFIPSCodesForNaturalEarth.put("SVN-251", "SIG6"); // Dolenjske Toplice
		fixedFIPSCodesForNaturalEarth.put("SVN-252", "SIH3"); // Horjul
		fixedFIPSCodesForNaturalEarth.put("SVN-253", "SIM2"); // Solcava
		fixedFIPSCodesForNaturalEarth.put("SVN-254", "SIG2"); // Dobje
		fixedFIPSCodesForNaturalEarth.put("SVN-255", "SI20"); // Dobrepolje
		fixedFIPSCodesForNaturalEarth.put("SVN-259", "SIF6"); // Bloke
		fixedFIPSCodesForNaturalEarth.put("SVN-261", "SI92"); // Podcetrtek
		fixedFIPSCodesForNaturalEarth.put("SVN-266", "SIN7"); // Žirovnica
		fixedFIPSCodesForNaturalEarth.put("SVN-267", "SIL6"); // Šempeter-Vrtojba
		fixedFIPSCodesForNaturalEarth.put("SVN-268", "SII1"); // Križevci
		fixedFIPSCodesForNaturalEarth.put("SVN-269", "SIN1"); // Veržej
		fixedFIPSCodesForNaturalEarth.put("SVN-270", "SIM4"); // Sveti Andraž v Slovenskih Goricah
		fixedFIPSCodesForNaturalEarth.put("SVN-271", "SIK9"); // Razkrižje
		fixedFIPSCodesForNaturalEarth.put("SVN-272", "SIM9"); // Velika Polana
		fixedFIPSCodesForNaturalEarth.put("SVN-1022", "SIG5"); // Dobrovnik
		fixedFIPSCodesForNaturalEarth.put("SVN-1039", "SIJ3"); // Markovci

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

		// Dominican Republic
		fixedFIPSCodesForNaturalEarth.put("DOM-1978", "DR36"); // San Jose de Ocoa
		fixedFIPSCodesForNaturalEarth.put("DOM-1394", "DR37"); // Santo Domingo
		fixedFIPSCodesForNaturalEarth.put("DOM-1971", "DR09"); // Independencia
		fixedFIPSCodesForNaturalEarth.put("DOM-1968", "DR19"); // Salcedo
		regionsThatAreMappedToOtherRegions.put("DO-17", "DO-35"); // Peravia
		regionsThatAreMappedToOtherRegions.put("DO-05","DO-34"); // Distrito Nacional

		// Lebanon
		fixedFIPSCodesForNaturalEarth.put("LBN-3022","LE08"); // Beqaa
		fixedFIPSCodesForNaturalEarth.put("LBN-3060","LE06"); // Liban-Sud
		fixedFIPSCodesForNaturalEarth.put("LBN-3023", "LE09"); // Liban-Nord
		regionsThatAreMappedToOtherRegions.put("LB-03", "LB-09"); // Liban-Nord
		regionsThatAreMappedToOtherRegions.put("LB-01", "LB-08"); //Beqaa
		regionsThatAreMappedToOtherRegions.put("LB-02", "LE-06"); // Al Janub
		unmappableRegions.add ("LB-10"); // Aakk,r
		unmappableRegions.add ("LB-11"); // Baalbek-Hermel

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

		// Marocco
		fixedFIPSCodesForNaturalEarth.put("MAR-1449", "MO50"); // Chaouia-Ouardigha
		fixedFIPSCodesForNaturalEarth.put("MAR-1457", "MO51"); // Doukkala-Abda
		fixedFIPSCodesForNaturalEarth.put("MAR-1448", "MO52"); // Gharb-Chrarda-Beni Hssen
		fixedFIPSCodesForNaturalEarth.put("MAR-3457", "MO53"); // Guelmim-Es Smara
		fixedFIPSCodesForNaturalEarth.put("MAR-1450", "MO45"); // Grand Casablanca
		fixedFIPSCodesForNaturalEarth.put("MAR-1446", "MO46"); // Fes-Boulemane

		// Mauritius
		fixedFIPSCodesForNaturalEarth.put("MUS-5180", "MP21"); // Agaléga
		fixedFIPSCodesForNaturalEarth.put("MUS-5181", "MP02"); // Beau Bassin-Rose Hill
		fixedFIPSCodesForNaturalEarth.put("MUS-5182", "MP22"); // Cargados Carajos Shoals
		fixedFIPSCodesForNaturalEarth.put("MUS-5183", "MP04"); // Curepipe
		fixedFIPSCodesForNaturalEarth.put("MUS-5184", "MP13"); // Flacq
		fixedFIPSCodesForNaturalEarth.put("MUS-5185", "MP14"); // Grand Port
		fixedFIPSCodesForNaturalEarth.put("MUS-5186", "MP15"); // Moka
		fixedFIPSCodesForNaturalEarth.put("MUS-5187", "MP16"); // Pamplemousses
		fixedFIPSCodesForNaturalEarth.put("MUS-5188", "MP17"); // Plaines Wilhems
		fixedFIPSCodesForNaturalEarth.put("MUS-5189", "MP18"); // Port Louis
		fixedFIPSCodesForNaturalEarth.put("MUS-5190", "MP07"); // Port Louis city
		fixedFIPSCodesForNaturalEarth.put("MUS-5191", "MP08"); // Quatre Bornes
		fixedFIPSCodesForNaturalEarth.put("MUS-5192", "MP19"); // Rivière du Rempart
		fixedFIPSCodesForNaturalEarth.put("MUS-5193", "MP12"); // Rivière Noire
		fixedFIPSCodesForNaturalEarth.put("MUS-5194", "MP23"); // Rodrigues
		fixedFIPSCodesForNaturalEarth.put("MUS-5195", "MP20"); // Savanne
		fixedFIPSCodesForNaturalEarth.put("MUS-5196", "MP11"); // Vacoas-Phoenix

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

		// North Korea
		fixedFIPSCodesForNaturalEarth.put("PRK-3313", "KN15"); // P'yongan-namdo
		fixedFIPSCodesForNaturalEarth.put("PRK-5495", "KN18"); // Najin Sonbong-si
		fixedFIPSCodesForNaturalEarth.put("PRK-3307", "KN17"); // Hamgyong-bukto
		regionsThatAreMappedToOtherRegions.put("KP-14", "KP-15");// Namp'o-si
		regionsThatAreMappedToOtherRegions.put("KP-08", "KP-07");// Kaesong-si

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

		// Mokdova
		fixedFIPSCodesForNaturalEarth.put("MDA-1624", "MD64"); // Cahul
		fixedFIPSCodesForNaturalEarth.put("MDA-1626", "MD51"); // Gagauzia
		fixedFIPSCodesForNaturalEarth.put("MDA-1628", "MD58"); // Stinga Nistrului
		fixedFIPSCodesForNaturalEarth.put("MDA-1647", "MD89"); // Straseni
		fixedFIPSCodesForNaturalEarth.put("MDA-1646", "MD86"); // Soldanesti
		fixedFIPSCodesForNaturalEarth.put("MDA-1653", "MD87"); // Soroca
		duplicateFipsCodesThatAreOkay.add("MD83"); // Rezina

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

		// Somalia
//		SO-02 - Banaadir
//		SO-11 - Nugaal
//		SO-12 - Sanaag
//		SO-14 - Shabeellaha Hoose
//		SO-16 - Woqooyi Galbeed
//		SO-19 - Togdheer
//		SO-22 - Sool
//		SO-21 - Awdal
//		SO-20 - Woqooyi Galbeed

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

		// Trinidad & Tobago
		fixedFIPSCodesForNaturalEarth.put("TTO-50", "TD11"); // Eastern Tobago
		fixedFIPSCodesForNaturalEarth.put("TTO-51", "TD15"); // Diego Martin
		fixedFIPSCodesForNaturalEarth.put("TTO-52", "TD22"); // Siparia
		fixedFIPSCodesForNaturalEarth.put("TTO-53", "TD01"); // Arima
		fixedFIPSCodesForNaturalEarth.put("TTO-54", "TD21"); // Sangre Grande
		fixedFIPSCodesForNaturalEarth.put("TTO-55", "TD16"); // Rio Claro-Mayaro
		fixedFIPSCodesForNaturalEarth.put("TTO-56", "TD20"); // San Juan-Laventille
		fixedFIPSCodesForNaturalEarth.put("TTO-57", "TD19"); // Princes Town
		fixedFIPSCodesForNaturalEarth.put("TTO-58", "TD14"); // Couva-Tabaquite-Talparo
		fixedFIPSCodesForNaturalEarth.put("TTO-59", "TD17"); // Penal-Debe
		fixedFIPSCodesForNaturalEarth.put("TTO-60", "TD18"); // Point Fortin
		fixedFIPSCodesForNaturalEarth.put("TTO-61", "TD23"); // Tunapuna/Piarco
		fixedFIPSCodesForNaturalEarth.put("TTO-62", "TD10"); // San Fernando
		fixedFIPSCodesForNaturalEarth.put("TTO-63", "TD13"); // Chaguanas
		fixedFIPSCodesForNaturalEarth.put("TTO-64", "TD05"); // Port of Spain
		fixedFIPSCodesForNaturalEarth.put("TTO-5090", "TD11"); // Western Tobago

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

		// Yemen
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-157", "YE-06"); // Lahij
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-335", "YE-09"); // Al Jawf
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-333", "YE-07"); // Al Bayda'
		fixedFIPSCodesForNaturalEarth.put("YEM-155", "YM19"); // Amran
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-334", "YE-18"); // Ad Dali
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-156", "YE-13"); // Ibb
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-154", "YE-12"); // Hajjah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("YEM-158", "YE-17"); // Taizz

		// Zambia
		fixedFIPSCodesForNaturalEarth.put("ZMB-520", "ZA09"); // Lusaka
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ZMB-516","ZM-02"); // Central
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ZMB-5511", "ZM-05"); // Muchinga
	}
}
