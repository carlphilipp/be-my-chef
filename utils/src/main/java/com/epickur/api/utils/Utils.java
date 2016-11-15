package com.epickur.api.utils;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.security.Security;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utils class
 *
 * @author cph
 * @version 1.0
 */
@AllArgsConstructor(onConstructor = @_(@Autowired))
@Component
public class Utils {

	@NonNull
	public EpickurProperties properties;

	/**
	 * Check is the User password is correct
	 *
	 * @param password The password
	 * @param user     The User
	 * @return True if the password is correct
	 * @throws EpickurException If someting went bad
	 */
	public boolean isPasswordCorrect(final String password, final User user) {
		boolean res = true;
		final int sixtyFour = 64;
		final String passwordHashed = Security.encodeToSha256(password);
		final String saltHashed = user.getPassword().substring(0, sixtyFour);
		final String encryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
		final String encryptedPasswordSaltToTest = Security.encodeToSha256(passwordHashed + saltHashed);
		if (!encryptedPasswordSalt.equals(encryptedPasswordSaltToTest)) {
			res = false;
		}
		return res;
	}

	/**
	 * @param file The file
	 * @return An InputStream of the file
	 */
	public InputStream getResource(final String file) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
	}

	/**
	 * Get the API key from the property file
	 *
	 * @return The API Key.
	 * @throws IOException If something went wrong
	 */
	public String getAPIKey() throws IOException {
		final Charset charset = Charset.forName("UTF8");
		try (final InputStreamReader in = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("api.key"), charset)) {
			return IOUtils.toString(in);
		}
	}

	/**
	 * Check if the Key is valid
	 *
	 * @param key The Key
	 * @return True if the Key is valid
	 */
	public boolean isValid(final Key key) {
		if (key == null) {
			return false;
		} else {
			final DateTime currentTime = new DateTime();
			int daysBetween = Math.abs(Days.daysBetween(key.getCreatedAt(), currentTime).getDays());
			if (daysBetween > properties.getSessionTimeout()) {
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
	public DateTime parseDate(final String date, final String format) throws EpickurParsingException {
		try {
			return CommonsUtil.parseDate(date, format);
		} catch (final Exception e) {
			throw new EpickurParsingException("Error while parsing date '" + date + "' with format '" + format + "'", e);
		}
	}

	/**
	 * Convert String to list of dish type
	 *
	 * @param types The String to convert
	 * @return The list of DishType created
	 */
	public List<DishType> stringToListDishType(final String types) {
		final List<DishType> res = new ArrayList<>();
		final List<String> typesArray = Arrays.asList(types.split(","));
		typesArray.forEach(temp -> res.add(DishType.fromString(temp)));
		return res;
	}

	/**
	 * Convert a string to Geo object.
	 *
	 * @param str the string
	 * @return A Geo object
	 */
	public Geo stringToGeo(final String str) {
		final Geo geo = new Geo();
		final String[] geoArray = str.split(",");
		// TODO Not sure about 0 or 1, check which one is correct
		geo.setLatitude(Double.parseDouble(geoArray[0]));
		geo.setLongitude(Double.parseDouble(geoArray[1]));
		return geo;
	}
}
