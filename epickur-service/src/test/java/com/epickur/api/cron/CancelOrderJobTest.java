package com.epickur.api.cron;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.epickur.api.dao.mongo.VoucherDAO;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.*;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.email.EmailUtils;

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
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecute() throws JobExecutionException, EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = EntityGenerator.generateVoucher();
		order.setVoucher(voucher);
		order = spy(order);
		User user = EntityGenerator.generateRandomUserWithId();
		user = spy(user);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(context.getScheduler()).thenReturn(scheduler);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getString("orderId")).thenReturn(order.getId().toHexString());
		when(jobDataMap.getString("userId")).thenReturn(user.getId().toHexString());
		when(orderDAO.read(order.getId().toHexString())).thenReturn(order);
		when(orderDAO.update(order)).thenReturn(order);
		when(userDAO.read(user.getId().toHexString())).thenReturn(user);
		when(voucherDAO.read(anyString())).thenReturn(voucher);

		orderJob.execute(context);

		verify(orderDAO, times(1)).read(order.getId().toHexString());
		verify(userDAO, times(1)).read(user.getId().toHexString());
		verify(emailUtils, times(1)).emailCancelOrder(user, order);
		verify(order, times(1)).setStatus(OrderStatus.CANCELED);
		verify(order, times(1)).setReadableId(null);
		verify(order, times(1)).setCreatedAt(null);
		verify(order, times(1)).setUpdatedAt(any(DateTime.class));
		verify(order, times(1)).prepareForUpdateIntoDB();
		//verify(voucherBusiness, times(1)).revertVoucher(voucher.getCode());
	}
	
	@Test
	public void testExecuteEpickurException() throws JobExecutionException, EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();
		Voucher voucher = EntityGenerator.generateVoucher();
		order.setVoucher(voucher);
		order = spy(order);
		User user = EntityGenerator.generateRandomUserWithId();
		user = spy(user);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(context.getScheduler()).thenReturn(scheduler);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getString("orderId")).thenReturn(order.getId().toHexString());
		when(jobDataMap.getString("userId")).thenReturn(user.getId().toHexString());
		when(orderDAO.read(order.getId().toHexString())).thenThrow(new EpickurException());
		when(orderDAO.update(order)).thenReturn(order);
		when(userDAO.read(user.getId().toHexString())).thenReturn(user);

		orderJob.execute(context);

		verify(orderDAO, times(1)).read(order.getId().toHexString());
		verify(userDAO, never()).read(user.getId().toHexString());
		verify(emailUtils, never()).emailCancelOrder(user, order);
		verify(order, never()).setStatus(any(OrderStatus.class));
		verify(order, never()).setReadableId(anyObject());
		verify(order, never()).setCreatedAt(anyObject());
		verify(order, never()).setUpdatedAt(anyObject());
		verify(order, never()).prepareForUpdateIntoDB();
	}
}
