package com.epickur.api.utils.security;

import com.epickur.api.entity.User;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.bson.types.ObjectId;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @author cph
 * @version 1.0
 */
public final class Security {

	private Security() {
	}

	/**
	 * Encode to sha256 the user password
	 *
	 * @param str the password to encode
	 * @return an encoded string
	 */
	@SneakyThrows
	public static String encodeToSha256(final String str) {
		final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		final Charset charset = Charset.forName("UTF8");
		final byte[] hash = digest.digest(str.getBytes(charset));
		return Hex.encodeHexString(hash);
	}

	/**
	 * Encode to md5 the user password
	 *
	 * @param str the password to encode
	 * @return The string encoded
	 */
	@SneakyThrows
	public static String encodeToMd5(final String str) {
		final MessageDigest digest = MessageDigest.getInstance("MD5");
		final Charset charset = Charset.forName("UTF8");
		final byte[] hash = digest.digest(str.getBytes(charset));
		return Hex.encodeHexString(hash);
	}

	/**
	 * Generate a salt, a random key, en encrypt it
	 *
	 * @return a key encrypted
	 */
	public static String generateSalt() {
		final SecureRandom random = new SecureRandom();
		return Security.encodeToSha256(random.toString());
	}

	/**
	 * Generate random md5
	 *
	 * @return A md5 String
	 */
	public static String generateRandomMd5() {
		final SecureRandom random = new SecureRandom();
		return Security.encodeToMd5(random.toString());
	}

	/**
	 * Get Hashed Key
	 *
	 * @param user The User
	 * @return A hashed password
	 */
	public static String getUserCode(final User user) {
		final int sixtyFour = 64;
		final String email = user.getEmail();
		final String saltHashed = user.getPassword().substring(0, sixtyFour);
		final String cryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
		return Security.createUserCode(user.getName(), saltHashed, cryptedPasswordSalt, email);
	}

	/**
	 * Get code from input parameters
	 *
	 * @param name                  The name of the User
	 * @param saltHashed            The SaltHashed string
	 * @param encryptedPasswordSalt The encrypted password salt
	 * @param email                 The Email
	 * @return A Code
	 */
	public static String createUserCode(final String name, final String saltHashed, final String encryptedPasswordSalt, final String email) {
		return Security.encodeToSha256(name + saltHashed + encryptedPasswordSalt + email);
	}

	/**
	 * @param orderId   The order id
	 * @param cardToken The card token
	 * @return A order code
	 */
	public static String createOrderCode(final ObjectId orderId, final String cardToken) {
		return Security.encodeToSha256(orderId.toHexString() + cardToken);
	}

	/**
	 * @param orderId The order Id
	 * @param email   The user email
	 * @return A reset code
	 */
	public static String createResetCode(final ObjectId orderId, final String email) {
		return Security.encodeToSha256(orderId.toHexString() + email);
	}
}
