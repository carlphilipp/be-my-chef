package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.UserValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.epickur.api.enumeration.EndpointType.*;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.utils.ErrorUtils.*;

@Aspect
public class ComplexAccessRightsAspect {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserValidator userValidator;
	@Autowired
	private DishValidator dishValidator;
	@Autowired
	private CatererValidator catererValidator;

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private DishDAO dishDAO;
	@Autowired
	private CatererDAO catererDAO;
	@Autowired
	private OrderDAO orderDAO;

	@Before("execution(* com.epickur.api.service.*.*(..)) && @annotation(com.epickur.api.aop.ValidateComplexAccessRights)")
	public void checkUserAccessRightsBefore(final JoinPoint joinPoint) throws Throwable {
		Method method = getMethodFromJointPoint(joinPoint);

		ValidateComplexAccessRights validateRequestBefore = method.getAnnotation(ValidateComplexAccessRights.class);
		Operation operation = validateRequestBefore.operation();
		EndpointType type = validateRequestBefore.type();

		Object[] objects = joinPoint.getArgs();
		Key key = (Key) request.getAttribute("key");

		if (type == USER) {
			handleUser(operation, objects, key);
		} else if (type == CATERER) {
			handleCaterer(operation, objects, key);
		} else if (type == DISH) {
			handleDish(operation, objects, key);
		} else if (type == ORDER) {
			handleOrder(operation, objects, key);
		}
	}

	private Method getMethodFromJointPoint(final JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getMethod();
	}

	private void handleUser(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		String userId;
		if (operation == READ) {
			userId = (String) objects[0];
		} else {
			userId = ((User) objects[0]).getId().toHexString();
		}
		User user = userDAO.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(USER_NOT_FOUND, userId);
		}
		userValidator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, operation);
	}

	private void handleCaterer(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		Caterer caterer = (Caterer) objects[0];
		String catererId = caterer.getId().toHexString();
		Caterer read = catererDAO.read(catererId);
		if (read == null) {
			throw new EpickurNotFoundException(CATERER_NOT_FOUND, catererId);
		}
		catererValidator.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	private void handleDish(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		String dishId;
		if (operation == UPDATE) {
			dishId = ((Dish) objects[0]).getId().toHexString();
		} else {
			dishId = (String) objects[0];
		}
		Dish read = dishDAO.read(dishId);
		if (read == null) {
			throw new EpickurNotFoundException(DISH_NOT_FOUND, dishId);
		}
		dishValidator.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	private void handleOrder(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		String orderId;
		if (operation == READ) {
			orderId = (String) objects[0];
		} else {
			orderId = ((Order) objects[0]).getId().toHexString();
		}
		Order read = orderDAO.read(orderId);
		if (read == null) {
			throw new EpickurNotFoundException(ORDER_NOT_FOUND, orderId);
		}
		userValidator.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, operation);
		if (operation == UPDATE) {
			userValidator.checkOrderStatus(read);
		}
	}
}
