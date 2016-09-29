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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.*;

import java.util.Optional;

import static org.mockito.Mockito.*;

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
		when(orderDAO.read(order.getId().toHexString())).thenReturn(Optional.of(order));
		when(orderDAO.update(order)).thenReturn(order);
		when(userDAO.read(user.getId().toHexString())).thenReturn(Optional.of(user));
		when(voucherDAO.read(anyString())).thenReturn(Optional.of(voucher));

		orderJob.execute(context);

		verify(orderDAO).read(order.getId().toHexString());
		verify(userDAO).read(user.getId().toHexString());
		verify(emailUtils).emailCancelOrder(user, order);
		verify(order).setStatus(OrderStatus.CANCELED);
		verify(order).setReadableId(null);
		verify(order).setCreatedAt(null);
		verify(order).setUpdatedAt(any(DateTime.class));
		verify(order).prepareForUpdateIntoDB();
		//verify(voucherBusiness).revertVoucher(voucher.getCode());
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
		when(userDAO.read(user.getId().toHexString())).thenReturn(Optional.of(user));

		orderJob.execute(context);

		verify(orderDAO).read(order.getId().toHexString());
		verify(userDAO, never()).read(user.getId().toHexString());
		verify(emailUtils, never()).emailCancelOrder(user, order);
		verify(order, never()).setStatus(any(OrderStatus.class));
		verify(order, never()).setReadableId(anyString());
		verify(order, never()).setCreatedAt(isA(DateTime.class));
		verify(order, never()).setUpdatedAt(isA(DateTime.class));
		verify(order, never()).prepareForUpdateIntoDB();
	}
}
