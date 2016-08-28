package com.epickur.api.entity;

import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Location entity
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "address", "geo" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class Location extends AbstractEntity {

	/**
	 * Address
	 */
	private Address address;
	/**
	 * Geo
	 */
	private Geo geo;

	/**
	 * @param obj The Document to convert to Location
	 * @return The Location
	 * @throws EpickurParsingException If a parsing exception occured
	 */
	public static Location getObject(final Document obj) throws EpickurParsingException {
		return Location.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json The json string
	 * @return The Location
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	public static Location getObject(final String json) throws EpickurParsingException {
		Location location;
		try {
			final ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			location = mapper.readValue(json, Location.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Location: " + json, e);
		}
		return location;
	}

	/**
	 * @param prefix The prefix
	 * @return A map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		final Map<String, Object> res = new HashMap<>();
		if (address != null) {
			res.putAll(address.getUpdateMap(prefix + ".address"));
		}
		if (geo != null) {
			res.putAll(geo.getUpdateMap(prefix + ".geo"));
		}
		return res;
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Location clone() {
		return (Location) super.clone();
	}
}
