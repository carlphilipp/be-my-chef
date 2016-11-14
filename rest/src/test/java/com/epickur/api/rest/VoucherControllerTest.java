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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class VoucherControllerTest {

	@Mock
	private VoucherService voucherBusiness;
	@Mock
	private HttpServletRequest context;
	@Mock
	private Utils utils;
	@InjectMocks
	private VoucherController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
		given(context.getContentType()).willReturn(MediaType.APPLICATION_JSON_VALUE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate() throws EpickurException {
		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);
		Set<Voucher> vouchers = new HashSet<>();
		vouchers.add(voucherAfterCreate);
		given(voucherBusiness.generate(anyInt(), isA(DiscountType.class), anyInt(), isA(ExpirationType.class), anyObject())).willReturn(vouchers);

		// When
		ResponseEntity<?> actual = controller.generate(1, DiscountType.AMOUNT, 1, ExpirationType.ONETIME, "05/05/2020", "MM/dd/yyyy");

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Set<Voucher> actualVoucher = (Set<Voucher>) actual.getBody();
		assertNotNull(actualVoucher);
		assertEquals(1, actualVoucher.size());
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);
		given(voucherBusiness.read(anyString())).willReturn(Optional.of(voucherAfterCreate));

		// When
		ResponseEntity<?> actual = controller.read(CommonsUtil.generateRandomCode());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Voucher actualVoucher = (Voucher) actual.getBody();
		assertNotNull(actualVoucher);
		assertNotNull(actualVoucher.getCode());
	}

	@Test
	public void testReadVoucherNotFound() throws EpickurException {
		// Given
		given(voucherBusiness.read(anyString())).willReturn(Optional.empty());

		// When
		ResponseEntity<?> actual = controller.read(CommonsUtil.generateRandomCode());

		// Then
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}
}
