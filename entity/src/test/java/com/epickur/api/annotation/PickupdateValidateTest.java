package com.epickur.api.annotation;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.operation.Update;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PickupdateValidateTest {

	private Validator validator;

	@Before
	public void init() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		this.validator = vf.getValidator();
	}

	@Test
	public void testValidateNewPasswordSuccessGoodTime() {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		WorkingTimes workingTimes = order.getDish().getCaterer().getWorkingTimes();
		Hours hours = new Hours();
		List<TimeFrame> hoursMonday = new ArrayList<>();
		TimeFrame frame = new TimeFrame();
		hoursMonday.add(frame);
		frame.setOpen(400);
		frame.setClose(1500);
		hours.setMon(hoursMonday);
		workingTimes.setHours(hours);
		order.setPickupdate("mon-10:30");

		// When
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);

		// Then
		assertThat(violations, is(empty()));
	}

	@Test
	public void testValidateNewPasswordFailWrongPickuptDateFormat() {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setPickupdate("mon-10:30eeee");

		// When
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);

		// Then
		assertThat(violations, is(not(empty())));
	}

	@Test
	public void testValidateNewPasswordFailWrongTime() {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		WorkingTimes workingTimes = order.getDish().getCaterer().getWorkingTimes();
		Hours hours = new Hours();
		List<TimeFrame> hoursMonday = new ArrayList<>();
		TimeFrame frame = new TimeFrame();
		hoursMonday.add(frame);
		frame.setOpen(400);
		frame.setClose(600);
		hours.setMon(hoursMonday);
		workingTimes.setHours(hours);
		order.setPickupdate("mon-10:30");

		// When
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);

		// Then
		assertThat(violations, is(not(empty())));
	}
}
