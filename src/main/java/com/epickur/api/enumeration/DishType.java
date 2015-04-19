package com.epickur.api.enumeration;

/**
 * @author cph
 * @version 1.0
 */
public enum DishType {

	MEAT("meat"), FISH("fish"), VEGAN("vegan");

	DishType(final String type) {
		this.type = type;
	}

	public static DishType fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (DishType dishType : values()) {
			if (value.equalsIgnoreCase(dishType.getType())) {
				return dishType;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public String getType() {
		return type;
	}

	private String type;

}
