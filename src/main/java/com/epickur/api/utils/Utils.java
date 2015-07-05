package com.epickur.api.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarOutputStream;

import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Utils class
 * 
 * @author cph
 * @version 1.0
 */
public final class Utils {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Utils.class.getSimpleName());
	/** Session timeout */
	private static Integer sessionTimeout;

	/**
	 * Private Constructor
	 */
	private Utils() {
	}

	static {
		Properties prop = Utils.getEpickurProperties();
		sessionTimeout = Integer.valueOf(prop.getProperty("session.timeout"));
	}

	/**
	 * Check is the User password is correct
	 * 
	 * @param password
	 *            The password
	 * @param user
	 *            The User
	 * @return True if the password is correct
	 * @throws EpickurException
	 *             If someting went bad
	 */
	public static boolean isPasswordCorrect(final String password, final User user) throws EpickurException {
		boolean res = true;
		final int sixtyFour = 64;
		String passwordHashed = Security.encodeToSha256(password);
		String saltHashed = user.getPassword().substring(0, sixtyFour);
		String encryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
		String encryptedPasswordSaltToTest = Security.encodeToSha256(passwordHashed + saltHashed);
		if (!encryptedPasswordSalt.equals(encryptedPasswordSaltToTest)) {
			res = false;
		}
		return res;
	}

	/**
	 * @param password
	 *            The password
	 * @return The encrypted password
	 * @throws EpickurException
	 *             If someting went bad
	 */
	public static String getEncryptedPassword(final String password) throws EpickurException {
		String saltHashed = null;
		String cryptedPasswordSalt = null;
		String passwordHashed = Security.encodeToSha256(password);
		saltHashed = Security.generateSalt();
		cryptedPasswordSalt = Security.encodeToSha256(passwordHashed + saltHashed);
		return saltHashed + cryptedPasswordSalt;
	}

	/**
	 * Get properties from a property file
	 * 
	 * @return A properties file
	 */
	public static Properties getEpickurProperties() {
		Properties prop = new Properties();
		try {
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("epickur.properties"));
			if (prop.getProperty("address").equals("${address}")) {
				prop = loadLocal(prop);
			}
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return prop;
	}

	/**
	 * Get properties from a the local file. Used only to inject param at run time for eclipse. Not needed for Maven
	 * 
	 * @param properties
	 *            The properties we want to inject some new properties in.
	 * @return The new properties
	 */
	private static Properties loadLocal(final Properties properties) {
		Properties prop = new Properties();
		try {
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("env/local.properties"));
			for (Entry<Object, Object> e : prop.entrySet()) {
				properties.put(e.getKey(), e.getValue());
			}
		} catch (Exception e) {
			LOG.error("Can't load resource env/local.properties. Please create it and put the right value in it.", e);
		}
		return properties;
	}

	/**
	 * @param file
	 *            The file
	 * @return An InputStream of the file
	 */
	public static InputStream getResource(final String file) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
	}

	/**
	 * Get the API key from the property file
	 * 
	 * @return The API Key.
	 * @throws IOException
	 *             If something went wrong
	 */
	public static String getAPIKey() throws IOException {
		InputStreamReader in = null;
		String apiKey = null;
		try {
			Charset charset = Charset.forName("UTF8");
			in = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("api.key"), charset);
			apiKey = IOUtils.toString(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return apiKey;
	}

	/**
	 * @author cph
	 *
	 */
	public static final class MyStrategy extends PropertyNamingStrategy {
		/** Serializer **/
		private static final long serialVersionUID = 1L;

		@Override
		public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName) {
			return convert(defaultName);
		}

		@Override
		public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
			return convert(defaultName);
		}

		@Override
		public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
			return convert(defaultName);
		}

		/**
		 * @param defaultName
		 *            The default name
		 * @return a String
		 */
		private String convert(final String defaultName) {
			if (defaultName.equals("id")) {
				return "_id";
			}
			return defaultName;
		}
	}

	/**
	 * Check if the Key is valid
	 * 
	 * @param key
	 *            The Key
	 * @return True if the Key is valid
	 */
	public static boolean isValid(final Key key) {
		if (key == null) {
			return false;
		} else {
			DateTime currentTime = new DateTime();
			if (Days.daysBetween(key.getCreatedAt(), currentTime).getDays() > sessionTimeout) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param date
	 *            The date
	 * @param format
	 *            The date format
	 * @return A DateTime
	 * @throws EpickurParsingException
	 *             If a parsing exception occured
	 */
	public static DateTime parseDate(final String date, final String format) throws EpickurParsingException {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
			return fmt.parseDateTime(date);
		} catch (Exception e) {
			throw new EpickurParsingException("Error while parsing date '" + date + "' with format '" + format + "'", e);
		}
	}

	/**
	 * Convert String to list of dish type
	 * 
	 * @param types
	 *            The String to convert
	 * @return The list of DishType created
	 */
	public static List<DishType> stringToListDishType(final String types) {
		List<DishType> res = new ArrayList<DishType>();
		String[] typesArray = types.split(",");
		for (String temp : typesArray) {
			res.add(DishType.fromString(temp));
		}
		return res;
	}

	/**
	 * Convert a string to Geo object.
	 * 
	 * @param str
	 *            the string
	 * @return A Geo object
	 */
	public static Geo stringToGeo(final String str) {
		Geo geo = new Geo();
		String[] geoArray = str.split(",");
		// TODO Not sure about 0 or 1, check which one is correct
		geo.setLatitude(Double.valueOf(geoArray[0]));
		geo.setLongitude(Double.valueOf(geoArray[1]));
		return geo;
	}

	/**
	 * @param pickupdate
	 *            The pickup date
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
	 * @param format
	 *            The format
	 * @return A string
	 */
	public static String getCurrentDateInFormat(final String format) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"));
		DateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(cal.getTimeZone());
		return formatter.format(cal.getTime());
	}

	/**
	 * @param list
	 *            The list to convert
	 * @return A converted array
	 */
	public static String[] convertListToArray(final List<String> list) {
		String[] res = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			res[i] = list.get(i);
		}
		return res;
	}

	/**
	 * Create tar.gz file
	 * 
	 * @param inputs
	 *            the input path list
	 * @param output
	 *            the output path
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
				File f = new File(input);
				out.putNextEntry(new TarEntry(f, f.getName()));
				origin = new BufferedInputStream(new FileInputStream(f));
				int count;
				byte [] data = new byte[2048];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}
				out.flush();
				origin.close();
			}
			out.close();
			dest.close();
		} catch (IOException e) {
			if (dest != null) {
				try {
					dest.close();
				} catch (IOException e1) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
				}
			}
			if (origin != null) {
				try {
					origin.close();
				} catch (IOException e1) {
				}
			}
		}
	}
}
