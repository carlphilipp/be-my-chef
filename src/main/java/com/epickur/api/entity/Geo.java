package com.epickur.api.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Geo entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "type", "coordinates" })
public final class Geo extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Geo.class.getSimpleName());

	/** Type **/
	private String type = "Point";
	/** Coordinates [ longitude, latitude] **/
	private Double[] coordinates;

	/** Constructor **/
	public Geo() {
		this.coordinates = new Double[2];
		this.coordinates[0] = 0.0;
		this.coordinates[1] = 0.0;
	}

	/**
	 * @return The latitude
	 */
	@JsonIgnore
	public Double getLatitude() {
		return coordinates[1];
	}

	/**
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(final Double latitude) {
		this.coordinates[1] = latitude;
	}

	/**
	 * @return The Longitude
	 */
	@JsonIgnore
	public Double getLongitude() {
		return this.coordinates[0];
	}

	/**
	 * @param longitude
	 *            The Longitude
	 */
	public void setLongitude(final Double longitude) {
		this.coordinates[0] = longitude;
	}

	/**
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type
	 */
	public void setType(final String type) {
		this.type = "Point";
	}

	/**
	 * @return The coordinates
	 */
	public Double[] getCoordinates() {
		return coordinates.clone();
	}

	/**
	 * @param coordinates
	 *            The coordinates
	 */
	public void setCoordinates(final Double[] coordinates) {
		this.coordinates = coordinates.clone();
	}

	/**
	 * @param prefix
	 *            The prefix
	 * @return a map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (this.type != null) {
			res.put(prefix + ".type", this.type);
		}
		if (this.coordinates[0] != null && this.coordinates[1] != null) {
			res.put(prefix + ".coordinates", this.coordinates);
		}
		return res;
	}

	/**
	 * @param minDistance
	 *            The minimum distance
	 * @param maxDistance
	 *            The maximum distance
	 * @return a BasicDBObject
	 */
	public Document getSearch(final Integer minDistance, final Integer maxDistance) {
		Document nearSphere = new Document();
		Document geometry = new Document();
		BsonArray coord = new BsonArray(Arrays.asList(new BsonDouble(this.coordinates[0]), new BsonDouble(this.coordinates[1])));
		geometry.append("type", "Point");
		geometry.append("coordinates", coord);
		nearSphere.append("$nearSphere", geometry);
		nearSphere.append("$minDistance", minDistance);
		nearSphere.append("$maxDistance", maxDistance);
		return nearSphere;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinates);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Geo)) {
			return false;
		}
		Geo other = (Geo) obj;
		if (!Arrays.equals(coordinates, other.coordinates)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public Geo clone() {
		try {
			return (Geo) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
