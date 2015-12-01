package com.epickur.api.commons;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonsUtil {

	/**
	 * @param pickupdate The pickup date
	 * @return An array of object containing in the first cell the day and in the second cell the time.
	 */
	public static Object[] parsePickupdate(final String pickupdate) {
		Object[] result = null;
		if (pickupdate != null) {
			Pattern pattern = Pattern.compile("^(mon|tue|wed|thu|fri|sat|sun)\\-(([0-1][0-9]|2[0-3]):(([0-5][0-9])))$");
			Matcher matcher = pattern.matcher(pickupdate);
			if (matcher.matches()) {
				result = new Object[2];
				// Extract the day of the week
				result[0] = matcher.group(1).toLowerCase();
				// Convert in minutes the given time
				result[1] = Integer.parseInt(matcher.group(3)) * 60 + Integer.parseInt(matcher.group(4));
			}
		}
		return result;
	}

	/**
	 * @param date   The date
	 * @param format The date format
	 * @return A DateTime
	 */
	public static DateTime parseDate(final String date, final String format) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return fmt.parseDateTime(date);
	}
}
