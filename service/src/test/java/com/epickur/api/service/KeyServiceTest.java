package com.epickur.api.service;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class KeyServiceTest {

	@Mock
	private KeyDAO keyDAOMock;
	@InjectMocks
	private KeyService keyBusiness;

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterCreate = EntityGenerator.mockKeyAfterCreate(key);
		given(keyDAOMock.create(any(Key.class))).willReturn(keyAfterCreate);

		// When
		Key actual = keyBusiness.create(key);

		// Then
		assertNotNull(actual.getId());
		assertNotNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNotNull(actual.getKey());
	}

	@Test
	public void testReadWithName() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterRead = EntityGenerator.mockKeyAfterCreate(key);
		given(keyDAOMock.readWithName(any(String.class))).willReturn(keyAfterRead);

		// When
		Key actual = keyBusiness.readWithName(EntityGenerator.generateRandomString());

		// Then
		assertNotNull(actual);
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		given(keyDAOMock.delete(any(String.class))).willReturn(true);

		// When
		boolean actual = keyBusiness.delete(EntityGenerator.generateRandomString());

		// Then
		assertTrue(actual);
	}

	@Test
	public void testDeleteWithKey() throws EpickurException {
		// Given
		given(keyDAOMock.deleteWithKey(any(String.class))).willReturn(true);

		// When
		boolean actual = keyBusiness.deleteWithKey(EntityGenerator.generateRandomString());

		// Then
		assertTrue(actual);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterRead = EntityGenerator.mockKeyAfterCreate(key);
		List<Key> keyList = new ArrayList<>();
		keyList.add(keyAfterRead);
		given(keyDAOMock.readAll()).willReturn(keyList);

		// When
		List<Key> actual = keyBusiness.readAll();

		// Then
		assertNotNull(actual);
		assertEquals(1, actual.size());
	}
}
