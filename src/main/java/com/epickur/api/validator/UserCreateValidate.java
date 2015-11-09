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

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.entity.User;
import com.epickur.api.validator.UserCreateValidate.UserCreateValidator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

@NotNull(message = "{user.create.null}")
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = UserCreateValidator.class)
@Documented
public @interface UserCreateValidate {

	String message() default "{user.create.default}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class UserCreateValidator implements ConstraintValidator<UserCreateValidate, User> {
		
		private static final String ENTITY = "user";

		@Override
		public void initialize(final UserCreateValidate constraintAnnotation) {
		}

		@Override
		public boolean isValid(final User user, final ConstraintValidatorContext constraintContext) {
			if (user == null) {
				return false;
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
					constraintContext.buildConstraintViolationWithTemplate("{user.create.phoneNumber}").addConstraintViolation();
					isValid = false;
				}
			}
			if(!isValid){
				constraintContext.disableDefaultConstraintViolation();
			}
			return isValid;
		}
	}
}
