package com.epickur.api.geocoder.here;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.exception.HereException;

public class HereTest {

	@Test
	public void testHereSuccess() {
		try {
			Here here = new Here("832 W. Wrightwood, Chicago");
			Geo geo = here.getGeolocation();
			assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
			assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
		} catch (GeoLocationException e) {
			e.printStackTrace();
			fail("Geo location error: " + e.getLocalizedMessage());
		}
	}

	@Test(expected = HereException.class)
	public void testHereFailure() throws HereException {
		Here here = new Here("WTF, Paris, Turkey");
		here.getGeolocation();
	}
}