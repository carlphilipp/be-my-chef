package com.epickur.api.commons;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarOutputStream;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Commons Util. Collections of function that can be needed anywhere in the project and that have no dependencies on other libraries.
 *
 * @author cph
 */
@Log4j2
public class CommonsUtil {

	private static String PICKUP_DATE_REGEX = "^(mon|tue|wed|thu|fri|sat|sun)\\-(([0-1][0-9]|2[0-3]):([0-5][0-9]))$";

	/**
	 * @param pickupdate The pickup date
	 * @return An array of object containing in the first cell the day and in the second cell the time.
	 */
	public static Optional<Object[]> parsePickupdate(@NonNull final String pickupdate) {
		Object[] result = null;
		final Pattern pattern = Pattern.compile(PICKUP_DATE_REGEX);
		final Matcher matcher = pattern.matcher(pickupdate);
		if (matcher.matches()) {
			result = new Object[2];
			// Extract the day of the week
			result[0] = matcher.group(1).toLowerCase();
			// Convert in minutes the given time
			result[1] = Integer.parseInt(matcher.group(3)) * 60 + Integer.parseInt(matcher.group(4));
		}
		return Optional.ofNullable(result);
	}

	public static String convertToReadableDate(@NonNull final String pickupdate) {
		final StringBuilder result = new StringBuilder();
		final Pattern pattern = Pattern.compile(PICKUP_DATE_REGEX);
		final Matcher matcher = pattern.matcher(pickupdate);
		if (matcher.matches()) {
			result.append(convertToReadableDay(matcher.group(1).toLowerCase())).append(" ");
			// Convert in minutes the given time
			int hours = Integer.parseInt(matcher.group(3));
			int mins = Integer.parseInt(matcher.group(4));
			result.append("at ").append(formatWithAmPm(hours, mins));
		}
		return result.toString();
	}

	protected static String convertToReadableDay(@NonNull final String day) {
		if (day != null && day.length() != 3) {
			throw new IllegalArgumentException();
		}
		switch (day) {
			case "mon":
				return "Monday";
			case "tue":
				return "Tuesday";
			case "wed":
				return "Wednesday";
			case "thu":
				return "Thursday";
			case "fri":
				return "Friday";
			case "sat":
				return "Saturday";
			case "sun":
				return "Sunday";
		}
		throw new IllegalArgumentException();
	}

	public static String formatWithAmPm(final int hour, final int minute) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);

		final StringBuilder result = new StringBuilder();
		int hour12FormatLocal = calendar.get(Calendar.HOUR) != 0 ? calendar.get(Calendar.HOUR) : 12;

		final String minute1 = calendar.get(Calendar.MINUTE) < 10
			? "0" + calendar.get(Calendar.MINUTE)
			: Integer.toString(calendar.get(Calendar.MINUTE));

		final int amPm = calendar.get(Calendar.AM_PM);
		final String amPmResult = (amPm == 0) ? "AM" : "PM";

		result.append(hour12FormatLocal)
			.append(":")
			.append(minute1)
			.append(amPmResult);

		return result.toString();
	}

	/**
	 * @param date   The date
	 * @param format The date format
	 * @return A DateTime
	 */
	public static DateTime parseDate(@NonNull final String date, @NonNull final String format) {
		final DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return fmt.parseDateTime(date);
	}

	/**
	 * @param format The format
	 * @return A string
	 */
	public static String getCurrentDateInFormat(@NonNull final String format) {
		final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"));
		final DateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(cal.getTimeZone());
		return formatter.format(cal.getTime());
	}

	/**
	 * Create tar.gz file
	 *
	 * @param inputs the input path list
	 * @param output the output path
	 */
	public static void createTarGz(@NonNull final List<String> inputs, @NonNull final String output) {
		try (final FileOutputStream dest = new FileOutputStream(output);
			 final TarOutputStream out = new TarOutputStream(new BufferedOutputStream(dest))) {
			for (String input : inputs) {
				final File f = new File(input);
				out.putNextEntry(new TarEntry(f, f.getName()));
				try (final BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f))) {
					int count;
					byte[] data = new byte[2048];
					while ((count = origin.read(data)) != -1) {
						out.write(data, 0, count);
					}
					out.flush();
				}
			}
		} catch (final IOException e) {
			log.error("Error while creating tar.gz: {}", e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @return A random voucher code.
	 */
	public static String generateRandomCode() {
		return new StringBuilder()
			.append(getRandomConsonants(3))
			.append(getRandomNumber())
			.append(getRandomConsonants(3))
			.append(getRandomNumber()).toString();
	}

	/**
	 * @return A random number between 2 and 9
	 */
	private static int getRandomNumber() {
		final RandomDataGenerator randomData = new RandomDataGenerator();
		// Removed 0 and 1
		return randomData.nextInt(2, 9);
	}

	/**
	 * @param size Max size
	 * @return A Random consonants string
	 */
	private static String getRandomConsonants(final int size) {
		// Removed l
		final String[] consonants = {"q", "w", "r", "t", "p", "s", "d", "f", "g", "h", "j", "k", "z", "x", "c", "v", "b", "n", "m"};
		final StringBuilder res = new StringBuilder();
		final RandomDataGenerator randomData = new RandomDataGenerator();
		for (int i = 0; i < size; i++) {
			res.append(consonants[randomData.nextInt(0, consonants.length - 1)]);
		}
		return res.toString().toUpperCase();
	}
}
