package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.business.VoucherBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Utils;

public class VoucherServiceTest extends InitMocks {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private VoucherService voucherService;
	@Mock
	private VoucherBusiness voucherBusiness;
	@Mock
	private ContainerRequestContext context;

	@Before
	public void setUp() {
		this.voucherService = new VoucherService(voucherBusiness, context);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate() throws EpickurException {
		Voucher voucher = TestUtils.generateVoucher();
		Voucher voucherAfterCreate = TestUtils.mockVoucherAfterCreate(voucher);
		Set<Voucher> vouchers = new HashSet<Voucher>();
		vouchers.add(voucherAfterCreate);

		when(voucherBusiness.generate(anyInt(), (DiscountType) anyObject(), anyInt(), (ExpirationType) anyObject(), (DateTime) anyObject()))
				.thenReturn(vouchers);

		Response actual = voucherService.generate(1, DiscountType.AMOUNT, 1, ExpirationType.ONETIME, "05/05/2020", "MM/dd/yyyy");
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Set<Voucher> actualVoucher = (Set<Voucher>) actual.getEntity();
		assertNotNull(actualVoucher);
		assertEquals(1, actualVoucher.size());
	}

	@Test
	public void testRead() throws EpickurException {
		Voucher voucher = TestUtils.generateVoucher();
		Voucher voucherAfterCreate = TestUtils.mockVoucherAfterCreate(voucher);

		when(voucherBusiness.read(anyString())).thenReturn(voucherAfterCreate);

		Response actual = voucherService.read(Utils.generateRandomCode());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Voucher actualVoucher = (Voucher) actual.getEntity();
		assertNotNull(actualVoucher);
		assertNotNull(actualVoucher.getCode());
	}
	
	@Test
	public void testReadVoucherNotFound() throws EpickurException {
		when(voucherBusiness.read(anyString())).thenReturn(null);

		Response actual = voucherService.read(Utils.generateRandomCode());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}
}
