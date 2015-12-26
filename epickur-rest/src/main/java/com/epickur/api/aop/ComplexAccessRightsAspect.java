package com.epickur.api.aop;

import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

import static com.epickur.api.enumeration.EndpointType.CATERER;
import static com.epickur.api.enumeration.EndpointType.DISH;
import static com.epickur.api.enumeration.EndpointType.ORDER;
import static com.epickur.api.enumeration.EndpointType.USER;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.utils.ErrorConstants.CATERER_NOT_FOUND;
import static com.epickur.api.utils.ErrorConstants.DISH_NOT_FOUND;
import static com.epickur.api.utils.ErrorConstants.ORDER_NOT_FOUND;
import static com.epickur.api.utils.ErrorConstants.USER_NOT_FOUND;

@Aspect
public class ComplexAccessRightsAspect extends AccesRightsAspect {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private DishDAO dishDAO;
	@Autowired
	private OrderDAO orderDAO;

	@Before("execution(* com.epickur.api.service.*.*(..)) && @annotation(com.epickur.api.aop.ValidateComplexAccessRights)")
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
		final String userId;
		if (operation == READ) {
			userId = (String) objects[0];
		} else {
			userId = ((User) objects[0]).getId().toHexString();
		}
		final User user = userDAO.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(USER_NOT_FOUND, userId);
		}
		userValidator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, operation);
	}

	protected void handleCaterer(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final Caterer caterer = (Caterer) objects[0];
		final String catererId = caterer.getId().toHexString();
		final Caterer read = catererDAO.read(catererId);
		if (read == null) {
			throw new EpickurNotFoundException(CATERER_NOT_FOUND, catererId);
		}
		catererValidator.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	protected void handleDish(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final String dishId;
		if (operation == UPDATE) {
			dishId = ((Dish) objects[0]).getId().toHexString();
		} else {
			dishId = (String) objects[0];
		}
		final Dish read = dishDAO.read(dishId);
		if (read == null) {
			throw new EpickurNotFoundException(DISH_NOT_FOUND, dishId);
		}
		dishValidator.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
	}

	protected void handleOrder(final Operation operation, final Object[] objects, final Key key) throws EpickurException {
		final String orderId;
		if (operation == READ) {
			orderId = (String) objects[0];
		} else {
			orderId = ((Order) objects[0]).getId().toHexString();
		}
		final Order read = orderDAO.read(orderId);
		if (read == null) {
			throw new EpickurNotFoundException(ORDER_NOT_FOUND, orderId);
		}
		userValidator.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, operation);
		if (operation == UPDATE) {
			userValidator.checkOrderStatus(read);
		}
	}
}
