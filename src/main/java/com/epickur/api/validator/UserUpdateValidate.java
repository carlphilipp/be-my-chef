package com.epickur.api.validator;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

import com.epickur.api.entity.User;
import com.epickur.api.validator.UserUpdateValidate.UserUpdateValidator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

@NotNull(message = "{user.create.null}")
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = UserUpdateValidator.class)
@Documented
public @interface UserUpdateValidate {

	String message() default "{user.default}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class UserUpdateValidator implements ConstraintValidator<UserUpdateValidate, User> {

		private static final String ENTITY = "user";

		@Override
		public void initialize(final UserUpdateValidate constraintAnnotation) {
		}

		@Override
		public boolean isValid(final User user, final ConstraintValidatorContext constraintContext) {
			if (user == null) {
				return false;
			}
			boolean isValid = true;
			if (user.getId() == null) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "id")).addConstraintViolation();
				isValid = false;
			}
			if (user.getPhoneNumber() != null) {
				PhoneNumberUtil util = PhoneNumberUtil.getInstance();
				if (!util.isValidNumber(user.getPhoneNumber())) {
					constraintContext.buildConstraintViolationWithTemplate("{user.phoneNumber}").addConstraintViolation();
					isValid = false;
				}
			}
			if (!isValid) {
				constraintContext.disableDefaultConstraintViolation();
			}
			return isValid;
		}
	}
}
