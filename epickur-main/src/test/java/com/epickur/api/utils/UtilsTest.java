package com.epickur.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

import com.epickur.api.exception.EpickurParsingException;

public class UtilsTest {

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest() throws EpickurParsingException {
		String date = "";
		String format = "";
		Utils.parseDate(date, format);
	}

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest2() throws EpickurParsingException {
		String date = "";
		String format = "YYYY/MM/dd";
		Utils.parseDate(date, format);
	}

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest3() throws EpickurParsingException {
		String date = "2015/03/03";
		String format = "";
		Utils.parseDate(date, format);
	}

	@Test
	public void parseDateTest4() throws EpickurParsingException {
		String date = "2015/03/19";
		String format = "YYYY/MM/dd";
		DateTime dateTime = Utils.parseDate(date, format);
		assertNotNull(dateTime);
		assertEquals(2015, dateTime.getYear());
		assertEquals(3, dateTime.getMonthOfYear());
		assertEquals(19, dateTime.getDayOfMonth());
	}
	
	@Test
	public void parseDateTest5() throws EpickurParsingException {
		String date = "2015/03/19 083035";
		String format = "YYYY/MM/dd hhmmss";
		DateTime dateTime = Utils.parseDate(date, format);
		assertNotNull(dateTime);
		assertEquals(2015, dateTime.getYear());
		assertEquals(3, dateTime.getMonthOfYear());
		assertEquals(19, dateTime.getDayOfMonth());
		assertEquals(8, dateTime.getHourOfDay());
		assertEquals(30, dateTime.getMinuteOfHour());
		assertEquals(35, dateTime.getSecondOfMinute());
	}
}
