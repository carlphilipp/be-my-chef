package com.epickur.api.service;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class CatererServiceTest {

	@Mock
	private CatererDAO catererDAOMock;
	@InjectMocks
	private CatererService catererService;

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		given(catererDAOMock.create(isA(Caterer.class))).willReturn(catererAfterCreate);

		// When
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
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		given(catererDAOMock.read(anyString())).willReturn(Optional.of(catererAfterCreate));

		// When
		Optional<Caterer> actualCaterer = catererService.read(EntityGenerator.generateRandomString());

		// Then
		Caterer actual = actualCaterer.orElseThrow(AssertionError::new);
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
		// Given
		Caterer caterer1 = EntityGenerator.generateRandomCatererWithId();
		Caterer caterer2 = EntityGenerator.generateRandomCatererWithId();
		List<Caterer> caterers = new ArrayList<>();
		caterers.add(caterer1);
		caterers.add(caterer2);

		// When
		given(catererDAOMock.readAll()).willReturn(caterers);

		// Then
		List<Caterer> actual = catererService.readAll();
		assertNotNull("Caterer is null", actual);
		assertEquals(caterer1.getId(), actual.get(0).getId());
		assertEquals(caterer2.getId(), actual.get(1).getId());
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterUpdate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterUpdate.setDescription("new desc");
		given(catererDAOMock.read(anyString())).willReturn(Optional.of(catererAfterUpdate));
		given(catererDAOMock.update(caterer)).willReturn(catererAfterUpdate);

		// When
		Caterer actual = catererService.update(caterer);

		// Then
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
