package com.epickur.api.service;

import com.epickur.api.annotation.ValidateComplexAccessRights;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurDuplicateKeyException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.utils.security.PasswordManager;
import com.epickur.api.utils.security.Security;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.enumeration.EndpointType.USER;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;

/**
 * User business layer. Accesses User DAO layer and executes logic.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Service
public class UserService {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private KeyService keyService;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private Utils utils;

	/**
	 * Create a User
	 *
	 * @param user         The User
	 * @param autoValidate True if you want to auto validate the User
	 * @return The User created
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User create(final User user, final boolean autoValidate) throws EpickurException {
		checkIfUserExists(user);
		user.setAllow(autoValidate ? 1 : 0);
		final PasswordManager passwordManager = PasswordManager.createPasswordManager(user.getPassword());
		final String dbPassword = passwordManager.createDBPassword();
		final String code = passwordManager.getCode(user.getName(), user.getEmail());

		user.setPassword(dbPassword);

		user.prepareForInsertionIntoDB();

		final User userCreated = userDAO.create(user);

		emailUtils.emailNewRegistration(userCreated, code);

		userCreated.setCode(code);
		return userCreated;
	}

	private void checkIfUserExists(final User user) throws EpickurDBException {
		if (userDAO.exists(user.getName(), user.getEmail())) {
			throw new EpickurDuplicateKeyException("The user already exists");
		}
	}

	/**
	 * @param id the User id to read
	 * @return The User
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateComplexAccessRights(operation = READ, type = USER)
	public Optional<User> read(final String id) throws EpickurException {
		return userDAO.read(id);
	}

	/**
	 * Read a User with its email
	 *
	 * @param email The email of the User
	 * @return The User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public Optional<User> readWithEmail(final String email) throws EpickurException {
		return userDAO.readWithEmail(email);
	}

	/**
	 * Read a list of User
	 *
	 * @return A list of User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public List<User> readAll() throws EpickurException {
		return userDAO.readAll();
	}

	/**
	 * @param user The User to update
	 * @return The User updated
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateComplexAccessRights(operation = UPDATE, type = USER)
	public User update(final User user) throws EpickurException {
		user.prepareForUpdateIntoDB();
		return userDAO.update(user);
	}

	/**
	 * Delete a User
	 *
	 * @param id The id of the User to delete
	 * @return True if the User has been deleted
	 * @throws EpickurException If an epickur exception occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		final boolean isDeleted = userDAO.delete(id);
		if (!isDeleted) {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, id);
		}
		return true;
	}

	/**
	 * Login
	 *
	 * @param email    The email of the User
	 * @param password The password of the User
	 * @return A User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User login(final String email, final String password) throws EpickurException {
		final Optional<User> userFoundOptional = readWithEmail(email);
		if (userFoundOptional.isPresent()) {
			final User user = userFoundOptional.get();
			if (!utils.isPasswordCorrect(password, user)) {
				throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
			} else if (user.getAllow() == 1) {
				final String tempKey = Security.generateRandomMd5();
				user.setKey(tempKey);
				final Key currentKey = keyService.readWithName(user.getName());
				if (currentKey != null) {
					keyService.delete(currentKey.getId().toHexString());
				}
				final Key key = new Key();
				key.setCreatedAt(new DateTime());
				key.setUserId(user.getId());
				key.setKey(user.getKey());
				key.setRole(user.getRole());
				keyService.create(key);
			} else {
				throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
			}
			return user;
		} else {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
		}
	}

	/**
	 * Inject a new encoded password into the User
	 *
	 * @param user The User
	 * @return The User modified with its new encoded password
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User injectNewPassword(final User user) throws EpickurException {
		final Optional<User> userFoundOptional = readWithEmail(user.getEmail());
		if (!userFoundOptional.isPresent()) {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, user.getEmail());
		} else {
			if (!utils.isPasswordCorrect(user.getPassword(), userFoundOptional.get())) {
				throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, user.getEmail());
			} else {
				final String newEncryptedPassword = PasswordManager.createPasswordManager(user.getNewPassword()).createDBPassword();
				user.setPassword(newEncryptedPassword);
			}
		}
		return user;
	}

	/**
	 * Check if the code provided is correct and unlock the User
	 *
	 * @param email The email of the User
	 * @param code  The code provided by the API to the User
	 * @return An updated User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User checkCode(final String email, final String code) throws EpickurException {
		final Optional<User> userFoundOptional = readWithEmail(email);
		if (userFoundOptional.isPresent()) {
			User userFound = userFoundOptional.get();
			final String codeFound = Security.getUserCode(userFound);
			if (!codeFound.equals(code)) {
				throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
			} else {
				userFound.setAllow(1);
				userFound.prepareForUpdateIntoDB();
				userFound = userDAO.update(userFound);
			}
			return userFound;
		} else {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
		}
	}

	/**
	 * @param email The email
	 * @throws EpickurException If an epickur exception occurred
	 */
	public void resetPasswordFirstStep(final String email) throws EpickurException {
		final Optional<User> userOptional = readWithEmail(email);
		if (!userOptional.isPresent()) {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, email);
		}
		final User user = userOptional.get();
		final String resetCode = Security.createResetCode(user.getId(), email);
		emailUtils.resetPassword(user, resetCode);
	}

	/**
	 * @param id          The User Id
	 * @param newPassword The new password
	 * @param resetCode   The reset code
	 * @return A User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User resetPasswordSecondStep(final String id, final String newPassword, final String resetCode) throws EpickurException {
		final Optional<User> userOptional = userDAO.read(id);
		if (!userOptional.isPresent()) {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, id);
		}
		final User user = userOptional.get();
		final String resetCodeDB = Security.createResetCode(user.getId(), user.getEmail());
		if (!resetCodeDB.equals(resetCode)) {
			throw new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, id);
		} else {
			final String newEncryptedPassword = PasswordManager.createPasswordManager(newPassword).createDBPassword();
			user.setPassword(newEncryptedPassword);
			user.prepareForUpdateIntoDB();
			return userDAO.update(user);
		}
	}

	/**
	 * Suscribe a user to newsletter
	 *
	 * @param user The user.
	 */
	public void suscribeToNewsletter(final User user) {
		final String url = buildNewsletterUrl(user);
		subscribeUserToNewsletter(url, user.getEmail());
	}

	protected String buildNewsletterUrl(final User user) {
		String url = "https://bemychef.us10.list-manage.com/subscribe/post-json"
			+ "?u=b0fe27a209ea8ffa59b813767"
			+ "&id=10d0ff2b3b"
			+ "&FNAME=@@FIRST@@"
			+ "&LNAME=@@LAST@@"
			+ "&EMAIL=@@EMAIL@@"
			+ "&ZCODE=@@ZIP@@"
			+ "&STATE=@@STATE@@"
			+ "&COUNTRY=@@COUNTRY@@";
		if (StringUtils.isBlank(user.getFirst())) {
			url = url.replaceFirst("@@FIRST@@", "-");
		} else {
			url = url.replaceFirst("@@FIRST@@", user.getFirst());
		}
		if (StringUtils.isBlank(user.getLast())) {
			url = url.replaceFirst("@@LAST@@", "-");
		} else {
			url = url.replaceFirst("@@LAST@@", user.getLast());
		}
		url = url.replaceFirst("@@EMAIL@@", user.getEmail());
		url = url.replaceFirst("@@COUNTRY@@", user.getCountry());
		url = url.replaceFirst("@@STATE@@", user.getState());
		url = url.replaceFirst("@@ZIP@@", user.getZipcode());
		return url;
	}

	protected void subscribeUserToNewsletter(final String url, final String email) {
		try {
			final HttpPost request = new HttpPost(url);
			HttpClientBuilder.create().build().execute(request);
		} catch (IOException ioe) {
			log.error("Could not subscribe {} to our newsletter", email, ioe);
		}
	}
}
