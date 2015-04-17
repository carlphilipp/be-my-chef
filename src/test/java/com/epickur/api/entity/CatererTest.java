package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Test;

import com.epickur.api.TestUtils;

public class CatererTest {

	@Test
	public void testCaterer() {
		ObjectId id = new ObjectId();
		String name = "name";
		String description = "description";
		String manager = "manager";
		String email = "email@email.com";
		String phone = "0215787236";
		Location location = TestUtils.generateRandomLocation();
		DateTime created = new DateTime();
		DateTime updated = new DateTime();
		Caterer cat = new Caterer();
		cat.setId(id);
		cat.setName(name);
		cat.setDescription(description);
		cat.setManager(manager);
		cat.setEmail(email);
		cat.setPhone(phone);
		cat.setLocation(location);
		cat.setCreatedAt(created);
		cat.setUpdatedAt(updated);

		Caterer cat2 = new Caterer();
		cat2.setId(id);
		cat2.setName(name);
		cat2.setDescription(description);
		cat2.setManager(manager);
		cat2.setEmail(email);
		cat2.setPhone(phone);
		cat2.setLocation(location);
		cat2.setCreatedAt(created);
		cat2.setUpdatedAt(updated);

		assertEquals(cat.hashCode(), cat2.hashCode());
		assertEquals(cat, cat2);

		Caterer cat3 = cat;
		assertEquals(cat, cat3);
		assertFalse(cat.equals(null));
		assertFalse(cat.equals(new User()));
	}

}
