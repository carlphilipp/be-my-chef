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

import static com.epickur.api.enumeration.EndpointType.*;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.enumeration.Role.ADMIN;
import static org.mockito.Mockito.*;

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
		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		when(orderDAO.read(order.getId().toHexString())).thenReturn(Optional.of(order));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = order.getId().toHexString();

		accessRightsAspect.handleOrder(READ, args, key);

		verify(userValidator).checkOrderRightsAfter(ADMIN, userId, order, READ);
	}

	@Test
	public void testHandleOrderUpdate() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		when(orderDAO.read(order.getId().toHexString())).thenReturn(Optional.of(order));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = order;

		accessRightsAspect.handleOrder(UPDATE, args, key);

		verify(userValidator).checkOrderRightsAfter(ADMIN, userId, order, UPDATE);
		verify(userValidator).checkOrderStatus(order);
	}

	@Test
	public void testHandleOrderUpdateNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Order order = EntityGenerator.generateRandomOrderWithId();
		ObjectId userId = new ObjectId();
		when(orderDAO.read(order.getId().toHexString())).thenReturn(Optional.empty());
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = order;

		accessRightsAspect.handleOrder(UPDATE, args, key);
	}

	@Test
	public void testHandleDishRead() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		when(dishDAO.read(dish.getId().toHexString())).thenReturn(Optional.of(dish));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = dish.getId().toHexString();

		accessRightsAspect.handleDish(READ, args, key);

		verify(dishValidator).checkRightsAfter(ADMIN, userId, dish, READ);
	}

	@Test
	public void testHandleDishUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		when(dishDAO.read(dish.getId().toHexString())).thenReturn(Optional.of(dish));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = dish;

		accessRightsAspect.handleDish(UPDATE, args, key);

		verify(dishValidator).checkRightsAfter(ADMIN, userId, dish, UPDATE);
	}

	@Test
	public void testHandleDishUpdateNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Dish dish = EntityGenerator.generateRandomDishWithId();
		ObjectId userId = new ObjectId();
		when(dishDAO.read(dish.getId().toHexString())).thenReturn(Optional.empty());
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = dish;

		accessRightsAspect.handleDish(UPDATE, args, key);
	}

	@Test
	public void testHandleCatererRead() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		ObjectId userId = new ObjectId();
		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.of(caterer));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = caterer;

		accessRightsAspect.handleCaterer(READ, args, key);

		verify(catererValidator).checkRightsAfter(ADMIN, userId, caterer, READ);
	}

	@Test
	public void testHandleCatererReadNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		ObjectId userId = new ObjectId();
		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(Optional.empty());
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = caterer;

		accessRightsAspect.handleCaterer(READ, args, key);
	}

	@Test
	public void testHandleUserRead() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		when(userDAO.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = user.getId().toHexString();

		accessRightsAspect.handleUser(READ, args, key);

		verify(userValidator).checkUserRightsAfter(ADMIN, userId, user, READ);
	}

	@Test
	public void testHandleUserUpdate() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		when(userDAO.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = user;

		accessRightsAspect.handleUser(UPDATE, args, key);

		verify(userValidator).checkUserRightsAfter(ADMIN, userId, user, UPDATE);
	}

	@Test
	public void testHandleUserUpdateNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		User user = EntityGenerator.generateRandomUserWithId();
		ObjectId userId = new ObjectId();
		when(userDAO.read(user.getId().toHexString())).thenReturn(Optional.empty());
		when(key.getRole()).thenReturn(ADMIN);
		when(key.getUserId()).thenReturn(userId);

		Object[] args = new Object[1];
		args[0] = user;

		accessRightsAspect.handleUser(UPDATE, args, key);

		verify(userValidator).checkUserRightsAfter(ADMIN, userId, user, UPDATE);
	}

	@Test
	public void testCheckAccessRightsUser() throws Throwable {
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleRead", String.class);
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(request.getAttribute("key")).thenReturn(key);
		when(key.getRole()).thenReturn(Role.ADMIN);

		doNothing().when(accessRightsAspect).handleUser(any(), any(), any());

		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		verify(accessRightsAspect).getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = READ, type = USER)
	public void exampleRead(final String id) {
	}

	@Test
	public void testCheckAccessRightsCaterer() throws Throwable {
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleUpdate", Caterer.class);
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(request.getAttribute("key")).thenReturn(key);
		when(key.getRole()).thenReturn(Role.ADMIN);

		doNothing().when(accessRightsAspect).handleCaterer(any(), any(), any());

		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		verify(accessRightsAspect).getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = UPDATE, type = CATERER)
	public void exampleUpdate(final Caterer caterer) {
	}

	@Test
	public void testCheckAccessRightsDish() throws Throwable {
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleUpdateDish", Dish.class);
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(request.getAttribute("key")).thenReturn(key);
		when(key.getRole()).thenReturn(Role.ADMIN);

		doNothing().when(accessRightsAspect).handleDish(any(), any(), any());

		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		verify(accessRightsAspect).getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = UPDATE, type = DISH)
	public void exampleUpdateDish(final Dish dish) {
	}

	@Test
	public void testCheckAccessRightsOrder() throws Throwable {
		MethodSignature signature = mock(MethodSignature.class);
		Method method = ComplexAccessRightsAspectTest.class.getMethod("exampleReadOrder", String.class);
		when(signature.getMethod()).thenReturn(method);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(request.getAttribute("key")).thenReturn(key);
		when(key.getRole()).thenReturn(Role.ADMIN);

		doNothing().when(accessRightsAspect).handleOrder(any(), any(), any());

		accessRightsAspect.checkUserAccessRightsBefore(joinPoint);

		verify(accessRightsAspect).getMethodFromJointPoint(joinPoint);
	}

	@ValidateComplexAccessRights(operation = READ, type = ORDER)
	public void exampleReadOrder(final String id) {
	}
}
