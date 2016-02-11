package com.epickur.api.entity;

import com.epickur.api.enumeration.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Ingredient entity
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "sequence", "quantity", "measurementUnit" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class Ingredient extends AbstractEntity {

	/**
	 * Name
	 */
	private String name;
	/**
	 * Sequence
	 */
	private int sequence;
	/**
	 * Quantity
	 */
	private double quantity;
	/**
	 * Measurement Unit
	 */
	private MeasurementUnit measurementUnit;

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Ingredient clone() {
		return (Ingredient) super.clone();
	}
}
