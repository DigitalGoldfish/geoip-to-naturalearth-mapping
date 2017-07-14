package at.localhost.vectorworldmap.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.FactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import at.localhost.vectorworldmap.util.MakeFolder;
import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;
import at.localhost.vectorworldmap.util.SHPFileUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

public class ShapeFileGenerator {

	// pathing modified to allow reading in shp files from nonn project folder

//    private static final String OUTPUT_PATH = "d:/GitHub/map/input/";
    private static final String OUTPUT_PATH = "C:/Node/Data/shapefiles-new/";

//    private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
//    private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind1386079801077.shp";
//    private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind.shp";
//    private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";


//    private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "C:/Jakob/geojsongenerator/resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
//  private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind1386079801077.shp";
//  private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind.shp";
//  private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "C:/Jakob/geojsongenerator/resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";


    // Paul changed to ...
	private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "C:/Node/Data/shp/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
//	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "C:/Node/Data/shp/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";

	// this shape file has the larger French regions
//	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v20/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";

	private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/20170524/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp";


	// old instances of the shp files had attributes in lower case
	// Flag to allow us to run either as new format or old format
	private static boolean convertToUpperCase = true;
//    private static List<Geometry> BRE = new ArrayList<Geometry>();;			// Brittany
//    private static Region breRegion = null;
    private static Map<String, Region> regionMap = new HashMap< String, Region>();
    private static Map<String, Region> regionQueue = new HashMap< String, Region>();

    private static SimpleFeatureSource countryFeatureSource;
    private static SimpleFeatureSource regionFeatureSource;

    private static SimpleFeatureType outputFeatureType;

    private static final Map<String, String> continentNameToContinentCode = new HashMap<String, String>();
    private static final Map<String, String> continentCodeToContinentName = new HashMap<String, String>();

    private static final Map<String, String> subregionNameToSubregionCode = new HashMap<String, String>();
    private static final Map<String, String> subregionCodeToSubregionName = new HashMap<String, String>();

    // map of French regions to remove from the shp file. these regions are French terittories the other side of the world and would cause the
    // FR.shp file to try and generate a rectangle over the whole world rather than just france
    private static final Map<String, String> regionsToIgnore = new HashMap<String, String>();
    private static final Map<String, String> regionsToMerge = new HashMap<String, String>();


    private static final Map<String, List<Region>> subregionsByContinents = new HashMap<String, List<Region>>();
    private static final Map<String, List<Region>> countriesByContinents = new HashMap<String, List<Region>>();
    private static final Map<String, List<Region>> countriesBySubregions = new HashMap<String, List<Region>>();
    private static final Map<String, List<Region>> regionsByCountry   = new HashMap<String, List<Region>>();
    private static final Map<String, Region> countries   = new HashMap<String, Region>();
    private static final Map<String, Region> continents = new HashMap<String, Region>();
    private static final Map<String, Region> subregions = new HashMap<String, Region>();

    private static final Map<String, String> subregionToContinent = new HashMap<String, String>();

    private static final List<String> attributesToCopy = new ArrayList<String>();

    private static SimpleFeatureType featureType = null;
    static {
        attributesToCopy.add("the_geom");
        attributesToCopy.add(UCase("iso_a2"));
        attributesToCopy.add(UCase("name"));
        attributesToCopy.add(UCase("type"));
        // added additional attributes to include in the shp file so that we could (if we want to) use the output shp file as input to GeoJSONGenerator
//        attributesToCopy.add(UCase( "continent") );
//        attributesToCopy.add(UCase( "subregion") );

    }


    // Regions to merge for spain
    // The original topo json file needed the following changes
    // Castila y Leon - add Avila and Segovia, remove Alava
    // Castilla-La Macha - remove Avila
    // Madrid - remove Segvia
    // Pais Vasco (Basque) - add Alava
    //
    // Existing ES.json uses ISO Codes
    //   ISO   FIPS    Names
    //   AN    51      Andalucia
    //   AR    52      Aragon
    //   AS    34      Asturias
    //   CB    39      Cantabria
    //   CT    9       Catalonia
    //   CL    55      Castilla y Leon
    //   CM    54      Castilla-La Mancha
    //   CN    53      Canarias
    //   EX    57      Extremadura
    //   GA    58      Galacia
    //   IB    07      Islas Baleares
    //   MC    31      Murcia
    //   MD    29      Madrid
    //   NC    32      Navarra
    //   PV    59      Pais Vasco, Euskal Autonomia Erkidegoa
    //   RI    27      La Rioja
    //   VC    60      Comunidad Valenciana
    //
    //
    static {
	    regionsToMerge.put( "ESP-5801", "ES@ES-PV@Pais Vasco"); 				// Ãlava
	    regionsToMerge.put( "ESP-5802", "ES@ES-CM@Castilla-La Mancha"); 		// Albacete
	    regionsToMerge.put( "ESP-5803", "ES@ES-VC@Comunidad Valenciana"); 		// Alicante
	    regionsToMerge.put( "ESP-5804", "ES@ES-AN@Andalucia"); 					// Almeria
	    regionsToMerge.put( "ESP-5805", "ES@ES-AS@Asturias"); 					// Asturias
	    regionsToMerge.put( "ESP-5806", "ES@ES-CL@Castilla y Leon");			// Ãvila
	    regionsToMerge.put( "ESP-5807", "ES@ES-EX@Extremadura"); 				// Badajoz
	    regionsToMerge.put( "ESP-5808", "ES@ES-IB@Islas Baleares"); 			// Baleares
	    regionsToMerge.put( "ESP-5809", "ES@ES-CT@Catalonia"); 					// Barcelona
	    regionsToMerge.put( "ESP-5810", "ES@ES-CL@Castilla y Leon"); 			// Burgos
	    regionsToMerge.put( "ESP-5811", "ES@ES-EX@Extremadura"); 				// Caceres
	    regionsToMerge.put( "ESP-5812", "ES@ES-AN@Andalucia"); 					// Cadiz
	    regionsToMerge.put( "ESP-5813", "ES@ES-CB@Cantabria"); 					// Cantabria
	    regionsToMerge.put( "ESP-5814", "ES@ES-VC@Comunidad Valenciana"); 		// Castellon
	    regionsToMerge.put( "ESP-5816", "ES@ES-CM@Castilla-La Mancha"); 		// Ciudad Real
	    regionsToMerge.put( "ESP-5817", "ES@ES-AN@Andalucia"); 					// Cordoba
	    regionsToMerge.put( "ESP-5818", "ES@ES-CM@Castilla-La Mancha"); 		// Cuenca
	    regionsToMerge.put( "ESP-5820", "ES@ES-CT@Catalonia"); 					// Gerona
	    regionsToMerge.put( "ESP-5821", "ES@ES-AN@Andalucia"); 					// Granada
	    regionsToMerge.put( "ESP-5822", "ES@ES-CM@Castilla-La Mancha");			// Guadalajara
	    regionsToMerge.put( "ESP-5823", "ES@ES-PV@Pais Vasco"); 				// Gipuzkoa     AKA Gupuzcoa
	    regionsToMerge.put( "ESP-5824", "ES@ES-AN@Andalucia"); 					// Huelva
	    regionsToMerge.put( "ESP-5825", "ES@ES-AR@Aragon"); 					// Huesca
	    regionsToMerge.put( "ESP-5826", "ES@ES-AN@Andalucia"); 					// Jaen
	    regionsToMerge.put( "ESP-5827", "ES@ES-GA@Galacia"); 					// La Coruna
	    regionsToMerge.put( "ESP-5828", "ES@ES-RI@La Rioja"); 					// La Rioja
	    regionsToMerge.put( "ESP-5829", "ES@ES-CN@Canarias"); 					// Las Palmas
	    regionsToMerge.put( "ESP-5830", "ES@ES-CL@Castilla y Leon"); 			// Leon
	    regionsToMerge.put( "ESP-5831", "ES@ES-CT@Catalonia"); 					// Lerida
	    regionsToMerge.put( "ESP-5832", "ES@ES-GA@Galacia"); 					// Lugo
	    regionsToMerge.put( "ESP-5833", "ES@ES-MD@Madrid"); 					// Madrid
	    regionsToMerge.put( "ESP-5834", "ES@ES-AN@Andalucia"); 					// Malaga
	    regionsToMerge.put( "ESP-5836", "ES@ES-MC@Murcia"); 					// Murcia
	    regionsToMerge.put( "ESP-5837", "ES@ES-NC@Navarra"); 					// Navarra
	    regionsToMerge.put( "ESP-5838", "ES@ES-GA@Galacia"); 					// Orense
	    regionsToMerge.put( "ESP-5839", "ES@ES-CL@Castilla y Leon"); 			// Palencia
	    regionsToMerge.put( "ESP-5840", "ES@ES-GA@Galacia"); 					// Pontevedra
	    regionsToMerge.put( "ESP-5841", "ES@ES-CL@Castilla y Leon"); 			// Salamanca
	    regionsToMerge.put( "ESP-5843", "ES@ES-CL@Castilla y Leon"); 			// Segovia
	    regionsToMerge.put( "ESP-5844", "ES@ES-AN@Andalucia"); 					// Sevilla
	    regionsToMerge.put( "ESP-5845", "ES@ES-CL@Castilla y Leon"); 			// Soria
	    regionsToMerge.put( "ESP-5846", "ES@ES-CT@Catalonia"); 					// Tarragona
	    regionsToMerge.put( "ESP-5847", "ES@ES-AR@Aragon"); 					// Teruel
	    regionsToMerge.put( "ESP-5848", "ES@ES-CM@Castilla-La Mancha"); 		// Toledo
	    regionsToMerge.put( "ESP-5849", "ES@ES-VC@Comunidad Valenciana");		// Valencia
	    regionsToMerge.put( "ESP-5850", "ES@ES-CL@Castilla y Leon"); 			// Valladolid
	    regionsToMerge.put( "ESP-5851", "ES@ES-PV@Pais Vasco"); 				// Bizkaia			AKA Biscay, Vizcaya
	    regionsToMerge.put( "ESP-5852", "ES@ES-CL@Castilla y Leon"); 			// Zamora
	    regionsToMerge.put( "ESP-5853", "ES@ES-AR@Aragon"); 					// Zaragoza

// Not processed
//	    regionsToMerge.put( "ESP-5815", "ES@ES-@"); // Ceuta
//	    regionsToMerge.put( "ESP-5835", "ES@ES-@"); // Melilla
//	    regionsToMerge.put( "ESP-5842", "ES@ES-@"); // Santa Cruz de Tenerife

    }

    /**
     * List of French regions to not include when creating the French SHP file
     */
    static {
    	// French regions that are outside Europe
    	regionsToIgnore.put( "FRA-1442", "Martinique" );
    	regionsToIgnore.put( "FRA-2000", "Guyane franÃ§aise" );
    	regionsToIgnore.put( "FRA-4601", "La RÃ©union" );
    	regionsToIgnore.put( "FRA-4602", "Mayotte" );
    	regionsToIgnore.put( "FRA-4603", "Guadeloupe" );

    	// Irish region that doesn't makes sense!
    	regionsToIgnore.put( "IRL+99?", "???" );

    	// Spanish region that doesnt make sence
    	regionsToIgnore.put( "ESP+99?", 	"???"); //
    	regionsToIgnore.put( "ESP-5815", 	"???"); // Ceuta
    	regionsToIgnore.put( "ESP-5835", 	"???"); // Melilla
    	regionsToIgnore.put( "ESP-5842", 	"???"); // Santa Cruz de Tenerife

    }

    // List of French regions for merging when using the ne_10m_admin_1_states_provinces_shp.shp *** V20 **** shape file
    // IE private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v20/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_shp.shp";
    static {
    	// region code, countrycode hyphen new name of the combined region
    	// so you could combine regions in different countries as we key on the counry code
    	regionsToMerge.put( "FRA-2670", "FR@FR-ARA@Auvergne-Rhone-Alpes");		// FRA-2670 = Auvergne in France, Auvergne will be the new name of the combined region
    	regionsToMerge.put( "FRA-1265", "FR@FR-ARA@Auvergne-Rhone-Alpes");		// FRA-1265 = Rhône-Alpes

    	regionsToMerge.put( "FRA-2669", "FR@FR-OCC@Occitanie");		// [FRA-2669], name [Midi-Pyrénées]
    	regionsToMerge.put( "FRA-2668", "FR@FR-OCC@Occitanie");		//[FRA-2668], name [Languedoc-Roussillon]

    	regionsToMerge.put( "FRA-2665", "FR@FR-NAQ@Nouvelle-Aquitaine");		//[FRA-2665], name [Aquitaine]
    	regionsToMerge.put( "FRA-2663", "FR@FR-NAQ@Nouvelle-Aquitaine");		//[FRA-2663], name [Poitou-Charentes]
    	regionsToMerge.put( "FRA-2681", "FR@FR-NAQ@Nouvelle-Aquitaine");		//[FRA-2681], name [Limousin]

    	regionsToMerge.put( "FRA-2661", "FR@FR-NOR@Normandy");		//[FRA-2661], name [Basse-Normandie]
    	regionsToMerge.put( "FRA-2673", "FR@FR-NOR@Normandy");		//[FRA-2673], name [Haute-Normandie]

    	regionsToMerge.put( "FRA-2683", "FR@FR-HDF@Hautes-de-France");		//[FRA-2683], name [Nord-Pas-de-Calais]
    	regionsToMerge.put( "FRA-2684", "FR@FR-HDF@Hautes-de-France");		//[FRA-2684], name [Picardie]


    	regionsToMerge.put( "FRA-2682", "FR@FR-GES@Grand Est");		//[FRA-2682], name [Champagne-Ardenne]
    	regionsToMerge.put( "FRA-2687", "FR@FR-GES@Grand Est");		//[FRA-2687], name [Lorraine]
    	regionsToMerge.put( "FRA-2686", "FR@FR-GES@Grand Est");		//[FRA-2686], name [Alsace]

    	regionsToMerge.put( "FRA-2671", "FR@FR-BFC@Bourgogne-Franche-Comte");		//[FRA-2671], name [Bourgogne]
    	regionsToMerge.put( "FRA-2685", "FR@FR-BFC@Bourgogne-Franche-Comte");		//[FRA-2685], name [Franche-Comté]

    	regionsToMerge.put( "FRA-2662", "FR@FR-BRE@Bretagne");		//[FRA-2662], name [Bretagne]
    	regionsToMerge.put( "FRA-2664", "FR@FR-PDL@Pays de la Loire");		//[FRA-2664], name [Pays de la Loire]
    	regionsToMerge.put( "FRA-2667", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur");		//    	[FRA-2667], name [Provence-Alpes-Côte-d'Azur]
    	regionsToMerge.put( "FRA-2672", "FR@FR-CVL@Centre Val de Loire");		//[FRA-2672], name [Centre]
    	regionsToMerge.put( "FRA-2666", "FR@FR-COR@Corse");		//[FRA-2666], name [Corse]
    	regionsToMerge.put( "FRA-2680", "FR@FR-IDF@Île-de-France");		//[FRA-2680], name [Île-de-France],
    }


    // region codes using V30 file
    //
    static {
    	regionsToMerge.put( "FRA-5262", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Ain
    	regionsToMerge.put( "FRA-5263", "FR@FR-HDF@Hautes-de-France"); // Aisne
    	regionsToMerge.put( "FRA-5264", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Allier
    	regionsToMerge.put( "FRA-5265", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Alpes-de-Haute-Provence
    	regionsToMerge.put( "FRA-5266", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Alpes-Maritimes
    	regionsToMerge.put( "FRA-5267", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Ardeche
    	regionsToMerge.put( "FRA-5268", "FR@FR-GES@Grand Est"); // Ardennes
    	regionsToMerge.put( "FRA-5269", "FR@FR-OCC@Occitanie"); // Ariege
    	regionsToMerge.put( "FRA-5270", "FR@FR-GES@Grand Est"); // Aube
    	regionsToMerge.put( "FRA-5271", "FR@FR-OCC@Occitanie"); // Aude
    	regionsToMerge.put( "FRA-5272", "FR@FR-OCC@Occitanie"); // Aveyron
    	regionsToMerge.put( "FRA-5273", "FR@FR-GES@Grand Est"); // Bas-Rhin
    	regionsToMerge.put( "FRA-5274", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Bouches-du-Rhone
    	regionsToMerge.put( "FRA-5275", "FR@FR-NOR@Normandy"); // Calvados
    	regionsToMerge.put( "FRA-5276", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Cantal
    	regionsToMerge.put( "FRA-5277", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Charente
    	regionsToMerge.put( "FRA-5278", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Charente-Maritime
    	regionsToMerge.put( "FRA-5279", "FR@FR-CVL@Centre Val de Loire"); // Cher
    	regionsToMerge.put( "FRA-5280", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Correze
    	regionsToMerge.put( "FRA-5281", "FR@FR-COR@Corse"); // Corse-du-Sud
    	regionsToMerge.put( "FRA-5282", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Cote-d'Or
    	regionsToMerge.put( "FRA-5283", "FR@FR-BRE@Bretagne"); // CÃ´tes-d'Armor
    	regionsToMerge.put( "FRA-5284", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Creuse
    	regionsToMerge.put( "FRA-5285", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Deux-Sevres
    	regionsToMerge.put( "FRA-5286", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Dordogne
    	regionsToMerge.put( "FRA-5287", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Doubs
    	regionsToMerge.put( "FRA-5288", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Drome
    	regionsToMerge.put( "FRA-5289", "FR@FR-IDF@Île-de-France"); // Essonne
    	regionsToMerge.put( "FRA-5290", "FR@FR-NOR@Normandy"); // Eure
    	regionsToMerge.put( "FRA-5291", "FR@FR-CVL@Centre Val de Loire"); // Eure-et-Loir
    	regionsToMerge.put( "FRA-5292", "FR@FR-BRE@Bretagne"); // FinistÃ¨re
    	regionsToMerge.put( "FRA-5293", "FR@FR-OCC@Occitanie"); // Gard
    	regionsToMerge.put( "FRA-5294", "FR@FR-OCC@Occitanie"); // Gers
    	regionsToMerge.put( "FRA-5295", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Gironde
    	regionsToMerge.put( "FRA-5296", "FR@FR-GES@Grand Est"); // Haute-Rhin
    	regionsToMerge.put( "FRA-5297", "FR@FR-COR@Corse"); // Haute-Corse
    	regionsToMerge.put( "FRA-5298", "FR@FR-OCC@Occitanie"); // Haute-Garonne
    	regionsToMerge.put( "FRA-5299", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Haute-Loire
    	regionsToMerge.put( "FRA-5300", "FR@FR-GES@Grand Est"); // Haute-Marne
    	regionsToMerge.put( "FRA-5301", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Haute-Saone
    	regionsToMerge.put( "FRA-5302", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Haute-Savoie
    	regionsToMerge.put( "FRA-5303", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Haute-Vienne
    	regionsToMerge.put( "FRA-5304", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Hautes-Alpes
    	regionsToMerge.put( "FRA-5305", "FR@FR-OCC@Occitanie"); // Hautes-Pyrenees
    	regionsToMerge.put( "FRA-5306", "FR@FR-IDF@Île-de-France"); // Hauts-de-Seine
    	regionsToMerge.put( "FRA-5307", "FR@FR-OCC@Occitanie"); // Herault
    	regionsToMerge.put( "FRA-5308", "FR@FR-BRE@Bretagne"); // Ille-et-Vilaine
    	regionsToMerge.put( "FRA-5309", "FR@FR-CVL@Centre Val de Loire"); // Indre
    	regionsToMerge.put( "FRA-5310", "FR@FR-CVL@Centre Val de Loire"); // Indre-et-Loire
    	regionsToMerge.put( "FRA-5311", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Isere
    	regionsToMerge.put( "FRA-5312", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Jura
    	regionsToMerge.put( "FRA-5313", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Landes
    	regionsToMerge.put( "FRA-5314", "FR@FR-CVL@Centre Val de Loire"); // Loir-et-Cher
    	regionsToMerge.put( "FRA-5315", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Loire
    	regionsToMerge.put( "FRA-5316", "FR@FR-PDL@Pays de la Loire"); // Loire-Atlantique
    	regionsToMerge.put( "FRA-5317", "FR@FR-CVL@Centre Val de Loire"); // Loiret
    	regionsToMerge.put( "FRA-5318", "FR@FR-OCC@Occitanie"); // Lot
    	regionsToMerge.put( "FRA-5319", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Lot-et-Garonne
    	regionsToMerge.put( "FRA-5320", "FR@FR-OCC@Occitanie"); // Lozere
    	regionsToMerge.put( "FRA-5321", "FR@FR-PDL@Pays de la Loire"); // Maine-et-Loire
    	regionsToMerge.put( "FRA-5322", "FR@FR-NOR@Normandy"); // Manche
    	regionsToMerge.put( "FRA-5323", "FR@FR-GES@Grand Est"); // Marne
    	regionsToMerge.put( "FRA-5324", "FR@FR-PDL@Pays de la Loire"); // Mayenne
    	regionsToMerge.put( "FRA-5325", "FR@FR-GES@Grand Est"); // Meurhe-et-Moselle
    	regionsToMerge.put( "FRA-5326", "FR@FR-GES@Grand Est"); // Meuse
    	regionsToMerge.put( "FRA-5327", "FR@FR-BRE@Bretagne"); // Morbihan
    	regionsToMerge.put( "FRA-5328", "FR@FR-GES@Grand Est"); // Moselle
    	regionsToMerge.put( "FRA-5329", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Nievre
    	regionsToMerge.put( "FRA-5330", "FR@FR-HDF@Hautes-de-France"); // Nord
    	regionsToMerge.put( "FRA-5331", "FR@FR-HDF@Hautes-de-France"); // Oise
    	regionsToMerge.put( "FRA-5332", "FR@FR-NOR@Normandy"); // Orne
    	regionsToMerge.put( "FRA-5333", "FR@FR-IDF@Île-de-France"); // Paris
    	regionsToMerge.put( "FRA-5334", "FR@FR-HDF@Hautes-de-France"); // Pas-de-Calais
    	regionsToMerge.put( "FRA-5335", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Puy-de-Dome
    	regionsToMerge.put( "FRA-5336", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Pyrenees-Atlantiques
    	regionsToMerge.put( "FRA-5337", "FR@FR-OCC@Occitanie"); // Pyrenees-Orientales
    	regionsToMerge.put( "FRA-5338", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Rhone
    	regionsToMerge.put( "FRA-5339", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Saone-et-Loire
    	regionsToMerge.put( "FRA-5340", "FR@FR-PDL@Pays de la Loire"); // Sarthe
    	regionsToMerge.put( "FRA-5341", "FR@FR-ARA@Auvergne-Rhone-Alpes"); // Savoie
    	regionsToMerge.put( "FRA-5342", "FR@FR-IDF@Île-de-France"); // Seien-et-Marne
    	regionsToMerge.put( "FRA-5343", "FR@FR-NOR@Normandy"); // Seine-Maritime
    	regionsToMerge.put( "FRA-5344", "FR@FR-IDF@Île-de-France"); // Seine-Saint-Denis
    	regionsToMerge.put( "FRA-5345", "FR@FR-HDF@Hautes-de-France"); // Somme
    	regionsToMerge.put( "FRA-5346", "FR@FR-OCC@Occitanie"); // Tarn
    	regionsToMerge.put( "FRA-5347", "FR@FR-OCC@Occitanie"); // Tarn-et-Garonne
    	regionsToMerge.put( "FRA-5348", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Territoire de Belfort
    	regionsToMerge.put( "FRA-5349", "FR@FR-IDF@Île-de-France"); // Val-d'Oise
    	regionsToMerge.put( "FRA-5350", "FR@FR-IDF@Île-de-France"); // Val-de-Marne
    	regionsToMerge.put( "FRA-5351", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Var
    	regionsToMerge.put( "FRA-5352", "FR@FR-PAC@Provence-Alpes-Côte-d'Azur"); // Vaucluse
    	regionsToMerge.put( "FRA-5353", "FR@FR-PDL@Pays de la Loire"); // Vendee
    	regionsToMerge.put( "FRA-5354", "FR@FR-NAQ@Nouvelle-Aquitaine"); // Vienne
    	regionsToMerge.put( "FRA-5355", "FR@FR-GES@Grand Est"); // Vosges
    	regionsToMerge.put( "FRA-5356", "FR@FR-BFC@Bourgogne-Franche-Comte"); // Yonne
    	regionsToMerge.put( "FRA-5357", "FR@FR-IDF@Île-de-France"); // Yvelines
    }


    static {
    	// Irelands provinces
    	regionsToMerge.put( "IRL-5569", "IE@IE-M@Munster"); // Cork
    	regionsToMerge.put( "IRL-5570", "IE@IE-M@Munster"); // Limerick
    	regionsToMerge.put( "IRL-78", "IE@IE-M@Munster"); // Cork			??
    	regionsToMerge.put( "IRL-726", "IE@IE-M@Munster"); // Limerick
    	regionsToMerge.put( "IRL-725", "IE@IE-M@Munster"); // Kerry
    	regionsToMerge.put( "IRL-724", "IE@IE-M@Munster"); // North Tipperary
    	regionsToMerge.put( "IRL-5572", "IE@IE-M@Munster"); // South Tipperary
    	regionsToMerge.put( "IRL-1444", "IE@IE-M@Munster"); // Waterford
    	regionsToMerge.put( "IRL-5571", "IE@IE-M@Munster"); // Waterford
    	regionsToMerge.put( "IRL-76", "IE@IE-M@Munster"); // Clare

    	regionsToMerge.put( "IRL-731", "IE@IE-L@Leinster"); // Longford
    	regionsToMerge.put( "IRL-717", "IE@IE-L@Leinster"); // Westmeath
    	regionsToMerge.put( "IRL-716", "IE@IE-L@Leinster"); // Offaly
    	regionsToMerge.put( "IRL-5576", "IE@IE-L@Leinster"); // DÃºn Laoghaireâ??Rathdown  (Dun Laoghaire-Rathdown)
    	regionsToMerge.put( "IRL-715", "IE@IE-L@Leinster"); // Meath
    	regionsToMerge.put( "IRL-712", "IE@IE-L@Leinster"); // Louth
    	regionsToMerge.put( "IRL-5573", "IE@IE-L@Leinster"); // South Dublin
    	regionsToMerge.put( "IRL-5575", "IE@IE-L@Leinster"); // Dublin
    	regionsToMerge.put( "IRL-5574", "IE@IE-L@Leinster"); // Fingal					(near Dublin)
    	regionsToMerge.put( "IRL-721", "IE@IE-L@Leinster"); // Kildare
    	regionsToMerge.put( "IRL-723", "IE@IE-L@Leinster"); // Laoighis
    	regionsToMerge.put( "IRL-722", "IE@IE-L@Leinster"); // Kilkenny
    	regionsToMerge.put( "IRL-77", "IE@IE-L@Leinster"); // Carlow
    	regionsToMerge.put( "IRL-719", "IE@IE-L@Leinster"); // Wicklow
    	regionsToMerge.put( "IRL-718", "IE@IE-L@Leinster"); // Wexford

	   	regionsToMerge.put( "IRL-714", "IE@IE-C@Connaght"); // Mayo
    	regionsToMerge.put( "IRL-728", "IE@IE-C@Connaght"); // Sligo
    	regionsToMerge.put( "IRL-727", "IE@IE-C@Connaght"); // Roscommon
    	regionsToMerge.put( "IRL-730", "IE@IE-C@Connaght"); // Leitrim
    	regionsToMerge.put( "IRL-713", "IE@IE-C@Connaght"); // Galway
    	regionsToMerge.put( "IRL-5568", "IE@IE-C@Connaght"); // Galway

    	regionsToMerge.put( "IRL-79", "IE@IE-U@Ulster"); // Cavan
    	regionsToMerge.put( "IRL-729", "IE@IE-U@Ulster"); // Donegal
    	regionsToMerge.put( "IRL-3412", "IE@IE-U@Ulster"); // Monaghan
    }



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

    	// make sure the output folders already exist before running the process
    	makeSubFolders();

        initShapeFileDataSources();
        featureType = createModifiedFeatureType();
        loadCountries();
        loadRegions();

        generateAdditionalShapes();

        // generate GEOJson Files
        generateShapeFiles();

        System.out.println("Done");
    }

    /**
     * Generates all the output folders required when running the process
     *
     */
    private static void makeSubFolders( ) {
    	MakeFolder m = new MakeFolder( OUTPUT_PATH );
    	m.makeSubFolder( "country" );
    	m.makeSubFolder( "country/500" );
    	m.makeSubFolder( "country/1000" );
    	m.makeSubFolder( "country/5000" );
    	m.makeSubFolder( "country/10000" );
    	m.makeSubFolder( "country/50000" );
    	m.makeSubFolder( "country/more" );
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
        // List<Geometry> geometries = new ArrayList<Geometry>();
        Geometry[] geometries = new Geometry[regions.size()];

        List<Polygon> allPolygons = new ArrayList<Polygon>();
        int i = 0;
        for (Region region : regions) {
            Geometry geometry = region.getGeometry();
            allPolygons.addAll(extractPolygonsOfGeometry(geometry));
            i++;
        }
        allPolygons.size();

        /*if (geometry.getNumGeometries() > 1) {
            GeometryCollection geometryCollection = (GeometryCollection) geometry;
            Geometry unionedGeometry = geometryCollection.union();
            validateGeometry(unionedGeometry, code);
            return unionedGeometry;
        }*/
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
        Polygon[] polygons = GeometryFactory.toPolygonArray(allPolygons);
        // Geometry[] geomArray = GeometryFactory.toGeometryArray(geometries);
        return factory.createMultiPolygon(polygons);
        // return factory.createGeometryCollection(geometries);
        // return new GeometryCollection(geometries, new PrecisionModel(PrecisionModel.FLOATING));

        /* System.out.println("No region collection for " + code);

        /*try {
            return geometryCollection.union().buffer(0.005);
        } catch (Exception e) {
            System.out.println("Error generating unioned shape");
            return null;
        }*/
    }

    private static void generateShapeFiles() throws IOException {


        Collection<Region> continents = ShapeFileGenerator.continents.values();
        SimpleFeatureCollection continentFeatures = createFeatures(continents);
        Collection<Region> subregions = ShapeFileGenerator.subregions.values();
        SimpleFeatureCollection subregionFeatures = createFeatures(subregions);
        Collection<Region> countries2 = ShapeFileGenerator.countries.values();
        SimpleFeatureCollection countryFeatures = createFeatures(countries2);

        DefaultFeatureCollection unifiedFeatures = new DefaultFeatureCollection();
        unifiedFeatures.addAll(continentFeatures);
        generateShapeFile("WORLD", null, null, unifiedFeatures);

        unifiedFeatures = new DefaultFeatureCollection();
        unifiedFeatures.addAll(subregionFeatures);
        generateShapeFile("WORLD_SUBREGIONS", null, null, unifiedFeatures);

        unifiedFeatures = new DefaultFeatureCollection();
        unifiedFeatures.addAll(countryFeatures);
        generateShapeFile("WORLD_COUNTRIES", null, null, countryFeatures);

        /*
        for (String continentCode: subregionsByContinents.keySet()) {
            List<Region> subregionsForContinent = subregionsByContinents.get(continentCode);
            SimpleFeatureCollection features = createFeatures(subregionsForContinent);
            generateGeoJSONFile("subregions-continent-" + continentCode, features);
        } */


        for (String continentCode: countriesByContinents.keySet()) {
            List<Region> countriesForContinent = countriesByContinents.get(continentCode);

            SimpleFeatureCollection features;
            if ("CONT_EU".equals(continentCode)) {
                ReferencedEnvelope envelope = new ReferencedEnvelope(-20, 50, 20, 83, featureType.getCoordinateReferenceSystem());
                features = createFeatures(countries.values(), envelope);
                System.out.println(continentCode + ": " + features.getBounds());
            } else if ("CONT_NA".equals(continentCode)) {
                ReferencedEnvelope envelope = new ReferencedEnvelope(-179.9999999999999, -42, 5, 84, featureType.getCoordinateReferenceSystem());
                features = createFeatures(countries.values(), envelope);
                System.out.println(continentCode + ": " + features.getBounds());
            } else if ("CONT_OC".equals(continentCode)) {
                ReferencedEnvelope envelope = new ReferencedEnvelope(100, 180, -57, 7, featureType.getCoordinateReferenceSystem());
                features = createFeatures(countries.values(), envelope);
                System.out.println(continentCode + ": " + features.getBounds());
            } else {
                features = createFeatures(countriesForContinent);
                ReferencedEnvelope envelope = features.getBounds();
                double width = envelope.getWidth();
                double height = envelope.getHeight();
                double expandWidth;
                double expandHeight;
                if (width > height) {
                    expandWidth = width * 0.1;
                    expandHeight = width * 0.1 + (width - height) * 0.5;
                } else {
                    expandHeight = height * 0.1;
                    expandWidth = height * 0.1 + (height - width) * 0.5;
                }
                envelope.expandBy(expandWidth, expandHeight);
                features = createFeatures(countries.values(), envelope);
                System.out.println(continentCode + ": " + features.getBounds());
            }
            generateShapeFile(continentCode, null, null, features);

            /*Geometry geometry = mergeRegions(countriesForContinent, continentCode);*/
            /* Region continent = new Region(continentCode, "Continent", RegionType.CONTINENT, continentCode);
            continent.setGeometry(geometry);
            SimpleFeature continentFeature = createSimpleFeature(continent);

            DefaultFeatureCollection continentFeatures2 = new DefaultFeatureCollection();
            continentFeatures2.add(continentFeature);

            generateShapeFile(continentCode + "_2", null, null, continentFeatures2); */
        }

        /* if (true) {
            return;
        } */

        for (String countryCode: regionsByCountry.keySet()) {
//            System.out.println("Processing country " + countryCode);

            List<Region> regionsForCountry = regionsByCountry.get(countryCode);
            DefaultFeatureCollection features = createFeatures(regionsForCountry);

            long numberOfPoints = 0;
            for (Region region: regionsForCountry) {
                numberOfPoints += region.getGeometry().getNumPoints();
            }

            ReferencedEnvelope bounds = features.getBounds();
            double width = bounds.getWidth();
            double height = bounds.getHeight();
            double expandWidth;
            double expandHeight;
            if (width > (height * 2)) {
                double difference = width - (height * 2);
                expandWidth = width * 0.5;
                expandHeight = difference + width * 0.5;
            } else {
                double difference = (height * 2) - width;
                expandHeight = height * 2 * 0.5;
                expandWidth = difference + height * 2 * 0.5;
            }
            bounds.expandBy(expandWidth, expandHeight);

            ReferencedEnvelope[] boundsArray = new ReferencedEnvelope[2];

            if ("FR".equals(countryCode)) {
                bounds = new ReferencedEnvelope(-13, 18, 37, 55, featureType.getCoordinateReferenceSystem());
            } else if ("NL".equals(countryCode)) {
                 bounds = new ReferencedEnvelope(2, 10, 48, 58, featureType.getCoordinateReferenceSystem());
            } else if ("CA".equals(countryCode)) {
                 bounds = new ReferencedEnvelope(-175, -45, 32.000, 89, featureType.getCoordinateReferenceSystem());
            } else if ("CN".equals(countryCode)) {
                 bounds = new ReferencedEnvelope(60, 150, -10, 60, featureType.getCoordinateReferenceSystem());
            } else if ("NO".equals(countryCode)) {
                bounds = new ReferencedEnvelope(-10, 47, 50, 80, featureType.getCoordinateReferenceSystem());
            }  else if ("NZ".equals(countryCode)) {
                boundsArray[0] = new ReferencedEnvelope(155, 180.0, -20, -60, featureType.getCoordinateReferenceSystem());
                boundsArray[1] = new ReferencedEnvelope(-180, -165, -20, -60, featureType.getCoordinateReferenceSystem());
            } else if ("FJ".equals(countryCode)) {

            } else if ("KI".equals(countryCode)) {

            }

            if ("NZ".equals(countryCode)) {
            	features = createFeatures(regionsForCountry, boundsArray);
            } else {
           		features = createFeatures(regionsForCountry, bounds);
            }

            if (!countryCode.equals("US")) {

                for (String countryCode2: countries.keySet()) {
                    try {
                        // try to include country boundary for ease of rendering
                        Region country = countries.get(countryCode2);
                        if ("NZ".equals(countryCode)) {
                        	for (ReferencedEnvelope envelope: boundsArray) {
	                        	if (envelope.intersects(country.getFeature().getBounds())) {
//	                        		System.out.println("Country " + country.getName() + " is close to " + countryCode);
	 	                            SimpleFeature clippedFeature = createSimpleClippedFeature(country, bounds);
	 	                            Geometry geometry = (Geometry) clippedFeature.getDefaultGeometry();

	 	                            if (geometry.getNumPoints() > 0) {
//	 	                                System.out.println("Adding " + countryCode2 + " because it is close and it is a valid shape");
	 	                                features.add(clippedFeature);
	 	                            } else {
	 	                                System.out.println("Ignoring " + countryCode2 + " because it's area is 0");
	 	                            }
	 	                        }
                        	}
                        } else {
	                        if (bounds.intersects(country.getFeature().getBounds())) {
//	                            System.out.println("Country " + country.getName() + " is close to " + countryCode);
	                            SimpleFeature clippedFeature = createSimpleClippedFeature(country, bounds);
	                            Geometry geometry = (Geometry) clippedFeature.getDefaultGeometry();

	                            if (geometry.getNumPoints() > 0) {
//	                                System.out.println("Adding " + countryCode2 + " because it is close and it is a valid shape");
	                                features.add(clippedFeature);
	                            } else {
	                                System.out.println("Ignoring " + countryCode2 + " because it's area is 0");
	                            }
	                        }
                        }

                    } catch (Exception e) {
                        System.out.println("Exception " + e.getMessage() + " while processing " + countryCode);
                    }

                }
            }

            if (numberOfPoints <= 500) {
                generateShapeFile(countryCode, "country", "500", features);
            } else if (numberOfPoints <= 1000) {
                generateShapeFile(countryCode, "country", "1000", features);
            } else if (numberOfPoints <= 5000) {
                generateShapeFile(countryCode, "country", "5000", features);
            } else if (numberOfPoints <= 10000) {
                generateShapeFile(countryCode, "country", "10000", features);
            } else if (numberOfPoints <= 50000) {
                generateShapeFile(countryCode, "country", "50000", features);
            } else {
                generateShapeFile(countryCode, "country", "more", features);
            }

        }
    }

    private static void generateShapeFile(String fileName, String type, String numNodes, SimpleFeatureCollection features) {
        try {
            SHPFileUtils.writeResultToFile(featureType, features, getShapeFilePath(fileName, type, numNodes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getShapeFilePath(String fileName, String type, String numNodes) {
        String path =  OUTPUT_PATH;
        if (type != null) {
            path +=  type + "/";
        }
        if (numNodes != null) {
            path +=  numNodes + "/";
        }
        path += fileName + ".shp";
        return path;
    }

    private static DefaultFeatureCollection createFeatures(Collection<Region> regions) {
        return createFeatures(regions, null);
    }

    private static DefaultFeatureCollection createFeatures(Collection<Region> regions, com.vividsolutions.jts.geom.Envelope ...envelope) {
        DefaultFeatureCollection features = new DefaultFeatureCollection();
        HashMap<String, List<Region>> regionsSortedByCode = new HashMap<String, List<Region>>();

        for (Region region : regions) {
            String regionCode = region.getCode();
            if (!regionsSortedByCode.containsKey(regionCode)) {
                regionsSortedByCode.put(regionCode, new ArrayList<Region>());
            }
            List<Region> regionsWithSameCode = regionsSortedByCode.get(regionCode);
            regionsWithSameCode.add(region);
        }

        for (Entry<String, List<Region>> regionsWithSameCode : regionsSortedByCode.entrySet()) {
            SimpleFeature feature;
            String regionCode = regionsWithSameCode.getKey();
            List<Region> regionsForCode = regionsWithSameCode.getValue();

            Region region;
            if (regionsForCode.size() == 1) {
                region = regionsForCode.get(0);
            } else {
                Region template = regionsForCode.get(0);
                Geometry geometry = mergeRegions(regionsForCode, regionCode);
                region = new Region(regionCode, "MergedRegion", template.getType(), template.getCountryCode(), template.getContinentCode());
                region.setGeometry(geometry);
            }

            if (envelope == null || envelope.length == 0) {
                feature = createSimpleFeature(region);
            } else {
                feature = createSimpleClippedFeature(region, envelope);
            }
            if (feature != null) {
                features.add(feature);
            }
        }
        return features;
    }



    private static void loadCountries() throws IOException {
        SimpleFeatureCollection countries = countryFeatureSource.getFeatures();
        SimpleFeatureIterator iterator = countries.features();

        try {
            while(iterator.hasNext()) {
                SimpleFeature country = iterator.next();

                String continentName = country.getAttribute(UCase("continent")).toString();
                String continentCode = continentNameToContinentCode.get(continentName);
                continentCodeToContinentName.put(continentCode, continentName);

                String subregionName = country.getAttribute(UCase("subregion")).toString();
                String subregionCode = subregionNameToSubregionCode.get(subregionName);
                subregionCodeToSubregionName.put(subregionCode, subregionName);

                String name = country.getAttribute(UCase("name")).toString();
                String code = country.getAttribute(UCase("iso_a2")).toString();

                validateGeometry((Geometry) country.getDefaultGeometry(), country.getAttribute(UCase("iso_a2")).toString());

                /**
                 * In the latest shp file FR has code -99 we need to switch this to FR
                 */
              	if (name.compareTo("France")==0 && (code.compareTo("-99")==0)) {
              		// force the code to be FR for some unknown reason its coming back as -99 and messing up
              		code = "FR";
              	}


                if (continentCode != null) {
                    // I don't care if it is already in the map since i just overwrite
                    // it with the same values anyway - no need for a check
                    subregionToContinent.put(subregionCode, continentCode);

                    Region region = new Region(code, name, RegionType.COUNTRY, continentCode);
                    region.setFeature(country);
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
                    ShapeFileGenerator.countries.put(code, region);
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
                Object geoIpCode = admin1Region.getAttribute("geoip_code");
                String regionCode;
                if (geoIpCode == null || geoIpCode.toString().isEmpty()) {
                    regionCode = admin1Region.getAttribute("adm1_code").toString();
                } else {
                    regionCode = geoIpCode.toString();
                }

                String countryCode = admin1Region.getAttribute("iso_a2").toString();

                Region country = countries.get(countryCode);
                if (country == null) {
                    System.out.println("no country found for " + countryCode);
                } else {
                    Region region = new Region(regionCode, name, RegionType.REGION, countryCode, country.getContinentCode());
                    region.setGeometry((Geometry) admin1Region.getDefaultGeometry());
                    region.setParentId(countryCode);

                    // in the case of France dont include Soverign countries the other side of the world
                    // as it will really mess up the size of the SHP file
                    boolean addRegion = true;

                    if ("ES".compareTo(countryCode)==0 || "FR".compareTo(countryCode)==0  || "IE".compareTo(countryCode) ==0 ) {
                    	// some French regions are over the other side of the world - we dont want to see them when drilling into France
                    	if ( regionsToIgnore.containsKey(region.getCode()) ) {
                    		addRegion = false;
                    	}
         //               System.out.println( "regionsToMerge.put( \"" + region.getCode() + "\"" + ", \"" + countryCode + "@" + countryCode + "-@\"); // " + name  );
                    }

                	if (regionsToMerge.containsKey( region.getCode( ))) {
                		mergeRegion( region, true );
                		addRegion = false;			// save to the end
                	}


                    if (addRegion) {
	                    if (!regionsByCountry.containsKey(countryCode)) {
	                        regionsByCountry.put(countryCode, new ArrayList<Region>());
	                    }
	                    regionsByCountry.get(countryCode).add(region);
                    }

                }
            }
        } catch ( Exception e ) {
        	System.out.println( e );
        } finally {
            iterator.close();
        }

        Iterator it ;

        // process any remaining regions that need merging that didn't have a region next to them at the time they were pulled from the list
        it = regionQueue.entrySet().iterator();
        while ( it.hasNext()  ){
        	Map.Entry pair = (Map.Entry)it.next();
            String regionCodeToMerge = (String)pair.getKey();
            Region region = (Region)pair.getValue();

            // false means if this fails dont go adding to the regionQueue once more - we give up at this point
            if (mergeRegion( region, false) ) {
            	System.out.println("Unable to merge region [" + region.getCode() + " " + region.getName());
            }
        }



        // add all the regions that were merged now all merging has been completed
        it = regionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            String regionName = (String)pair.getKey();
            Region region = (Region)pair.getValue();

            // get the country from the region name
            String arr[] = regionName.split("@");
            String thisCountry = arr[0];
            String thisRegionCode = arr[1];
            String thisRegionName = arr[2];
            region.setCode(thisRegionCode);
            region.setName(thisRegionName);

            if (!regionsByCountry.containsKey(thisCountry)){
            	regionsByCountry.put(thisCountry,  new ArrayList<Region>());
            }
            regionsByCountry.get(thisCountry).add(region);
        }





    }


    static boolean mergeRegion( Region region, Boolean addToQueueOnFailure ) {

    	boolean regionMerged = true;
		String regionCodeToMerge = regionsToMerge.get( region.getCode() );

		if (regionMap.containsKey(regionCodeToMerge)) {
			Region thisRegion = regionMap.get(regionCodeToMerge);
			Geometry g = thisRegion.getGeometry();

    		ArrayList<Geometry> lst = new ArrayList<Geometry>();
    		lst.add(g);
    		lst.add(region.getGeometry());
			Geometry mergedGeometry = combineIntoOneGeometry( thisRegion, region );
			// if a null came back then this region is trying to connect to another region when they are not next to each other
			// if this is the case we add the region to a list for them to be done again at the end of the processing
			if (mergedGeometry !=null) {
    			thisRegion.setGeometry(mergedGeometry);
    			regionMap.put( regionCodeToMerge, thisRegion);
			} else {
				if (addToQueueOnFailure) {
	                System.out.println( "Adding [" + thisRegion.getCode() + "] to queue or regions to do at the end " );
					regionQueue.put(thisRegion.getCode(), region);
				} else {
					// this probably happens when the region still can't find an adjacent region
					// investigate where this region is and which regions are near it - have they been processed yet?
	                System.out.println( "Failed to merge [" + thisRegion.getCode() + "] name " + thisRegion.getName());
				}
				regionMerged = false;
			}
		}
		else {
			regionMap.put( regionCodeToMerge,  region);
		}
       	return regionMerged;

    }

    // combines the geometry of one region with that of a second and returns the combined Geometry object
    // In the case of Gironde this causes a exception when using the more grandular shp file C:/Node/Data/shp/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces.shp
    static Geometry combineIntoOneGeometry( Region r1, Region r2) {
		ArrayList<Geometry> lst = new ArrayList<Geometry>();

		Geometry g1 = r1.getGeometry();
		Geometry g2 = r2.getGeometry();
		lst.add(g1);
		lst.add(g2);
    	try {
	    	GeometryFactory factory = FactoryFinder.getGeometryFactory( null );

	        // note the following geometry collection may be invalid (say with overlapping polygons)
	        GeometryCollection geometryCollection1 =(GeometryCollection) factory.buildGeometry( lst  );
	        return geometryCollection1.union();

    	} catch (Exception e ) {
        	System.out.println( e );
        	System.out.println( e.getMessage() );
        	return null;
    	}


    }




    private static SimpleFeature createSimpleFeature(Region region)
    {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.set(UCase("iso_a2"), region.getCode());
        builder.set(UCase("type"), region.getType().toString() );
        builder.set(UCase("name"), region.getName());
        builder.set("the_geom", region.getGeometry());

//        // so that we can input the shp file into GeoJSONGenerator (should we want to)
//        // addd two additional fields
//        String continentCode = region.getContinentCode();
//        String continentName = continentCodeToContinentName.get(continentCode);
//        builder.set(UCase("continent"), continentName);
//        String subregionName = getSubregionNameForCountryCode( region.getCountryCode() );
//        builder.set(UCase("subregion"), subregionName);

        SimpleFeature newFeature = builder.buildFeature(region.getCode());

        return newFeature;
    }

    /**
     * calculates the subRegionName for this country Code
     * @param countryCode
     * @return
     */
    private static String getSubregionNameForCountryCode( String countryCode ) {
        for (String subregionCode: countriesBySubregions.keySet()) {
            List<Region> countriesForSubregion = countriesBySubregions.get(subregionCode);
            if (countriesForSubregion != null && countriesForSubregion.size() > 0) {

            	for ( Region r : countriesForSubregion) {
            		String c = r.getCountryCode();
            		if (c.compareTo(countryCode)==0) {
            			String subregionName = subregionCodeToSubregionName.get(subregionCode);
            			return subregionName;
            		}
            	}
            }
        }
        return "";
    }
    private static SimpleFeature createSimpleClippedFeature(Region region, com.vividsolutions.jts.geom.Envelope ...envelopes)
    {


        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.set(UCase("iso_a2"), region.getCode());
        builder.set(UCase("type"), region.getType().toString() );
        builder.set(UCase("name"), region.getName());


//        // so that we can input the shp file into GeoJSONGenerator (should we want to)
//        // addd two additional fields
//        String continentCode = region.getContinentCode();
//        String continentName = continentCodeToContinentName.get(continentCode);
//        builder.set(UCase("continent"), continentName);
//        String subregionName = getSubregionNameForCountryCode( region.getCountryCode() );
//        builder.set(UCase("subregion"), subregionName);

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));


        if (envelopes.length > 1) {
        	List<Geometry> geometries = new ArrayList<Geometry>();
        	for (com.vividsolutions.jts.geom.Envelope envelope: envelopes) {
        		Geometry envelopeGeometry = factory.toGeometry(envelope);
        		Geometry intersection = envelopeGeometry.intersection(region.getGeometry());
        		if (intersection.getNumPoints() > 0) {
        			geometries.add(intersection);
        		}

        	}

        	List<Polygon> allPolygons = new ArrayList<Polygon>();
            int i = 0;
            for (Geometry geometry : geometries) {
                allPolygons.addAll(extractPolygonsOfGeometry(geometry));
                i++;
            }
            allPolygons.size();

            Polygon[] polygons = GeometryFactory.toPolygonArray(allPolygons);
            // Geometry[] geomArray = GeometryFactory.toGeometryArray(geometries);
            builder.set("the_geom", factory.createMultiPolygon(polygons));
        } else {
        	Geometry envelopeGeometry = factory.toGeometry(envelopes[0]);
        	try {
	            Geometry intersection = envelopeGeometry.intersection(region.getGeometry());
		        if (intersection.getNumPoints() <= 0) {
		            return null;
		        }
		        builder.set("the_geom", intersection);
        	} catch (Exception e ) {
        		System.out.println( "Exception Country " + region.getCode() + " " +   e.getMessage());
        	}
        }

        SimpleFeature newFeature = builder.buildFeature(region.getCode());

        return newFeature;
    }

    /*
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
*/
    private static SimpleFeatureType createModifiedFeatureType() {
        SimpleFeatureType featureType = countryFeatureSource.getSchema();
        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setName(featureType.getName());
        featureTypeBuilder.setCRS(featureType.getCoordinateReferenceSystem());
        for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
            /* System.out.println("Attribute: " + ad.getName().toString()); */
            if (attributesToCopy.contains(ad.getName().toString())) {
                featureTypeBuilder.add(ad);
            }
        }

        SimpleFeatureType modifiedFeatureType = featureTypeBuilder.buildFeatureType();
        return modifiedFeatureType;
    }


    private static void validateGeometry(Geometry geometry, String code) {
        IsValidOp validityOperation = new IsValidOp(geometry);
        TopologyValidationError err = validityOperation.getValidationError();
        if (err != null) {
            System.out.println("Error found for region " + code);
            System.out.println(err);
        }
    }

    /**
     * Helper method that returns all polygons of a single <code>SimpleFeature
     * </code> or an empty list if there are no polygons in the feature
     *
     * @param feature the feature from which to extract the polygoons
     * @return a <code>List</code> with 0 or more <code>Polygons</code>
     */
    private static List<Polygon> extractPolygons(SimpleFeature feature)
    {
        // retrieve geometry of feature
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        String geometryType = geometry.getGeometryType();

        // only process polygons and multipolygons, ignore all others
        if ("MultiPolygon".equals(geometryType)
                || "Polygon".equals(geometryType)) {
            return extractPolygonsOfGeometry(geometry);
        }
        // TODO: remove this after testing
        System.out.println("Unsupported Type:" + geometryType + " Ignoring Feature");

        return new ArrayList<Polygon>(0);
    }

    /**
     *  Helper method that returns all polygons of a single <code>Geometry
     * </code> or an empty list if there are no polygons in the geometry.
     *
     * @param geometry
     * @return
     */
    private static List<Polygon> extractPolygonsOfGeometry(Geometry geometry)
    {
        List<Polygon> polygons = new ArrayList<Polygon>();
        if ("Polygon".equals(geometry.getGeometryType())) {
            polygons.add((Polygon) geometry);
        } else if ("MultiPolygon".equals(geometry.getGeometryType())) {
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                polygons.add((Polygon) geometry.getGeometryN(i));
            }
        } else {
            // TODO: remove this after testing
            System.out.println("Unsupported Type:" + geometry.getGeometryType()
                    + " Ignoring Feature");
        }
        return polygons;
    }

    /**
     * The original shp files downloaded from naturalearthdata.com had fields in lower case
     * however they are now in upper case.  Use this method to control the expected case based on the convertToUpperCase flag set at the top of the class
     * @param s
     * @return
     */
    private static String UCase( String s ) {

    	if (convertToUpperCase) {
    		return s.toUpperCase();
    	}
    	return s;

    }


}
