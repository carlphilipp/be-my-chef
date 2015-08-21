package com.epickur.api.validator;

/**
 * @author cph
 * @version 1.0
 */
public final class FactoryValidator {

	/** User validator */
	private static UserValidator userValidator = null;
	/** Caterer validator */
	private static CatererValidator catererValidator = null;
	/** Dish validator */
	private static DishValidator dishValidator = null;
	/** Voucher validator */
	private static VoucherValidator voucherValidator = null;

	/** Constructor */
	private FactoryValidator() {
	}

	/**
	 * Get the validator
	 * 
	 * @param name
	 *            The name of the validator
	 * @return A ServiceValidator
	 */
	public static Validator getValidator(final String name) {
		if (name.equals("user")) {
			if (userValidator == null) {
				userValidator = new UserValidator();
			}
			return userValidator;
		} else if (name.equals("caterer")) {
			if (catererValidator == null) {
				catererValidator = new CatererValidator();
			}
			return catererValidator;
		} else if (name.equals("dish")) {
			if (dishValidator == null) {
				dishValidator = new DishValidator();
			}
			return dishValidator;
		} else if (name.equals("voucher")) {
			if (voucherValidator == null) {
				voucherValidator = new VoucherValidator();
			}
			return voucherValidator;
		}
		return null;
	}
}
