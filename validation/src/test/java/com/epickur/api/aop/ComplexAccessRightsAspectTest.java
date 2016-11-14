package com.epickur.api.aop;

import com.epickur.api.annotation.ValidateComplexAccessRights;
import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.validation.CatererValidation;
import com.epickur.api.validation.DishValidation;
import com.epickur.api.validation.UserValidation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.bson.types.ObjectId;
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

import static com.epickur.api.enumeration.EndpointType.CATERER;
import static com.epickur.api.enumeration.EndpointType.DISH;
import static com.epickur.api.enumeration.EndpointType.ORDER;
import static com.epickur.api.enumeration.EndpointType.USER;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.enumeration.Role.ADMIN;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ComplexAccessRightsAspectTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private HttpServletRequest request;
	@Mock
	private DishValidation dishValidator;
	@Mock
	private UserValidation userValidator;
	@Mock
	private CatererValidation catererValidator;
	@Mock
	private DishDAO dishDAO;
	@Mock
	private OrderDAO orderDAO;
	@Mock
	private CatererDAO catererDAO;
	@Mock
	private UserDAO userDAO;
	@Mock
	private Key key;
	@Mock
	private JoinPoint joinPoint;
	@InjectMocks
	@Spy
	private ComplexAccessRightsAspect accessRightsAspect;

	@Test
	public void testHandleOrderRead() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		given(orderDAO.read(order.getId().toHexString())).willReturn(Optional.of(order));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = order.getId().toHexString();

		// When
		accessRightsAspect.handleOrder(READ, args, key);

		// Then
		then(userValidator).should().checkOrderRightsAfter(ADMIN, userId, order, READ);
	}

	@Test
	public void testHandleOrderUpdate() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		given(orderDAO.read(order.getId().toHexString())).willReturn(Optional.of(order));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = order;

		// When
		accessRightsAspect.handleOrder(UPDATE, args, key);

		// Then
		then(userValidator).should().checkOrderRightsAfter(ADMIN, userId, order, UPDATE);
		then(userValidator).should().checkOrderStatus(order);
	}

	@Test
	public void testHandleOrderUpdateNotFound() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);

		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		given(orderDAO.read(order.getId().toHexString())).willReturn(Optional.empty());
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = order;

		// When
		accessRightsAspect.handleOrder(UPDATE, args, key);
	}

	@Test
	public void testHandleDishRead() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		given(dishDAO.read(dish.getId().toHexString())).willReturn(Optional.of(dish));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = dish.getId().toHexString();

		// When
		accessRightsAspect.handleDish(READ, args, key);

		// Then
		then(dishValidator).should().checkRightsAfter(ADMIN, userId, dish, READ);
	}

	@Test
	public void testHandleDishUpdate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		given(dishDAO.read(dish.getId().toHexString())).willReturn(Optional.of(dish));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = dish;

		// When
		accessRightsAspect.handleDish(UPDATE, args, key);

		// Then
		then(dishValidator).should().checkRightsAfter(ADMIN, userId, dish, UPDATE);
	}

	@Test
	public void testHandleDishUpdateNotFound() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);

		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		given(dishDAO.read(dish.getId().toHexString())).willReturn(Optional.empty());
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = dish;

		// When
		accessRightsAspect.handleDish(UPDATE, args, key);
	}

	@Test
	public void testHandleCatererRead() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		ObjectId userId = new ObjectId();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.of(caterer));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = caterer;

		// When
		accessRightsAspect.handleCaterer(READ, args, key);

		// Then
		then(catererValidator).should().checkRightsAfter(ADMIN, userId, caterer, READ);
	}

	@Test
	public void testHandleCatererReadNotFound() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);

		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		ObjectId userId = new ObjectId();
		given(catererDAO.read(caterer.getId().toHexString())).willReturn(Optional.empty());
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = caterer;

		// When
		accessRightsAspect.handleCaterer(READ, args, key);
	}

	@Test
	public void testHandleUserRead() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		given(userDAO.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = user.getId().toHexString();

		// When
		accessRightsAspect.handleUser(READ, args, key);

		// Then
		then(userValidator).should().checkUserRightsAfter(ADMIN, userId, user, READ);
	}

	@Test
	public void testHandleUserUpdate() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		given(userDAO.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = user;

		// When
		accessRightsAspect.handleUser(UPDATE, args, key);

		// Then
		then(userValidator).should().checkUserRightsAfter(ADMIN, userId, user, UPDATE);
	}

	@Test
	public void testHandleUserUpdateNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		given(userDAO.read(user.getId().toHexString())).willReturn(Optional.empty());
		given(key.getRole()).willReturn(ADMIN);
		given(key.getUserId()).willReturn(userId);
		Object[] args = new Object[1];
		args[0] = user;

		// When
		accessRightsAspect.handleUser(UPDATE, args, key);

		// Then
		then(userValidator).should().checkUserRightsAfter(ADMIN, userId, user, UPDATE);
	}

	@Test
	public void testCheckAccessRightsUser() throws Throwable {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleRead", String.class);
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);
		given(request.getAttribute("key")).willReturn(key);
		given(key.getRole()).willReturn(Role.ADMIN);
		willDoNothing().given(accessRightsAspect).handleUser(any(), any(), any());

		// When
		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		// Then
		then(accessRightsAspect).should().getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = READ, type = USER)
	public void exampleRead(final String id) {
	}

	@Test
	public void testCheckAccessRightsCaterer() throws Throwable {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleUpdate", Caterer.class);
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);
		given(request.getAttribute("key")).willReturn(key);
		given(key.getRole()).willReturn(Role.ADMIN);
		willDoNothing().given(accessRightsAspect).handleCaterer(any(), any(), any());

		// When
		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		// Then
		then(accessRightsAspect).should().getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = UPDATE, type = CATERER)
	public void exampleUpdate(final Caterer caterer) {
	}

	@Test
	public void testCheckAccessRightsDish() throws Throwable {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleUpdateDish", Dish.class);
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);
		given(request.getAttribute("key")).willReturn(key);
		given(key.getRole()).willReturn(Role.ADMIN);
		willDoNothing().given(accessRightsAspect).handleDish(any(), any(), any());

		// When
		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		// Then
		then(accessRightsAspect).should().getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = UPDATE, type = DISH)
	public void exampleUpdateDish(final Dish dish) {
	}

	@Test
	public void testCheckAccessRightsOrder() throws Throwable {
		// Given
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleReadOrder", String.class);
		given(signature.getMethod()).willReturn(method);
		given(joinPoint.getSignature()).willReturn(signature);
		given(request.getAttribute("key")).willReturn(key);
		given(key.getRole()).willReturn(Role.ADMIN);
		willDoNothing().given(accessRightsAspect).handleOrder(any(), any(), any());

		// When
		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		// Then
		then(accessRightsAspect).should().getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = READ, type = ORDER)
	public void exampleReadOrder(final String id) {
	}
}
