package com.epickur.api.cron;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionException;

@RunWith(MockitoJUnitRunner.class)
public final class CleanVouchersJobTest {

	@Mock
	private VoucherDAO voucherDAO;
	@Mock
	private VoucherService voucherService;
	@InjectMocks
	private CleanVouchersJob voucherJob;

	@Test
	public void testExecute() throws JobExecutionException, EpickurException {
		voucherJob.execute();

		// TODO test not good enough
	}
}
