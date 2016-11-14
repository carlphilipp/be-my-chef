package com.epickur.api.cron;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.email.EmailUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderJobTest {

	@Mock
	private VoucherDAO voucherDAO;
	@Mock
	private EmailUtils emailUtils;
	@Mock
	private UserDAO userDAO;
	@Mock
	private OrderDAO orderDAO;
	@Mock
	private JobExecutionContext context;
	@Mock
	private Scheduler scheduler;
	@Mock
	private VoucherService voucherService;
	@InjectMocks
	private CancelOrderJob orderJob;

	@Test
	public void testExecute() throws JobExecutionException, EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = EntityGenerator.generateVoucher();
		order.setVoucher(voucher);
		order = spy(order);
		User user = EntityGenerator.generateRandomUserWithId();
		user = spy(user);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		given(context.getJobDetail()).willReturn(jobDetail);
		given(context.getScheduler()).willReturn(scheduler);
		given(jobDetail.getJobDataMap()).willReturn(jobDataMap);
		given(jobDataMap.getString("orderId")).willReturn(order.getId().toHexString());
		given(jobDataMap.getString("userId")).willReturn(user.getId().toHexString());
		given(orderDAO.read(order.getId().toHexString())).willReturn(Optional.of(order));
		given(orderDAO.update(order)).willReturn(order);
		given(userDAO.read(user.getId().toHexString())).willReturn(Optional.of(user));
		given(voucherDAO.read(anyString())).willReturn(Optional.of(voucher));

		// When
		orderJob.execute(context);

		// Then
		then(orderDAO).should().read(order.getId().toHexString());
		then(userDAO).should().read(user.getId().toHexString());
		then(emailUtils).should().emailCancelOrder(user, order);
		then(order).should().setStatus(OrderStatus.CANCELED);
		then(order).should().setReadableId(null);
		then(order).should().setCreatedAt(null);
		then(order).should().setUpdatedAt(any(DateTime.class));
		then(order).should().prepareForUpdateIntoDB();
		//then(voucherBusiness).should().revertVoucher(voucher.getCode());
	}

	@Test
	public void testExecuteEpickurException() throws JobExecutionException, EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = EntityGenerator.generateVoucher();
		order.setVoucher(voucher);
		order = spy(order);
		User user = EntityGenerator.generateRandomUserWithId();
		user = spy(user);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		given(context.getJobDetail()).willReturn(jobDetail);
		given(context.getScheduler()).willReturn(scheduler);
		given(jobDetail.getJobDataMap()).willReturn(jobDataMap);
		given(jobDataMap.getString("orderId")).willReturn(order.getId().toHexString());
		given(jobDataMap.getString("userId")).willReturn(user.getId().toHexString());
		given(orderDAO.read(order.getId().toHexString())).willThrow(new EpickurException());
		given(orderDAO.update(order)).willReturn(order);
		given(userDAO.read(user.getId().toHexString())).willReturn(Optional.of(user));

		// When
		orderJob.execute(context);

		// Then
		then(orderDAO).should().read(order.getId().toHexString());
		then(userDAO).should(never()).read(user.getId().toHexString());
		then(emailUtils).should(never()).emailCancelOrder(user, order);
		then(order).should(never()).setStatus(any(OrderStatus.class));
		then(order).should(never()).setReadableId(anyString());
		then(order).should(never()).setCreatedAt(isA(DateTime.class));
		then(order).should(never()).setUpdatedAt(isA(DateTime.class));
		then(order).should(never()).prepareForUpdateIntoDB();
	}
}
