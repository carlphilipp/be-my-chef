package com.epickur.api.business;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.epickur.api.dao.mongo.UserDaoImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurDuplicateKeyException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Info;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import com.epickur.api.utils.email.EmailType;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;

/**
 * User business layer. Access User DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public final class UserBusiness {

	/** User dao **/
	private UserDaoImpl userDao;
	/** Key Business **/
	private KeyBusiness keyBusiness;
	/** User validator **/
	private UserValidator validator;

	/**
	 * The constructor
	 */
	public UserBusiness() {
		this.userDao = new UserDaoImpl();
		this.keyBusiness = new KeyBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	/**
	 * Create a User
	 * 
	 * @param user
	 *            The User
	 * @param sendEmail
	 *            True if you want to trigger an email
	 * @param autoValidate
	 *            True if you want to auto validate the User
	 * @return The User created
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User create(final User user, final boolean sendEmail, final boolean autoValidate) throws EpickurException {
		if (userDao.exists(user.getName(), user.getEmail())) {
			throw new EpickurDuplicateKeyException("The user already exists");
		} 
		if (autoValidate) {
			user.setAllow(1);
		} else {
			user.setAllow(0);
		}
		String name = user.getName();
		String email = user.getEmail();
		String saltHashed = null;
		String encryptedPasswordSalt = null;
		String code = null;
		String passwordHashed = Security.encodeToSha256(user.getPassword());
		saltHashed = Security.generateSalt();
		encryptedPasswordSalt = Security.encodeToSha256(passwordHashed + saltHashed);
		code = Security.createCode(name, saltHashed, encryptedPasswordSalt, email);
		user.setPassword(saltHashed + encryptedPasswordSalt);
		user.setRole(Role.USER);
		User res = userDao.create(user);

		if (sendEmail) {
			// Convert data to use email template
			Map<String, String> emailData = EmailTemplate.convertToDataRegistration(name, code);
			// Send an email to the user
			Email.sendMail(EmailType.REGISTRATION, emailData, new String[] { email });

			// Convert data to use email template
			Map<String, String> emailDataAdmin = EmailTemplate.convertToDataRegistrationAdmin(name, email);
			// Send an email to admins
			Email.sendMail(EmailType.REGISTRATION_ADMIN, emailDataAdmin, Info.admins.toArray(new String[Info.admins.size()]));
		}
		// We do not send back the password
		res.setPassword(null);
		res.setRole(null);
		res.setCode(code);
		return res;
	}

	/**
	 * Read a User
	 * 
	 * @param id
	 *            the User id to read
	 * @return The User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	/**
	 * Read a User
	 * 
	 * @param id
	 *            the User id to read
	 * @param key
	 *            The Key
	 * @return The User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User read(final String id, final Key key) throws EpickurException {
		User res = userDao.read(id);
		if (res != null) {
			validator.checkUserRightsAfter(key.getRole(), key.getUserId(), res, Crud.READ);
			res.setPassword(null);
			res.setRole(null);
		}
		return res;
	}

	/**
	 * Read a User with its email
	 * 
	 * @param email
	 *            The email of the User
	 * @return The User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User readWithEmail(final String email) throws EpickurException {
		return userDao.readWithEmail(email);
	}

	/**
	 * Read a User with its name
	 * 
	 * @param name
	 *            The name of the User
	 * @return The User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User readWithName(final String name) throws EpickurException {
		return userDao.readWithName(name);
	}

	/**
	 * Read a list of User
	 * 
	 * @return A list of User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public List<User> readAll() throws EpickurException {
		List<User> users = userDao.readAll();
		for (User user : users) {
			// We do not send back the password or the role
			user.setPassword(null);
			user.setRole(null);
		}
		return users;
	}

	/**
	 * @param user
	 *            The User to update
	 * @param key
	 *            The key
	 * @return The User updated
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User update(final User user, final Key key) throws EpickurException {
		User read = userDao.read(user.getId().toHexString());
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), read, Crud.UPDATE);
		User res = userDao.update(user);
		if (res != null) {
			// We do not send back the password or the role
			res.setPassword(null);
			res.setRole(null);
		}
		return res;
	}

	/**
	 * Delete a User
	 * 
	 * @param id
	 *            The id of the User to delete
	 * @return True if the User has been deleted
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		return userDao.delete(id);
	}

	/**
	 * Login
	 * 
	 * @param email
	 *            The email of the User
	 * @param password
	 *            The password of the User
	 * @return A User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User login(final String email, final String password) throws EpickurException {
		User found = this.readWithEmail(email);
		if (found != null) {
			if (!Utils.isPasswordCorrect(password, found)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
			} else if (found.getAllow() == 1) {
				String tempKey = Security.generateRandomMd5();
				found.setKey(tempKey);
				Key currentKey = this.keyBusiness.readWithName(found.getName());
				if (currentKey != null) {
					this.keyBusiness.delete(currentKey.getId().toHexString());
				}
				Key key = new Key();
				key.setCreatedAt(new DateTime());
				key.setUserId(found.getId());
				key.setKey(found.getKey());
				key.setRole(found.getRole());
				this.keyBusiness.create(key);
				found.setPassword(null);
				found.setRole(null);
			} else {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
			}
		} else {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, email);
		}
		return found;
	}

	/**
	 * Inject a new encoded password into the User
	 * 
	 * @param user
	 *            The User
	 * @return The User modified with its new encoded password
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User injectNewPassword(final User user) throws EpickurException {
		User found = this.readWithEmail(user.getEmail());
		if (found == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, user.getEmail());
		} else {
			if (!Utils.isPasswordCorrect(user.getPassword(), found)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, user.getEmail());
			} else {
				String newEnryptedPassword = Utils.getEncryptedPassword(user.getNewPassword());
				user.setPassword(newEnryptedPassword);
			}
		}
		return user;
	}

	/**
	 * Check if the code provided is correct and unlock the User
	 * 
	 * @param name
	 *            The name of the User
	 * @param code
	 *            The code provided by the API to the User
	 * @return An updated User
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public User checkCode(final String name, final String code) throws EpickurException {
		User dbUser = this.readWithName(name);
		if (dbUser != null) {
			String codeFound = Security.getUserCode(dbUser);
			if (!codeFound.equals(code)) {
				throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, name);
			} else {
				dbUser.setAllow(1);
				dbUser = userDao.update(dbUser);
			}
		} else {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, name);
		}
		dbUser.setPassword(null);
		dbUser.setRole(null);
		return dbUser;
	}
}
