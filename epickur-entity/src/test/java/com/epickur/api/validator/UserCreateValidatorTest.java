package com.epickur.api.validator;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.User;
import com.epickur.api.helper.EntityGenerator;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class UserCreateValidatorTest {
	
	@Mock
	private ConstraintValidatorContext contextMock;
	@Mock
	private ConstraintViolationBuilder builderMock;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(contextMock.buildConstraintViolationWithTemplate(anyString())).thenReturn(builderMock);
	}
	
	@Test
	public void testIsValid(){
		User user = EntityGenerator.generateRandomUser();
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertTrue(actual);
		verify(contextMock, never()).disableDefaultConstraintViolation();
		verify(contextMock, never()).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidUserNull(){
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(null, contextMock);
		
		assertTrue(actual);
		verify(contextMock, never()).disableDefaultConstraintViolation();
		verify(contextMock, never()).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidNameNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setName(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidPasswordNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setPassword(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidEmailNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setEmail(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidCountryNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setCountry(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidZipCodeNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setZipcode(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidStateNull(){
		User user = EntityGenerator.generateRandomUser();
		user.setState(null);
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidPhoneNumberFail(){
		User user = EntityGenerator.generateRandomUser();
		user.setPhoneNumber(new PhoneNumber());
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidAllFail(){
		User user = EntityGenerator.generateRandomUser();
		user.setName(null);
		user.setZipcode(null);
		user.setPassword(null);
		user.setEmail(null);
		user.setCountry(null);
		user.setState(null);
		user.setPhoneNumber(new PhoneNumber());
		UserValidate.UserValidator validator = new UserValidate.UserValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(7)).buildConstraintViolationWithTemplate(anyString());
	}
}
