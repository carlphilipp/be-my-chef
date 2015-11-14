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

import com.epickur.api.TestUtils;
import com.epickur.api.entity.User;
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
		User user = TestUtils.generateRandomUser();
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertTrue(actual);
		verify(contextMock, never()).disableDefaultConstraintViolation();
		verify(contextMock, never()).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidUserNull(){
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(null, contextMock);
		
		assertFalse(actual);
		verify(contextMock, never()).disableDefaultConstraintViolation();
		verify(contextMock, never()).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidNameNull(){
		User user = TestUtils.generateRandomUser();
		user.setName(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidPasswordNull(){
		User user = TestUtils.generateRandomUser();
		user.setPassword(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidEmailNull(){
		User user = TestUtils.generateRandomUser();
		user.setEmail(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidCountryNull(){
		User user = TestUtils.generateRandomUser();
		user.setCountry(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidZipCodeNull(){
		User user = TestUtils.generateRandomUser();
		user.setZipcode(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidStateNull(){
		User user = TestUtils.generateRandomUser();
		user.setState(null);
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidPhoneNumberFail(){
		User user = TestUtils.generateRandomUser();
		user.setPhoneNumber(new PhoneNumber());
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(1)).buildConstraintViolationWithTemplate(anyString());
	}
	
	@Test
	public void testIsValidAllFail(){
		User user = TestUtils.generateRandomUser();
		user.setName(null);
		user.setZipcode(null);
		user.setPassword(null);
		user.setEmail(null);
		user.setCountry(null);
		user.setState(null);
		user.setPhoneNumber(new PhoneNumber());
		UserCreateValidate.UserCreateValidator validator = new UserCreateValidate.UserCreateValidator();
		
		boolean actual = validator.isValid(user, contextMock);
		
		assertFalse(actual);
		verify(contextMock, times(1)).disableDefaultConstraintViolation();
		verify(contextMock, times(7)).buildConstraintViolationWithTemplate(anyString());
	}
}
