package com.epickur.api.service;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class KeyServiceTest {
	
	@Mock
	private KeyDAO keyDAOMock;
	@InjectMocks
	private KeyService keyBusiness;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreate() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterCreate = EntityGenerator.mockKeyAfterCreate(key);

		when(keyDAOMock.create(anyObject())).thenReturn(keyAfterCreate);

		Key actual = keyBusiness.create(key);
		assertNotNull(actual.getId());
		assertNotNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNotNull(actual.getKey());
	}

	@Test
	public void testReadWithName() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterRead = EntityGenerator.mockKeyAfterCreate(key);

		when(keyDAOMock.readWithName(isA(String.class))).thenReturn(keyAfterRead);

		Key actual = keyBusiness.readWithName(EntityGenerator.generateRandomString());
		assertNotNull(actual);
	}

	@Test
	public void testDelete() throws EpickurException {
		when(keyDAOMock.delete(isA(String.class))).thenReturn(true);

		boolean actual = keyBusiness.delete(EntityGenerator.generateRandomString());
		assertTrue(actual);
	}

	@Test
	public void testDeleteWithKey() throws EpickurException {
		when(keyDAOMock.deleteWithKey(isA(String.class))).thenReturn(true);

		boolean actual = keyBusiness.deleteWithKey(EntityGenerator.generateRandomString());
		assertTrue(actual);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Key keyAfterRead = EntityGenerator.mockKeyAfterCreate(key);
		List<Key> keyList = new ArrayList<>();
		keyList.add(keyAfterRead);

		when(keyDAOMock.readAll()).thenReturn(keyList);

		List<Key> actual = keyBusiness.readAll();
		assertNotNull(actual);
		assertEquals(1, actual.size());
	}
}
