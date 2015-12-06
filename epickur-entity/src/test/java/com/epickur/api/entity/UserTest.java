package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

	@Test
	public void testUser() {
		User user = EntityGenerator.generateRandomUser();
		User user2 = user.clone();

		assertEquals(user.hashCode(), user2.hashCode());
		assertEquals(user, user2);

		User user3 = user2;
		assertEquals(user, user3);
		assertNotNull(user);
		assertFalse(user.equals(new Address()));
	}

	@Test
	public void testUserNull() {
		User user = EntityGenerator.generateRandomUser();
		user.toString();
	}

	@Test
	public void testUserId() {
		User user = EntityGenerator.generateRandomUser();
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
		User user = EntityGenerator.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setName(null);
		user2.setName(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);

		user.setName(null);
		user2.setName(null);
		assertEquals(user, user2);

		user.setName(EntityGenerator.generateRandomString());
		user2.setName(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserPassword() {
		User user = EntityGenerator.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setPassword(null);
		user2.setPassword(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);

		user.setPassword(null);
		user2.setPassword(null);
		assertEquals(user, user2);

		user.setPassword(EntityGenerator.generateRandomString());
		user2.setPassword(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserEmail() {
		User user = EntityGenerator.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setEmail(null);
		user2.setEmail(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);

		user.setEmail(null);
		user2.setEmail(null);
		assertEquals(user, user2);

		user.setEmail(EntityGenerator.generateRandomString());
		user2.setEmail(EntityGenerator.generateRandomString());
		assertNotEquals(user, user2);
	}

	@Test
	public void testUserAllow() {
		User user = EntityGenerator.generateRandomUser();
		User user2 = user.clone();
		assertEquals(user, user2);

		user.setAllow(null);
		user2.setAllow(EntityGenerator.generateRandomInteger());
		assertNotEquals(user, user2);

		user.setAllow(null);
		user2.setAllow(null);
		assertEquals(user, user2);

		user.setAllow(EntityGenerator.generateRandomInteger());
		user2.setAllow(EntityGenerator.generateRandomInteger());
		assertNotEquals(user, user2);
	}
}
