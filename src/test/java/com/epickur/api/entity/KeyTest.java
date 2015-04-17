package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.TestUtils;

public class KeyTest {

	@Test
	public void testKey() {
		Key key = TestUtils.generateRandomKey();
		Key key2 = key.clone();

		assertEquals(key.hashCode(), key2.hashCode());
		assertEquals(key, key2);

		Key key3 = key2;
		assertEquals(key, key3);
		assertFalse(key.equals(null));
		assertFalse(key.equals(new User()));
	}
}
