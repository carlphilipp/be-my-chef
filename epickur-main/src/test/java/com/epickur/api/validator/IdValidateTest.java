package com.epickur.api.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.TestUtils;

public class IdValidateTest {
	
	@Mock
	private ConstraintValidatorContext contextMock;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testIsValidTrue(){
		String id = new ObjectId().toHexString();
		IdValidate.IdValidator validator = new IdValidate.IdValidator();
		boolean actual = validator.isValid(id, contextMock);
		assertTrue(actual);
	}
	
	@Test
	public void testIsValidFalse(){
		String id = TestUtils.generateRandomString();
		IdValidate.IdValidator validator = new IdValidate.IdValidator();
		boolean actual = validator.isValid(id, contextMock);
		assertFalse(actual);
	}
}
