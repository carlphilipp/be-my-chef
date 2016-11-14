package com.epickur.api.service;

import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.utils.security.PasswordManager;
import com.epickur.api.utils.security.Security;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private UserDAO userDAOMock;
	@Mock
	private KeyService keyBusinessMock;
	@Mock
	private EmailUtils emailUtilsMock;
	@Mock
	private Utils utilsMock;
	@InjectMocks
	private UserService service;

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		user = spy(user);
		userAfterCreate = spy(userAfterCreate);

		given(userDAOMock.create(user)).willReturn(userAfterCreate);

		User actual = service.create(user, false);

		assertNotNull(actual.getId());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		assertNull(actual.getKey());
		assertNotNull(actual.getCode());
		then(userDAOMock).should().exists(user.getName(), user.getEmail());
		then(userDAOMock).should().create(user);
		then(user).should().setAllow(0);
		then(user).should().setPassword(anyString());
		then(user).should().setKey(null);
		then(user).should().setRole(Role.USER);
		then(userAfterCreate).should().setCode(anyString());
		then(emailUtilsMock).should().emailNewRegistration(userAfterCreate, actual.getCode());
	}

	@Test
	public void testCreateAlreadyExistsFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage("The user already exists");

		User user = EntityGenerator.generateRandomUser();

		given(userDAOMock.exists(user.getName(), user.getEmail())).willReturn(true);

		try {
			service.create(user, true);
		} finally {
			then(userDAOMock).should().exists(user.getName(), user.getEmail());
			then(userDAOMock).should(never()).create(user);
			then(emailUtilsMock).should(never()).emailNewRegistration(any(User.class), anyString());
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

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));
		given(keyBusinessMock.readWithName(user.getName())).willReturn(keyMock);
		given(utilsMock.isPasswordCorrect(user.getPassword(), userAfterRead)).willReturn(true);

		User actual = service.login(user.getEmail(), user.getPassword());

		assertNotNull(actual.getId());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
		then(userDAOMock).should().readWithEmail(user.getEmail());
		then(userAfterRead).should().getAllow();
		then(userAfterRead).should().setKey(anyString());
		then(keyBusinessMock).should().readWithName(user.getName());
		then(keyBusinessMock).should().delete(id.toHexString());
		then(keyBusinessMock).should().create(any(Key.class));
	}

	@Test
	public void testLoginUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);

		String randomLogin = EntityGenerator.generateRandomString();
		given(userDAOMock.readWithEmail(randomLogin)).willReturn(Optional.empty());

		try {
			service.login(randomLogin, EntityGenerator.generateRandomString());
		} finally {
			then(userDAOMock).should().readWithEmail(randomLogin);
		}
	}

	@Test
	public void testLoginWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String dbPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));

		try {
			service.login(user.getEmail(), EntityGenerator.generateRandomString());
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userAfterRead).should(never()).getAllow();
		}
	}

	@Test
	public void testLoginUserNotAllowedFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String dbPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(dbPassword);

		given(utilsMock.isPasswordCorrect(user.getPassword(), userAfterRead)).willReturn(true);
		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));

		try {
			service.login(user.getEmail(), user.getPassword());
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userAfterRead).should().getAllow();
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

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));
		given(utilsMock.isPasswordCorrect(user.getPassword(), userAfterRead)).willReturn(true);

		try {
			User modified = service.injectNewPassword(user);
			String newPassword = PasswordManager.createPasswordManager("newpassword").createDBPassword();
			// Must be different because the salt is random
			assertNotEquals(newPassword, modified.getPassword());
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userAfterRead).should().setPassword(anyString());
		}
	}

	@Test
	public void testInjectNewPasswordUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.empty());

		try {
			service.injectNewPassword(user);
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
		}
	}

	@Test
	public void testInjectNewPasswordWrongPasswordFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();

		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		String wrongPassword = PasswordManager.createPasswordManager("wrongpassword").createDBPassword();
		userAfterRead.setPassword(wrongPassword);
		userAfterRead = spy(userAfterRead);

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));

		try {
			service.injectNewPassword(user);
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userAfterRead).should(never()).setPassword(anyString());
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

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));
		given(userDAOMock.update(userAfterRead)).willReturn(userAfterRead);

		try {
			User actual = service.checkCode(user.getEmail(), code);
			assertEquals(1, actual.getAllow().intValue());
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userDAOMock).should().update(userAfterRead);
			then(userAfterRead).should().setAllow(1);
			then(userAfterRead).should().setCreatedAt(null);
			then(userAfterRead).should().setUpdatedAt(any(DateTime.class));
			then(userAfterRead).should().setKey(null);
			then(userDAOMock).should().update(userAfterRead);
		}
	}

	@Test
	public void testCheckCodeUserNotFound() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		String email = EntityGenerator.generateRandomString();
		String code = EntityGenerator.generateRandomString();

		given(userDAOMock.readWithEmail(email)).willReturn(Optional.empty());

		try {
			service.checkCode(email, code);
		} finally {
			then(userDAOMock).should().readWithEmail(email);
			then(userDAOMock).should(never()).update(any());
			then(userDAOMock).should(never()).update(any());
		}
	}

	@Test
	public void testCheckCodeWrongCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String newPassword = PasswordManager.createPasswordManager(EntityGenerator.generateRandomString()).createDBPassword();
		userAfterRead.setPassword(newPassword);
		String code = "fail";
		userAfterRead.setCode(code);

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));
		given(userDAOMock.update(userAfterRead)).willReturn(userAfterRead);
		try {
			service.checkCode(user.getEmail(), code);
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(userDAOMock).should(never()).update(userAfterRead);
			then(userAfterRead).should(never()).setAllow(1);
			then(userAfterRead).should(never()).setCreatedAt(null);
			then(userAfterRead).should(never()).setUpdatedAt(any(DateTime.class));
			then(userAfterRead).should(never()).setKey(null);
			then(userDAOMock).should(never()).update(userAfterRead);
		}
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);

		given(userDAOMock.readWithEmail(user.getEmail())).willReturn(Optional.of(userAfterRead));

		try {
			service.resetPasswordFirstStep(user.getEmail());
		} finally {
			then(userDAOMock).should().readWithEmail(user.getEmail());
			then(emailUtilsMock).should().resetPassword(eq(userAfterRead), anyString());
		}
	}

	@Test
	public void testResetPasswordFirstStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		String email = EntityGenerator.generateRandomString();
		given(userDAOMock.readWithEmail(email)).willReturn(Optional.empty());
		try {
			service.resetPasswordFirstStep(email);
		} finally {
			then(userDAOMock).should().readWithEmail(email);
			then(emailUtilsMock).should(never()).resetPassword(any(User.class), anyString());
		}
	}

	@Test
	public void testResetPasswordSecond() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		userAfterRead = spy(userAfterRead);
		String resetCode = Security.createResetCode(userAfterRead.getId(), userAfterRead.getEmail());
		String newPassword = PasswordManager.createPasswordManager(user.getPassword()).createDBPassword();
		userAfterRead.setPassword(newPassword);

		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(userAfterRead));
		given(userDAOMock.update(userAfterRead)).willReturn(userAfterRead);

		service.resetPasswordSecondStep(user.getId().toHexString(), "newPass", resetCode);

		then(userDAOMock).should().read(user.getId().toHexString());
		then(userDAOMock).should().update(userAfterRead);
		then(userAfterRead).should(times(2)).setPassword(anyString());
	}

	@Test
	public void testResetPasswordSecondStepUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(userAfterCreate));
		try {
			service.resetPasswordSecondStep(user.getId().toHexString(), "", "");
		} finally {
			then(userDAOMock).should().read(user.getId().toHexString());
			then(userDAOMock).should(never()).update(any(User.class));
		}
	}

	@Test
	public void testResetPasswordSecondWrongResetCodeFail() throws EpickurException {
		thrown.expect(EpickurException.class);
		thrown.expectMessage(ErrorConstants.USER_NOT_FOUND);

		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(userAfterCreate));
		try {
			service.resetPasswordSecondStep(user.getId().toHexString(), "", "");
		} finally {
			then(userDAOMock).should().read(user.getId().toHexString());
			then(userDAOMock).should(never()).update(any(User.class));
		}
	}

	@Test
	public void testSuscribeToNewsletter() {
		User user = EntityGenerator.generateRandomUser();
		user = spy(user);

		service.suscribeToNewsletter(user);

		then(user).should(times(2)).getFirst();
		then(user).should(times(2)).getLast();
		then(user).should(times(2)).getEmail();
		then(user).should().getCountry();
		then(user).should().getState();
		then(user).should().getZipcode();
	}

	@Test
	public void testSuscribeToNewsletterMoreCoverage() {
		User user = EntityGenerator.generateRandomUser();
		user.setFirst(null);
		user.setLast(null);
		user = spy(user);

		service.suscribeToNewsletter(user);

		then(user).should().getFirst();
		then(user).should().getLast();
		then(user).should(times(2)).getEmail();
		then(user).should().getCountry();
		then(user).should().getState();
		then(user).should().getZipcode();
	}
}
