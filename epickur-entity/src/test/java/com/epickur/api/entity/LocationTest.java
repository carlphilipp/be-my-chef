package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class LocationTest {

	@Test
	public void testLocation() {
		Location location = EntityGenerator.generateRandomLocation();
		Location location2 = location.clone();

		assertEquals(location.hashCode(), location2.hashCode());
		assertEquals(location, location2);
	}
}
