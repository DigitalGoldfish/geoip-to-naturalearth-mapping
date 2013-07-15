package at.localhost.vectorworldmap.util;

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author cwat-moehler
 *
 */
public class Region {

	public enum RegionType {
		CONTINENT("continent"),
		SUBREGION("subregion"),
		COUNTRY("country"),
		REGION("region");

		private final String name;

		RegionType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private String name;

	private String code;

	private RegionType type;

	private String countryCode;

	private String continentCode;

	private Geometry geometry;

	private String naturalEarthName;

	private boolean mappable = false;

	// private SimpleFeature feature;

	private List<SimpleFeature> features = new ArrayList<SimpleFeature>();

	public Region(String code, String name, RegionType type, String continentCode) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.continentCode = continentCode;
		this.countryCode = code;
	}

	public Region(String code, String name, RegionType type, String countryCode, String continentCode) {
		this.code = code;
		this.type = type;
		this.name = name;
		this.countryCode = countryCode;
		this.continentCode = continentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public RegionType getType() {
		return type;
	}

	public void setType(RegionType type) {
		this.type = type;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getContinentCode() {
		return continentCode;
	}

	public void setContinentCode(String continentCode) {
		this.continentCode = continentCode;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getNaturalEarthName() {
		return naturalEarthName;
	}

	public void setNaturalEarthName(String naturalEarthName) {
		this.naturalEarthName = naturalEarthName;
	}

	public boolean isMappable() {
		return mappable;
	}

	public void setMappable(boolean mappable) {
		this.mappable = mappable;
	}

	public void setFeatures(List<SimpleFeature> features) {
		this.features = features;
	}

	public void addFeature(SimpleFeature feature) {
		this.features.add(feature);
	}

	public List<SimpleFeature> getFeatures()
	{
		return this.features;
	}

}
