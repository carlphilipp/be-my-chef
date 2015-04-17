package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.mongodb.DBObject;

public class CatererServiceTest {

	private static CatererService service;
	private static List<ObjectId> idsToDelete;
	private static ContainerRequestContext context;


	@BeforeClass
	public static void beforeClass() {
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		service = new CatererService();
		idsToDelete = new ArrayList<ObjectId>();
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			service.delete(id.toHexString(), context);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCreateFail() throws EpickurException {
		Caterer caterer = null;
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());
			String id = catererResult.getId().toHexString();
			Response result2 = service.read(id);
			if (result2.getEntity() != null) {
				Caterer catererResult2 = (Caterer) result.getEntity();
				assertEquals(catererResult, catererResult2);
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadFail() throws EpickurException {
		Response result = service.read(null);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());
			String id = catererResult.getId().toHexString();
			Response result2 = service.readAll(context);
			if (result2.getEntity() != null) {
				List<Caterer> cateres = (List<Caterer>) result2.getEntity();
				for (Caterer cat : cateres) {
					if (cat.getId().equals(id)) {
						assertEquals(catererResult, cat);
					}
				}
			} else {
				fail("List of catereres returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			Response result2 = service.update(catererUpdate.getId().toHexString(), catererUpdate, context);
			if (result2.getEntity() != null) {
				int statusCode = result2.getStatus();
				assertEquals("Wrong status code: " + statusCode + " with " + result2.getEntity(), 200, statusCode);
				Caterer catererUpdate2 = (Caterer) result2.getEntity();
				assertNotNull("CreatedAt is null", catererUpdate2.getCreatedAt());
				assertNotNull("UpdatedAt is null", catererUpdate2.getUpdatedAt());
				assertEquals("modified", catererUpdate2.getDescription());
				assertEquals(catererResult.getEmail(), catererUpdate2.getEmail());
				assertEquals(catererResult.getManager(), catererUpdate2.getManager());
				assertEquals(catererResult.getName(), catererUpdate2.getName());
				assertEquals(catererResult.getPhone(), catererUpdate2.getPhone());
				assertEquals(catererResult.getLocation(), catererUpdate2.getLocation());
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdate2() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			service.update("id", catererUpdate, context);
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testUpdate3() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			Response result2 = service.update(catererUpdate.getId().toHexString(), catererUpdate, context);
			if (result2.getEntity() != null) {
				int statusCode = result.getStatus();
				assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
				Caterer catererUpdate2 = (Caterer) result2.getEntity();
				assertNotNull("CreatedAt is null", catererUpdate2.getCreatedAt());
				assertNotNull("UpdatedAt is null", catererUpdate2.getUpdatedAt());
				assertEquals("modified", catererUpdate2.getDescription());
				assertEquals(catererResult.getEmail(), catererUpdate2.getEmail());
				assertEquals(catererResult.getManager(), catererUpdate2.getManager());
				assertEquals(catererResult.getName(), catererUpdate2.getName());
				assertEquals(catererResult.getPhone(), catererUpdate2.getPhone());
				assertEquals(catererResult.getLocation(), catererUpdate2.getLocation());
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testFail() throws EpickurException {
		Response result = service.update(null, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testFail2() throws EpickurException {
		Response result = service.update(null, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testFail3() throws EpickurException {
		Response result = service.update("", null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testDelete() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = service.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsToDelete.add(catererResult.getId());

			Response result2 = service.delete(catererResult.getId().toHexString(), context);
			if (result2.getEntity() != null) {
				int statusCode = result2.getStatus();
				String entityResult = ((DBObject) result2.getEntity()).toString();
				assertNotEquals("Wrong status code: " + statusCode + " with " + entityResult, 403, statusCode);
				assertNotEquals("Wrong status code: " + statusCode + " with " + entityResult, 403, statusCode);
				assertNotEquals("Wrong status code: " + statusCode + " with " + entityResult, 404, statusCode);
				assertNotEquals("Wrong status code: " + statusCode + " with " + entityResult, 405, statusCode);
				assertNotEquals("Wrong status code: " + statusCode + " with " + entityResult, 500, statusCode);
				DBObject deleted = (DBObject) result2.getEntity();
				assertTrue((Boolean) deleted.get("deleted"));

				Response result3 = service.read(catererResult.getId().toHexString());
				DBObject dbObject = (DBObject) result3.getEntity();
				assertEquals(404, dbObject.get("error"));
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testDeleteFail() throws EpickurException {
		Response result = service.delete(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

}
