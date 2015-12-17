package com.epickur.api.validator.annotation;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneNumberValidate.PhoneNumberValidator.class)
@Documented
public @interface PhoneNumberValidate {

	String message() default "{user.phoneNumber}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Component
	class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidate, PhoneNumber> {
		@Override
		public void initialize(PhoneNumberValidate constraintAnnotation) {
		}

		@Override
		public boolean isValid(final PhoneNumber phoneNumber, final ConstraintValidatorContext constraintContext) {
			if (phoneNumber == null) {
				return true;
			}
			boolean isValid = true;
			PhoneNumberUtil util = PhoneNumberUtil.getInstance();
			if (!util.isValidNumber(phoneNumber)) {
				constraintContext.buildConstraintViolationWithTemplate("{user.phoneNumber}").addConstraintViolation();
				isValid = false;
			}
			if(!isValid){
				constraintContext.disableDefaultConstraintViolation();
			}
			return isValid;
		}
	}
}
