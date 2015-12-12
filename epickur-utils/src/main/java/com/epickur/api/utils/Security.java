package com.epickur.api.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;
import org.bson.types.ObjectId;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;

/**
 * @author cph
 * @version 1.0
 */
public final class Security {
	/** Logger **/
	// private static final Logger LOG = LogManager.getLogger(Security.class.getSimpleName());

	/*
	 * public static void main(final String[] args) throws EpickurException { try { String password = encodeToMd5("passwordAPI"); // Run this class to
	 * generate a key in src/resources/api.key File file = new File("C:/Users/Carl-Philipp/git/epickur-api-java/epickur/src/main/resources/api.key");
	 * BufferedWriter output = new BufferedWriter(new FileWriter(file)); output.write(password); output.close(); file = new
	 * File("C:/Users/Carl-Philipp/git/epickur-api-java/epickur/src/test/resources/api.key"); output = new BufferedWriter(new FileWriter(file));
	 * output.write(password); output.close(); } catch (IOException e) { e.printStackTrace(); } }
	 */

	/**
	 * Constructor
	 */
	private Security() {
	}

	/**
	 * Encode to sha256 the user password
	 * 
	 * @param str
	 *            the password to encode
	 * @return an encoded string
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String encodeToSha256(final String str) throws EpickurException {
		String encoded;
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final Charset charset = Charset.forName("UTF8");
			final byte[] hash = digest.digest(str.getBytes(charset));
			encoded = Hex.encodeHexString(hash);
		} catch (final NoSuchAlgorithmException e) {
			throw new EpickurException("Error while encoding string: " + e.getMessage(), e);
		}
		return encoded;
	}

	/**
	 * Encode to md5 the user password
	 * 
	 * @param str
	 *            the password to encode
	 * @return The string encoded
	 * @throws EpickurException
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String encodeToMd5(final String str) throws EpickurException {
		String encoded;
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			final Charset charset = Charset.forName("UTF8");
			final byte[] hash = digest.digest(str.getBytes(charset));
			encoded = Hex.encodeHexString(hash);
		} catch (final NoSuchAlgorithmException e) {
			throw new EpickurException("Error while encoding string: " + e.getMessage(), e);
		}
		return encoded;
	}

	/**
	 * Generate a salt, a random key, en encrypt it
	 * 
	 * @return a key encrypted
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String generateSalt() throws EpickurException {
		final SecureRandom random = new SecureRandom();
		return Security.encodeToSha256(random.toString());
	}

	/**
	 * Generate random md5
	 * 
	 * @return A md5 String
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String generateRandomMd5() throws EpickurException {
		final SecureRandom random = new SecureRandom();
		return Security.encodeToMd5(random.toString());
	}

	/**
	 * Get Hashed Key
	 * 
	 * @param user
	 *            The User
	 * @return A hashed password
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String getUserCode(final User user) throws EpickurException {
		final int sixtyFour = 64;
		final String email = user.getEmail();
		final String saltHashed = user.getPassword().substring(0, sixtyFour);
		final String cryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
		final String checkFound = Security.createUserCode(user.getName(), saltHashed, cryptedPasswordSalt, email);
		return checkFound;
	}

	/**
	 * Get code from input parameters
	 * 
	 * @param name
	 *            The name of the User
	 * @param saltHashed
	 *            The SaltHashed string
	 * @param encryptedPasswordSalt
	 *            The encrypted password salt
	 * @param email
	 *            The Email
	 * @return A Code
	 * @throws EpickurException
	 *             If an exception occurred while encoding the password
	 */
	public static String createUserCode(final String name, final String saltHashed, final String encryptedPasswordSalt, final String email)
			throws EpickurException {
		return Security.encodeToSha256(name + saltHashed + encryptedPasswordSalt + email);
	}

	/**
	 * @param orderId
	 *            The order id
	 * @param cardToken
	 *            The card token
	 * @return A order code
	 * @throws EpickurException
	 *             If an exception occurred
	 */
	public static String createOrderCode(final ObjectId orderId, final String cardToken) throws EpickurException {
		return Security.encodeToSha256(orderId.toHexString() + cardToken);
	}

	/**
	 * @param orderId
	 *            The order Id
	 * @param email
	 *            The user email
	 * @return A reset code
	 * @throws EpickurException
	 *             If an exception occurred
	 */
	public static String createResetCode(final ObjectId orderId, final String email) throws EpickurException {
		return Security.encodeToSha256(orderId.toHexString() + email);
	}
}
