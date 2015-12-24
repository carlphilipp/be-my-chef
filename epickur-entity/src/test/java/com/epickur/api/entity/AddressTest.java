package com.epickur.api.entity;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class AddressTest {

	@Test
	public void testAddress() {
		String city = "Chicago";
		String country = "USA";
		String houseNumber = "5";
		String label = "label";
		Integer postalCode = 60614;
		String state = "Illinois";
		String street = "Michigan avenue";

		Address address = new Address();
		address.setCity(city);
		address.setCountry(country);
		address.setHouseNumber(houseNumber);
		address.setLabel(label);
		address.setPostalCode(postalCode);
		address.setState(state);
		address.setStreet(street);

		Address address2 = new Address();
		address2.setCity(city);
		address2.setCountry(country);
		address2.setHouseNumber(houseNumber);
		address2.setLabel(label);
		address2.setPostalCode(postalCode);
		address2.setState(state);
		address2.setStreet(street);
	}
	
	@Test
	public void testAddress2() {
		Address address = EntityGenerator.generateRandomAddress();
		address.setCity(null);
		address.setCountry(null);
		address.setHouseNumber(null);
		address.setLabel(null);
		address.setPostalCode(null);
		address.setState(null);
		address.setStreet(null);
		Address address2 = address.clone();
		assertEquals(address2.hashCode(), address.hashCode());
	}
	
	@Test
	public void testAddressCity() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setCity(null);
		address2.setCity("city");
		assertNotEquals(address, address2);
		
		address.setCity(null);
		address2.setCity(null);
		assertEquals(address, address2);
		
		address.setCity(EntityGenerator.generateRandomString());
		address2.setCity(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String city = EntityGenerator.generateRandomString();
		address.setCity(city);
		address2.setCity(city);
	}
	
	@Test
	public void testAddressCountry() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setCountry(null);
		address2.setCountry("country");
		assertNotEquals(address, address2);
		
		address.setCountry(null);
		address2.setCountry(null);
		assertEquals(address, address2);
		
		address.setCountry(EntityGenerator.generateRandomString());
		address2.setCountry(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String country = EntityGenerator.generateRandomString();
		address.setCountry(country);
		address2.setCountry(country);
	}
	
	@Test
	public void testAddressHouseNumber() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setHouseNumber(null);
		address2.setHouseNumber("house number");
		assertNotEquals(address, address2);
		
		address.setHouseNumber(null);
		address2.setHouseNumber(null);
		assertEquals(address, address2);
		
		address.setHouseNumber(EntityGenerator.generateRandomString());
		address2.setHouseNumber(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String house = EntityGenerator.generateRandomString();
		address.setHouseNumber(house);
		address2.setHouseNumber(house);
	}
	
	@Test
	public void testAddressLabel() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setLabel(null);
		address2.setLabel("label");
		assertNotEquals(address, address2);
		
		address.setLabel(null);
		address2.setLabel(null);
		assertEquals(address, address2);
		
		address.setLabel(EntityGenerator.generateRandomString());
		address2.setLabel(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String label = EntityGenerator.generateRandomString();
		address.setLabel(label);
		address2.setLabel(label);
	}
	
	@Test
	public void testAddressPostCode() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setPostalCode(null);
		address2.setPostalCode(5);
		assertNotEquals(address, address2);
		
		address.setPostalCode(null);
		address2.setPostalCode(null);
		assertEquals(address, address2);
		
		address.setPostalCode(EntityGenerator.generateRandomInteger());
		address2.setPostalCode(EntityGenerator.generateRandomInteger());
		assertNotEquals(address, address2);
		Integer code = EntityGenerator.generateRandomInteger();
		address.setPostalCode(code);
		address2.setPostalCode(code);
	}
	
	@Test
	public void testAddressState() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setState(null);
		address2.setState("state");
		assertNotEquals(address, address2);
		
		address.setState(null);
		address2.setState(null);
		assertEquals(address, address2);
		
		address.setState(EntityGenerator.generateRandomString());
		address2.setState(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String state = EntityGenerator.generateRandomString();
		address.setState(state);
		address2.setState(state);
	}
	
	@Test
	public void testAddressStreet() {
		Address address = EntityGenerator.generateRandomAddress();
		Address address2 = address.clone();
		assertEquals(address, address2);
		
		address.setStreet(null);
		address2.setStreet("state");
		assertNotEquals(address, address2);
		
		address.setStreet(null);
		address2.setStreet(null);
		assertEquals(address, address2);
		
		address.setStreet(EntityGenerator.generateRandomString());
		address2.setStreet(EntityGenerator.generateRandomString());
		assertNotEquals(address, address2);
		String street = EntityGenerator.generateRandomString();
		address.setStreet(street);
		address2.setStreet(street);
	}
	
	@Test
	public void testUpdate(){
		Address address = new Address();
		Map<String, String> res = address.getUpdateMap("prefix");
		assertEquals(0, res.size());
	}
}
