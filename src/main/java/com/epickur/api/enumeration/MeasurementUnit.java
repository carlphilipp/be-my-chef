package com.epickur.api.enumeration;

/**
 * MeasurementUnit
 * 
 * @author cph
 * @version 1.0
 */
public enum MeasurementUnit {
	/** The enum **/
	g("g", "Gram", "g", true),
	kg("KG", "Kilogram", "kg", true),
	
	l("L", "Litre", "l", true),
	ml("ML", "Millilitre", "ml", true),
	
	cup("cup", "Cup", "cup", false),
	tbsp("tbsp", "Tablespoon", "tbsp", false),
	tsp("tsp", "Teaspoon", "tsp", false),
	
	each("each", "Each", "each", false),
	
	kj("kJ", "KiloJoules", "kJ", true);

	/** Short description **/
	private String shortDescription;
	/** Description **/
	private String description;
	/** Symbol **/
	private String symbol;
	/** Metric **/
	private boolean metric;

	/**
	 * @param shortDescription
	 *            the short description
	 * @param description
	 *            the description
	 * @param symbol
	 *            the symbol
	 * @param metric
	 *            the metric
	 */
	MeasurementUnit(final String shortDescription, final String description, final String symbol, final boolean metric) {
		this.shortDescription = shortDescription;
		this.description = description;
		this.symbol = symbol;
		this.metric = metric;
	}

	/**
	 * @return The short description
	 */
	public final String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @return The description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return The symbol
	 */
	public final String getSymbol() {
		return symbol;
	}

	/**
	 * @return True if it's metric
	 */
	public final boolean isMetric() {
		return metric;
	}
}
