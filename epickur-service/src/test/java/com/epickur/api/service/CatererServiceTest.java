package com.epickur.api.service;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class CatererServiceTest {

	@Mock
	private CatererDAO catererDAOMock;
	@InjectMocks
	private CatererService catererService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererDAOMock.create(anyObject())).thenReturn(catererAfterCreate);

		Caterer actual = catererService.create(caterer);

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
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererDAOMock.read(anyString())).thenReturn(Optional.of(catererAfterCreate));

		Optional<Caterer> actualCaterer = catererService.read(EntityGenerator.generateRandomString());

		assertTrue(actualCaterer.isPresent());
		Caterer actual = actualCaterer.get();
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
		Caterer caterer1 = EntityGenerator.generateRandomCatererWithId();
		Caterer caterer2 = EntityGenerator.generateRandomCatererWithId();
		List<Caterer> caterers = new ArrayList<>();
		caterers.add(caterer1);
		caterers.add(caterer2);

		when(catererDAOMock.readAll()).thenReturn(caterers);

		List<Caterer> actual = catererService.readAll();
		assertNotNull("Caterer is null", actual);
		assertEquals(caterer1.getId(), actual.get(0).getId());
		assertEquals(caterer2.getId(), actual.get(1).getId());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterUpdate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterUpdate.setDescription("new desc");

		when(catererDAOMock.read(anyString())).thenReturn(Optional.of(catererAfterUpdate));
		when(catererDAOMock.update(caterer)).thenReturn(catererAfterUpdate);

		Caterer actual = catererService.update(caterer);
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
