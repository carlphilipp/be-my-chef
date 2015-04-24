package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Info;

public class UserBusinessTest {

	private static UserBusiness business;
	private static List<ObjectId> idsToDelete;
	private static List<String> temp;

	@BeforeClass
	public static void beforeClass() {
		business = new UserBusiness();
		idsToDelete = new ArrayList<ObjectId>();
		temp = new ArrayList<String>(Info.admins);
		List<String> list = new ArrayList<String>();
		list.add("");
		Info.admins = list;
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			business.delete(id.toHexString());
		}
		Info.admins = temp;
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User res = business.create(user, true, false);
		idsToDelete.add(res.getId());
		assertNotNull(res.getId());
	}

	@Test(expected = EpickurException.class)
	public void testLogin() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User res = business.create(user, true, false);
		idsToDelete.add(res.getId());
		business.login("EMail not working", res.getPassword());
	}

	@Test(expected = EpickurException.class)
	public void testLogin2() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User res = business.create(user, true, false);
		idsToDelete.add(res.getId());
		business.login(res.getEmail(), "Email password");
	}

	@Test(expected = EpickurException.class)
	public void testLogin3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String pass = new String(user.getPassword());
		User res = business.create(user, true, false);
		idsToDelete.add(res.getId());
		business.login(res.getEmail(), pass);
	}

	@Test(expected = EpickurException.class)
	public void testInjectNewPassword() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		business.injectNewPassword(user);
	}

	public void testInjectNewPassword2() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User temp = user.clone();
		temp.setNewPassword("newpassword");
		User created = business.create(user, false, false);
		idsToDelete.add(created.getId());
		User modified = business.injectNewPassword(temp);
		assertNotEquals("newpassword", modified.getPassword());
	}

	@Test(expected = EpickurException.class)
	public void testInjectNewPassword3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User temp = user.clone();
		temp.setNewPassword("newpassword");
		temp.setPassword("faile");
		User created = business.create(user, false, false);
		idsToDelete.add(created.getId());
		business.injectNewPassword(temp);
	}

	public void testCheckCode() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String name = new String(user.getName());
		User created = business.create(user, false, false);
		idsToDelete.add(created.getId());
		String code = new String(created.getCode());
		User checked = business.checkCode(name, code);
		assertEquals(1, checked.getAllow().intValue());
		assertNull(checked.getPassword());
	}

	@Test(expected = EpickurException.class)
	public void testCheckCode2() throws EpickurException {
		business.checkCode(TestUtils.generateRandomString(), TestUtils.generateRandomString());
	}

	@Test(expected = EpickurException.class)
	public void testCheckCode3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String name = new String(user.getName());
		User created = business.create(user, false, false);
		idsToDelete.add(created.getId());
		String code = "fail";
		User checked = business.checkCode(name, code);
		assertEquals(1, checked.getAllow().intValue());
		assertNull(checked.getPassword());
	}
}
