package com.epickur.api.geocoder.here;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.epickur.api.config.GeocoderConfigTest;
import org.junit.Test;

import com.epickur.api.entity.Geo;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.exception.HereException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GeocoderConfigTest.class)
public class HereTest {

	@Autowired
	private Here here;

	@Test
	public void testHereSuccess() {
		try {
			here.setSearchText("832 W. Wrightwood, Chicago");
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
		here.setSearchText("WTF, Paris, Turkey");
		here.getGeolocation();
	}
}