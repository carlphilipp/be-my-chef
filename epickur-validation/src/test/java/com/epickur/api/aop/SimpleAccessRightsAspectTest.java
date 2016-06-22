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
import static org.mockito.Mockito.*;

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
		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		args[1] = order.getId().toHexString();
		args[2] = order;

		accessRightsAspect.handleOrder(UPDATE, args);
	}

	@Test
	public void testHandleOrderUpdateFail() {
		thrown.expect(EpickurIllegalArgument.class);

		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		args[1] = EntityGenerator.generateRandomString();
		args[2] = order;

		accessRightsAspect.handleOrder(UPDATE, args);
	}

	@Test
	public void testHandleUserUpdate() {
		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = user.getId().toHexString();
		args[1] = user;

		accessRightsAspect.handleUser(UPDATE, args);
	}

	@Test
	public void testHandleUserUpdateFail() {
		thrown.expect(EpickurIllegalArgument.class);

		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = EntityGenerator.generateRandomString();
		args[1] = user;

		accessRightsAspect.handleUser(UPDATE, args);
	}

	@Test
	public void testHandleUserResetPassword() {
		Object[] args = new Object[1];
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		args[0] = node;

		accessRightsAspect.handleUser(RESET_PASSWORD, args);

		verify(userValidator).checkResetPasswordData(node);
	}

	@Test
	public void testHandleCatererCreate() throws EpickurException {
		Object[] args = new Object[1];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer;

		accessRightsAspect.handleCaterer(CREATE, args);

		verify(catererValidator).checkCreateCaterer(caterer);
	}

	@Test
	public void testHandleCatererUpdate() throws EpickurException {
		Object[] args = new Object[2];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer.getId().toHexString();
		args[1] = caterer;

		accessRightsAspect.handleCaterer(UPDATE, args);

		verify(catererValidator).checkUpdateCaterer(caterer.getId().toHexString(), caterer);
	}

	@Test
	public void testHandleCatererPaymentInfo() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.of(caterer));

		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		accessRightsAspect.handleCaterer(PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleCatererPaymentInfoFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		when(catererDAO.read(isA(String.class))).thenReturn(Optional.empty());

		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		accessRightsAspect.handleCaterer(PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleVoucherRead() throws EpickurParsingException {
		Object[] args = new Object[1];
		args[0] = EntityGenerator.generateRandomString();
		accessRightsAspect.handleVoucher(READ, args);

		verify(voucherValidator).checkVoucherCode((String) args[0]);
	}

	@Test
	public void testHandleVoucherGenerateVoucher() throws EpickurParsingException {
		Object[] args = new Object[6];
		args[3] = ExpirationType.ONETIME;
		args[4] = "expiration";
		args[5] = "format";
		accessRightsAspect.handleVoucher(GENERATE_VOUCHER, args);

		verify(voucherValidator).checkVoucherGenerate((ExpirationType) args[3], (String) args[4], (String) args[5]);
	}

	@Test
	public void testHandleDishCreate() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();

		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.of(caterer));

		Object[] args = new Object[1];
		args[0] = dish;

		accessRightsAspect.handleDish(CREATE, args, key);
	}

	@Test
	public void testHandleDishCreateCatererNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();

		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.empty());

		Object[] args = new Object[1];
		args[0] = dish;

		accessRightsAspect.handleDish(CREATE, args, key);
	}

	@Test
	public void testHandleDishUpdate() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();

		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.of(caterer));

		Object[] args = new Object[2];
		args[0] = dish.getId().toHexString();
		args[1] = dish;

		accessRightsAspect.handleDish(UPDATE, args, key);
	}

	@Test
	public void testHandleDishSearch() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();

		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.of(caterer));

		Object[] args = new Object[5];
		args[0] = EntityGenerator.generateRandomString();
		args[1] = EntityGenerator.generateRandomString();
		args[3] = EntityGenerator.generateRandomString();
		args[4] = EntityGenerator.generateRandomString();

		accessRightsAspect.handleDish(SEARCH_DISH, args, key);
	}

	@Test
	public void testValidateLogicAccessRights() throws EpickurException {
		doNothing().when(accessRightsAspect).handleDish(any(), any(), any());
		doNothing().when(accessRightsAspect).handleVoucher(any(), any());
		doNothing().when(accessRightsAspect).handleCaterer(any(), any());
		doNothing().when(accessRightsAspect).handleUser(any(), any());
		doNothing().when(accessRightsAspect).handleOrder(any(), any());

		ObjectNode[] node = new ObjectNode[3];
		node[2] = JsonNodeFactory.instance.objectNode();

		when(joinPoint.getArgs()).thenReturn(node);

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
		MethodSignature signature = mock(MethodSignature.class);
		Method method = getClass().getMethod("testGetMethodFromJointPoint");
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		Method actual = accessRightsAspect.getMethodFromJointPoint(joinPoint);

		assertNotNull(actual);
		assertEquals(method, actual);
	}

	@Test
	public void testCheckAccessRights() throws Throwable {
		MethodSignature signature = mock(MethodSignature.class);
		Method method = SimpleAccessRightsAspectTest.class.getMethod("exampleCreateCaterer", Caterer.class);
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(request.getAttribute("key")).thenReturn(key);
		when(key.getRole()).thenReturn(Role.ADMIN);
		doNothing().when(accessRightsAspect).validateMatrixAccessRights(any(), any(), any());
		doNothing().when(accessRightsAspect).validateLogicAccessRights(any(), any(), any(), any());

		accessRightsAspect.checkAccessRights(joinPoint);

		verify(accessRightsAspect).validateMatrixAccessRights(any(), any(), any());
		verify(accessRightsAspect).validateLogicAccessRights(any(), any(), any(), any());
		verify(accessRightsAspect).getMethodFromJointPoint(joinPoint);
	}

	@ValidateSimpleAccessRights(operation = CREATE, endpoint = CATERER)
	public void exampleCreateCaterer(final Caterer caterer) {
	}
}
