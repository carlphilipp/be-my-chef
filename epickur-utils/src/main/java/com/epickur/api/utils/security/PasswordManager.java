package com.epickur.api.utils.security;

import com.epickur.api.exception.EpickurException;

public final class PasswordManager {

	private final String saltHashed;
	private final String cryptedPasswordSalt;

	PasswordManager(final String password) throws EpickurException {
		final String passwordHashed = Security.encodeToSha256(password);
		saltHashed = Security.generateSalt();
		cryptedPasswordSalt = Security.encodeToSha256(passwordHashed + saltHashed);
	}

	public static PasswordManager createPasswordManager(final String password) throws EpickurException {
		return new PasswordManager(password);
	}

	/**
	 * Create a db password.
	 *
	 * @return The encrypted password
	 * @throws EpickurException If something went bad
	 */
	public String createDBPassword() {
		return saltHashed + cryptedPasswordSalt;
	}

	public String getCode(final String name, final String email) throws EpickurException {
		return Security.createUserCode(name, saltHashed, cryptedPasswordSalt, email);
	}
}
