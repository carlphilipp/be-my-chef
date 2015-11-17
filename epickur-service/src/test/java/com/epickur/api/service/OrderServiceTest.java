package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.SequenceDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripePayment;
import com.epickur.api.service.KeyService;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(OrderService.class)
public class OrderServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String stripeMessage = "Fail while acquiring Stripe token. Internet issue?";
	@Mock
	private VoucherService voucherBusinessMock;
	@Mock
	private KeyService keyBusinessMock;
	@Mock
	private UserDAO userDAOMock;
	@Mock
	private OrderDAO orderDAOMock;
	@Mock
	private SequenceDAO seqDAOMock;
	@Mock
	private StripePayment stripePayementMock;
	@Mock
	private Charge chargeMock;
	@Mock
	private EmailUtils emailUtilsMock;
	@InjectMocks
	private OrderService orderBusiness;

	@Test
	public void testCreate() throws EpickurException {
		try {
			Token token = EntityGenerator.generateToken();

			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrder();
			order.setStatus(OrderStatus.PENDING);
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

			Order actual = orderBusiness.create(user.getId().toHexString(), order);

			assertNotNull(actual);
			assertEquals(token.getId(), actual.getCardToken());
			assertEquals(OrderStatus.PENDING, actual.getStatus());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testCreateWithVoucher() throws EpickurException {
		try {
			Token token = EntityGenerator.generateToken();

			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrder();
			order.setStatus(OrderStatus.PENDING);
			Voucher voucher = new Voucher();
			voucher.setCode(EntityGenerator.generateRandomString());
			order.setVoucher(voucher);
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);
			when(voucherBusinessMock.validateVoucher(anyString())).thenReturn(voucher);

			Order actual = orderBusiness.create(user.getId().toHexString(), order);
			assertNotNull(actual);
			assertEquals(token.getId(), actual.getCardToken());
			assertEquals(OrderStatus.PENDING, actual.getStatus());
			assertNotNull(actual.getVoucher());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testCreateUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");

		Order order = EntityGenerator.generateRandomOrder();
		orderBusiness.create(new ObjectId().toHexString(), order);
	}

	@Test
	public void testCreateWithVoucherOneTime() throws EpickurException {
		try {
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrder();
			order.setStatus(OrderStatus.PENDING);
			Voucher voucher = new Voucher();
			voucher.setCode(EntityGenerator.generateRandomString());
			voucher.setExpirationType(ExpirationType.ONETIME);
			Token token = EntityGenerator.generateToken();
			order.setVoucher(voucher);
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

			Order actual = orderBusiness.create(user.getId().toHexString(), order);
			assertNotNull(actual);
			assertEquals(token.getId(), actual.getCardToken());
			assertEquals(OrderStatus.PENDING, actual.getStatus());
			assertEquals(ExpirationType.ONETIME, actual.getVoucher().getExpirationType());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testUpdateNotFound() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);

		Order order = EntityGenerator.generateRandomOrder();
		order.setId(new ObjectId());
		Key key = new Key();
		key.setUserId(order.getCreatedBy());
		orderBusiness.update(order, key);

	}

	@Test
	public void testUpdatePendingStatusFail() throws EpickurException {
		OrderStatus orderStatus = OrderStatus.SUCCESSFUL;
		thrown.expect(EpickurException.class);
		thrown.expectMessage("It's not allowed to modify an order that has a " + orderStatus + " status");
		try {
			Token token = EntityGenerator.generateToken();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setId(new ObjectId());
			Key key = new Key();
			key.setUserId(order.getCreatedBy());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setStatus(orderStatus);

			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);

			orderBusiness.update(order, key);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrder() throws Exception {
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setId(order.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
			when(chargeMock.getPaid()).thenReturn(true);
			when(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
					.thenReturn(chargeMock);
			whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

			Order orderAfterCharge = orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
			assertTrue(orderAfterCharge.getPaid());
			assertEquals(OrderStatus.SUCCESSFUL, orderAfterCharge.getStatus());
		} catch (StripeException e) {
			fail("Fail while acquiring Stripe token. Internet issue?");
		}
	}

	@Test
	public void testExecuteOrderNegativeAmount() throws Exception {
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setAmount(-15);
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setId(order.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
			when(chargeMock.getPaid()).thenReturn(false);
			when(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
					.thenReturn(chargeMock);
			whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);
			Order orderAfterCharge = orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
			assertFalse(orderAfterCharge.getPaid());
			assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderOrderNotFoundFail() throws Exception {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("Order not found");
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(null);

			orderBusiness.executeOrder(user.getId().toHexString(), new ObjectId().toHexString(), true, true, orderCode);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");
		try {
			Token token = EntityGenerator.generateToken();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(null);

			orderBusiness.executeOrder(new ObjectId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderNotConfirm() throws Exception {
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			order.setAmount(150);
			Voucher voucher = new Voucher();
			voucher.setDiscount(5);
			voucher.setDiscountType(DiscountType.AMOUNT);
			voucher.setCode(EntityGenerator.generateRandomString());
			order.setVoucher(voucher);
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setId(order.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
			when(chargeMock.getPaid()).thenReturn(true);
			when(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
					.thenReturn(chargeMock);
			when(voucherBusinessMock.revertVoucher(anyString())).thenReturn(voucher);
			whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

			Order orderAfterCharge = orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, true, orderCode);
			assertNull(orderAfterCharge.getPaid());
			assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
			assertNotNull(orderAfterCharge.getVoucher());
			assertEquals(DiscountType.AMOUNT, orderAfterCharge.getVoucher().getDiscountType());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderWrongCodeFail() throws Exception {
		thrown.expect(EpickurForbiddenException.class);
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			String orderCode = "wrong code";

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);

			orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderDeclined() throws Exception {
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setId(order.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);

			Order actual = orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, false, orderCode);
			assertEquals(OrderStatus.DECLINED, actual.getStatus());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testExecuteOrderStripeException() throws Exception {
		try {
			Token token = EntityGenerator.generateToken();
			User user = EntityGenerator.generateRandomUserWithId();
			Order order = EntityGenerator.generateRandomOrderWithId();
			order.setCardToken(token.getId());
			Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, token);
			orderAfterCreate.setId(order.getId());
			String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

			when(userDAOMock.read(anyString())).thenReturn(user);
			when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
			when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
			when(chargeMock.getPaid()).thenReturn(true);
			when(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
					.thenThrow(new APIConnectionException(""));
			whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

			Order orderAfterCharge = orderBusiness.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
			assertFalse(orderAfterCharge.getPaid());
			assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}

	@Test
	public void testOrderFailed() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = new Voucher();
		voucher.setCode(EntityGenerator.generateRandomString());
		order.setVoucher(voucher);

		orderBusiness.handleOrderFail(order, user);

		assertNotNull(order);
		assertFalse(order.getPaid());
		assertEquals(OrderStatus.FAILED, order.getStatus());
	}
}
