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

	private final static EndpointType ENDPOINT_TYPE = EndpointType.CATERER;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void adminCreateTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.CREATE, ENDPOINT_TYPE);
	}

	@Test
	public void adminReadTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void adminUpdateTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void adminDeleteTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.DELETE, ENDPOINT_TYPE);
	}

	@Test
	public void adminReadAllTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.READ_ALL, ENDPOINT_TYPE);
	}

	@Test
	public void adminReadDishesTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.READ_DISHES, ENDPOINT_TYPE);
	}

	@Test
	public void adminPayementInfoTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.PAYEMENT_INFO, ENDPOINT_TYPE);
	}

	@Test
	public void sUserCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.CREATE, ENDPOINT_TYPE);
	}

	@Test
	public void sUserReadTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void sUserUpdateTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void sUserDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.DELETE, ENDPOINT_TYPE);
	}

	@Test
	public void sUserReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.READ_ALL, ENDPOINT_TYPE);
	}

	@Test
	public void sUserReadDishesTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.READ_DISHES, ENDPOINT_TYPE);
	}

	@Test
	public void sUserPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.PAYEMENT_INFO, ENDPOINT_TYPE);
	}

	@Test
	public void userCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.CREATE, ENDPOINT_TYPE);
	}

	@Test
	public void userReadTest() {
		MatrixAccessRights.check(Role.USER, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void userUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void userDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.DELETE, ENDPOINT_TYPE);
	}

	@Test
	public void userReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.READ_ALL, ENDPOINT_TYPE);
	}

	@Test
	public void userReadDishesTest() {
		MatrixAccessRights.check(Role.USER, Operation.READ_DISHES, ENDPOINT_TYPE);
	}

	@Test
	public void userPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.PAYEMENT_INFO, ENDPOINT_TYPE);
	}

	@Test
	public void webCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.CREATE, ENDPOINT_TYPE);
	}

	@Test
	public void webReadTest() {
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void webUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void webDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.DELETE, ENDPOINT_TYPE);
	}

	@Test
	public void webReadAllTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.READ_ALL, ENDPOINT_TYPE);
	}

	@Test
	public void webReadDishesTest() {
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.READ_DISHES, ENDPOINT_TYPE);
	}

	@Test
	public void webPayementInfoTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.PAYEMENT_INFO, ENDPOINT_TYPE);
	}
}
