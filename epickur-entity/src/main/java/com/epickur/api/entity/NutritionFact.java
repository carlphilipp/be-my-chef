package com.epickur.api.entity;

import com.epickur.api.enumeration.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * NutritionFacts enumeration
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "value", "unit" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class NutritionFact implements Cloneable {

	/** Name */
	private String name;
	/** Value */
	private Double value;
	/** Measurement unit */
	private MeasurementUnit unit;

	@Override
	public NutritionFact clone() throws CloneNotSupportedException {
		final NutritionFact nutritionFact = new NutritionFact();
		nutritionFact.setName(this.name);
		nutritionFact.setUnit(this.unit);
		nutritionFact.setValue(this.value);
		return nutritionFact;
	}
}
