package com.epickur.api.validator;

import javax.ws.rs.ForbiddenException;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Location;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;

public class CatererValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckUpdateCaterer2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.id is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setId(null);
		validator.checkUpdateCaterer2("id", caterer);
	}

	@Test
	public void testCheckCaterer() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.name is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setName(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.description is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setDescription(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.manager is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setManager(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.email is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setEmail(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.phone is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setPhone(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer6() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setLocation(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer7() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		location.setGeo(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer8() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		location.setAddress(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo.latitude is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Geo geo = location.getGeo();
		geo.setLatitude(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer10() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo.longitude is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Geo geo = location.getGeo();
		geo.setLongitude(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer11() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.city is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setCity(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer12() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.country is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setCountry(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer13() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.housenumber is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setHouseNumber(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer14() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.label is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setLabel(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer15() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.postalcode is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setPostalCode(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer16() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.state is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setState(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer17() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.street is not allowed to be null or empty");

		CatererValidator validator = new CatererValidator();
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setStreet(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckRights() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.ADMIN, Crud.CREATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights2() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights3() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.SUPER_USER, Crud.READ, null);
	}

	@Test
	public void testCheckRights4() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.SUPER_USER, Crud.UPDATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights5() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.SUPER_USER, Crud.DELETE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights6() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights7() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.USER, Crud.READ, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights8() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.USER, Crud.UPDATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights9() throws EpickurException {
		CatererValidator service = new CatererValidator();
		service.checkRightsBefore(Role.USER, Crud.DELETE, null);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		CatererValidator service = new CatererValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.ADMIN);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setCreatedBy(userId);
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Crud.UPDATE);
	}

	@Test
	public void testCheckRightsAfter2() throws EpickurException {
		CatererValidator service = new CatererValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setCreatedBy(userId);
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Crud.UPDATE);
	}

	@Test
	public void testCheckPayementInfo() {
		CatererValidator service = new CatererValidator();
		String id = new ObjectId().toHexString();
		DateTime start = new DateTime();
		DateTime end = new DateTime();
		end.plusMinutes(5);
		service.checkPaymentInfo(id, start, end);
	}
	
	@Test
	public void testCheckPayementInfo2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("Start date missing");
		
		CatererValidator service = new CatererValidator();
		String id = new ObjectId().toHexString();
		DateTime start = null;
		DateTime end = new DateTime();
		end = end.plusMinutes(5);
		service.checkPaymentInfo(id, start, end);
	}
	
	@Test
	public void testCheckPayementInfo3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The start date can not be after today");
		
		CatererValidator service = new CatererValidator();
		String id = new ObjectId().toHexString();
		DateTime start = new DateTime();
		DateTime end = null;
		start = start.plusHours(1);
		service.checkPaymentInfo(id, start, end);
	}
	
	@Test
	public void testCheckPayementInfo4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The end date should be after the start date");
		
		CatererValidator service = new CatererValidator();
		String id = new ObjectId().toHexString();
		DateTime start = new DateTime();
		DateTime end = new DateTime();;
		end = end.minusHours(1);
		service.checkPaymentInfo(id, start, end);
	}
}
