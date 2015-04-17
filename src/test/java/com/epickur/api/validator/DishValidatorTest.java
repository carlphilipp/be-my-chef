package com.epickur.api.validator;

import javax.ws.rs.ForbiddenException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Dish;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.validator.DishValidator;

public class DishValidatorTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testCheckCreateDish(){
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.name is not allowed to be null or empty");
		
		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setName(null);
		validator.checkCreateData(dish);
	}
	
	@Test
	public void testCheckCreateDish2(){
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.description is not allowed to be null or empty");
		
		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setDescription(null);
		validator.checkCreateData(dish);
	}
	
	@Test
	public void testCheckUpdateDish(){
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.id is not allowed to be null or empty");
		
		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(null);
		validator.checkUpdateData2("id", dish);
	}

	@Test
	public void testCheckRights() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.ADMIN, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights2() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights3() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.READ, null);
	}

	@Test
	public void testCheckRights4() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.UPDATE, null);
	}

	@Test
	public void testCheckRights5() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.DELETE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights6() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights7() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.READ, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights8() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.UPDATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights9() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.DELETE, null);
	}
}
