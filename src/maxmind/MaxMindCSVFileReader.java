package maxmind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;

public class MaxMindCSVFileReader {

	private static final String MAXMIND_COUNTRIES = "resources/maxmind-countries.csv";
	private static final String MAXMIND_REGIONS = "resources/maxmind-regions.csv";
	private static final String MAXMIND_COUNTRIES_TO_CONTINENTS = "resources/maxmind-countries-to-continents.csv";
	private static final String FIPS_TO_ISO_COUNTRY_CODE_MAPPING = "resources/fips10-4_to_iso3166_countrycodes.csv";

	private static Map<String, Region> countries = null;
	private static Map<String, Region> regions = null;

	private static Map<String, String> countryIdToContinentIdMap = null;
	private static Map<String, String> isoCountryCodeToFipsCountryCode = null;



	public static Map<String, String> readMaxmindCountryToContinentsMappings() throws IOException
	{
		countryIdToContinentIdMap = new HashMap<String, String>();

		File file = new File(MAXMIND_COUNTRIES_TO_CONTINENTS);
		BufferedReader fileReader = new BufferedReader(new FileReader(file));

		String currentlyProcessedline = null;

		do {
			currentlyProcessedline = fileReader.readLine();
			if (currentlyProcessedline != null) {
				// Format of a line
				// A1,--
				// A2,--
				// AD,EU
				// AE,AS

				// To check if a line is valid
				//   - the first two characters need to be letters in the range from A-Z (upper case), with the exceptions of A1,A2 and O1
				//   - the third characters needs to be a comma (,)
				//   - the fourth fifth characters need to be one of the entries in maxmindContinentIdToCompuwareContinentIdMap
				assert currentlyProcessedline.length() == 5;
				String countryCode = currentlyProcessedline.substring(0, 2);
				if (countryCode.equals("A1") || countryCode.equals("A2") || countryCode.equals("O1")) {
					// special cases, we know that they are there and that the are okay so we skip the checks
				} else {
					assert isAToZ(currentlyProcessedline.charAt(0));
					assert isAToZ(currentlyProcessedline.charAt(1));
				}
				assert currentlyProcessedline.charAt(2) == ',';
				String continentCode = currentlyProcessedline.substring(3);
				// assert maxmindContinentIdToCompuwareContinentIdMap.containsKey(continentCode);

				// add entry to country to continent id map
				countryIdToContinentIdMap.put(countryCode, continentCode);
			}
		} while (currentlyProcessedline != null);
		fileReader.close();

		return countryIdToContinentIdMap;
	}

	/**
	 * Reads the country ids (iso3266 as used by maxmind) from a csv file and returns it as
	 * a map. The keys of the map are the country ids and the entries the country names.
	 *
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Region> readMaxmindCountries() throws IOException
	{

		if (countryIdToContinentIdMap == null) {
			readMaxmindCountryToContinentsMappings();
		}
		countries = new HashMap<String, Region>();

		File file = new File(MAXMIND_COUNTRIES);
		BufferedReader fileReader = new BufferedReader(new FileReader(file));

		Object replaced = null;
		String currentlyProcessedline = null;
		do {
			currentlyProcessedline = fileReader.readLine();
			if (currentlyProcessedline != null) {

				// Format of a line
				// AT,"Austria"
				// DE,"Germany"
				//
				// But there are also case with a comma in the name, e.g.
				// BQ,"Bonaire, Saint Eustatius and Saba"
				// CD,"Congo, The Democratic Republic of the"
				//
				// To check if a line is valid
				//   - the first two characters need to be letters in the range from A-Z (upper case), with the exceptions of A1,A2 and O1
				//   - the third characters needs to be a comma (,)
				//   - the fourth and the last characters need to be quotation marks (")
				//
				// However there are also a few special cases that does not match that pattern
				//   A1,"Anonymous Proxy"
				//   A2,"Satellite Provider"
				//   O1,"Other Country"

				assert currentlyProcessedline.length() > 2;
				String countryCode = currentlyProcessedline.substring(0, 2);
				if (countryCode.equals("A1") || countryCode.equals("A2") || countryCode.equals("O1")) {
					// special cases, we know that they are there ant that the are okay so we skip the checks
				} else {
					assert isAToZ(currentlyProcessedline.charAt(0));
					assert isAToZ(currentlyProcessedline.charAt(1));
				}
				assert currentlyProcessedline.charAt(2) == ',';
				assert currentlyProcessedline.charAt(3) == '"';
				assert currentlyProcessedline.charAt(currentlyProcessedline.length() - 1) == '"';

				// TODO: temporary shortcut to speed things up - remove
				if (!getContinentCode(countryCode).equals("A")) {
					continue;
				}
				if (!countryCode.equals("SD")) {
					continue;
				}

				// Read the name from the line
				String countryName  = currentlyProcessedline.substring(3);

				// remove the quotation marks from the country name
				countryName = countryName.replace("\"", "");

				// create region object for the country
				Region country = new Region(countryCode, countryName, RegionType.COUNTRY, getContinentCode(countryCode));
				/* country.setMappable(!
					(unmappableRegions.contains(countryCode) || remappedRegionIds.containsKey(countryCode))
				); */

				replaced = countries.put(countryCode, country);
				// check if there was no previous entry with this id, otherwise fail
				assert replaced == null;

				// check if all values are correctly set
				assert country.getCode() != null && !country.getCode().isEmpty();
				assert country.getCountryCode() != null && !country.getCountryCode().isEmpty();
				assert country.getContinentCode() != null && !country.getContinentCode().isEmpty();
				assert country.getName() != null && !country.getName().isEmpty();

				// System.out.println("Successfully processed Country " + countryCode + " (line " + (++lineNumber) + ")");
			}

		} while (currentlyProcessedline != null);
		fileReader.close();

		return countries;
	}

	/**
	 * Reads the country ids (iso3266 as used by maxmind) from a csv file and returns it as
	 * a map. The keys of the map are the country ids and the entries the country names.
	 *
	 * @return
	 * @throws IOException
	 */
	public static Map <String, Region> readMaxmindRegions() throws IOException
	{
		if (countryIdToContinentIdMap == null) {
			readMaxmindCountryToContinentsMappings();
		}

		regions = new HashMap<String, Region>();

		File file = new File(MAXMIND_REGIONS);
		BufferedReader fileReader = new BufferedReader(new FileReader(file));

		Object replaced = null;
		String currentlyProcessedline = null;
		do {
			currentlyProcessedline = fileReader.readLine();
			if (currentlyProcessedline != null) {

				// Format of a line:
				// AT,01,"Burgenland"
				// AT,02,"Karnten"
				//
				// But there are also lines with a comma in the name
				// US,AE,"Armed Forces Europe, Middle East, & Canada"

				// To check if a line is valid
				//   - the first two characters need to be letters in the range from A-Z (upper case)
				//   - the third characters needs to be a comma (,)
				//   - the fourth and fifth characters need to be
				//       - letters in the case of CA and US
				//       - letters or numbers for the other countries
				//   - the sixth character needs to be a comma (,)
				//   - the seventh and the last characters need to be quotation marks (")

				assert currentlyProcessedline.length() > 8;
				assert isAToZ(currentlyProcessedline.charAt(0));
				assert isAToZ(currentlyProcessedline.charAt(1));
				assert currentlyProcessedline.charAt(2) == ',';

				// the fourth and fifth characters need to be
				String countryCode = currentlyProcessedline.substring(0,2);

				// TODO: temporary shortcut to speed things up - remove
				/*if (!countryCode.equals("DE")) {
					continue;
				}*/
				if (countryCode.equals("US") || countryCode.equals("CA")) {
					// letters in the case of CA and US
					assert isAToZ(currentlyProcessedline.charAt(3));
					assert isAToZ(currentlyProcessedline.charAt(4));
				} else {
					// letters or numbers for the other countries
					assert isAToZ(currentlyProcessedline.charAt(3)) || Character.isDigit(currentlyProcessedline.charAt(3));
					assert isAToZ(currentlyProcessedline.charAt(4)) || Character.isDigit(currentlyProcessedline.charAt(4));
				}

				assert currentlyProcessedline.charAt(5) == ',';
				assert currentlyProcessedline.charAt(6) == '"';
				assert currentlyProcessedline.charAt(currentlyProcessedline.length() - 1) == '"';


				// Read the values from the currently processed line
				String regionCode  = currentlyProcessedline.substring(3,5);
				String regionName  = currentlyProcessedline.substring(6);

				// remove the quotation marks from the region name
				regionName = regionName.replace("\"", "");

				String uniqueRegionCode = createUniqueRegionCode(countryCode, regionCode);
				// remapped regions are displayed in another region
				/* if (remappedRegionIds.containsKey(uniqueRegionCode)) {
					continue;
				} */
				Region region = new Region(uniqueRegionCode, regionName, RegionType.REGION, countryCode, getContinentCode(countryCode));
				/* region.setMappable(!
						(unmappableRegions.contains(uniqueRegionCode) || remappedRegionIds.containsKey(uniqueRegionCode))
				);*/

				/* if (specialCasesWhereRegionsAreThreatedAsCountries.containsKey(uniqueRegionCode)) {
					region.setType(RegionType.COUNTRY);
					region.setCountryCode(countryCode);
					region.setCode();
					region.setContinentCode(continentCode);
				} */

				replaced = regions.put(uniqueRegionCode, region);
				// check if there was no previous entry with this id, otherwise fail
				assert replaced == null;

				// check if all values are correctly set
				assert region.getCode() != null && !region.getCode().isEmpty();
				assert region.getCountryCode() != null && !region.getCountryCode().isEmpty();
				assert region.getContinentCode() != null && !region.getContinentCode().isEmpty();
				assert region.getName() != null && !region.getName().isEmpty();

				// System.out.println("Successfully processed Region " + uniqueRegionCode + " (line " + (++lineNumber) + ")");
			}

		} while (currentlyProcessedline != null);
		fileReader.close();



		return regions;
	}



	public static Map<String, String> getIsoCountryCodeToFIPSCountryCodeMapping() throws IOException
	{

		isoCountryCodeToFipsCountryCode = new HashMap<String, String>();

		File file = new File(FIPS_TO_ISO_COUNTRY_CODE_MAPPING);
		BufferedReader fileReader = new BufferedReader(new FileReader(file));

		String currentlyProcessedline = null;
		do {
			currentlyProcessedline = fileReader.readLine();
			if (currentlyProcessedline != null) {

				// Format of a line:
				// AF;AF;;
				// AL;AL;;
				// But there are also lines that divert from this
				// ;BQ;;AN(2060)
				// #NT;AN;	# ISO 3166-1 Change Notice V.8, 12/15/2010 drops entry Netherlands Antilles

				// We only process lines we deem valid and skip all other
				// Valid is defined as
				//  - First 2 characters are uppercase letters
				//  - Third character is a semicolon
				//  - Next 2 characters are uppercase letters
				if (currentlyProcessedline.length() >= 5
						&& isAToZ(currentlyProcessedline.charAt(0))
						&& isAToZ(currentlyProcessedline.charAt(1))
						&& isAToZ(currentlyProcessedline.charAt(3))
						&& isAToZ(currentlyProcessedline.charAt(4))
						&& currentlyProcessedline.charAt(2) == ';')
				{
					String fipsCode = currentlyProcessedline.substring(0,2);
					String isoCode = currentlyProcessedline.substring(3,5);
					isoCountryCodeToFipsCountryCode.put(isoCode, fipsCode);
				}

			}
		} while (currentlyProcessedline != null);
		fileReader.close();

		return isoCountryCodeToFipsCountryCode;
	}

	private static boolean isAToZ(char character)
	{
		int intValueOfCharacter = character;
		int intValueOfA = 'A';
		int intValueOfZ = 'Z';

		return (intValueOfA <= intValueOfCharacter && intValueOfCharacter <= intValueOfZ);
	}

	private static String getContinentCode(String countryCode)
	{
		return countryIdToContinentIdMap.get(countryCode);
	}

	private static String createUniqueRegionCode(String countryCode, String regionCode)
	{
		return countryCode + "-" + regionCode;
	}
}
