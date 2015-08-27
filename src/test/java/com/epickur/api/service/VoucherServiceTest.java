package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;

/**
 * @author cph
 * @version 1.0
 *
 */
public class VoucherServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static VoucherService voucherService;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		Mockito.when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		voucherService = new VoucherService();
	}

	@Before
	public void before() {
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		Mockito.when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate() throws EpickurException {
		Response result = voucherService.generate(5, DiscountType.AMOUNT, 1600, ExpirationType.ONETIME, null, null, context);
		if (result.getEntity() != null) {
			Set<Voucher> vouchers = (HashSet<Voucher>) result.getEntity();
			assertEquals(5, vouchers.size());
			Iterator<Voucher> iterator = vouchers.iterator();
			while (iterator.hasNext()) {
				Voucher voucher = iterator.next();
				assertEquals(DiscountType.AMOUNT, voucher.getDiscountType());
				assertEquals(new Integer(1600), voucher.getDiscount());
				assertEquals(ExpirationType.ONETIME, voucher.getExpirationType());
			}
		} else {
			fail("Voucher generation failed");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerate2() throws EpickurException {
		Response result = voucherService.generate(15, DiscountType.PERCENTAGE, 1600, ExpirationType.UNTIL, "05/05/2030", "MM/dd/yyyy", context);
		if (result.getEntity() != null) {
			Set<Voucher> vouchers = (HashSet<Voucher>) result.getEntity();
			assertEquals(15, vouchers.size());
			Iterator<Voucher> iterator = vouchers.iterator();
			while (iterator.hasNext()) {
				Voucher voucher = iterator.next();
				assertEquals(DiscountType.PERCENTAGE, voucher.getDiscountType());
				assertEquals(new Integer(1600), voucher.getDiscount());
				assertEquals(ExpirationType.UNTIL, voucher.getExpirationType());
				assertEquals(new Integer(0), voucher.getUsedCount());
				assertEquals(new DateTime(2030, 5, 5, 0, 0), voucher.getExpiration());
			}
		} else {
			fail("Voucher generation failed");
		}
	}

	@Test
	public void testGenerate3() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);
		thrown.expectMessage("GENERATE_VOUCHER is not allowed with role USER on VOUCHER endpoint");

		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomUserKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		Mockito.when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);

		voucherService.generate(15, DiscountType.PERCENTAGE, 16, ExpirationType.UNTIL, "05/05/2030", "MM/dd/yyyy", context);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRead() throws EpickurException {
		Response result = voucherService.generate(1, DiscountType.AMOUNT, 1600, ExpirationType.ONETIME, null, null, context);
		if (result.getEntity() != null) {
			Set<Voucher> vouchers = (HashSet<Voucher>) result.getEntity();
			Voucher voucher = vouchers.iterator().next();
			Response result2 = voucherService.read(voucher.getCode(), context);
			if (result2.getEntity() != null) {
				Voucher voucher2 = (Voucher) result2.getEntity();
				assertEquals(voucher.getCode(), voucher2.getCode());
			} else {
				fail("Voucher read failed");
			}
		} else {
			fail("Voucher generation failed");
		}
	}
}
