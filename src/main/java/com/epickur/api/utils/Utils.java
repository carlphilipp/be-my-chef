package com.epickur.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
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

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Utils.class.getSimpleName());

	/** Session timeout **/
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
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return prop;
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
		/** **/
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

	public static DateTime parseDate(final String date, final String format) throws EpickurParsingException {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
			return fmt.parseDateTime(date);
		} catch (Exception e) {
			throw new EpickurParsingException("Error while parsing date '" + date + "' with format '" + format + "'", e);
		}
	}
}
