package com.epickur.api.validator;

import com.epickur.api.entity.User;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotNull(message = "{user.null}")
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = UserValidate.UserCreateValidator.class)
@Documented
public @interface UserValidate {

	String message() default "{user.default}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Component class UserCreateValidator implements ConstraintValidator<UserValidate, User> {

		private static final Logger LOG = LogManager.getLogger(UserValidator.class.getSimpleName());

		private static final String ENTITY = "user";

		@Override
		public void initialize(final UserValidate constraintAnnotation) {
		}

		@Override
		public boolean isValid(final User user, final ConstraintValidatorContext constraintContext) {
			if (user == null) {
				return true;
			}
			boolean isValid = true;
			if (StringUtils.isBlank(user.getName())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "name")).addConstraintViolation();
				isValid = false;
			}
			if (StringUtils.isBlank(user.getPassword())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "password")).addConstraintViolation();
				isValid = false;
			}
			if (StringUtils.isBlank(user.getEmail())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "email")).addConstraintViolation();
				isValid = false;
			}
			if (StringUtils.isBlank(user.getCountry())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "country")).addConstraintViolation();
				isValid = false;
			}
			if (StringUtils.isBlank(user.getZipcode())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "zipcode")).addConstraintViolation();
				isValid = false;
			}
			if (StringUtils.isBlank(user.getState())) {
				constraintContext.buildConstraintViolationWithTemplate(Validator.fieldNull(ENTITY, "state")).addConstraintViolation();
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
