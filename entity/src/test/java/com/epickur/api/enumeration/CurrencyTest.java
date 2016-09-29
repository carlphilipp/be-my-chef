package com.epickur.api.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CurrencyTest {

	@Test
	public void testCurrency1() {
		Currency currency = Currency.AUD;
		assertEquals("Australia Dollar", currency.getName());
		assertEquals("$", currency.getSymbol());
		assertEquals("AUD", currency.getCode());
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
