package com.epickur.api.here;

import com.epickur.api.config.GeocoderConfigTest;
import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.exception.HereException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GeocoderConfigTest.class)
public class GeocoderHereImpIT {

	@Autowired
	private GeocoderHereImpl geocoderHere;

	@Test
	public void getPositionTest() {
		try {
			// When
			Geo geo = geocoderHere.getPosition("832 W. Wrightwood, Chicago");

			// Then
			assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
			assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
		} catch (GeoLocationException e) {
			fail("Geo location error: " + e.getLocalizedMessage());
		}
	}

	@Test(expected = HereException.class)
	public void getPositionTest2() throws GeoLocationException {
		// When
		Geo geo = geocoderHere.getPosition("WTF, Paris, Turkey");

		// Then
		assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
		assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
	}
}
