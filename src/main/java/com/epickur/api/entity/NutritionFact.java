package com.epickur.api.entity;

import com.epickur.api.enumeration.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * NutritionFacts enumeration
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "value", "unit" })
public final class NutritionFact implements Cloneable {

	/** Name **/
	private String name;
	/** Value **/
	private Double value;
	/** Measurement unit **/
	private MeasurementUnit unit;

	/**
	 * The constructor
	 */
	public NutritionFact() {
	}

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value
	 */
	public void setValue(final Double value) {
		this.value = value;
	}

	/**
	 * @return The MeasurementUnit
	 */
	public MeasurementUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            The MeasurementUnit
	 */
	public void setUnit(final MeasurementUnit unit) {
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (!(obj instanceof NutritionFact)) {
			return false;
		}
		NutritionFact other = (NutritionFact) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (unit != other.unit) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public NutritionFact clone() {
		NutritionFact nutritionFact = new NutritionFact();
		nutritionFact.setName(this.name);
		nutritionFact.setUnit(this.unit);
		nutritionFact.setValue(this.value);
		return nutritionFact;
	}
}
