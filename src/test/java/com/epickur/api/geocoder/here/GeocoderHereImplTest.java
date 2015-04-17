package com.epickur.api.geocoder.here;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.exception.HereException;

public class GeocoderHereImplTest {

	@Test
	public void getPositionTest() throws GeoLocationException {
		GeocoderHereImpl geoCoder = new GeocoderHereImpl();
		Geo geo = geoCoder.getPosition("832 W. Wrightwood, Chicago");
		assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
		assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
	}

	@Test(expected = HereException.class)
	public void getPositionTest2() throws GeoLocationException {
		GeocoderHereImpl geoCoder = new GeocoderHereImpl();
		Geo geo = geoCoder.getPosition("WTF, Paris, Turkey");
		assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
		assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
	}
}
