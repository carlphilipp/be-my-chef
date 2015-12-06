package com.epickur.api.utils;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.payment.stripe.StripeTestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Utils class
 *
 * @author cph
 * @version 1.0
 */
public final class Utils {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(Utils.class.getSimpleName());
	/**
	 * Session timeout
	 */
	public static final Integer SESSION_TIMEOUT;

	/**
	 * Private Constructor
	 */
	private Utils() {
	}

	static {
		Properties prop = Utils.getEpickurProperties();
		SESSION_TIMEOUT = Integer.valueOf(prop.getProperty("session.timeout"));
	}

	/**
	 * Check is the User password is correct
	 *
	 * @param password The password
	 * @param user     The User
	 * @return True if the password is correct
	 * @throws EpickurException If someting went bad
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
	 * Get properties from a property file
	 *
	 * @return A properties file
	 */
	public static Properties getEpickurProperties() {
		Properties prop = new Properties();
		try {
			prop.load(Utils.class.getClassLoader().getResourceAsStream("epickur.properties"));
			if (prop.getProperty("address").equals("${address}")) {
				LOG.trace("Loading local properties...");
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
	 * @param properties The properties we want to inject some new properties in.
	 * @return The new properties
	 */
	private static Properties loadLocal(final Properties properties) {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = Utils.class.getResource("/env/local.properties").openStream();
			prop.load(in);
			for (Entry<Object, Object> e : prop.entrySet()) {
				properties.put(e.getKey(), e.getValue());
			}
			injectStripeInProperties(properties);
		} catch (Exception e) {
			LOG.error("Can't load resource env/local.properties. Please create it and put the right value in it.", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return properties;
	}

	private static void injectStripeInProperties(final Properties properties) {
		String stripeKey = StripeTestUtils.getStripeTestKey();
		LOG.trace("Injecting stripe key into property file: {}", stripeKey);
		properties.put("stripe.key", stripeKey);
	}

	/**
	 * @param file The file
	 * @return An InputStream of the file
	 */
	public static InputStream getResource(final String file) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
	}

	/**
	 * Get the API key from the property file
	 *
	 * @return The API Key.
	 * @throws IOException If something went wrong
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
	 * Check if the Key is valid
	 *
	 * @param key The Key
	 * @return True if the Key is valid
	 */
	public static boolean isValid(final Key key) {
		if (key == null) {
			return false;
		} else {
			DateTime currentTime = new DateTime();
			int daysBetween = Math.abs(Days.daysBetween(key.getCreatedAt(), currentTime).getDays());
			if (daysBetween > SESSION_TIMEOUT) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param date   The date
	 * @param format The date format
	 * @return A DateTime
	 * @throws EpickurParsingException If a parsing exception occured
	 */
	public static DateTime parseDate(final String date, final String format) throws EpickurParsingException {
		try {
			return CommonsUtil.parseDate(date, format);
		} catch (Exception e) {
			throw new EpickurParsingException("Error while parsing date '" + date + "' with format '" + format + "'", e);
		}
	}

	/**
	 * Convert String to list of dish type
	 *
	 * @param types The String to convert
	 * @return The list of DishType created
	 */
	public static List<DishType> stringToListDishType(final String types) {
		List<DishType> res = new ArrayList<>();
		String[] typesArray = types.split(",");
		for (String temp : typesArray) {
			res.add(DishType.fromString(temp));
		}
		return res;
	}

	/**
	 * Convert a string to Geo object.
	 *
	 * @param str the string
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
}
