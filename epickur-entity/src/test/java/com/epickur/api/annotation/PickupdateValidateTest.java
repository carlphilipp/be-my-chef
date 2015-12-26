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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PickupdateValidateTest {

	private Validator validator;

	@Before
	public void init() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		this.validator = vf.getValidator();
	}

	@Test
	public void testValidateNewPasswordSuccessGoodTime() {
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
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateNewPasswordFailWrongPickuptDateFormat() {
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setPickupdate("mon-10:30eeee");
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);
		assertFalse(violations.isEmpty());
	}

	@Test
	public void testValidateNewPasswordFailWrongTime() {
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
		Set<ConstraintViolation<Order>> violations = validator.validate(order, Update.class);
		assertFalse(violations.isEmpty());
	}
}
