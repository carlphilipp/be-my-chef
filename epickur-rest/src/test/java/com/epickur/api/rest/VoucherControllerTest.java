package com.epickur.api.rest;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.Utils;
import com.epickur.api.validator.VoucherValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class VoucherControllerTest {

	@Mock
	private VoucherService voucherBusiness;
	@Mock
	private HttpServletRequest context;
	@Mock
	private VoucherValidator validator;
	@Mock
	private Utils utils;
	@InjectMocks
	private VoucherController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
		when(context.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);
		Set<Voucher> vouchers = new HashSet<>();
		vouchers.add(voucherAfterCreate);

		when(voucherBusiness.generate(anyInt(), anyObject(), anyInt(), anyObject(), anyObject()))
				.thenReturn(vouchers);

		ResponseEntity<?> actual = controller.generate(1, DiscountType.AMOUNT, 1, ExpirationType.ONETIME, "05/05/2020", "MM/dd/yyyy");
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Set<Voucher> actualVoucher = (Set<Voucher>) actual.getBody();
		assertNotNull(actualVoucher);
		assertEquals(1, actualVoucher.size());
	}

	@Test
	public void testRead() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);

		when(voucherBusiness.read(anyString())).thenReturn(voucherAfterCreate);

		ResponseEntity<?> actual = controller.read(CommonsUtil.generateRandomCode());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Voucher actualVoucher = (Voucher) actual.getBody();
		assertNotNull(actualVoucher);
		assertNotNull(actualVoucher.getCode());
	}

	@Test
	public void testReadVoucherNotFound() throws EpickurException {
		when(voucherBusiness.read(anyString())).thenReturn(null);

		ResponseEntity<?> actual = controller.read(CommonsUtil.generateRandomCode());
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}
}
