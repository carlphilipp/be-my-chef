package com.epickur.api.here;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Geo;
import com.epickur.api.exception.HereException;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Access to Here APIs.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Component
public class Here {

	@Autowired
	private Utils utils;
	@Autowired
	public EpickurProperties properties;
	/**
	 * Url base
	 */
	private static final String URL_BASE = "http://geocoder.api.here.com";
	/**
	 * Url option
	 */
	private static final String URL_APP_CODE = "app_code=";
	/**
	 * Url option
	 */
	private static final String URL_APP_ID = "&app_id=";
	/**
	 * Url option
	 */
	private static final String URL_APP_GEN = "&gen=8";
	/**
	 * Url option
	 */
	private static final String URL_SEARCH_TEXT = "&searchtext=";
	/**
	 * Url option
	 */
	private static final String URL_MAX_RESULTS = "&maxresults=1";
	/**
	 * Url option
	 */
	private static final String URL_RESPONSE_ATTRIBUTES = "&responseattributes=none";
	/**
	 * Url option
	 */
	private static final String URL_LOCATION_ATTRIBUTES = "&locationattributes=none,ar";
	/**
	 * Url option
	 */
	private static final String URL_ADDRESS_ATTRIBUTES = "&addressattributes=none";
	/**
	 * Url option
	 */
	private static final String URL_JSON_ATTRIBUTES = "&jsonattributes=1";
	/**
	 * Url option
	 */
	private static final double RELEVANCE_THRESHOLD = 0.85;
	/**
	 * The adress to find
	 */
	private String text;

	public void setSearchText(final String text) {
		this.text = text;
	}

	/**
	 * This function build the URL.
	 *
	 * @return a url The URL
	 * @throws HereException If we could not access the coordinates
	 */
	protected final String urlBuilder() throws HereException {
		final StringBuilder stb = new StringBuilder();
		stb.append(URL_BASE);
		stb.append('/').append(properties.getHereApiVersion());
		stb.append('/').append(properties.getHereApiResource()).append('?');
		stb.append(URL_APP_CODE).append(properties.getHereAppCode());
		stb.append(URL_APP_ID).append(properties.getHereAppId());
		stb.append(URL_APP_GEN);
		stb.append(URL_RESPONSE_ATTRIBUTES);
		stb.append(URL_LOCATION_ATTRIBUTES);
		stb.append(URL_ADDRESS_ATTRIBUTES);
		stb.append(URL_MAX_RESULTS);
		stb.append(URL_JSON_ATTRIBUTES);
		try {
			stb.append(URL_SEARCH_TEXT + URLEncoder.encode(text, "UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			throw new HereException("Encoding Exception", e);
		}
		return stb.toString();
	}

	/**
	 * This function connect to the given address and return in a string the content of the page
	 *
	 * @param address the address
	 * @return the content of the page
	 * @throws HereException If we could not access the coordinates
	 */
	protected final String connectUrl(final String address) throws HereException {
		log.debug("URL: " + address);
		String toreturn = null;
		InputStreamReader in = null;
		try {
			final URL url = new URL(address);
			final URLConnection uc = url.openConnection();
			uc.setConnectTimeout(1000);
			final Charset charset = Charset.forName("UTF8");
			in = new InputStreamReader(uc.getInputStream(), charset);
			toreturn = IOUtils.toString(in);
		} catch (final IOException e) {
			throw new HereException("Error: " + HereException.CONNECT_ERROR, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return toreturn;
	}

	/**
	 * Parse the data and put them in a Geo object
	 *
	 * @param data The data
	 * @return Geo containing the data
	 * @throws HereException If we could not access the coordinates
	 */
	@SuppressWarnings("unchecked")
	private Geo getGeoFromStr(final String data) throws HereException {
		Geo geo = null;
		try {
			log.info(data);
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, Object> mapObject = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});

			if (mapObject.containsKey("response")) {
				final Map<String, Object> response = (Map<String, Object>) mapObject.get("response");
				if (response.containsKey("view")) {
					final List<Map<String, Object>> views = (List<Map<String, Object>>) response.get("view");
					if (views.size() > 0) {
						final Map<String, Object> view = views.get(0);
						if (view.containsKey("result")) {
							final List<Map<String, Object>> results = (List<Map<String, Object>>) view.get("result");
							if (results.size() > 0) {
								final Map<String, Object> result = results.get(0);
								double relevance = (double) result.get("relevance");
								if (relevance >= RELEVANCE_THRESHOLD) {
									if (result.containsKey("location")) {
										final Map<String, Object> location = (Map<String, Object>) result.get("location");
										if (location.containsKey("displayPosition")) {
											final Map<String, Object> displayPosition = (Map<String, Object>) location.get("displayPosition");
											geo = new Geo();
											geo.setLatitude((Double) displayPosition.get("latitude"));
											geo.setLongitude((Double) displayPosition.get("longitude"));
										}
									}
								} else {
									final String message = String.format("Could not geocode accurately '%s'", this.text);
									throw new HereException(message);
								}
							}
						}
					}
				}
			}
		} catch (final IndexOutOfBoundsException | IOException e) {
			throw new HereException("Geolocation error", e);
		}
		if (geo == null) {
			throw new HereException("Geolocation error. Data sent by Here: '" + data + "'");
		}
		return geo;
	}

	/**
	 * Run the search of the coordinates
	 *
	 * @return A Geo
	 * @throws HereException If we could not access the coordinates
	 */
	public final Geo getGeolocation() throws HereException {
		final String data = connectUrl(urlBuilder());
		return getGeoFromStr(data);
	}
}