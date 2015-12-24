package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class KeyTest {

	@Test
	public void testKey() {
		Key key = EntityGenerator.generateRandomAdminKey();
		Key key2 = key.clone();

		assertEquals(key.hashCode(), key2.hashCode());
		assertEquals(key, key2);
	}
}
