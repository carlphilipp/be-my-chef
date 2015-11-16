package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.PasswordManager;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;

public class UserBusinessTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private UserDAO userDAOMock;
	@Mock
	private KeyBusiness keyBusinessMock;
	@Mock
	private EmailUtils emailUtilsMock;
	@InjectMocks
	private UserBusiness userBusiness;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.create((User) anyObject())).thenReturn(userAfterCreate);

		User actual = userBusiness.create(user, false);
		assertNotNull(actual.getId());
		assertNull(actual.getPassword());
		assertNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNull(actual.getKey());
		assertNotNull(actual.getCode());
	}

	@Test
	public void testCreateAlreadyExistsFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage("The user already exists");

		User user = EntityGenerator.generateRandomUser();

		when(userDAOMock.exists(anyString(), anyString())).thenReturn(true);

		userBusiness.create(user, true);
	}

	@Test
	public void testLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String newPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		userAfterRead.setAllow(1);
		Key keyMock = new Key();
		keyMock.setId(new ObjectId());

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);
		when(keyBusinessMock.readWithName(anyString())).thenReturn(keyMock);

		User actual = userBusiness.login(user.getEmail(), user.getPassword());
		assertNotNull(actual.getId());
		assertNull(actual.getPassword());
		assertNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
	}

	@Test(expected = EpickurException.class)
	public void testLoginUserNotFoundFail() throws EpickurException {
		String randomLogin = EntityGenerator.generateRandomString();
		when(userDAOMock.readWithEmail(randomLogin)).thenReturn(null);

		userBusiness.login(randomLogin, EntityGenerator.generateRandomString());
	}

	@Test
	public void testLoginWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String dbPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);

		userBusiness.login(user.getEmail(), EntityGenerator.generateRandomString());
	}

	@Test
	public void testLoginUserNotAllowedFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String dbPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);

		userBusiness.login(user.getEmail(), user.getPassword());
	}

	@Test
	public void testInjectNewPassword() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		user.setNewPassword("newpassword");
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String dbPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);

		User modified = userBusiness.injectNewPassword(user);
		String newPassword = PasswordManager.createPasswordManager("newpassword").createDBPassword();
		// Must be different because the salt is random
		assertNotEquals(newPassword, modified.getPassword());
	}

	@Test
	public void testInjectNewPasswordUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();

		when(userDAOMock.readWithEmail(anyString())).thenReturn(null);

		userBusiness.injectNewPassword(user);
	}

	@Test
	public void testInjectNewPasswordWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();

		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String wrongPassword = PasswordManager.createPasswordManager("wrongpassword").createDBPassword();
		userAfterRead.setPassword(wrongPassword);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);

		userBusiness.injectNewPassword(user);
	}

	@Test
	public void testCheckCode() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String newPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		String code = Security.getUserCode(userAfterRead);
		userAfterRead.setCode(code);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);
		when(userDAOMock.update((User) anyObject())).thenReturn(userAfterRead);

		User actual = userBusiness.checkCode(user.getEmail(), code);
		assertEquals(1, actual.getAllow().intValue());
		assertNull(actual.getPassword());
	}

	@Test
	public void testCheckCodeUserNotFound() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		String email = EntityGenerator.generateRandomString();
		String code = EntityGenerator.generateRandomString();

		when(userDAOMock.readWithEmail(anyString())).thenReturn(null);

		userBusiness.checkCode(email, code);
	}

	@Test
	public void testCheckCodeWrongCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String newPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		String code = "fail";
		userAfterRead.setCode(code);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);
		when(userDAOMock.update((User) anyObject())).thenReturn(userAfterRead);

		userBusiness.checkCode(user.getEmail(), code);
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterRead);

		userBusiness.resetPasswordFirstStep(anyString());
	}

	@Test
	public void testResetPasswordFirstStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		userBusiness.resetPasswordFirstStep(anyString());
	}

	@Test
	public void testResetPasswordSecond() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String resetCode = Security.createResetCode(userAfterRead.getId(), userAfterRead.getEmail());
		String newPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(newPassword);

		when(userDAOMock.read(anyString())).thenReturn(userAfterRead);
		when(userDAOMock.update((User) anyObject())).thenReturn(userAfterRead);

		userBusiness.resetPasswordSecondStep(anyString(), "newPass", resetCode);
	}

	@Test
	public void testResetPasswordSecondStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterCreate);

		userBusiness.resetPasswordSecondStep("", "", "");
	}

	@Test
	public void testResetPasswordSecondWrongResetCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.read(anyString())).thenReturn(userAfterCreate);

		userBusiness.resetPasswordSecondStep("", "", "");
	}
	
	@Test
	public void testSuscribeToNewsletter(){
		User user = EntityGenerator.generateRandomUser();
		userBusiness.suscribeToNewsletter(user);
	}
	
	@Test
	public void testSuscribeToNewsletterMoreCoverage(){
		User user = EntityGenerator.generateRandomUser();
		user.setFirst(null);
		user.setLast(null);
		userBusiness.suscribeToNewsletter(user);
	}
}
