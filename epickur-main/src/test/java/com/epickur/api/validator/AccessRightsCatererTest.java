package com.epickur.api.validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurForbiddenException;

/**
 * @author cph
 * @version 1.0
 *
 */
public class AccessRightsCatererTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final EndpointType endpoint = EndpointType.CATERER;

	@Test
	public void adminCreateTest() {
		AccessRights.check(Role.ADMIN, Operation.CREATE, endpoint);
	}

	@Test
	public void adminReadTest() {
		AccessRights.check(Role.ADMIN, Operation.READ, endpoint);
	}

	@Test
	public void adminUpdateTest() {
		AccessRights.check(Role.ADMIN, Operation.UPDATE, endpoint);
	}

	@Test
	public void adminDeleteTest() {
		AccessRights.check(Role.ADMIN, Operation.DELETE, endpoint);
	}

	@Test
	public void adminReadAllTest() {
		AccessRights.check(Role.ADMIN, Operation.READ_ALL, endpoint);
	}

	@Test
	public void adminReadDishesTest() {
		AccessRights.check(Role.ADMIN, Operation.READ_DISHES, endpoint);
	}

	@Test
	public void adminPayementInfoTest() {
		AccessRights.check(Role.ADMIN, Operation.PAYEMENT_INFO, endpoint);
	}

	@Test
	public void sUserCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.CREATE, endpoint);
	}

	@Test
	public void sUserReadTest() {
		AccessRights.check(Role.SUPER_USER, Operation.READ, endpoint);
	}

	@Test
	public void sUserUpdateTest() {
		AccessRights.check(Role.SUPER_USER, Operation.UPDATE, endpoint);
	}

	@Test
	public void sUserDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.DELETE, endpoint);
	}

	@Test
	public void sUserReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.READ_ALL, endpoint);
	}

	@Test
	public void sUserReadDishesTest() {
		AccessRights.check(Role.SUPER_USER, Operation.READ_DISHES, endpoint);
	}

	@Test
	public void sUserPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.PAYEMENT_INFO, endpoint);
	}

	@Test
	public void userCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.CREATE, endpoint);
	}

	@Test
	public void userReadTest() {
		AccessRights.check(Role.USER, Operation.READ, endpoint);
	}

	@Test
	public void userUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.UPDATE, endpoint);
	}

	@Test
	public void userDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.DELETE, endpoint);
	}

	@Test
	public void userReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.READ_ALL, endpoint);
	}

	@Test
	public void userReadDishesTest() {
		AccessRights.check(Role.USER, Operation.READ_DISHES, endpoint);
	}

	@Test
	public void userPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.PAYEMENT_INFO, endpoint);
	}

	@Test
	public void webCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.CREATE, endpoint);
	}

	@Test
	public void webReadTest() {
		AccessRights.check(Role.EPICKUR_WEB, Operation.READ, endpoint);
	}

	@Test
	public void webUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.UPDATE, endpoint);
	}

	@Test
	public void webDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.DELETE, endpoint);
	}

	@Test
	public void webReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.READ_ALL, endpoint);
	}

	@Test
	public void webReadDishesTest() {
		AccessRights.check(Role.EPICKUR_WEB, Operation.READ_DISHES, endpoint);
	}

	@Test
	public void webPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.PAYEMENT_INFO, endpoint);
	}
}
