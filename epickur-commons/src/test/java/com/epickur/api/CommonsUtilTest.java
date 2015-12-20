package com.epickur.api;

import com.epickur.api.commons.CommonsUtil;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class CommonsUtilTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testParsePickupdate() {
		Object[] actual = CommonsUtil.parsePickupdate("mon-10:30");
		assertNotNull(actual);
		assertEquals(2, actual.length);
		assertEquals("mon", actual[0]);
		assertEquals(630, actual[1]);
	}

	@Test
	public void testFormatWithAmPm() {
		String actual = CommonsUtil.formatWithAmPm(0, 10);
		assertNotNull(actual);
		assertEquals("12:10AM", actual);
	}

	@Test
	public void testFormatWithAmPm2() {
		String actual = CommonsUtil.formatWithAmPm(0, 6);
		assertNotNull(actual);
		assertEquals("12:06AM", actual);
	}

	@Test
	public void testConvertToReadableDate() {
		String actual = CommonsUtil.convertToReadableDate("mon-10:30");
		assertNotNull(actual);
		assertEquals("Monday at 10:30AM", actual);
	}

	@Test
	public void testParsPickupdateNull() {
		Object[] actual = CommonsUtil.parsePickupdate(null);
		assertNull(actual);
	}

	@Test
	public void testParsPickupdateNoMatch() {
		Object[] actual = CommonsUtil.parsePickupdate("eeeee");
		assertNull(actual);
	}

	@Test
	public void testParseDate() {
		DateTime actual = CommonsUtil.parseDate("2015-01-01", "yyyy-MM-dd");
		assertNotNull(actual);
	}

	@Test
	public void testParseDateNoMatch() {
		thrown.expect(IllegalArgumentException.class);
		CommonsUtil.parseDate("01/01/2015", "yyyy-MM-dd");
	}

	@Test
	public void testGetCurrentDateInFormat() {
		String actual = CommonsUtil.getCurrentDateInFormat("yyyy-MM-dd");
		assertNotNull(actual);
	}

	@Test
	public void testGenerateRandomCode() {
		String actual = CommonsUtil.generateRandomCode();
		assertNotNull(actual);
	}
}
