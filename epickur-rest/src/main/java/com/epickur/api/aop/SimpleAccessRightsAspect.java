package com.epickur.api.aop;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.validator.MatrixAccessRights;
import com.epickur.api.validator.VoucherValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

import static com.epickur.api.enumeration.EndpointType.CATERER;
import static com.epickur.api.enumeration.EndpointType.DISH;
import static com.epickur.api.enumeration.EndpointType.NO_KEY;
import static com.epickur.api.enumeration.EndpointType.ORDER;
import static com.epickur.api.enumeration.EndpointType.USER;
import static com.epickur.api.enumeration.EndpointType.VOUCHER;
import static com.epickur.api.enumeration.Operation.CREATE;
import static com.epickur.api.enumeration.Operation.GENERATE_VOUCHER;
import static com.epickur.api.enumeration.Operation.PAYEMENT_INFO;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.RESET_PASSWORD;
import static com.epickur.api.enumeration.Operation.SEARCH_DISH;
import static com.epickur.api.enumeration.Operation.UPDATE;
import static com.epickur.api.utils.ErrorConstants.CATERER_NOT_FOUND;

@Aspect
public class SimpleAccessRightsAspect extends AccesRightsAspect {

	@Autowired
	private VoucherValidator voucherValidator;

	@Before("execution(* com.epickur.api.rest.*.*(..)) && @annotation( com.epickur.api.aop.ValidateSimpleAccessRights)")
	public void checkAccessRights(final JoinPoint joinPoint) throws Throwable {
		final Method method = getMethodFromJointPoint(joinPoint);

		final ValidateSimpleAccessRights simpleAccessRights = method.getAnnotation(ValidateSimpleAccessRights.class);
		final Operation operation = simpleAccessRights.operation();
		final EndpointType endpointType = simpleAccessRights.endpoint();

		final Key key = (Key) request.getAttribute("key");

		validateMatrixAccessRights(key.getRole(), operation, endpointType);
		validateLogicAccessRights(joinPoint, operation, endpointType, key);
	}

	protected void validateMatrixAccessRights(final Role role, final Operation operation, final EndpointType endpointType) {
		MatrixAccessRights.check(role, operation, endpointType);
	}

	protected void validateLogicAccessRights(final JoinPoint joinPoint, final Operation operation, final EndpointType endpointType, final Key key)
			throws EpickurException {
		final Object[] args = joinPoint.getArgs();
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

	protected void handleDish(final Operation operation, final Object[] args, final Key key) throws EpickurException {
		if (operation == CREATE) {
			final Dish dish = (Dish) args[0];
			dishValidator.checkCreateData(dish);
			final String catererId = dish.getCaterer().getId().toHexString();
			final Caterer caterer = catererDAO.read(catererId);
			if (caterer == null) {
				throw new EpickurNotFoundException(CATERER_NOT_FOUND, catererId);
			}
			dishValidator.checkRightsBefore(key.getRole(), CREATE, caterer, key);
		} else if (operation == UPDATE) {
			final String id = (String) args[0];
			final Dish dish = (Dish) args[1];
			dishValidator.checkUpdateData(id, dish);
		} else if (operation == SEARCH_DISH) {
			final String pickupdate = (String) args[0];
			final String types = (String) args[1];
			final String at = (String) args[3];
			final String searchtext = (String) args[4];
			dishValidator.checkSearch(pickupdate, types, at, searchtext);
		}
	}

	protected void handleVoucher(final Operation operation, final Object[] args) throws EpickurParsingException {
		if (operation == READ) {
			final String code = (String) args[0];
			voucherValidator.checkVoucherCode(code);
		} else if (operation == GENERATE_VOUCHER) {
			final ExpirationType expirationType = (ExpirationType) args[3];
			final String expiration = (String) args[4];
			final String format = (String) args[5];
			voucherValidator.checkVoucherGenerate(expirationType, expiration, format);
		}
	}

	protected void handleCaterer(final Operation operation, final Object[] args) throws EpickurException {
		if (operation == CREATE) {
			final Caterer caterer = (Caterer) args[0];
			catererValidator.checkCreateCaterer(caterer);
		} else if (operation == UPDATE) {
			final String id = (String) args[0];
			final Caterer caterer = (Caterer) args[1];
			catererValidator.checkUpdateCaterer(id, caterer);
		} else if (operation == PAYEMENT_INFO) {
			String id = (String) args[0];
			// TODO : Find a better way to do that because the read() is done twice
			final Caterer caterer = catererDAO.read(id);
			if (caterer == null) {
				throw new EpickurNotFoundException(CATERER_NOT_FOUND, id);
			}
		}
	}

	protected void handleUser(final Operation operation, final Object[] args) {
		if (operation == UPDATE) {
			final String id = (String) args[0];
			final User user = (User) args[1];
			if (!user.getId().toHexString().equals(id)) {
				throw new EpickurIllegalArgument("The parameter id and the field user.id should match");
			}
		} else if (operation == RESET_PASSWORD) {
			final ObjectNode node = (ObjectNode) args[0];
			userValidator.checkResetPasswordData(node);
		}
	}

	protected void handleOrder(final Operation operation, final Object[] args) {
		if (operation == UPDATE) {
			final String orderId = (String) args[1];
			final Order order = (Order) args[2];
			if (!order.getId().toHexString().equals(orderId)) {
				throw new EpickurIllegalArgument("The parameter orderId and the field order.id should match");
			}
		}
	}
}