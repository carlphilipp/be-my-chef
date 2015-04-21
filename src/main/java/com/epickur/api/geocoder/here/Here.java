package com.epickur.api.geocoder.here;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.HereException;
import com.epickur.api.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

/**
 * Access to Here APIs.
 * 
 * @author cph
 * @version 1.0
 */
public class Here {
	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Here.class.getSimpleName());
	/** Url base **/
	private static final String URL_BASE = "http://geocoder.cit.api.here.com";
	/** Url option **/
	private static final String URL_APP_CODE = "app_code=";
	/** Url option **/
	private static final String URL_APP_ID = "&app_id=";
	/** Url option **/
	private static final String URL_APP_GEN = "&gen=8";
	/** Url option **/
	private static final String URL_SEARCH_TEXT = "&searchtext=";
	/** Url option **/
	private static final String URL_MAX_RESULTS = "&maxresults=1";
	/** Url option **/
	private static final String URL_RESPONSE_ATTRIBUTES = "&responseattributes=none";
	/** Url option **/
	private static final String URL_LOCATION_ATTRIBUTES = "&locationattributes=none,ar";
	/** Url option **/
	private static final String URL_ADDRESS_ATTRIBUTES = "&addressattributes=none";
	/** Url option **/
	private static final String URL_JSON_ATTRIBUTES = "&jsonattributes=1";
	/** Url option **/
	private static final double RELEVANCE_THRESHOLD = 0.85;
	/** Here App Id **/
	private String appId;
	/** Here App Code **/
	private String appCode;
	/** Here App Resource **/
	private String resource;
	/** Here App Version **/
	private String version;
	/** The adress to find **/
	private String text;

	/**
	 * @param text
	 *            The address to find
	 */
	public Here(final String text) {
		this.text = text;
		Properties properties = Utils.getEpickurProperties();
		this.appId = properties.getProperty("here.app.id");
		this.appCode = properties.getProperty("here.app.code");
		this.resource = properties.getProperty("here.api.resource");
		this.version = properties.getProperty("here.api.version");
	}

	/**
	 * This function build the URL.
	 * 
	 * @return a url The URL
	 * @throws HereException
	 *             If we could not access the coordinates
	 */
	protected final String urlBuilder() throws HereException {
		final StringBuilder stb = new StringBuilder();
		stb.append(URL_BASE);
		stb.append('/').append(this.version);
		stb.append('/').append(this.resource).append('?');
		stb.append(URL_APP_CODE).append(this.appCode);
		stb.append(URL_APP_ID).append(this.appId);
		stb.append(URL_APP_GEN);
		stb.append(URL_RESPONSE_ATTRIBUTES);
		stb.append(URL_LOCATION_ATTRIBUTES);
		stb.append(URL_ADDRESS_ATTRIBUTES);
		stb.append(URL_MAX_RESULTS);
		stb.append(URL_JSON_ATTRIBUTES);
		try {
			stb.append(URL_SEARCH_TEXT + URLEncoder.encode(text, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new HereException("Encoding Exception", e);
		}
		return stb.toString();
	}

	/**
	 * This function connect to the given address and return in a string the content of the page
	 * 
	 * @param address
	 *            the address
	 * @return the content of the page
	 * @throws HereException
	 *             If we could not access the coordinates
	 */
	protected final String connectUrl(final String address) throws HereException {
		LOG.debug("URL: " + address);
		String toreturn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(address);
			URLConnection uc = url.openConnection();
			uc.setConnectTimeout(1000);
			Charset charset = Charset.forName("UTF8");
			in = new InputStreamReader(uc.getInputStream(), charset);
			toreturn = IOUtils.toString(in);
		} catch (IOException e) {
			throw new HereException("Error: " + HereException.CONNECT_ERROR, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return toreturn;
	}

	/**
	 * Parse the data and put them in a Geo object
	 * 
	 * @param data
	 *            The data
	 * @return Geo containing the data
	 * @throws HereException
	 *             If we could not access the coordinates
	 */
	private Geo getGeoFromStr(final String data) throws HereException {
		Geo geo = null;
		try {
			BasicDBObject dbo = (BasicDBObject) JSON.parse(data);
			if (dbo.containsField("response")) {
				BasicBSONObject response = (BasicBSONObject) dbo.get("response");
				if (response.containsField("view")) {
					BasicBSONList views = (BasicBSONList) response.get("view");
					if (views.size() > 0) {
						BasicBSONObject view = (BasicBSONObject) views.get(0);
						if (view.containsField("result")) {
							BasicBSONList results = (BasicBSONList) view.get("result");
							if (results.size() > 0) {
								BasicBSONObject result = (BasicBSONObject) results.get(0);
								double relevance = result.getDouble("relevance");
								if (relevance >= RELEVANCE_THRESHOLD) {
									if (result.containsField("location")) {
										BasicBSONObject location = (BasicBSONObject) result.get("location");
										if (location.containsField("displayPosition")) {
											BasicBSONObject displayPosition = (BasicBSONObject) location.get("displayPosition");
											geo = new Geo();
											geo.setLatitude(Double.parseDouble(displayPosition.getString("latitude")));
											geo.setLongitude(Double.parseDouble(displayPosition.getString("longitude")));
										}
									}
								} else {
									String message = String.format("Could not geocode accurately '%s'", this.text);
									throw new HereException(message);
								}
							}
						}
					}
				}
			}
		} catch (IndexOutOfBoundsException | JSONParseException e) {
			throw new HereException("Geolocation error", e);
		}
		return geo;
	}

	/**
	 * Run the search of the coordinates
	 * 
	 * @return A Geo
	 * @throws HereException
	 *             If we could not access the coordinates
	 */
	public final Geo getGeolocation() throws HereException {
		String data = connectUrl(urlBuilder());
		return getGeoFromStr(data);
	}
}
