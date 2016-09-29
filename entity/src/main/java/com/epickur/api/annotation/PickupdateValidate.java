package com.epickur.api.annotation;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.times.WorkingTimes;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PickupdateValidate.PickupdateValidator.class)
@Documented
public @interface PickupdateValidate {

	String message() default "{pickupdate.default}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Component
	class PickupdateValidator implements ConstraintValidator<PickupdateValidate, Order> {

		@Override
		public void initialize(final PickupdateValidate constraintAnnotation) {
		}

		@Override
		public boolean isValid(final Order order, final ConstraintValidatorContext constraintContext) {
			if (order == null) {
				return true;
			}
			boolean isValid = true;
			if (order.getPickupdate() != null) {
				final Optional<Object[]> result = CommonsUtil.parsePickupdate(order.getPickupdate());
				if (!result.isPresent()) {
					constraintContext.buildConstraintViolationWithTemplate(
							"The field order.pickupdate has a wrong format. Should be: ddd-hh:mm, with ddd: mon|tue|wed|thu|fri|sat|sun. Found: "
									+ order.getPickupdate()).addConstraintViolation();
					isValid = false;
				} else {
					final Object[] pickupdate = result.get();
					final Caterer caterer = order.getDish().getCaterer();
					final WorkingTimes workingTimes = caterer.getWorkingTimes();
					if (!workingTimes.canBePickup((String) pickupdate[0], (Integer) pickupdate[1])) {
						constraintContext.buildConstraintViolationWithTemplate("The order has a wrong pickupdate").addConstraintViolation();
						isValid = false;
					}
				}
			}
			if(!isValid){
				constraintContext.disableDefaultConstraintViolation();
			}
			return isValid;
		}
	}
}
