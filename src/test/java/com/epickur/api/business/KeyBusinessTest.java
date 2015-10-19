package com.epickur.api.business;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;

public class KeyBusinessTest {

	private static KeyBusiness business = new KeyBusiness();
	private static List<String> toDelete;

	@BeforeClass
	public static void beforeClass() {
		business = new KeyBusiness();
		toDelete = new ArrayList<String>();
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (String key : toDelete) {
			business.deleteWithKey(key);
		}
	}

	@Test
	public void testReadAll() throws EpickurException {
		Key key = TestUtils.generateRandomAdminKey();
		business.create(key);
		toDelete.add(key.getKey());
		key = TestUtils.generateRandomAdminKey();
		business.create(key);
		toDelete.add(key.getKey());

		List<Key> keys = business.readAll();
		assertNotEquals(0, keys.size());
	}
}
