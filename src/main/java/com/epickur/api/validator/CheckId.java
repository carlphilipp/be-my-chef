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

import org.bson.types.ObjectId;

import com.epickur.api.validator.CheckId.CheckIdValidator;

@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckIdValidator.class)
@Documented
public @interface CheckId {
	String message() default "{objectid.notvalid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class CheckIdValidator implements ConstraintValidator<CheckId, String> {

		@Override
		public void initialize(final CheckId constraintAnnotation) {
		}

		@Override
		public boolean isValid(final String objectId, final ConstraintValidatorContext constraintContext) {
			return ObjectId.isValid(objectId);
		}
	}
}
