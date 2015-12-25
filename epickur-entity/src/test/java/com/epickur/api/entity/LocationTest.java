package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationTest {

	@Test
	public void testLocation() {
		Location location = EntityGenerator.generateRandomLocation();
		Location location2 = location.clone();

		assertEquals(location.hashCode(), location2.hashCode());
		assertEquals(location, location2);
	}
}
