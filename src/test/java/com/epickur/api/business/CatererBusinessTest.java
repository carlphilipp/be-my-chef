package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Location;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;

public class CatererBusinessTest {

	private static CatererBusiness business;
	private static List<ObjectId> idsToDelete;
	private static final String description = "description";
	private static final String email = "email@email.com";
	private static Location location;
	private static final String manager = "Mr Kidd";
	private static final String name = "Caterer Name";
	private static final String phone = "1112221111";

	private static final Float latitude = 41.92901f;
	private static final Float longitude = -87.650276f;
	private static final String city = "Chicago";
	private static final String country = "United States";
	private static final String houseNumber = "5";
	private static final String label = "derp";
	private static final Integer postalCode = 60614;
	private static final String state = "Illinois";
	private static final String street = "W. Wrightwood";

	@BeforeClass
	public static void beforeClass() {
		business = new CatererBusiness();
		idsToDelete = new ArrayList<ObjectId>();
		location = new Location();
		Address address = new Address();
		address.setCity(city);
		address.setCountry(country);
		address.setHouseNumber(houseNumber);
		address.setLabel(label);
		address.setPostalCode(postalCode);
		address.setState(state);
		address.setStreet(street);
		location.setAddress(address);
		Geo geo = new Geo();
		geo.setLatitude(latitude);
		geo.setLongitude(longitude);
		location.setGeo(geo);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			business.delete(id.toHexString());
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email);
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name);
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		assertNotNull("CreatedAt is null", result.getCreatedAt());
		assertNotNull("UpdatedAt is null", result.getUpdatedAt());
		assertEquals(description, result.getDescription());
		assertEquals(email, result.getEmail());
		assertEquals(manager, result.getManager());
		assertEquals(name, result.getName());
		assertEquals(phone, result.getPhone());
		assertEquals(location, result.getLocation());
	}

	@Test(expected = EpickurException.class)
	public void testCreateDuplicate() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "6");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "6");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		// Test to add again. Exception should happen
		business.create(caterer);
	}

	@Test
	public void testRead() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "2");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "2");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		Caterer result2 = business.read(result.getId().toHexString());
		assertNotNull("Caterer is null", result2);
		assertEquals(result, result2);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "3");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "3");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		List<Caterer> result2 = business.readAll();
		assertNotNull("Caterer is null", result2);
		for (Caterer cat : result2) {
			if (cat.getId().equals(result.getId())) {
				assertEquals(result, cat);
			}
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "7");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "7");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		result.setDescription("new desc");
		Key key = new Key();
		key.setRole(Role.ADMIN);

		Caterer result2 = business.update(result, key.getRole(), key.getUserId());
		assertNotNull("Caterer is null", result2);
		result.setCreatedAt(result2.getCreatedAt());
		assertEquals(result, result2);

		assertNotNull("CreatedAt is null", result2.getCreatedAt());
		assertNotNull("UpdatedAt is null", result2.getUpdatedAt());
		assertEquals("new desc", result2.getDescription());
		assertEquals(email + "7", result2.getEmail());
		assertEquals(manager, result2.getManager());
		assertEquals(name + "7", result2.getName());
		assertEquals(phone, result2.getPhone());
		assertEquals(location, result2.getLocation());
	}

	@Test
	public void testUpdate2() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "4");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "4");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		result.setDescription("new desc");
		result.setId(new ObjectId());

		Key key = new Key();
		key.setRole(Role.ADMIN);

		Caterer result2 = business.update(result, key.getRole(), key.getUserId());
		assertNull("Caterer is not null", result2);
	}

	@Test
	public void testDelete() throws EpickurException {
		// Create a new caterer
		Caterer caterer = new Caterer();
		caterer.setDescription(description);
		caterer.setEmail(email + "5");
		caterer.setLocation(location);
		caterer.setManager(manager);
		caterer.setName(name + "5");
		caterer.setPhone(phone);
		caterer.setCreatedBy(new ObjectId());
		Caterer result = business.create(caterer);

		// Check result
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		boolean deleted = business.delete(result.getId().toHexString());
		assertTrue(deleted);

		Caterer result2 = business.read(result.getId().toHexString());
		assertNull(result2);
	}

}
