package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.*;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.UserValidator;
import com.epickur.api.validator.VoucherValidator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleAccessRightsAspectTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private HttpServletRequest request;
	@Mock
	private UserValidator userValidator;
	@Mock
	private DishValidator dishValidator;
	@Mock
	private CatererValidator catererValidator;
	@Mock
	private VoucherValidator voucherValidator;
	@Mock
	private CatererDAO catererDAO;
	@InjectMocks
	private SimpleAccessRightsAspect accessRightsAspect;

	@Test
	public void testHandleOrderUpdate() {
		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		;
		args[1] = order.getId().toHexString();
		args[2] = order;

		accessRightsAspect.handleOrder(Operation.UPDATE, args);
	}

	@Test
	public void testHandleOrderUpdateFail() {
		thrown.expect(EpickurIllegalArgument.class);

		Object[] args = new Object[3];
		Order order = EntityGenerator.generateRandomOrderWithId();
		;
		args[1] = EntityGenerator.generateRandomString();
		args[2] = order;

		accessRightsAspect.handleOrder(Operation.UPDATE, args);
	}

	@Test
	public void testHandleUserUpdate() {
		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = user.getId().toHexString();
		args[1] = user;

		accessRightsAspect.handleUser(Operation.UPDATE, args);
	}

	@Test
	public void testHandleUserUpdateFail() {
		thrown.expect(EpickurIllegalArgument.class);

		Object[] args = new Object[2];
		User user = EntityGenerator.generateRandomUserWithId();
		args[0] = EntityGenerator.generateRandomString();
		args[1] = user;

		accessRightsAspect.handleUser(Operation.UPDATE, args);
	}

	@Test
	public void testHandleUserResetPassword() {
		Object[] args = new Object[1];
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		args[0] = node;

		accessRightsAspect.handleUser(Operation.RESET_PASSWORD, args);

		verify(userValidator, times(1)).checkResetPasswordData(node);
	}

	@Test
	public void testHandleCatererCreate() throws EpickurException {
		Object[] args = new Object[1];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer;

		accessRightsAspect.handleCaterer(Operation.CREATE, args);

		verify(catererValidator, times(1)).checkCreateCaterer(caterer);
	}

	@Test
	public void testHandleCatererUpdate() throws EpickurException {
		Object[] args = new Object[2];
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		args[0] = caterer.getId().toHexString();
		args[1] = caterer;

		accessRightsAspect.handleCaterer(Operation.UPDATE, args);

		verify(catererValidator, times(1)).checkUpdateCaterer(caterer.getId().toHexString(), caterer);
	}

	@Test
	public void testHandleCatererPaymentInfo() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(caterer);

		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		accessRightsAspect.handleCaterer(Operation.PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleCatererPaymentInfoFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		when(catererDAO.read(anyString())).thenReturn(null);

		Object[] args = new Object[1];
		args[0] = caterer.getId().toHexString();

		accessRightsAspect.handleCaterer(Operation.PAYEMENT_INFO, args);
	}

	@Test
	public void testHandleVoucherRead() throws EpickurParsingException {
		Object[] args = new Object[1];
		args[0] = EntityGenerator.generateRandomString();
		accessRightsAspect.handleVoucher(Operation.READ, args);

		verify(voucherValidator, times(1)).checkVoucherCode((String) args[0]);
	}

	@Test
	public void testHandleVoucherGenerateVoucher() throws EpickurParsingException {
		Object[] args = new Object[6];
		args[3] = ExpirationType.ONETIME;
		args[4] = "expiration";
		args[5] = "format";
		accessRightsAspect.handleVoucher(Operation.GENERATE_VOUCHER, args);

		verify(voucherValidator, times(1)).checkVoucherGenerate((ExpirationType) args[3], (String) args[4], (String) args[5]);
	}

	@Test
	public void testHandleDishCreate() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Caterer caterer = dish.getCaterer();

		when(catererDAO.read(caterer.getId().toHexString())).thenReturn(caterer);

		Object[] args = new Object[1];
		args[0] = dish;

		accessRightsAspect.handleDish(Operation.CREATE, args, key);
	}
}
