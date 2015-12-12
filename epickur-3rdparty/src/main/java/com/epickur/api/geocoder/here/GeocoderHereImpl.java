package com.epickur.api.geocoder.here;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.geocoder.IGeocoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that implement the access to external services of geocoding (Here).
 * 
 * @author cph
 * @version 1.0
 */
@Component
public class GeocoderHereImpl implements IGeocoder {

	@Autowired
	private Here here;

	@Override
	public Geo getPosition(final String text) throws GeoLocationException {
		here.setSearchText(text);
		return here.getGeolocation();
	}
}
