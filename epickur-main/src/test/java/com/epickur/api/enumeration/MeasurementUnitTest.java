package com.epickur.api.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MeasurementUnitTest {

	@Test
	public void testCurrency1() {
		MeasurementUnit mu = MeasurementUnit.KG;
		assertEquals("KG", mu.getShortDescription());
		assertEquals("Kilogram", mu.getDescription());
		assertEquals("kg", mu.getSymbol());
		assertEquals(true, mu.isMetric());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCurrency2() {
		Currency.getEnum(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCurrency3() {
		Currency.getEnum("    ");
	}

	@Test
	public void testCurrency4() {
		Currency aud = Currency.getEnum("AUD");
		assertEquals(Currency.AUD, aud);
	}
}
