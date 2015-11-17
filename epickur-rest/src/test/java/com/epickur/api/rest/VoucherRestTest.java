package com.epickur.api.rest;

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
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.Utils;

public class VoucherRestTest {

	@Mock
	private VoucherService voucherBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private VoucherRest voucherService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);
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
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);

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
