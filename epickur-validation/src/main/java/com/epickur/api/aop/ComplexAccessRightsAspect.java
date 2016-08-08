package com.epickur.api.aop;

import com.epickur.api.annotation.ValidateComplexAccessRights;
import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

import static com.epickur.api.enumeration.EndpointType.*;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.utils.ErrorConstants.*;

@Aspect
public class ComplexAccessRightsAspect extends AccessRightsAspect {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private DishDAO dishDAO;
	@Autowired
	private OrderDAO orderDAO;

	@Before("execution(* com.epickur.api.service.*.*(..)) && @annotation(com.epickur.api.annotation.ValidateComplexAccessRights)")
	public void checkUserAccessRightsBefore(final JoinPoint joinPoint) throws Throwable {
		final Method method = getMethodFromJointPoint(joinPoint);

		final ValidateComplexAccessRights validateRequestBefore = method.getAnnotation(ValidateComplexAccessRights.class);
		final Operation operation = validateRequestBefore.operation();
		final EndpointType type = validateRequestBefore.type();

		final Object[] objects = joinPoint.getArgs();
		final Key key = (Key) request.getAttribute("key");

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

	protected void handleUser(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final String userId = operation == READ
			? (String) objects[0]
			: ((User) objects[0]).getId().toHexString();
		final User user = userDAO.read(userId).orElseThrow(() -> new EpickurNotFoundException(USER_NOT_FOUND, userId));
		userValidation.checkUserRightsAfter(key.getRole(), key.getUserId(), user, operation);
	}

	protected void handleCaterer(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final Caterer caterer = (Caterer) objects[0];
		final String catererId = caterer.getId().toHexString();
		final Caterer read = catererDAO.read(catererId).orElseThrow(() -> new EpickurNotFoundException(CATERER_NOT_FOUND, catererId));
		catererValidation.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	protected void handleDish(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final String dishId = operation == UPDATE
			? ((Dish) objects[0]).getId().toHexString()
			: (String) objects[0];
		final Dish read = dishDAO.read(dishId).orElseThrow(() -> new EpickurNotFoundException(DISH_NOT_FOUND, dishId));
		dishValidation.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	protected void handleOrder(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final String orderId = operation == READ
			? (String) objects[0]
			: ((Order) objects[0]).getId().toHexString();
		final Order read = orderDAO.read(orderId).orElseThrow(() -> new EpickurNotFoundException(ORDER_NOT_FOUND, orderId));
		userValidation.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, operation);
		if (operation == UPDATE) {
			userValidation.checkOrderStatus(read);
		}
	}
}
