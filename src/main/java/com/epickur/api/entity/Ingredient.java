package com.epickur.api.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.enumeration.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Ingredient entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "sequence", "quantity", "measurementUnit" })
public final class Ingredient extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Ingredient.class.getSimpleName());
	/** Name **/
	private String name;
	/** Sequence **/
	private int sequence;
	/** Quantity **/
	private double quantity;
	/** Measurement Unit **/
	private MeasurementUnit measurementUnit;

	/** Constructor **/
	public Ingredient() {
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
	 * @return The quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            The quantity
	 */
	public void setQuantity(final double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return The MeasurementUnit
	 */
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}

	/**
	 * @param measurementUnit
	 *            The MeasurementUnit
	 */
	public void setMeasurementUnit(final MeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	/**
	 * @return The sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            The sequence
	 */
	public void setSequence(final int sequence) {
		this.sequence = sequence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((measurementUnit == null) ? 0 : measurementUnit.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + sequence;
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
		if (!(obj instanceof Ingredient)) {
			return false;
		}
		Ingredient other = (Ingredient) obj;
		if (measurementUnit != other.measurementUnit) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity)) {
			return false;
		}
		if (sequence != other.sequence) {
			return false;
		}
		return true;
	}

	@Override
	public Ingredient clone() {
		try {
			return (Ingredient) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
