package com.epickur.api.aop;

import com.epickur.api.annotation.ValidateSimpleAccessRights;
import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.Role;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.validation.CatererValidation;
import com.epickur.api.validation.DishValidation;
import com.epickur.api.validation.UserValidation;
import com.epickur.api.validation.VoucherValidation;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.epickur.api.enumeration.EndpointType.*;
import static com.epickur.api.enumeration.Operation.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SimpleAccessRightsAspectTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private HttpServletRequest request;
	@Mock
	private UserValidation userValidator;
	@Mock
	private DishValidation dishValidator;
	@Mock
	private CatererValidation catererValidator;
	@Mock
	private VoucherValidation voucherValidator;
	@Mock
	private CatererDAO catererDAO;
	@Mock
	private JoinPoint joinPoint;
	@Mock
	private Key key;
	@InjectMocks
	@Spy
	private SimpleAccessRightsAspect accessRightsAspect;

	@Test
	public void testHandleOrderUpdate() {
		// Given
		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		args[1] = order.getId().toHexString();
		args[2] = order;

		// When
		accessRightsAspect.handleOrder(UPDATE, args);
	}

	@Test
	public void testHandleOrderUpdateFail() {
		// Then
		thrown.expect(EpickurIllegalArgument.class);

		// Given
		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		args[1] = EntityGenerator.generateRandomString();
		args[2] = order;

		// When
		accessRightsAspect.handleOrder(UPDATE, args);
	}

	@Test
	public void testHandleUserUpdate() {
		// Given
		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = user.getId().toHexString();
		args[1] = user;

		// When
		accessRightsAspect.handleUser(UPDATE, args);
	}

	@Test
	public void testHandleUserUpdateFail() {
		// Then
		thrown.expect(EpickurIllegalArgument.class);

		// Given
		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = EntityGenerator.generateRandomString();
		args[1] = user;

		// When
		accessRightsAspect.handleUser(UPDATE, args);
	}

	@Test
	public void testHandleUserResetPassword() {
		// Given
		Object[] args = new Object[1];
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		args[0] = node;

		// When
		accessRightsAspect.handleUser(RESET_PASSWORD, args);

		// Then
		then(userValidator).should().checkResetPasswordData(node);
	}

	@Test
	public void testHandleCatererCreate() throws EpickurException {
		// Given
		Object[] args = new Object[1];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer;

		// When
		accessRightsAspect.handleCaterer(CREATE, args);

		// Then
		then(catererValidator).should().checkCreateCaterer(caterer);
	}

	@Test
	public void testHandleCatererUpdate() throws EpickurException {
		// Given
		Object[] args = new Object[2];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer.getId().toHexString();
		args[1] = caterer;

		// When
		accessRightsAspect.handleCaterer(UPDATE, args);

		// Then
		then(catererValidator).should().checkUpdateCaterer(caterer.getId().toHexString(), caterer);
	}

	@Test
	public void testHandleCatererPaymentInfo() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.of(caterer));
		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		// When
		accessRightsAspect.handleCaterer(PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleCatererPaymentInfoFail() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);

		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(catererDAO.read(anyString())).willReturn(Optional.empty());
		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		// When
		accessRightsAspect.handleCaterer(PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleVoucherRead() throws EpickurParsingException {
		// Given
		Object[] args = new Object[1];
		args[0] = EntityGenerator.generateRandomString();

		// When
		accessRightsAspect.handleVoucher(READ, args);

		// Then
		then(voucherValidator).should().checkVoucherCode((String) args[0]);
	}

	@Test
	public void testHandleVoucherGenerateVoucher() throws EpickurParsingException {
		// Given
		Object[] args = new Object[6];
		args[3] = ExpirationType.ONETIME;
		args[4] = "expiration";
		args[5] = "format";

		// When
		accessRightsAspect.handleVoucher(GENERATE_VOUCHER, args);

		// Then
		then(voucherValidator).should().checkVoucherGenerate((ExpirationType) args[3], (String) args[4], (String) args[5]);
	}

	@Test
	public void testHandleDishCreate() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.of(caterer));
		Object[] args = new Object[1];
		args[0] = dish;

		// When
		accessRightsAspect.handleDish(CREATE, args, key);
	}

	@Test
	public void testHandleDishCreateCatererNotFound() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);

		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.empty());
		Object[] args = new Object[1];
		args[0] = dish;

		// When
		accessRightsAspect.handleDish(CREATE, args, key);
	}

	@Test
	public void testHandleDishUpdate() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.of(caterer));
		Object[] args = new Object[2];
		args[0] = dish.getId().toHexString();
		args[1] = dish;

		// When
		accessRightsAspect.handleDish(UPDATE, args, key);
	}

	@Test
	public void testHandleDishSearch() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.of(caterer));
		Object[] args = new Object[5];
		args[0] = EntityGenerator.generateRandomString();
		args[1] = EntityGenerator.generateRandomString();
		args[3] = EntityGenerator.generateRandomString();
		args[4] = EntityGenerator.generateRandomString();

		// When
		accessRightsAspect.handleDish(SEARCH_DISH, args, key);
	}

	@Test
	public void testValidateLogicAccessRights() throws EpickurException {
		// Given
		willDoNothing().given(accessRightsAspect).handleDish(any(), any(), any());
		willDoNothing().given(accessRightsAspect).handleVoucher(any(), any());
		willDoNothing().given(accessRightsAspect).handleCaterer(any(), any());
		willDoNothing().given(accessRightsAspect).handleUser(any(), any());
		willDoNothing().given(accessRightsAspect).handleOrder(any(), any());
		ObjectNode[] node = new ObjectNode[3];
		node[2] = JsonNodeFactory.instance.objectNode();
		given(joinPoint.getArgs()).willReturn(node);

		// When
		accessRightsAspect.validateLogicAccessRights(joinPoint, CREATE, DISH, key);
		accessRightsAspect.validateLogicAccessRights(joinPoint, CREATE, VOUCHER, key);
		accessRightsAspect.validateLogicAccessRights(joinPoint, RESET_PASSWORD, NO_KEY, key);
		accessRightsAspect.validateLogicAccessRights(joinPoint, CREATE, CATERER, key);
		accessRightsAspect.validateLogicAccessRights(joinPoint, CREATE, USER, key);
		accessRightsAspect.validateLogicAccessRights(joinPoint, CREATE, ORDER, key);
	}

	@Test
	public void testValidateMatrixAccessRights() {
		accessRightsAspect.validateMatrixAccessRights(Role.ADMIN, CREATE, CATERER);
	}

	@Test
	public void testGetMethodFromJointPoint() throws NoSuchMethodException {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = getClass().getMethod("testGetMethodFromJointPoint");
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);

		// When
		Method actual = accessRightsAspect.getMethodFromJointPoint(joinPoint);

		// Then
		assertNotNull(actual);
		assertEquals(method, actual);
	}

	@Test
	public void testCheckAccessRights() throws Throwable {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = SimpleAccessRightsAspectTest.class.getMethod("exampleCreateCaterer", Caterer.class);
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);
		given(request.getAttribute("key")).willReturn(key);
		given(key.getRole()).willReturn(Role.ADMIN);
		willDoNothing().given(accessRightsAspect).validateMatrixAccessRights(any(), any(), any());
		willDoNothing().given(accessRightsAspect).validateLogicAccessRights(any(), any(), any(), any());

		// When
		accessRightsAspect.checkAccessRights(joinPoint);

		// Then
		then(accessRightsAspect).should().validateMatrixAccessRights(any(), any(), any());
		then(accessRightsAspect).should().validateLogicAccessRights(any(), any(), any(), any());
		then(accessRightsAspect).should().getMethodFromJointPoint(joinPoint);
	}

	@ValidateSimpleAccessRights(operation = CREATE, endpoint = CATERER)
	public void exampleCreateCaterer(final Caterer caterer) {
	}
}
