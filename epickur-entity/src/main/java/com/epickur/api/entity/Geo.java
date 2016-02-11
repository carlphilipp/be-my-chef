package com.epickur.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Geo entity
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "type", "coordinates" })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Geo extends AbstractEntity {

	/**
	 * Type
	 */
	private String type = "Point";
	/**
	 * Coordinates [ longitude, latitude]
	 */
	private Double[] coordinates;

	/**
	 * Constructor
	 */
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
	 * @param latitude The latitude
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
	 * @param longitude The Longitude
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
	 * @param type The type
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
	 * @param coordinates The coordinates
	 */
	public void setCoordinates(final Double[] coordinates) {
		this.coordinates = coordinates.clone();
	}

	/**
	 * @param prefix The prefix
	 * @return a map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		final Map<String, Object> res = new HashMap<>();
		if (this.type != null) {
			res.put(prefix + ".type", this.type);
		}
		if (this.coordinates[0] != null && this.coordinates[1] != null) {
			res.put(prefix + ".coordinates", this.coordinates);
		}
		return res;
	}

	/**
	 * @param minDistance The minimum distance
	 * @param maxDistance The maximum distance
	 * @return a Document
	 */
	public Document getSearch(final Integer minDistance, final Integer maxDistance) {
		final Document nearSphere = new Document();
		final Document geometry = new Document();
		final BsonArray coord = new BsonArray(Arrays.asList(new BsonDouble(this.coordinates[0]), new BsonDouble(this.coordinates[1])));
		geometry.append("type", "Point");
		geometry.append("coordinates", coord);
		nearSphere.append("$nearSphere", geometry);
		nearSphere.append("$minDistance", minDistance);
		nearSphere.append("$maxDistance", maxDistance);
		return nearSphere;
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Geo clone() {
		return (Geo) super.clone();
	}
}
