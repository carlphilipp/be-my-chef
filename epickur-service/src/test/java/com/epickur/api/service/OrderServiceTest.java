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
import com.epickur.api.stripe.StripePayment;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.utils.security.Security;
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

import java.util.Optional;

import static org.junit.Assert.*;
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

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

		Order actual = orderService.create(user.getId().toHexString(), order);

		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());

		verify(order).setCreatedBy(user.getId());
		verify(order).setStatus(OrderStatus.PENDING);
		verify(order).setCreatedAt(any(DateTime.class));
		verify(order).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).create(order);
		verify(order).getVoucher();
		verify(voucherBusinessMock, never()).validateVoucher(anyString());
		verify(emailUtilsMock).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
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

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);
		when(voucherBusinessMock.validateVoucher(anyString())).thenReturn(voucher);

		Order actual = orderService.create(user.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertNotNull(actual.getVoucher());

		verify(order).setCreatedBy(user.getId());
		verify(order).setStatus(OrderStatus.PENDING);
		verify(order).setCreatedAt(any(DateTime.class));
		verify(order).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).create(order);
		verify(order).getVoucher();
		verify(voucherBusinessMock).validateVoucher(voucher.getCode());
		verify(emailUtilsMock).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testCreateUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");

		Order order = EntityGenerator.generateRandomOrder();
		String userId = new ObjectId().toHexString();
		when(userDAOMock.read(userId)).thenReturn(Optional.empty());

		try {
			orderService.create(userId, order);
		} finally {
			verify(userDAOMock).read(userId);
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

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.create(order)).thenReturn(orderAfterCreate);

		Order actual = orderService.create(user.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertEquals(ExpirationType.ONETIME, actual.getVoucher().getExpirationType());

		verify(order).setCreatedBy(user.getId());
		verify(order).setStatus(OrderStatus.PENDING);
		verify(order).setCreatedAt(any(DateTime.class));
		verify(order).setUpdatedAt(any(DateTime.class));
		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).create(order);
		verify(order).getVoucher();
		verify(voucherBusinessMock).validateVoucher(voucher.getCode());
		verify(emailUtilsMock).emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
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

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(Optional.of(orderAfterRead));
		when(orderDAOMock.update(orderAfterRead)).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(true);
		when(chargeMock.getId()).thenReturn(chargeId);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).thenReturn(chargeMock);
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertTrue(orderAfterCharge.getPaid());

		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(stripePayementMock).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead).setChargeId(chargeId);
		verify(orderAfterRead).setPaid(true);
		verify(orderAfterRead).setStatus(OrderStatus.SUCCESSFUL);
		verify(emailUtilsMock).emailSuccessOrder(user, orderAfterRead);
		verify(orderAfterRead).setReadableId(null);
		verify(orderAfterRead).setCreatedAt(null);
		verify(orderAfterRead).setUpdatedAt(any(DateTime.class));
		verify(orderAfterRead).setReadableId(null);
		verify(orderDAOMock).read(order.getId().toHexString());

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

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(Optional.of(orderAfterRead));
		when(orderDAOMock.update(orderAfterRead)).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(false);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).thenReturn(chargeMock);
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(stripePayementMock).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead, never()).setChargeId(anyString());
		verify(orderAfterRead).setPaid(false);
		verify(orderAfterRead).setStatus(OrderStatus.FAILED);
		verify(emailUtilsMock, never()).emailSuccessOrder(isA(User.class), isA(Order.class));
		verify(emailUtilsMock).emailFailOrder(isA(User.class), isA(Order.class));

	}

	@Test
	public void testExecuteOrderOrderNotFoundFail() throws Exception {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("Order not found");

		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(order.getId().toHexString())).thenReturn(Optional.empty());
		try {
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock).read(user.getId().toHexString());
			verify(orderDAOMock).read(order.getId().toHexString());
			verify(emailUtilsMock, never()).emailSuccessOrder(isA(User.class), isA(Order.class));
		}
	}

	@Test
	public void testExecuteOrderUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");
		String userId = new ObjectId().toHexString();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());

		when(userDAOMock.read(userId)).thenReturn(Optional.empty());
		try {
			orderService.executeOrder(userId, order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock).read(userId);
			verify(orderDAOMock, never()).read(order.getId().toHexString());
			verify(emailUtilsMock, never()).emailSuccessOrder(isA(User.class), isA(Order.class));
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

		when(userDAOMock.read(anyString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(anyString())).thenReturn(Optional.of(orderAfterCreate));
		when(orderDAOMock.update(isA(Order.class))).thenReturn(orderAfterCreate);
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

		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(orderAfterCreate).setStatus(OrderStatus.DECLINED);
		verify(emailUtilsMock, never()).emailSuccessOrder(user, orderAfterCreate);
		verify(emailUtilsMock).emailDeclineOrder(user, orderAfterCreate);
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(orderDAOMock).update(orderAfterCreate);
		verify(voucherBusinessMock).revertVoucher(orderAfterCreate.getVoucher().getCode());
		verify(orderAfterCharge).setVoucher(voucher);
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

		when(userDAOMock.read(anyString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(anyString())).thenReturn(Optional.of(orderAfterCreate));
		when(orderDAOMock.update(isA(Order.class))).thenReturn(orderAfterCreate);

		Order actual = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, false, orderCode);
		assertEquals(OrderStatus.DECLINED, actual.getStatus());

		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(orderAfterCreate).setStatus(OrderStatus.DECLINED);
		verify(emailUtilsMock, never()).emailSuccessOrder(user, orderAfterCreate);
		verify(emailUtilsMock).emailDeclineOrder(user, orderAfterCreate);
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(orderDAOMock).update(orderAfterCreate);
		verify(voucherBusinessMock, never()).revertVoucher(anyString());
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

		when(userDAOMock.read(anyString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(anyString())).thenReturn(Optional.of(orderAfterCreate));
		when(orderDAOMock.update(isA(Order.class))).thenReturn(orderAfterCreate);
		try {
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			verify(userDAOMock).read(user.getId().toHexString());
			verify(orderDAOMock).read(order.getId().toHexString());
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

		when(userDAOMock.read(anyString())).thenReturn(Optional.of(user));
		when(orderDAOMock.read(anyString())).thenReturn(Optional.of(orderAfterRead));
		when(orderDAOMock.update(isA(Order.class))).thenReturn(orderAfterRead);
		when(chargeMock.getPaid()).thenReturn(true);
		when(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).thenThrow(new APIConnectionException(""));
		whenNew(StripePayment.class).withNoArguments().thenReturn(stripePayementMock);

		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());

		verify(userDAOMock).read(user.getId().toHexString());
		verify(orderDAOMock).read(order.getId().toHexString());
		verify(stripePayementMock).chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		verify(orderAfterRead).setPaid(false);
		verify(orderAfterRead).setStatus(OrderStatus.FAILED);
		verify(emailUtilsMock).emailFailOrder(user, orderAfterRead);
		verify(orderAfterRead).setReadableId(null);
		verify(orderAfterRead).setCreatedAt(null);
		verify(orderAfterRead).setUpdatedAt(any(DateTime.class));
		verify(orderAfterRead).setReadableId(null);
		verify(orderDAOMock).read(order.getId().toHexString());
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
		verify(emailUtilsMock).emailFailOrder(user, order);
		verify(voucherBusinessMock).revertVoucher(code);
	}
}
