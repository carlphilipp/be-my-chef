package com.epickur.api.service;

import com.epickur.api.aop.ValidateRequestAfter;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurDuplicateKeyException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.PasswordManager;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.EmailUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.epickur.api.enumeration.EndpointType.USER;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;

/**
 * User business layer. Accesses User DAO layer and executes logic.
 *
 * @author cph
 * @version 1.0
 */
@Service
public class UserService {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(UserService.class.getSimpleName());
	/**
	 * User dao
	 */
	@Autowired
	private UserDAO userDAO;
	/**
	 * Key Business
	 */
	@Autowired
	private KeyService keyService;
	/**
	 * User Email utils
	 */
	private EmailUtils emailUtils;

	/**
	 * The constructor
	 */
	public UserService() {
		this.emailUtils = new EmailUtils();
	}

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
		if (autoValidate) {
			user.setAllow(1);
		} else {
			user.setAllow(0);
		}
		PasswordManager passwordManager = PasswordManager.createPasswordManager(user.getPassword());
		String dbPassword = passwordManager.createDBPassword();
		String code = passwordManager.getCode(user.getName(), user.getEmail());

		user.setPassword(dbPassword);

		user.prepareForInsertionIntoDB();

		User userCreated = userDAO.create(user);

		emailUtils.emailNewRegistration(userCreated, code);

		// We do not send back the password
		userCreated.setPassword(null);
		userCreated.setRole(null);
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
	@ValidateRequestAfter(operation = READ, type = USER)
	public User read(final String id) throws EpickurException {
		User user = userDAO.read(id);
		user.setPassword(null);
		user.setRole(null);
		return user;
	}

	/**
	 * Read a User with its email
	 *
	 * @param email The email of the User
	 * @return The User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User readWithEmail(final String email) throws EpickurException {
		return userDAO.readWithEmail(email);
	}

	/**
	 * Read a list of User
	 *
	 * @return A list of User
	 * @throws EpickurException If an epickur exception occurred
	 */
	public List<User> readAll() throws EpickurException {
		List<User> users = userDAO.readAll();
		for (User user : users) {
			// We do not send back the password or the role
			user.setPassword(null);
			user.setRole(null);
		}
		return users;
	}

	/**
	 * @param user The User to update
	 * @return The User updated
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateRequestAfter(operation = UPDATE, type = USER)
	public User update(final User user) throws EpickurException {
		user.prepareForUpdateIntoDB();
		User res = userDAO.update(user);
		// We do not send back the password or the role
		res.setPassword(null);
		res.setRole(null);
		return res;
	}

	/**
	 * Delete a User
	 *
	 * @param id The id of the User to delete
	 * @return True if the User has been deleted
	 * @throws EpickurException If an epickur exception occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		boolean isDeleted = userDAO.delete(id);
		if (!isDeleted) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, id);
		}
		return isDeleted;
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
		User userFound = readWithEmail(email);
		if (userFound != null) {
			if (!Utils.isPasswordCorrect(password, userFound)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
			} else if (userFound.getAllow() == 1) {
				String tempKey = Security.generateRandomMd5();
				userFound.setKey(tempKey);
				Key currentKey = keyService.readWithName(userFound.getName());
				if (currentKey != null) {
					keyService.delete(currentKey.getId().toHexString());
				}
				Key key = new Key();
				key.setCreatedAt(new DateTime());
				key.setUserId(userFound.getId());
				key.setKey(userFound.getKey());
				key.setRole(userFound.getRole());
				keyService.create(key);
				userFound.setPassword(null);
				userFound.setRole(null);
			} else {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
			}
		} else {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
		}
		return userFound;
	}

	/**
	 * Inject a new encoded password into the User
	 *
	 * @param user The User
	 * @return The User modified with its new encoded password
	 * @throws EpickurException If an epickur exception occurred
	 */
	public User injectNewPassword(final User user) throws EpickurException {
		User userFound = readWithEmail(user.getEmail());
		if (userFound == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, user.getEmail());
		} else {
			if (!Utils.isPasswordCorrect(user.getPassword(), userFound)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, user.getEmail());
			} else {
				String newEnryptedPassword = PasswordManager.createPasswordManager(user.getNewPassword()).createDBPassword();
				user.setPassword(newEnryptedPassword);
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
		User userFound = readWithEmail(email);
		if (userFound != null) {
			String codeFound = Security.getUserCode(userFound);
			if (!codeFound.equals(code)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
			} else {
				userFound.setAllow(1);
				userFound.prepareForUpdateIntoDB();
				userFound = userDAO.update(userFound);
			}
		} else {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
		}
		userFound.setPassword(null);
		userFound.setRole(null);
		return userFound;
	}

	/**
	 * @param email The email
	 * @throws EpickurException If an epickur exception occurred
	 */
	public void resetPasswordFirstStep(final String email) throws EpickurException {
		User user = readWithEmail(email);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
		}
		String resetCode = Security.createResetCode(user.getId(), email);
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
		User user = userDAO.read(id);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, id);
		}
		String resetCodeDB = Security.createResetCode(user.getId(), user.getEmail());
		if (!resetCodeDB.equals(resetCode)) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, id);
		} else {
			String newEncryptedPassword = PasswordManager.createPasswordManager(newPassword).createDBPassword();
			user.setPassword(newEncryptedPassword);
			user.prepareForUpdateIntoDB();
			User res = userDAO.update(user);
			res.setPassword(null);
			res.setRole(null);
			return res;
		}

	}

	/**
	 * Suscribe a user to newsletter
	 *
	 * @param user The user.
	 */
	public void suscribeToNewsletter(final User user) {
		String url = buildNewsletterUrl(user);
		suscribeUserToNewsletter(url, user.getEmail());
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

	protected void suscribeUserToNewsletter(final String url, final String email) {
		try {
			HttpPost request = new HttpPost(url);
			HttpClientBuilder.create().build().execute(request);
		} catch (IOException ioe) {
			LOG.error("Could not suscribe " + email + " to our newsletter", ioe);
		}
	}
}
