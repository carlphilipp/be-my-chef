package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.PasswordManager;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;

public class UserServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private UserDAO userDAOMock;
	@Mock
	private KeyService keyBusinessMock;
	@Mock
	private EmailUtils emailUtilsMock;
	@InjectMocks
	private UserService service;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		user = spy(user);
		userAfterCreate = spy(userAfterCreate);

		when(userDAOMock.create(user)).thenReturn(userAfterCreate);

		User actual = service.create(user, false);

		assertNotNull(actual.getId());
		assertNull(actual.getPassword());
		assertNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNull(actual.getKey());
		assertNotNull(actual.getCode());
		verify(userDAOMock, times(1)).exists(user.getName(), user.getEmail());
		verify(userDAOMock, times(1)).create(user);
		verify(user, times(1)).setAllow(0);
		verify(user, times(1)).setPassword(anyString());
		verify(user, times(1)).setKey(null);
		verify(user, times(1)).setRole(Role.USER);
		verify(userAfterCreate, times(1)).setPassword(null);
		verify(userAfterCreate, times(1)).setRole(null);
		verify(userAfterCreate, times(1)).setCode(anyString());
		String name = userAfterCreate.getName();
		String email = userAfterCreate.getEmail();
		String first = userAfterCreate.getFirst();
		verify(emailUtilsMock, times(1)).emailNewRegistration(eq(name), eq(first), anyString(), eq(email));
	}

	@Test
	public void testCreateAlreadyExistsFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage("The user already exists");

		User user = EntityGenerator.generateRandomUser();

		when(userDAOMock.exists(user.getName(), user.getEmail())).thenReturn(true);

		try {
			service.create(user, true);
		} finally {
			verify(userDAOMock, times(1)).exists(user.getName(), user.getEmail());
			verify(userDAOMock, never()).create(user);
			verify(emailUtilsMock, never()).emailNewRegistration(anyString(), anyString(), anyString(), anyString());
		}
	}

	@Test
	public void testLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		user = spy(user);
		userAfterRead = spy(userAfterRead);

		String newPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		userAfterRead.setAllow(1);
		Key keyMock = new Key();
		ObjectId id = new ObjectId();
		keyMock.setId(id);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);
		when(keyBusinessMock.readWithName(user.getName())).thenReturn(keyMock);

		User actual = service.login(user.getEmail(), user.getPassword());

		assertNotNull(actual.getId());
		assertNull(actual.getPassword());
		assertNull(actual.getRole());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
		verify(userAfterRead, times(1)).getAllow();
		verify(userAfterRead, times(1)).setKey(anyString());
		verify(keyBusinessMock, times(1)).readWithName(user.getName());
		verify(keyBusinessMock, times(1)).delete(id.toHexString());
		verify(keyBusinessMock, times(1)).create(any(Key.class));
		verify(userAfterRead, times(1)).setPassword(null);
		verify(userAfterRead, times(1)).setRole(null);
	}

	@Test
	public void testLoginUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);

		String randomLogin = EntityGenerator.generateRandomString();
		when(userDAOMock.readWithEmail(randomLogin)).thenReturn(null);

		try {
			service.login(randomLogin, EntityGenerator.generateRandomString());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(randomLogin);
		}
	}

	@Test
	public void testLoginWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String dbPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);

		try {
			service.login(user.getEmail(), EntityGenerator.generateRandomString());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userAfterRead, never()).getAllow();
		}
	}

	@Test
	public void testLoginUserNotAllowedFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String dbPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);

		try {
			service.login(user.getEmail(), user.getPassword());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userAfterRead, times(1)).getAllow();
		}
	}

	@Test
	public void testInjectNewPassword() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		user.setNewPassword("newpassword");
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String dbPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);

		try {
			User modified = service.injectNewPassword(user);
			String newPassword = PasswordManager.createPasswordManager("newpassword").createDBPassword();
			// Must be different because the salt is random
			assertNotEquals(newPassword, modified.getPassword());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userAfterRead, times(1)).setPassword(anyString());
		}
	}

	@Test
	public void testInjectNewPasswordUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(null);

		try {
			service.injectNewPassword(user);
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
		}
	}

	@Test
	public void testInjectNewPasswordWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();

		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String wrongPassword = PasswordManager.createPasswordManager("wrongpassword").createDBPassword();
		userAfterRead.setPassword(wrongPassword);
		userAfterRead = spy(userAfterRead);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);

		try {
			service.injectNewPassword(user);
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userAfterRead, never()).setPassword(anyString());
		}
	}

	@Test
	public void testCheckCode() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String newPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		String code = Security.getUserCode(userAfterRead);
		userAfterRead.setCode(code);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);
		when(userDAOMock.update(userAfterRead)).thenReturn(userAfterRead);

		try {
			User actual = service.checkCode(user.getEmail(), code);
			assertEquals(1, actual.getAllow().intValue());
			assertNull(actual.getPassword());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userDAOMock, times(1)).update(userAfterRead);
			verify(userAfterRead, times(1)).setAllow(1);
			verify(userAfterRead, times(1)).setCreatedAt(null);
			verify(userAfterRead, times(1)).setUpdatedAt(any(DateTime.class));
			verify(userAfterRead, times(1)).setKey(null);
			verify(userDAOMock, times(1)).update(userAfterRead);
		}
	}

	@Test
	public void testCheckCodeUserNotFound() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		String email = EntityGenerator.generateRandomString();
		String code = EntityGenerator.generateRandomString();

		when(userDAOMock.readWithEmail(email)).thenReturn(null);

		try {
			service.checkCode(email, code);
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(email);
			verify(userDAOMock, never()).update(any());
			verify(userDAOMock, never()).update(any());
		}
	}

	@Test
	public void testCheckCodeWrongCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String newPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		String code = "fail";
		userAfterRead.setCode(code);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);
		when(userDAOMock.update(userAfterRead)).thenReturn(userAfterRead);
		try {
			service.checkCode(user.getEmail(), code);
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(userDAOMock, never()).update(userAfterRead);
			verify(userAfterRead, never()).setAllow(1);
			verify(userAfterRead, never()).setCreatedAt(null);
			verify(userAfterRead, never()).setUpdatedAt(any(DateTime.class));
			verify(userAfterRead, never()).setKey(null);
			verify(userDAOMock, never()).update(userAfterRead);
		}
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.readWithEmail(user.getEmail())).thenReturn(userAfterRead);
		
		try {
			service.resetPasswordFirstStep(user.getEmail());
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(user.getEmail());
			verify(emailUtilsMock, times(1)).resetPassword(eq(userAfterRead), anyString());
		}
	}

	@Test
	public void testResetPasswordFirstStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);
		String email = EntityGenerator.generateRandomString();
		try {
			service.resetPasswordFirstStep(email);
		} finally {
			verify(userDAOMock, times(1)).readWithEmail(email);
			verify(emailUtilsMock, never()).resetPassword(any(User.class), anyString());
		}
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

		service.resetPasswordSecondStep(anyString(), "newPass", resetCode);
	}

	@Test
	public void testResetPasswordSecondStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.readWithEmail(anyString())).thenReturn(userAfterCreate);

		service.resetPasswordSecondStep("", "", "");
	}

	@Test
	public void testResetPasswordSecondWrongResetCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorUtils.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userDAOMock.read(anyString())).thenReturn(userAfterCreate);

		service.resetPasswordSecondStep("", "", "");
	}

	@Test
	public void testSuscribeToNewsletter() {
		User user = EntityGenerator.generateRandomUser();
		service.suscribeToNewsletter(user);
	}

	@Test
	public void testSuscribeToNewsletterMoreCoverage() {
		User user = EntityGenerator.generateRandomUser();
		user.setFirst(null);
		user.setLast(null);
		service.suscribeToNewsletter(user);
	}
}
