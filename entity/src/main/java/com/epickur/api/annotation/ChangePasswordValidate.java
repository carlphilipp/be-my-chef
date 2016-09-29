package com.epickur.api.annotation;

import com.epickur.api.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ChangePasswordValidate.ChangePasswordConstraint.class)
@Documented
public @interface ChangePasswordValidate {

	String message() default "Default password validation error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Component
	class ChangePasswordConstraint implements ConstraintValidator<ChangePasswordValidate, User> {

		@Override public void initialize(ChangePasswordValidate constraintAnnotation) {
		}

		@Override public boolean isValid(final User user, final ConstraintValidatorContext constraintContext) {
			boolean isValid = true;
			if (StringUtils.isNotBlank(user.getNewPassword()) && StringUtils.isBlank(user.getPassword())) {
				constraintContext.buildConstraintViolationWithTemplate("The field user.password is mandatory when a new password is provided")
						.addConstraintViolation();
				isValid = false;
			}
			if(!isValid){
				constraintContext.disableDefaultConstraintViolation();
			}
			return isValid;
		}
	}
}
