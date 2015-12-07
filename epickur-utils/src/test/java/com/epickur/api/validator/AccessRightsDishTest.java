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
public class AccessRightsDishTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final EndpointType endpoint = EndpointType.DISH;

	@Test
	public void adminCreateTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.CREATE, endpoint);
	}

	@Test
	public void adminReadTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.READ, endpoint);
	}

	@Test
	public void adminUpdateTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.UPDATE, endpoint);
	}

	@Test
	public void adminDeleteTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.DELETE, endpoint);
	}

	@Test
	public void adminSearchTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.SEARCH_DISH, endpoint);
	}

	@Test
	public void sUserCreateTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.CREATE, endpoint);
	}

	@Test
	public void sUserReadTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.READ, endpoint);
	}

	@Test
	public void sUserUpdateTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.UPDATE, endpoint);
	}

	@Test
	public void sUserDeleteTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.DELETE, endpoint);
	}

	@Test
	public void sUserSearchTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.SEARCH_DISH, endpoint);
	}

	@Test
	public void userCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.CREATE, endpoint);
	}

	@Test
	public void userReadTest() {
		MatrixAccessRights.check(Role.USER, Operation.READ, endpoint);
	}

	@Test
	public void userUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.UPDATE, endpoint);
	}

	@Test
	public void userDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.DELETE, endpoint);
	}

	@Test
	public void userSearchTest() {
		MatrixAccessRights.check(Role.USER, Operation.SEARCH_DISH, endpoint);
	}

	@Test
	public void webCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.CREATE, endpoint);
	}

	@Test
	public void webReadTest() {
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.READ, endpoint);
	}

	@Test
	public void webUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.UPDATE, endpoint);
	}

	@Test
	public void webDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.DELETE, endpoint);
	}

	@Test
	public void webSearchTest() {
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.SEARCH_DISH, endpoint);
	}
}
