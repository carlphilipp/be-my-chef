package com.epickur.api.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.BasicBSONList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

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
	private Float[] coordinates;

	/** Constructor **/
	public Geo() {
		this.coordinates = new Float[2];
		this.coordinates[0] = 0.0f;
		this.coordinates[1] = 0.0f;
	}

	/**
	 * @return The latitude
	 */
	@JsonIgnore
	public Float getLatitude() {
		return coordinates[1];
	}

	/**
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(final Float latitude) {
		this.coordinates[1] = latitude;
	}

	/**
	 * @return The Longitude
	 */
	@JsonIgnore
	public Float getLongitude() {
		return this.coordinates[0];
	}

	/**
	 * @param longitude
	 *            The Longitude
	 */
	public void setLongitude(final Float longitude) {
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
	public Float[] getCoordinates() {
		return coordinates.clone();
	}

	/**
	 * @param coordinates
	 *            The coordinates
	 */
	public void setCoordinates(final Float[] coordinates) {
		this.coordinates = coordinates.clone();
	}

	/**
	 * @param prefix
	 *            The prefix
	 * @return a map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateListBasicDBObject(final String prefix) {
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
	public BasicDBObject getSearch(final Integer minDistance,
			final Integer maxDistance) {
		BasicDBObject nearSphere = (BasicDBObject) BasicDBObjectBuilder.start().get();
		BasicDBObject geometry = (BasicDBObject) BasicDBObjectBuilder.start().get();
		BasicBSONList coord = new BasicBSONList();
		coord.add(this.coordinates[0]);
		coord.add(this.coordinates[1]);
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
