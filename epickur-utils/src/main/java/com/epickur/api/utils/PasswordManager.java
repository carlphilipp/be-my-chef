package com.epickur.api.utils;

import com.epickur.api.exception.EpickurException;

public final class PasswordManager {

	private final String password;
	private final String saltHashed;
	private final String cryptedPasswordSalt;

	PasswordManager(final String password) throws EpickurException {
		this.password = password;
		this.saltHashed = Security.generateSalt();
		String passwordHashed = Security.encodeToSha256(this.password);
		this.cryptedPasswordSalt = Security.encodeToSha256(passwordHashed + saltHashed);
	}

	public static PasswordManager createPasswordManager(final String password) throws EpickurException {
		return new PasswordManager(password);
	}

	/**
	 * Create a db password.
	 * 
	 * @return The encrypted password
	 * @throws EpickurException
	 *             If something went bad
	 */
	public String createDBPassword() throws EpickurException {
		return this.saltHashed + this.cryptedPasswordSalt;
	}

	public String getCode(final String name, final String email) throws EpickurException {
		return Security.createUserCode(name, this.saltHashed, this.cryptedPasswordSalt, email);
	}
}
