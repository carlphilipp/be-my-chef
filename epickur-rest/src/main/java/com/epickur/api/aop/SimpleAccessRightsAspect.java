package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.exception.EpickurParsingException;
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
import static com.epickur.api.utils.ErrorUtils.CATERER_NOT_FOUND;
import static com.epickur.api.utils.ErrorUtils.DISH_NOT_FOUND;

@Aspect
public class SimpleAccessRightsAspect {

	private static final Logger LOG = LogManager.getLogger(SimpleAccessRightsAspect.class.getSimpleName());

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
	private CatererDAO catererDAO;

	@Before("execution(* com.epickur.api.rest.*.*(..)) && @annotation( com.epickur.api.aop.ValidateSimpleAccessRights)")
	public void checkAccessRights(final JoinPoint joinPoint) throws Throwable {
		Method method = getMethodFromJointPoint(joinPoint);

		ValidateSimpleAccessRights simpleAccessRights = method.getAnnotation(ValidateSimpleAccessRights.class);
		Operation operation = simpleAccessRights.operation();
		EndpointType endpointType = simpleAccessRights.endpoint();

		Key key = (Key) request.getAttribute("key");

		validateMatrixAccessRights(key.getRole(), operation, endpointType);
		validateLogicAccessRights(joinPoint, operation, endpointType, key);
	}

	private Method getMethodFromJointPoint(final JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getMethod();
	}

	private void validateMatrixAccessRights(final Role role, final Operation operation, final EndpointType endpointType) {
		MatrixAccessRights.check(role, operation, endpointType);
	}

	private void validateLogicAccessRights(final JoinPoint joinPoint, final Operation operation, final EndpointType endpointType, final Key key)
			throws EpickurException {
		Object[] args = joinPoint.getArgs();
		if (endpointType == DISH) {
			handleDish(operation, args, key);
		} else if (endpointType == VOUCHER) {
			handleVoucher(operation, args);
		} else if (operation == RESET_PASSWORD && endpointType == NO_KEY) {
			ObjectNode node = (ObjectNode) args[2];
			userValidator.checkResetPasswordDataSecondStep(node);
		} else if (endpointType == CATERER) {
			handleCaterer(operation, args);
		} else if (endpointType == USER) {
			handleUser(operation, args);
		} else if (endpointType == ORDER) {
			handleOrder(operation, args);
		}
	}

	private void handleDish(final Operation operation, final Object[] args, final Key key) throws EpickurException {
		if (operation == CREATE) {
			Dish dish = (Dish) args[0];
			dishValidator.checkCreateData(dish);
			String catererId = dish.getCaterer().getId().toHexString();
			Caterer caterer = catererDAO.read(catererId);
			if (caterer == null) {
				throw new EpickurNotFoundException(DISH_NOT_FOUND, catererId);
			}
			dishValidator.checkRightsBefore(key.getRole(), CREATE, caterer, key);
		} else if (operation == UPDATE) {
			String id = (String) args[0];
			Dish dish = (Dish) args[1];
			dishValidator.checkUpdateData(id, dish);
		} else if (operation == SEARCH_DISH) {
			String pickupdate = (String) args[0];
			String types = (String) args[1];
			String at = (String) args[3];
			String searchtext = (String) args[4];
			dishValidator.checkSearch(pickupdate, types, at, searchtext);
		}
	}

	private void handleVoucher(final Operation operation, final Object[] args) throws EpickurParsingException {
		if (operation == READ) {
			String code = (String) args[0];
			voucherValidator.checkVoucherCode(code);
		} else if (operation == GENERATE_VOUCHER) {
			ExpirationType expirationType = (ExpirationType) args[3];
			String expiration = (String) args[4];
			String format = (String) args[5];
			voucherValidator.checkVoucherGenerate(expirationType, expiration, format);
		}
	}

	private void handleCaterer(final Operation operation, final Object[] args) throws EpickurException {
		if (operation == CREATE) {
			Caterer caterer = (Caterer) args[0];
			catererValidator.checkCreateCaterer(caterer);
		} else if (operation == UPDATE) {
			String id = (String) args[0];
			Caterer caterer = (Caterer) args[1];
			catererValidator.checkUpdateCaterer(id, caterer);
		} else if (operation == PAYEMENT_INFO) {
			String id = (String) args[0];
			// TODO : Find a better way to do that because the read() is done twice
			Caterer caterer = catererDAO.read(id);
			if (caterer == null) {
				throw new EpickurNotFoundException(CATERER_NOT_FOUND, id);
			}
		}
	}

	private void handleUser(final Operation operation, final Object[] args) {
		if (operation == UPDATE) {
			String id = (String) args[0];
			User user = (User) args[1];
			if (!user.getId().toHexString().equals(id)) {
				throw new EpickurIllegalArgument("The parameter id and the field user.id should match");
			}
		} else if (operation == RESET_PASSWORD) {
			ObjectNode node = (ObjectNode) args[0];
			userValidator.checkResetPasswordData(node);
		}
	}

	private void handleOrder(final Operation operation, final Object[] args) {
		if (operation == UPDATE) {
			String orderId = (String) args[1];
			Order order = (Order) args[2];
			if (!order.getId().toHexString().equals(orderId)) {
				throw new EpickurIllegalArgument("The parameter orderId and the field order.id should match");
			}
		} else if (operation == READ){
			String userId = (String) args[0];
			String orderId = (String) args[1];
			userValidator.checkReadOneOrder(userId, orderId);
		}
	}
}