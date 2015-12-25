package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyTest {

	@Test
	public void testKey() {
		Key key = EntityGenerator.generateRandomAdminKey();
		Key key2 = key.clone();

		assertEquals(key.hashCode(), key2.hashCode());
		assertEquals(key, key2);
	}
}
