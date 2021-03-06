package com.epickur.api.validation;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Location;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.helper.EntityGenerator;

public class CatererValidationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckUpdateCaterer2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.id is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setId(null);
		validator.checkUpdateCaterer(new ObjectId().toHexString(), caterer);
	}

	@Test
	public void testCheckCaterer() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.name is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setName(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.description is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setDescription(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.manager is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setManager(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.email is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setEmail(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.phone is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setPhone(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer6() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setLocation(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer7() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		location.setGeo(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer8() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		location.setAddress(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo.latitude is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Geo geo = location.getGeo();
		geo.setLatitude(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer10() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.geo.longitude is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Geo geo = location.getGeo();
		geo.setLongitude(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer11() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.city is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setCity(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer12() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.country is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setCountry(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer13() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.housenumber is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setHouseNumber(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer14() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.label is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setLabel(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer15() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.postalcode is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setPostalCode(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer16() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.state is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setState(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckCaterer17() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field caterer.location.address.street is not allowed to be null or empty");

		CatererValidation validator = new CatererValidation();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Location location = caterer.getLocation();
		Address address = location.getAddress();
		address.setStreet(null);
		validator.checkCaterer(caterer);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		CatererValidation service = new CatererValidation();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setCreatedBy(userId);
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Operation.UPDATE);
	}

	@Test
	public void testCheckRightsAfter2() throws EpickurException {
		CatererValidation service = new CatererValidation();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setCreatedBy(userId);
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Operation.UPDATE);
	}

	@Test
	public void testCheckRightsAfter3() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);

		CatererValidation service = new CatererValidation();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Operation.UPDATE);
	}

	@Test
	public void testCheckRightsAfter4() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage("Rights issue. This case should not happen");

		CatererValidation service = new CatererValidation();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		caterer.setCreatedBy(userId);
		service.checkRightsAfter(key.getRole(), key.getUserId(), caterer, Operation.UPDATE);
	}

	@Test
	public void testCheckPayementInfo() {
		CatererValidation service = new CatererValidation();
		DateTime start = new DateTime();
		DateTime end = new DateTime();
		end.plusMinutes(5);
		service.checkPaymentInfo(start, end);
	}

	@Test
	public void testCheckPayementInfo2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("Start date missing");

		CatererValidation service = new CatererValidation();
		DateTime end = new DateTime();
		end = end.plusMinutes(5);
		service.checkPaymentInfo(null, end);
	}

	@Test
	public void testCheckPayementInfo3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The start date can not be after today");

		CatererValidation service = new CatererValidation();
		DateTime start = new DateTime();
		start = start.plusHours(1);
		service.checkPaymentInfo(start, null);
	}

	@Test
	public void testCheckPayementInfo4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The end date should be after the start date");

		CatererValidation service = new CatererValidation();
		DateTime start = new DateTime();
		DateTime end = new DateTime();
		end = end.minusHours(1);
		service.checkPaymentInfo(start, end);
	}
}
