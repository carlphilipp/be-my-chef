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
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private final EndpointType endpoint = EndpointType.VOUCHER;
	
	@Test
	public void adminCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.ADMIN, Operation.CREATE, endpoint);
	}
	
	@Test
	public void adminReadTest() {
		AccessRights.check(Role.ADMIN, Operation.READ, endpoint);
	}

	@Test
	public void adminUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.ADMIN, Operation.UPDATE, endpoint);
	}

	@Test
	public void adminDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.ADMIN, Operation.DELETE, endpoint);
	}
	
	@Test
	public void adminGenerateTest() {
		AccessRights.check(Role.ADMIN, Operation.GENERATE_VOUCHER, endpoint);
	}
	
	@Test
	public void superUserCreateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.CREATE, endpoint);
	}
	
	@Test
	public void superUserReadTest() {
		AccessRights.check(Role.SUPER_USER, Operation.READ, endpoint);
	}

	@Test
	public void superUserUpdateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.UPDATE, endpoint);
	}

	@Test
	public void superUserDeleteTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.DELETE, endpoint);
	}
	
	@Test
	public void superUserGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.SUPER_USER, Operation.GENERATE_VOUCHER, endpoint);
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
	public void userGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.USER, Operation.GENERATE_VOUCHER, endpoint);
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
	public void webGenerateTest() {
		thrown.expect(EpickurForbiddenException.class);
		AccessRights.check(Role.EPICKUR_WEB, Operation.GENERATE_VOUCHER, endpoint);
	}
}
