package com.epickur.api.utils;

import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.config.EmailConfigTest;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailConfigTest.class)
public class UtilsTest {

	@Autowired
	private Utils utils;

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest() throws EpickurParsingException {
		// Given
		String date = "";
		String format = "";

		// When
		utils.parseDate(date, format);
	}

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest2() throws EpickurParsingException {
		// Given
		String date = "";
		String format = "YYYY/MM/dd";

		// When
		utils.parseDate(date, format);
	}

	@Test(expected = EpickurParsingException.class)
	public void parseDateTest3() throws EpickurParsingException {
		String date = "2015/03/03";
		String format = "";

		// When
		utils.parseDate(date, format);
	}

	@Test
	public void parseDateTest4() throws EpickurParsingException {
		// Given
		String date = "2015/03/19";
		String format = "YYYY/MM/dd";

		// When
		DateTime dateTime = utils.parseDate(date, format);

		// Then
		assertNotNull(dateTime);
		assertEquals(2015, dateTime.getYear());
		assertEquals(3, dateTime.getMonthOfYear());
		assertEquals(19, dateTime.getDayOfMonth());
	}

	@Test
	public void parseDateTest5() throws EpickurParsingException {
		// Given
		String date = "2015/03/19 083035";
		String format = "YYYY/MM/dd hhmmss";

		// When
		DateTime dateTime = utils.parseDate(date, format);

		// Then
		assertNotNull(dateTime);
		assertEquals(2015, dateTime.getYear());
		assertEquals(3, dateTime.getMonthOfYear());
		assertEquals(19, dateTime.getDayOfMonth());
		assertEquals(8, dateTime.getHourOfDay());
		assertEquals(30, dateTime.getMinuteOfHour());
		assertEquals(35, dateTime.getSecondOfMinute());
	}
}
