package com.epickur.api.service;

import com.epickur.api.cron.OrderJob;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.SequenceDAO;
import com.epickur.api.dao.mongo.UserDAO;
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
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.validator.UserValidator;
import com.stripe.exception.APIConnectionException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(OrderService.class)
public class OrderServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
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
	private Token tokenMock;
	@Mock
	private EmailUtils emailUtilsMock;
	@Mock
	private OrderJob jobs;
	@Mock
	private UserValidator validator;
	@InjectMocks
	private OrderService orderService;

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrder();
		order.setStatus(OrderStatus.PENDING);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate = spy(orderAfterCreate);
		order = spy(order);

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

		Order actual = orderService.create(user.getId().toHexString(), order);

		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());

		verify(order, times(1)).setCreatedBy(user.getId());
		verify(order, times(1)).setStatus(OrderStatus.PENDING);
		verify(order, times(1)).setCreatedAt(any(DateTime.class));
		verify(order, times(1)).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).create(order);
		verify(order, times(1)).getVoucher();
		verify(voucherBusinessMock, never()).validateVoucher(anyString());
		verify(emailUtilsMock, times(1)).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testCreateWithVoucher() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrder();
		order.setStatus(OrderStatus.PENDING);
		Voucher voucher = new Voucher();
		voucher.setCode(EntityGenerator.generateRandomString());
		order.setVoucher(voucher);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate = spy(orderAfterCreate);
		order = spy(order);

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);
		when(voucherBusinessMock.validateVoucher(anyString())).thenReturn(voucher);

		Order actual = orderService.create(user.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertNotNull(actual.getVoucher());

		verify(order, times(1)).setCreatedBy(user.getId());
		verify(order, times(1)).setStatus(OrderStatus.PENDING);
		verify(order, times(1)).setCreatedAt(any(DateTime.class));
		verify(order, times(1)).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).create(order);
		verify(order, times(1)).getVoucher();
		verify(voucherBusinessMock, times(1)).validateVoucher(voucher.getCode());
		verify(emailUtilsMock, times(1)).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testCreateUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");

		Order order = EntityGenerator.generateRandomOrder();
		String userId = new ObjectId().toHexString();
		try {
			orderService.create(userId, order);
		} finally {
			verify(userDAOMock, times(1)).read(userId);
			verify(orderDAOMock, never()).create(any(Order.class));
		}
	}

	@Test
	public void testCreateWithVoucherOneTime() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrder();
		order.setStatus(OrderStatus.PENDING);
		Voucher voucher = new Voucher();
		voucher.setCode(EntityGenerator.generateRandomString());
		voucher.setExpirationType(ExpirationType.ONETIME);
		order.setVoucher(voucher);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate = spy(orderAfterCreate);
		order = spy(order);

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

		Order actual = orderService.create(user.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertEquals(ExpirationType.ONETIME, actual.getVoucher().getExpirationType());

		verify(order, times(1)).setCreatedBy(user.getId());
		verify(order, times(1)).setStatus(OrderStatus.PENDING);
		verify(order, times(1)).setCreatedAt(any(DateTime.class));
		verify(order, times(1)).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).create(order);
		verify(order, times(1)).getVoucher();
		verify(voucherBusinessMock, times(1)).validateVoucher(voucher.getCode());
		verify(emailUtilsMock, times(1)).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testExecuteOrder() throws Exception {
		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenId);
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		String chargeId = EntityGenerator.generateRandomString();

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(orderAfterRead);
		when(orderDAOMock.update(orderAfterRead)).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(true);
		when(chargeMock.getId()).thenReturn(chargeId);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
				.thenReturn(chargeMock);
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertTrue(orderAfterCharge.getPaid());

		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(stripePayementMock, times(1)).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead, times(1)).setChargeId(chargeId);
		verify(orderAfterRead, times(1)).setPaid(true);
		verify(orderAfterRead, times(1)).setStatus(OrderStatus.SUCCESSFUL);
		verify(emailUtilsMock, times(1)).emailSuccessOrder(user, orderAfterRead);
		verify(orderAfterRead, times(1)).setReadableId(null);
		verify(orderAfterRead, times(1)).setCreatedAt(null);
		verify(orderAfterRead, times(1)).setUpdatedAt(any(DateTime.class));
		verify(orderAfterRead, times(1)).setReadableId(null);
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());

	}

	@Test
	public void testExecuteOrderNegativeAmount() throws Exception {
		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setAmount(-15);
		order.setCardToken(tokenMock.getId());
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(orderAfterRead);
		when(orderDAOMock.update(orderAfterRead)).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(false);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
				.thenReturn(chargeMock);
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(stripePayementMock, times(1)).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead, never()).setChargeId(anyObject());
		verify(orderAfterRead, times(1)).setPaid(false);
		verify(orderAfterRead, times(1)).setStatus(OrderStatus.FAILED);
		verify(emailUtilsMock, never()).emailSuccessOrder(anyObject(), anyObject());
		verify(emailUtilsMock, times(1)).emailFailOrder(anyObject(), anyObject());

	}

	@Test
	public void testExecuteOrderOrderNotFoundFail() throws Exception {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("Order not found");

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(user);
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(null);
		try {
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock, times(1)).read(user.getId().toHexString());
			verify(orderDAOMock, times(1)).read(order.getId().toHexString());
			verify(emailUtilsMock, never()).emailSuccessOrder(anyObject(), anyObject());
		}
	}

	@Test
	public void testExecuteOrderUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");
		String userId = new ObjectId().toHexString();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(userId)).thenReturn(null);
		try {
			orderService.executeOrder(userId, order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock, times(1)).read(userId);
			verify(orderDAOMock, never()).read(order.getId().toHexString());
			verify(emailUtilsMock, never()).emailSuccessOrder(anyObject(), anyObject());
		}
	}

	@Test
	public void testExecuteOrderNotConfirmWithVoucher() throws Exception {
		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		order.setAmount(150);
		Voucher voucher = new Voucher();
		voucher.setDiscount(5);
		voucher.setDiscountType(DiscountType.AMOUNT);
		voucher.setCode(EntityGenerator.generateRandomString());
		order.setVoucher(voucher);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate.setId(order.getId());
		orderAfterCreate = spy(orderAfterCreate);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(anyString())).thenReturn(user);
		when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
		when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
		when(chargeMock.getPaid()).thenReturn(true);
		when(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
				.thenReturn(chargeMock);
		when(voucherBusinessMock.revertVoucher(anyString())).thenReturn(voucher);
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, true, orderCode);
		assertNull(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
		assertNotNull(orderAfterCharge.getVoucher());
		assertEquals(DiscountType.AMOUNT, orderAfterCharge.getVoucher().getDiscountType());

		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(orderAfterCreate, times(1)).setStatus(OrderStatus.DECLINED);
		verify(emailUtilsMock, never()).emailSuccessOrder(user, orderAfterCreate);
		verify(emailUtilsMock, times(1)).emailDeclineOrder(user, orderAfterCreate);
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(orderDAOMock, times(1)).update(orderAfterCreate);
		verify(voucherBusinessMock, times(1)).revertVoucher(orderAfterCreate.getVoucher().getCode());
		verify(orderAfterCharge, times(1)).setVoucher(voucher);
	}

	@Test
	public void testExecuteOrderNotConfirmWithoutVoucher() throws Exception {
		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		order = spy(order);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate.setId(order.getId());
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(anyString())).thenReturn(user);
		when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
		when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);

		Order actual = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, false, orderCode);
		assertEquals(OrderStatus.DECLINED, actual.getStatus());

		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(orderAfterCreate, times(1)).setStatus(OrderStatus.DECLINED);
		verify(emailUtilsMock, never()).emailSuccessOrder(user, orderAfterCreate);
		verify(emailUtilsMock, times(1)).emailDeclineOrder(user, orderAfterCreate);
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(orderDAOMock, times(1)).update(orderAfterCreate);
		verify(voucherBusinessMock, never()).revertVoucher(anyObject());
	}

	@Test
	public void testExecuteOrderWrongCodeFail() throws Exception {
		thrown.expect(EpickurForbiddenException.class);

		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		String orderCode = "wrong code";

		when(userDAOMock.read(anyString())).thenReturn(user);
		when(orderDAOMock.read(anyString())).thenReturn(orderAfterCreate);
		when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterCreate);
		try {
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock, times(1)).read(user.getId().toHexString());
			verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		}
	}

	@Test
	public void testExecuteOrderStripeException() throws Exception {
		String tokenId = "tokenId";
		when(tokenMock.getId()).thenReturn(tokenId);

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(anyString())).thenReturn(user);
		when(orderDAOMock.read(anyString())).thenReturn(orderAfterRead);
		when(orderDAOMock.update((Order) anyObject())).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(true);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
				.thenThrow(new APIConnectionException(""));
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());

		verify(userDAOMock, times(1)).read(user.getId().toHexString());
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
		verify(stripePayementMock, times(1)).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead, times(1)).setPaid(false);
		verify(orderAfterRead, times(1)).setStatus(OrderStatus.FAILED);
		verify(emailUtilsMock, times(1)).emailFailOrder(user, orderAfterRead);
		verify(orderAfterRead, times(1)).setReadableId(null);
		verify(orderAfterRead, times(1)).setCreatedAt(null);
		verify(orderAfterRead, times(1)).setUpdatedAt(any(DateTime.class));
		verify(orderAfterRead, times(1)).setReadableId(null);
		verify(orderDAOMock, times(1)).read(order.getId().toHexString());
	}

	@Test
	public void testOrderFailed() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = new Voucher();
		String code = EntityGenerator.generateRandomString();
		voucher.setCode(code);
		order.setVoucher(voucher);

		orderService.handleOrderFail(order, user);

		assertNotNull(order);
		assertFalse(order.getPaid());
		assertEquals(OrderStatus.FAILED, order.getStatus());
		verify(emailUtilsMock, times(1)).emailFailOrder(user, order);
		verify(voucherBusinessMock, times(1)).revertVoucher(code);
	}
}
