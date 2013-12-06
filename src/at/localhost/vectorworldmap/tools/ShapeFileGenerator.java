package at.localhost.vectorworldmap.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import at.localhost.vectorworldmap.util.Region;
import at.localhost.vectorworldmap.util.Region.RegionType;
import at.localhost.vectorworldmap.util.SHPFileUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

public class ShapeFileGenerator {

    private static final String OUTPUT_PATH = "d:/GitHub/map/input/";

    private static final String NATURAL_EARTH_ADMIN_0_BOUNDARIES_V2 = "resources/naturalearth/v20/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
    private static final String NATURAL_EARTH_ADMIN_1_BOUNDARIES_V3 = "resources/naturalearth/v30/ne_10m_admin_1_states_provinces/ne_10m_admin_1_states_provinces_maxmind1386079801077.shp";

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

    private static final List<String> attributesToCopy = new ArrayList<String>();

    private static SimpleFeatureType featureType = null;
    static {
        attributesToCopy.add("the_geom");
        attributesToCopy.add("iso_a2");
        attributesToCopy.add("name");
        attributesToCopy.add("type");
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

        initShapeFileDataSources();
        featureType = createModifiedFeatureType();
        loadCountries();
        loadRegions();

        generateAdditionalShapes();

        // generate GEOJson Files
        generateShapeFiles();
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
            System.out.println("Processing country " + countryCode);

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
	 	                            System.out.println("Country " + country.getName() + " is close to " + countryCode);
	 	                            SimpleFeature clippedFeature = createSimpleClippedFeature(country, bounds);
	 	                            Geometry geometry = (Geometry) clippedFeature.getDefaultGeometry();

	 	                            if (geometry.getNumPoints() > 0) {
	 	                                System.out.println("Adding " + countryCode2 + " because it is close and it is a valid shape");
	 	                                features.add(clippedFeature);
	 	                            } else {
	 	                                System.out.println("Ignoring " + countryCode2 + " because it's area is 0");
	 	                            }
	 	                        }
                        	}
                        } else {
	                        if (bounds.intersects(country.getFeature().getBounds())) {
	                            System.out.println("Country " + country.getName() + " is close to " + countryCode);
	                            SimpleFeature clippedFeature = createSimpleClippedFeature(country, bounds);
	                            Geometry geometry = (Geometry) clippedFeature.getDefaultGeometry();

	                            if (geometry.getNumPoints() > 0) {
	                                System.out.println("Adding " + countryCode2 + " because it is close and it is a valid shape");
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
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.set("iso_a2", region.getCode());
        builder.set("type", region.getType().toString() );
        builder.set("name", region.getName());
        builder.set("the_geom", region.getGeometry());

        SimpleFeature newFeature = builder.buildFeature(region.getCode());

        return newFeature;
    }

    private static SimpleFeature createSimpleClippedFeature(Region region, com.vividsolutions.jts.geom.Envelope ...envelopes)
    {

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.set("iso_a2", region.getCode());
        builder.set("type", region.getType().toString() );
        builder.set("name", region.getName());

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
	        Geometry intersection = envelopeGeometry.intersection(region.getGeometry());
	        if (intersection.getNumPoints() <= 0) {
	            return null;
	        }
	        builder.set("the_geom", intersection);
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
}
