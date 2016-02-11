package com.epickur.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Address entity
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "label", "houseNumber", "street", "city", "postalCode", "state", "country" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class Address extends AbstractEntity {

	/**
	 * Label
	 */
	private String label;
	/**
	 * House number
	 */
	private String houseNumber;
	/**
	 * Street
	 */
	private String street;
	/**
	 * City
	 */
	private String city;
	/**
	 * Postal code
	 */
	private Integer postalCode;
	/**
	 * State
	 */
	private String state;
	/**
	 * Country
	 */
	private String country;

	/**
	 * @param prefix The prefix
	 * @return A Map
	 */
	@JsonIgnore
	public Map<String, String> getUpdateMap(final String prefix) {
		final Map<String, String> res = new HashMap<>();
		if (label != null) {
			res.put(prefix + ".label", label);
		}
		if (houseNumber != null) {
			res.put(prefix + ".houseNumber", houseNumber);
		}
		if (street != null) {
			res.put(prefix + ".street", street);
		}
		if (city != null) {
			res.put(prefix + ".city", city);
		}
		if (postalCode != null) {
			res.put(prefix + ".postalCode", postalCode.toString());
		}
		if (state != null) {
			res.put(prefix + ".state", state);
		}
		if (country != null) {
			res.put(prefix + ".country", country);
		}
		return res;
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Address clone() {
		return (Address) super.clone();
	}
}
