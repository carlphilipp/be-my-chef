package com.epickur.api.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.enumeration.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Ingredient entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "sequence", "quantity", "measurementUnit" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class Ingredient extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Ingredient.class.getSimpleName());
	/** Name */
	private String name;
	/** Sequence */
	private int sequence;
	/** Quantity */
	private double quantity;
	/** Measurement Unit */
	private MeasurementUnit measurementUnit;

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
