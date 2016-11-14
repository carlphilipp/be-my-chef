package com.epickur.api;

import com.epickur.api.commons.CommonsUtil;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CommonsUtilTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testParsePickupdate() {
		// When
		Optional<Object[]> actual = CommonsUtil.parsePickupdate("mon-10:30");

		// Then
		Object[] objects = actual.orElseThrow(AssertionError::new);
		assertEquals(2, objects.length);
		assertEquals("mon", objects[0]);
		assertEquals(630, objects[1]);
	}

	@Test
	public void testFormatWithAmPm() {
		// When
		String actual = CommonsUtil.formatWithAmPm(0, 10);

		// Then
		assertNotNull(actual);
		assertEquals("12:10AM", actual);
	}

	@Test
	public void testFormatWithAmPm2() {
		// When
		String actual = CommonsUtil.formatWithAmPm(0, 6);

		// Then
		assertNotNull(actual);
		assertEquals("12:06AM", actual);
	}

	@Test
	public void testConvertToReadableDate() {
		// When
		String actual = CommonsUtil.convertToReadableDate("mon-10:30");

		// Then
		assertNotNull(actual);
		assertEquals("Monday at 10:30AM", actual);
	}

	@Test
	public void testParsPickupdateNull() {
		// Then
		thrown.expect(NullPointerException.class);

		// When
		CommonsUtil.parsePickupdate(null);
	}

	@Test
	public void testParsPickupdateNoMatch() {
		// When
		Optional<Object[]> actual = CommonsUtil.parsePickupdate("eeeee");

		// Then
		assertFalse(actual.isPresent());
	}

	@Test
	public void testParseDate() {
		// When
		DateTime actual = CommonsUtil.parseDate("2015-01-01", "yyyy-MM-dd");

		// Then
		assertNotNull(actual);
	}

	@Test
	public void testParseDateNoMatch() {
		// Then
		thrown.expect(IllegalArgumentException.class);

		// When
		CommonsUtil.parseDate("01/01/2015", "yyyy-MM-dd");
	}

	@Test
	public void testGetCurrentDateInFormat() {
		// When
		String actual = CommonsUtil.getCurrentDateInFormat("yyyy-MM-dd");

		// Then
		assertNotNull(actual);
	}

	@Test
	public void testGenerateRandomCode() {
		// When
		String actual = CommonsUtil.generateRandomCode();

		// Then
		assertNotNull(actual);
	}
}
