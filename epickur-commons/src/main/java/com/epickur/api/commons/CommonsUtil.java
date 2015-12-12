package com.epickur.api.commons;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonsUtil {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(CommonsUtil.class.getSimpleName());

	/**
	 * @param pickupdate The pickup date
	 * @return An array of object containing in the first cell the day and in the second cell the time.
	 */
	public static Object[] parsePickupdate(final String pickupdate) {
		Object[] result = null;
		if (pickupdate != null) {
			final Pattern pattern = Pattern.compile("^(mon|tue|wed|thu|fri|sat|sun)\\-(([0-1][0-9]|2[0-3]):(([0-5][0-9])))$");
			final Matcher matcher = pattern.matcher(pickupdate);
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
		final DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return fmt.parseDateTime(date);
	}

	/**
	 * @param format The format
	 * @return A string
	 */
	public static String getCurrentDateInFormat(final String format) {
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
	public static void createTarGz(final List<String> inputs, final String output) {
		FileOutputStream dest = null;
		TarOutputStream out = null;
		BufferedInputStream origin = null;
		try {
			// Output file stream
			dest = new FileOutputStream(output);

			// Create a TarOutputStream
			out = new TarOutputStream(new BufferedOutputStream(dest));

			for (String input : inputs) {
				final File f = new File(input);
				out.putNextEntry(new TarEntry(f, f.getName()));
				origin = new BufferedInputStream(new FileInputStream(f));
				int count;
				byte[] data = new byte[2048];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}
				out.flush();
				origin.close();
			}
			out.close();
			dest.close();
		} catch (final IOException e) {
			LOG.error("Error while creating tar.gz: " + e.getLocalizedMessage(), e);
		} finally {
			IOUtils.closeQuietly(dest);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(origin);
		}
	}

	/**
	 * @return A random voucher code.
	 */
	public static String generateRandomCode() {
		final StringBuilder stb = new StringBuilder();
		stb.append(getRandomConsonants(3));
		stb.append(getRandomNumber());
		stb.append(getRandomConsonants(3));
		stb.append(getRandomNumber());
		return stb.toString();
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
		final String[] consonants = { "q", "w", "r", "t", "p", "s", "d", "f", "g", "h", "j", "k", "z", "x", "c", "v", "b", "n", "m" };
		final StringBuilder res = new StringBuilder();
		final RandomDataGenerator randomData = new RandomDataGenerator();
		for (int i = 0; i < size; i++) {
			res.append(consonants[randomData.nextInt(0, consonants.length - 1)]);
		}
		return res.toString().toUpperCase();
	}
}
