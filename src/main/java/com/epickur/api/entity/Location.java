package com.epickur.api.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Location entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "address", "geo" })
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class Location extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Location.class.getSimpleName());
	/** Address */
	private Address address;
	/** Geo */
	private Geo geo;

	/**
	 * @param obj
	 *            The Document to convert to Location
	 * @return The Location
	 * @throws EpickurParsingException
	 *             If a parsing exception occured
	 */
	public static Location getObject(final Document obj) throws EpickurParsingException {
		return Location.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json string
	 * @return The Location
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Location getObject(final String json) throws EpickurParsingException {
		Location location = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			location = mapper.readValue(json, Location.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Location: " + json, e);
		}
		return location;
	}

	/**
	 * @param prefix
	 *            The prefix
	 * @return A map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (address != null) {
			res.putAll(address.getUpdateMap(prefix + ".address"));
		}
		if (geo != null) {
			res.putAll(geo.getUpdateMap(prefix + ".geo"));
		}
		return res;
	}
	
	@Override
	public Location clone() {
		try {
			return (Location) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
