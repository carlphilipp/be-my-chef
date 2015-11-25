package com.epickur.api.cron;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;

@RunWith(MockitoJUnitRunner.class)
public final class CleanVouchersJobTest {

	@Mock
	private JobExecutionContext context;
	@Mock
	private VoucherService voucherBusiness;
	@InjectMocks
	private CleanVouchersJob voucherJob;

	@Test
	public void testExecute() throws JobExecutionException, EpickurException {
		voucherJob.execute(context);
		
		verify(voucherBusiness, times(1)).clean();
	}
	
	@Test
	public void testExecuteException() throws JobExecutionException, EpickurException {
		when(voucherBusiness.clean()).thenThrow(new EpickurException());
		
		voucherJob.execute(context);
		
		verify(voucherBusiness, times(1)).clean();
	}
}
