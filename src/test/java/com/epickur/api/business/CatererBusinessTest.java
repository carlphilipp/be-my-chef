package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;

public class CatererBusinessTest extends InitMocks {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CatererBusiness catererBusiness;
	@Mock
	private CatererDAO catererDAOMock;

	@Before
	public void setUp() {
		this.catererBusiness = new CatererBusiness(catererDAOMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);

		when(catererDAOMock.create((Caterer) anyObject())).thenReturn(catererAfterCreate);

		Caterer actual = catererBusiness.create(caterer);

		assertNotNull("Caterer is null", actual);
		assertNotNull("Id not generated", actual.getId());
		assertNotNull("CreatedAt is null", actual.getCreatedAt());
		assertNotNull("UpdatedAt is null", actual.getUpdatedAt());
		assertEquals(caterer.getDescription(), actual.getDescription());
		assertEquals(caterer.getEmail(), actual.getEmail());
		assertEquals(caterer.getManager(), actual.getManager());
		assertEquals(caterer.getName(), actual.getName());
		assertEquals(caterer.getPhone(), actual.getPhone());
		assertEquals(caterer.getLocation(), actual.getLocation());
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);

		when(catererDAOMock.read(anyString())).thenReturn(catererAfterCreate);

		Caterer actual = catererBusiness.read(TestUtils.generateRandomString());

		assertNotNull("Caterer is null", actual);
		assertNotNull("Id not generated", actual.getId());
		assertNotNull("CreatedAt is null", actual.getCreatedAt());
		assertNotNull("UpdatedAt is null", actual.getUpdatedAt());
		assertEquals(caterer.getDescription(), actual.getDescription());
		assertEquals(caterer.getEmail(), actual.getEmail());
		assertEquals(caterer.getManager(), actual.getManager());
		assertEquals(caterer.getName(), actual.getName());
		assertEquals(caterer.getPhone(), actual.getPhone());
		assertEquals(caterer.getLocation(), actual.getLocation());
	}

	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer1 = TestUtils.generateRandomCatererWithId();
		Caterer caterer2 = TestUtils.generateRandomCatererWithId();
		List<Caterer> caterers = new ArrayList<Caterer>();
		caterers.add(caterer1);
		caterers.add(caterer2);
		
		when(catererDAOMock.readAll()).thenReturn(caterers);
		
		List<Caterer> actual = catererBusiness.readAll();
		assertNotNull("Caterer is null", actual);
		assertEquals(caterer1.getId(), actual.get(0).getId());
		assertEquals(caterer2.getId(), actual.get(1).getId());
	}

	@Test
	public void testUpdate() throws EpickurException {		
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Caterer catererAfterUpdate = TestUtils.mockCatererAfterCreate(caterer);
		catererAfterUpdate.setDescription("new desc");
		Key keyMock = new Key();
		keyMock.setId(new ObjectId());
		keyMock.setUserId(new ObjectId());
		keyMock.setRole(Role.ADMIN);
		
		when(catererDAOMock.read(anyString())).thenReturn(catererAfterUpdate);
		when(catererDAOMock.update(caterer)).thenReturn(catererAfterUpdate);

		Caterer actual = catererBusiness.update(caterer, keyMock);
		assertNotNull("Caterer is null", actual);

		assertNotNull("CreatedAt is null", actual.getCreatedAt());
		assertNotNull("UpdatedAt is null", actual.getUpdatedAt());
		assertEquals("new desc", actual.getDescription());
		assertEquals(caterer.getEmail(), actual.getEmail());
		assertEquals(caterer.getManager(), actual.getManager());
		assertEquals(caterer.getName(), actual.getName());
		assertEquals(caterer.getPhone(), actual.getPhone());
		assertEquals(caterer.getLocation(), actual.getLocation());
	}
}
