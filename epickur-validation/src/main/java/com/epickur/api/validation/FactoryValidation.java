package com.epickur.api.validation;

/**
 * @author cph
 * @version 1.0
 */
public final class FactoryValidation {

	/** User validation */
	private static UserValidation userValidation;
	/** Caterer validation */
	private static CatererValidation catererValidation;
	/** Dish validation */
	private static DishValidation dishValidation;
	/** Voucher validation */
	private static VoucherValidation voucherValidation;

	/** Constructor */
	private FactoryValidation() {
	}

	/**
	 * Get the validation
	 *
	 * @param name
	 *            The name of the validation
	 * @return A ServiceValidator
	 */
	public static Validation getValidation(final String name) {
		switch (name) {
			case "user":
				if (userValidation == null) {
					userValidation = new UserValidation();
				}
				return userValidation;
			case "caterer":
				if (catererValidation == null) {
					catererValidation = new CatererValidation();
				}
				return catererValidation;
			case "dish":
				if (dishValidation == null) {
					dishValidation = new DishValidation();
				}
				return dishValidation;
			case "voucher":
				if (voucherValidation == null) {
					voucherValidation = new VoucherValidation();
				}
				return voucherValidation;
		}
		return null;
	}
}
