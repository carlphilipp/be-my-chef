package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.epickur.api.TestUtils;

public class UserTest {

	@Test
	public void testUser() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();

		assertEquals(user.hashCode(), user2.hashCode());
		assertEquals(user, user2);

		User user3 = user2;
		assertEquals(user, user3);
		assertFalse(user.equals(null));
		assertFalse(user.equals(new Address()));
	}

	@Test
	public void testUserNull() {
		User user = TestUtils.generateRandomUser();
		user.toString();
	}

	@Test
	public void testUserId() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setId(null);
		user2.setId(new ObjectId());
		assertNotEquals(user, user2);

		user.setId(null);
		user2.setId(null);
		assertEquals(user, user2);

		user.setId(new ObjectId());
		user2.setId(new ObjectId());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserName() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setName(null);
		user2.setName(TestUtils.generateRandomString());
		assertNotEquals(user, user2);

		user.setName(null);
		user2.setName(null);
		assertEquals(user, user2);

		user.setName(TestUtils.generateRandomString());
		user2.setName(TestUtils.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserPassword() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setPassword(null);
		user2.setPassword(TestUtils.generateRandomString());
		assertNotEquals(user, user2);

		user.setPassword(null);
		user2.setPassword(null);
		assertEquals(user, user2);

		user.setPassword(TestUtils.generateRandomString());
		user2.setPassword(TestUtils.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserEmail() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setEmail(null);
		user2.setEmail(TestUtils.generateRandomString());
		assertNotEquals(user, user2);

		user.setEmail(null);
		user2.setEmail(null);
		assertEquals(user, user2);

		user.setEmail(TestUtils.generateRandomString());
		user2.setEmail(TestUtils.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserAllow() {
		User user = TestUtils.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setAllow(null);
		user2.setAllow(TestUtils.generateRandomInteger());
		assertNotEquals(user, user2);

		user.setAllow(null);
		user2.setAllow(null);
		assertEquals(user, user2);

		user.setAllow(TestUtils.generateRandomInteger());
		user2.setAllow(TestUtils.generateRandomInteger());
		assertNotEquals(user, user2);
	}
}
