package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.validator.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.epickur.api.enumeration.EndpointType.*;
import static com.epickur.api.enumeration.Operation.*;
import static com.epickur.api.utils.ErrorUtils.*;

@Aspect
public class ValidateRequestAspect {

	private static final Logger LOG = LogManager.getLogger(ValidateRequestAspect.class.getSimpleName());

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserValidator userValidator;
	@Autowired
	private DishValidator dishValidator;
	@Autowired
	private CatererValidator catererValidator;
	@Autowired
	private VoucherValidator voucherValidator;

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private DishDAO dishDAO;
	@Autowired
	private CatererDAO catererDAO;
	@Autowired
	private OrderDAO orderDAO;

	@Before("execution(* com.epickur.api.rest.*.*(..)) && @annotation(validateRequest)")
	public void checkAccessRights(final JoinPoint joinPoint, final ValidateRequest validateRequest) throws Throwable {
		LOG.info("Check Access rights");
		Key key = (Key) request.getAttribute("key");
		Operation operation = validateRequest.operation();
		EndpointType endpointType = validateRequest.endpoint();
		AccessRights.check(key.getRole(), operation, endpointType);
		Object[] objects = joinPoint.getArgs();
		if (endpointType == DISH) {
			if (operation == CREATE) {
				Dish dish = (Dish) objects[0];
				dishValidator.checkCreateData(dish);
				String catererId = dish.getCaterer().getId().toHexString();
				Caterer caterer = catererDAO.read(catererId);
				if (caterer == null) {
					throw new EpickurNotFoundException(DISH_NOT_FOUND, catererId);
				}
				dishValidator.checkRightsBefore(key.getRole(), CREATE, caterer, key);
			} else if (operation == UPDATE) {
				String id = (String) objects[0];
				Dish dish = (Dish) objects[1];
				dishValidator.checkUpdateData(id, dish);
			} else if (operation == SEARCH_DISH) {
				String pickupdate = (String) objects[0];
				String types = (String) objects[1];
				String at = (String) objects[3];
				String searchtext = (String) objects[4];
				dishValidator.checkSearch(pickupdate, types, at, searchtext);
			}
		} else if (endpointType == VOUCHER) {
			if (operation == READ) {
				String code = (String) objects[0];
				voucherValidator.checkVoucherCode(code);
			} else if (operation == GENERATE_VOUCHER) {
				ExpirationType expirationType = (ExpirationType) objects[3];
				String expiration = (String) objects[4];
				String format = (String) objects[5];
				voucherValidator.checkVoucherGenerate(expirationType, expiration, format);
			}
		} else if (operation == RESET_PASSWORD && endpointType == NO_KEY) {
			ObjectNode node = (ObjectNode) objects[2];
			userValidator.checkResetPasswordDataSecondStep(node);
		} else if (endpointType == CATERER) {
			if (operation == CREATE) {
				Caterer caterer = (Caterer) objects[0];
				catererValidator.checkCreateCaterer(caterer);
			} else if (operation == UPDATE) {
				String id = (String) objects[0];
				Caterer caterer = (Caterer) objects[1];
				catererValidator.checkUpdateCaterer(id, caterer);
			} else if (operation == PAYEMENT_INFO) {
				String id = (String) objects[0];
				// TODO : Find a better way to do that because the read() is done twice
				Caterer caterer = catererDAO.read(id);
				if (caterer == null) {
					throw new EpickurNotFoundException(CATERER_NOT_FOUND, id);
				}
			}
		} else if (endpointType == USER) {
			if (operation == UPDATE) {
				String id = (String) objects[0];
				User user = (User) objects[1];
				userValidator.checkUpdateUser(id, user);
			} else if (operation == RESET_PASSWORD) {
				ObjectNode node = (ObjectNode) objects[0];
				userValidator.checkResetPasswordData(node);
			}
		} else if (endpointType == ORDER) {
			if (operation == CREATE) {
				Order order = (Order) objects[1];
				userValidator.checkCreateOneOrder(order);
			} else if (operation == UPDATE) {
				String id = (String) objects[1];
				Order order = (Order) objects[2];
				userValidator.checkUpdateOneOrder(id, order);
			}
		}
	}

	@Before("execution(* com.epickur.api.service.*.*(..)) && @annotation(com.epickur.api.aop.ValidateRequestBefore)")
	public void checkUserAccessRightsBefore(final JoinPoint joinPoint) throws Throwable {
		LOG.info("Check access rights after");
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		ValidateRequestBefore validateRequestBefore = method.getAnnotation(ValidateRequestBefore.class);
		Operation operation = validateRequestBefore.operation();
		EndpointType type = validateRequestBefore.type();
		Object[] objects = joinPoint.getArgs();
		Key key = (Key) request.getAttribute("key");
		if (type == USER) {
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
		} else if (type == CATERER) {
			Caterer caterer = (Caterer) objects[0];
			String catererId = caterer.getId().toHexString();
			Caterer read = catererDAO.read(catererId);
			if (read == null) {
				throw new EpickurNotFoundException(CATERER_NOT_FOUND, catererId);
			}
			catererValidator.checkRightsAfter(key.getRole(), key.getUserId(), read, operation);
		} else if (type == DISH) {
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
		} else if (type == ORDER) {
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
}