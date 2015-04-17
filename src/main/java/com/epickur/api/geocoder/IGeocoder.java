package com.epickur.api.geocoder;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;

/**
 * Interface that allow to access to external services of geocoding.
 * 
 * @author cph
 * @version 1.0
 */
public interface IGeocoder {
	/**
	 * @param text
	 *            The text to search
	 * @return a Geo that represents the coordinate of the text given in parameter
	 * @throws GeoLocationException
	 *             If we could not access the coordinates
	 */
	Geo getPosition(String text) throws GeoLocationException;
}
