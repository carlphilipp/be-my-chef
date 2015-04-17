package com.epickur.api.enumeration;

/**
 * @author cph
 * @version 1.0
 *
 */
public enum Role {
	/** The enum **/
	ADMIN, USER, SUPER_USER;

	/**
	 * Get enum from str
	 * 
	 * @param value
	 *            The str value.
	 * @return The User role.
	 */
	public static Role getEnum(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (Role role : values()) {
			if (value.equalsIgnoreCase(role.toString().toLowerCase())) {
				return role;
			}
		}
		throw new IllegalArgumentException();
	}
}
