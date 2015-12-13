package com.epickur.api.here;

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
public class GeocoderHereImplTest {

	@Autowired
	private GeocoderHereImpl geoCoder;

	@Test
	public void getPositionTest() {
		try {
			Geo geo = geoCoder.getPosition("832 W. Wrightwood, Chicago");
			assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
			assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
		} catch (GeoLocationException e) {
			fail("Geo location error: " + e.getLocalizedMessage());
		}
	}

	@Test(expected = HereException.class)
	public void getPositionTest2() throws GeoLocationException {
		Geo geo = geoCoder.getPosition("WTF, Paris, Turkey");
		assertEquals(41.92901, geo.getLatitude().floatValue(), 0.001);
		assertEquals(-87.650276, geo.getLongitude().floatValue(), 0.001);
	}
}
