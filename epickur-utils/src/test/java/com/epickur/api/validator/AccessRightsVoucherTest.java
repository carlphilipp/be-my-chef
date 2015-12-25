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
public class AccessRightsVoucherTest {

	private static final EndpointType ENDPOINT_TYPE = EndpointType.VOUCHER;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void adminCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.ADMIN, Operation.CREATE, ENDPOINT_TYPE);
	}
	
	@Test
	public void adminReadTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void adminUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.ADMIN, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void adminDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.ADMIN, Operation.DELETE, ENDPOINT_TYPE);
	}
	
	@Test
	public void adminGenerateTest() {
		MatrixAccessRights.check(Role.ADMIN, Operation.GENERATE_VOUCHER, ENDPOINT_TYPE);
	}
	
	@Test
	public void superUserCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.CREATE, ENDPOINT_TYPE);
	}
	
	@Test
	public void superUserReadTest() {
		MatrixAccessRights.check(Role.SUPER_USER, Operation.READ, ENDPOINT_TYPE);
	}

	@Test
	public void superUserUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.UPDATE, ENDPOINT_TYPE);
	}

	@Test
	public void superUserDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.DELETE, ENDPOINT_TYPE);
	}
	
	@Test
	public void superUserGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.SUPER_USER, Operation.GENERATE_VOUCHER, ENDPOINT_TYPE);
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
	public void userGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.USER, Operation.GENERATE_VOUCHER, ENDPOINT_TYPE);
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
	public void webGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		MatrixAccessRights.check(Role.EPICKUR_WEB, Operation.GENERATE_VOUCHER, ENDPOINT_TYPE);
	}
}
