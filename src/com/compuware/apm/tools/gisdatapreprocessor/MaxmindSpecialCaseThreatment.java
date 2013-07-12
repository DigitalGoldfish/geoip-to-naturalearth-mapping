package com.compuware.apm.tools.gisdatapreprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxmindSpecialCaseThreatment {

	public static final List<String> countriesWithoutRegions = new ArrayList<String>();
	public static final List<String> regionsThatAreIgnored = new ArrayList<String>();
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

		// Algeria
		//  - AG48 appeared 2 times
		fixedFIPSCodesForNaturalEarth.put("DZA-2217", "AG04"); // Constantine

		// Sudan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-880", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-875", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-879", "SD-27"); // Al Wusta
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-874", "SD-29"); // Al Khartum
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-878", "SD-30"); // Ash Shamaliyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-872", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-873", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-147", "SD-31"); // Ash Sharqiyah
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-881", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-811", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-797", "SD-33"); // Darfur
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-882", "SD-34"); // Kurdufan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("SDN-883", "SD-34"); // Kurdufan

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

		// Canada
		regionsThatAreIgnored.add("CAN+99?");

		// Philippines
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5541", "PH-B9"); // Dagupan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5528", "PH-C3"); // Davao City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5526", "PH-C9"); // Iloilo City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5530", "PH-C8"); // Iligan
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5524", "PH-D4"); // Lapu-Lapu
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5560", "PH-D7"); // Lucena
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5549", "PH-H2"); // Quezon / PH-F2 - Quezon City
		staticMappingsFromAdmin1CodeToMaxMindCode.put("PHL-5535", "PH-G1"); // Tacloban
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

		// Zambia
		fixedFIPSCodesForNaturalEarth.put("ZMB-520", "ZM09"); // Lusaka
		staticMappingsFromAdmin1CodeToMaxMindCode.put("ZMB-5511", "ZM-05"); // Muchinga
	}
}
