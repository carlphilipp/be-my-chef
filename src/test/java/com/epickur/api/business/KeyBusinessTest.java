package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.dao.mongo.KeyDAOImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;

public class KeyBusinessTest extends InitMocks {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private KeyBusiness keyBusiness;
	@Mock
	private KeyDAOImpl keyDAOMock;

	@Before
	public void setUp() {
		reset(keyDAOMock);
		keyBusiness = new KeyBusiness(keyDAOMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		Key key = TestUtils.generateRandomAdminKey();
		Key keyAfterCreate = TestUtils.mockKeyAfterCreate(key);

		when(keyDAOMock.create((Key) anyObject())).thenReturn(keyAfterCreate);

		Key actual = keyBusiness.create(key);
		assertNotNull(actual.getId());
		assertNotNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNotNull(actual.getKey());
	}

	@Test
	public void testReadWithName() throws EpickurException {
		Key key = TestUtils.generateRandomAdminKey();
		Key keyAfterRead = TestUtils.mockKeyAfterCreate(key);

		when(keyDAOMock.readWithName(anyString())).thenReturn(keyAfterRead);

		Key actual = keyBusiness.readWithName(TestUtils.generateRandomString());
		assertNotNull(actual);
	}

	@Test
	public void testDelete() throws EpickurException {
		when(keyDAOMock.delete(anyString())).thenReturn(true);

		boolean actual = keyBusiness.delete(TestUtils.generateRandomString());
		assertTrue(actual);
	}

	@Test
	public void testDeleteWithKey() throws EpickurException {
		when(keyDAOMock.deleteWithKey(anyString())).thenReturn(true);

		boolean actual = keyBusiness.deleteWithKey(TestUtils.generateRandomString());
		assertTrue(actual);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Key key = TestUtils.generateRandomAdminKey();
		Key keyAfterRead = TestUtils.mockKeyAfterCreate(key);
		List<Key> keyList = new ArrayList<Key>();
		keyList.add(keyAfterRead);

		when(keyDAOMock.readAll()).thenReturn(keyList);

		List<Key> actual = keyBusiness.readAll();
		assertNotNull(actual);
		assertEquals(1, actual.size());
	}
}
