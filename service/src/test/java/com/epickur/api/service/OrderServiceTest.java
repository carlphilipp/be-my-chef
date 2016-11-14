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
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
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
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrder();
		order.setStatus(OrderStatus.PENDING);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate = spy(orderAfterCreate);
		order = spy(order);
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.create(order)).willReturn(orderAfterCreate);

		// When
		Order actual = orderService.create(user.getId().toHexString(), order);

		// Then
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		then(order).should().setCreatedBy(user.getId());
		then(order).should().setStatus(OrderStatus.PENDING);
		then(order).should().setCreatedAt(any(DateTime.class));
		then(order).should().setUpdatedAt(any(DateTime.class));
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().create(order);
		then(order).should().getVoucher();
		then(voucherBusinessMock).should(never()).validateVoucher(anyString());
		then(emailUtilsMock).should().emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testCreateWithVoucher() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrder();
		order.setStatus(OrderStatus.PENDING);
		Voucher voucher = new Voucher();
		voucher.setCode(EntityGenerator.generateRandomString());
		order.setVoucher(voucher);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate = spy(orderAfterCreate);
		order = spy(order);
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.create(order)).willReturn(orderAfterCreate);
		given(voucherBusinessMock.validateVoucher(anyString())).willReturn(voucher);

		// When
		Order actual = orderService.create(user.getId().toHexString(), order);

		// Then
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertNotNull(actual.getVoucher());
		then(order).should().setCreatedBy(user.getId());
		then(order).should().setStatus(OrderStatus.PENDING);
		then(order).should().setCreatedAt(any(DateTime.class));
		then(order).should().setUpdatedAt(any(DateTime.class));
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().create(order);
		then(order).should().getVoucher();
		then(voucherBusinessMock).should().validateVoucher(voucher.getCode());
		then(emailUtilsMock).should().emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testCreateUserNotFoundFail() throws EpickurException {
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");

		// Given
		Order order = EntityGenerator.generateRandomOrder();
		String userId = new ObjectId().toHexString();
		given(userDAOMock.read(userId)).willReturn(Optional.empty());
		try {
			// When
			orderService.create(userId, order);
		} finally {
			// Then
			then(userDAOMock).should().read(userId);
			then(orderDAOMock).should(never()).create(any(Order.class));
		}
	}

	@Test
	public void testCreateWithVoucherOneTime() throws EpickurException {
		// Given
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
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.create(order)).willReturn(orderAfterCreate);

		// When
		Order actual = orderService.create(user.getId().toHexString(), order);

		// Then
		assertNotNull(actual);
		assertEquals(OrderStatus.PENDING, actual.getStatus());
		assertEquals(ExpirationType.ONETIME, actual.getVoucher().getExpirationType());
		then(order).should().setCreatedBy(user.getId());
		then(order).should().setStatus(OrderStatus.PENDING);
		then(order).should().setCreatedAt(any(DateTime.class));
		then(order).should().setUpdatedAt(any(DateTime.class));
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().create(order);
		then(order).should().getVoucher();
		then(voucherBusinessMock).should().validateVoucher(voucher.getCode());
		then(emailUtilsMock).should().emailNewOrder(eq(user), eq(orderAfterCreate), anyString());
	}

	@Test
	public void testExecuteOrder() throws Exception {
		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenId);
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		String chargeId = EntityGenerator.generateRandomString();
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(order.getId().toHexString())).willReturn(Optional.of(orderAfterRead));
		given(orderDAOMock.update(orderAfterRead)).willReturn(orderAfterRead);
		given(chargeMock.getPaid()).willReturn(true);
		given(chargeMock.getId()).willReturn(chargeId);
		given(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).willReturn(chargeMock);
		//givenNew(StripePayment.class).withNoArguments().willReturn(stripePayementMock);

		// When
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);

		// Then
		assertTrue(orderAfterCharge.getPaid());
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(stripePayementMock).should().chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		then(orderAfterRead).should().setChargeId(chargeId);
		then(orderAfterRead).should().setPaid(true);
		then(orderAfterRead).should().setStatus(OrderStatus.SUCCESSFUL);
		then(emailUtilsMock).should().emailSuccessOrder(user, orderAfterRead);
		then(orderAfterRead).should().setReadableId(null);
		then(orderAfterRead).should().setCreatedAt(null);
		then(orderAfterRead).should().setUpdatedAt(any(DateTime.class));
		then(orderAfterRead).should().setReadableId(null);
		then(orderDAOMock).should().read(order.getId().toHexString());

	}

	@Test
	public void testExecuteOrderNegativeAmount() throws Exception {
		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setAmount(-15);
		order.setCardToken(tokenMock.getId());
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(order.getId().toHexString())).willReturn(Optional.of(orderAfterRead));
		given(orderDAOMock.update(orderAfterRead)).willReturn(orderAfterRead);
		given(chargeMock.getPaid()).willReturn(false);
		given(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).willReturn(chargeMock);
		//givenNew(StripePayment.class).withNoArguments().willReturn(stripePayementMock);

		// When
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);

		// Then
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(stripePayementMock).should().chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		then(orderAfterRead).should(never()).setChargeId(anyString());
		then(orderAfterRead).should().setPaid(false);
		then(orderAfterRead).should().setStatus(OrderStatus.FAILED);
		then(emailUtilsMock).should(never()).emailSuccessOrder(isA(User.class), isA(Order.class));
		then(emailUtilsMock).should().emailFailOrder(isA(User.class), isA(Order.class));

	}

	@Test
	public void testExecuteOrderOrderNotFoundFail() throws Exception {
		// Then
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("Order not found");

		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		given(userDAOMock.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(order.getId().toHexString())).willReturn(Optional.empty());
		try {
			// When
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			// Then
			then(userDAOMock).should().read(user.getId().toHexString());
			then(orderDAOMock).should().read(order.getId().toHexString());
			then(emailUtilsMock).should(never()).emailSuccessOrder(isA(User.class), isA(Order.class));
		}
	}

	@Test
	public void testExecuteOrderUserNotFoundFail() throws EpickurException {
		// Then
		thrown.expect(EpickurNotFoundException.class);
		thrown.expectMessage("User not found");

		// Given
		String userId = new ObjectId().toHexString();
		Order order = EntityGenerator.generateRandomOrderWithId();
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		given(userDAOMock.read(userId)).willReturn(Optional.empty());
		try {
			// When
			orderService.executeOrder(userId, order.getId().toHexString(), true, true, orderCode);
		} finally {
			// Then
			then(userDAOMock).should().read(userId);
			then(orderDAOMock).should(never()).read(order.getId().toHexString());
			then(emailUtilsMock).should(never()).emailSuccessOrder(isA(User.class), isA(Order.class));
		}
	}

	@Test
	public void testExecuteOrderNotConfirmWithVoucher() throws Exception {
		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);

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
		given(userDAOMock.read(anyString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(anyString())).willReturn(Optional.of(orderAfterCreate));
		given(orderDAOMock.update(isA(Order.class))).willReturn(orderAfterCreate);
		given(chargeMock.getPaid()).willReturn(true);
		given(stripePayementMock.chargeCard(orderAfterCreate.getCardToken(), order.calculateTotalAmount(), order.getCurrency()))
			.willReturn(chargeMock);
		given(voucherBusinessMock.revertVoucher(anyString())).willReturn(voucher);
		//givenNew(StripePayment.class).withNoArguments().willReturn(stripePayementMock);

		// When
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, true, orderCode);
		assertNull(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
		assertNotNull(orderAfterCharge.getVoucher());
		assertEquals(DiscountType.AMOUNT, orderAfterCharge.getVoucher().getDiscountType());

		// Then
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(orderAfterCreate).should().setStatus(OrderStatus.DECLINED);
		then(emailUtilsMock).should(never()).emailSuccessOrder(user, orderAfterCreate);
		then(emailUtilsMock).should().emailDeclineOrder(user, orderAfterCreate);
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(orderDAOMock).should().update(orderAfterCreate);
		then(voucherBusinessMock).should().revertVoucher(orderAfterCreate.getVoucher().getCode());
		then(orderAfterCharge).should().setVoucher(voucher);
	}

	@Test
	public void testExecuteOrderNotConfirmWithoutVoucher() throws Exception {
		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		order = spy(order);
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterCreate.setId(order.getId());
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		given(userDAOMock.read(anyString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(anyString())).willReturn(Optional.of(orderAfterCreate));
		given(orderDAOMock.update(isA(Order.class))).willReturn(orderAfterCreate);

		// When
		Order actual = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), false, false, orderCode);
		assertEquals(OrderStatus.DECLINED, actual.getStatus());

		// Then
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(orderAfterCreate).should().setStatus(OrderStatus.DECLINED);
		then(emailUtilsMock).should(never()).emailSuccessOrder(user, orderAfterCreate);
		then(emailUtilsMock).should().emailDeclineOrder(user, orderAfterCreate);
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(orderDAOMock).should().update(orderAfterCreate);
		then(voucherBusinessMock).should(never()).revertVoucher(anyString());
	}

	@Test
	public void testExecuteOrderWrongCodeFail() throws Exception {
		// Then
		thrown.expect(EpickurForbiddenException.class);

		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		String orderCode = "wrong code";
		given(userDAOMock.read(anyString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(anyString())).willReturn(Optional.of(orderAfterCreate));
		given(orderDAOMock.update(isA(Order.class))).willReturn(orderAfterCreate);
		try {
			// When
			orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		} finally {
			// Then
			then(userDAOMock).should().read(user.getId().toHexString());
			then(orderDAOMock).should().read(order.getId().toHexString());
		}
	}

	@Test
	public void testExecuteOrderStripeException() throws Exception {
		// Given
		String tokenId = "tokenId";
		given(tokenMock.getId()).willReturn(tokenId);
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setCardToken(tokenMock.getId());
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order, tokenMock);
		orderAfterRead.setId(order.getId());
		orderAfterRead = spy(orderAfterRead);
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		given(userDAOMock.read(anyString())).willReturn(Optional.of(user));
		given(orderDAOMock.read(anyString())).willReturn(Optional.of(orderAfterRead));
		given(orderDAOMock.update(isA(Order.class))).willReturn(orderAfterRead);
		given(chargeMock.getPaid()).willReturn(true);
		given(stripePayementMock.chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency())).willThrow(new APIConnectionException(""));
		//givenNew(StripePayment.class).withNoArguments().willReturn(stripePayementMock);

		// When
		Order orderAfterCharge = orderService.executeOrder(user.getId().toHexString(), order.getId().toHexString(), true, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());

		// Then
		then(userDAOMock).should().read(user.getId().toHexString());
		then(orderDAOMock).should().read(order.getId().toHexString());
		then(stripePayementMock).should().chargeCard(orderAfterRead.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
		then(orderAfterRead).should().setPaid(false);
		then(orderAfterRead).should().setStatus(OrderStatus.FAILED);
		then(emailUtilsMock).should().emailFailOrder(user, orderAfterRead);
		then(orderAfterRead).should().setReadableId(null);
		then(orderAfterRead).should().setCreatedAt(null);
		then(orderAfterRead).should().setUpdatedAt(any(DateTime.class));
		then(orderAfterRead).should().setReadableId(null);
		then(orderDAOMock).should().read(order.getId().toHexString());
	}

	@Test
	public void testOrderFailed() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = new Voucher();
		String code = EntityGenerator.generateRandomString();
		voucher.setCode(code);
		order.setVoucher(voucher);

		// When
		orderService.handleOrderFail(order, user);

		// Then
		assertNotNull(order);
		assertFalse(order.getPaid());
		assertEquals(OrderStatus.FAILED, order.getStatus());
		then(emailUtilsMock).should().emailFailOrder(user, order);
		then(voucherBusinessMock).should().revertVoucher(code);
	}
}
