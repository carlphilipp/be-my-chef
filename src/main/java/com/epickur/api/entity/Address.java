package com.epickur.api.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Address entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "label", "houseNumber", "street", "city", "postalCode", "state", "country" })
public final class Address extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Address.class.getSimpleName());
	/** Label */
	private String label;
	/** House number */
	private String houseNumber;
	/** Street */
	private String street;
	/** City */
	private String city;
	/** Postal code */
	private Integer postalCode;
	/** State */
	private String state;
	/** Country */
	private String country;

	/** Constructor */
	public Address() {
	}

	/**
	 * @return The label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            The label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * @return The house number
	 */
	public String getHouseNumber() {
		return houseNumber;
	}

	/**
	 * @param houseNumber
	 *            The house number
	 */
	public void setHouseNumber(final String houseNumber) {
		this.houseNumber = houseNumber;
	}

	/**
	 * @return The Street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street
	 *            The Street
	 */
	public void setStreet(final String street) {
		this.street = street;
	}

	/**
	 * @return The City
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            The City
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * @return The postal code
	 */
	public Integer getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            The postal code
	 */
	public void setPostalCode(final Integer postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return The state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            The state
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * @return The country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            The country
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * @param prefix
	 *            The prefix
	 * @return A Map
	 */
	@JsonIgnore
	public Map<String, String> getUpdateMap(final String prefix) {
		Map<String, String> res = new HashMap<String, String>();
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((houseNumber == null) ? 0 : houseNumber.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
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
		if (!(obj instanceof Address)) {
			return false;
		}
		Address other = (Address) obj;
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		if (houseNumber == null) {
			if (other.houseNumber != null) {
				return false;
			}
		} else if (!houseNumber.equals(other.houseNumber)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (postalCode == null) {
			if (other.postalCode != null) {
				return false;
			}
		} else if (!postalCode.equals(other.postalCode)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (street == null) {
			if (other.street != null) {
				return false;
			}
		} else if (!street.equals(other.street)) {
			return false;
		}
		return true;
	}

	@Override
	public Address clone() {
		try {
			return (Address) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
