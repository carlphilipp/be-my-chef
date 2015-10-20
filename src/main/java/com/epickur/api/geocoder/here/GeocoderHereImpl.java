package com.epickur.api.geocoder.here;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.geocoder.IGeocoder;

/**
 * Class that implement the access to external services of geocoding (Here).
 * 
 * @author cph
 * @version 1.0
 */
public class GeocoderHereImpl implements IGeocoder {

	@Override
	public Geo getPosition(final String text) throws GeoLocationException {
		Here here = new Here(text);
		return here.getGeolocation();
	}
}
