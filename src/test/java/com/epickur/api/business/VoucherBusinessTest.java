package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.dao.mongo.VoucherDAOImpl;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;

public class VoucherBusinessTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static VoucherBusiness business;
	private static VoucherDAOImpl dao;

	@BeforeClass
	public static void beforeClass() {
		business = new VoucherBusiness();
		dao = new VoucherDAOImpl();
		dao.deleteAll();
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		dao.deleteAll();
	}

	@Before
	public void before() {
		dao.deleteAll();
	}

	@Test
	public void testClean() throws EpickurException {
		DateTime date = new DateTime();
		date = date.minusWeeks(1);
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.UNTIL, date);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		business.clean();
		Voucher actual = business.read(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
	}

	@Test
	public void testValidate1() throws EpickurException {
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.ONETIME, null);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		Voucher actual = business.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
	}

	@Test
	public void testValidate2() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid.toString() + "' not found");
		business.validateVoucher(uuid.toString());
	}

	@Test
	public void testValidate3() throws EpickurException {
		thrown.expect(EpickurException.class);
		DateTime date = new DateTime();
		date = date.minusWeeks(1);
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.UNTIL, date);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		business.clean();
		Voucher actual = business.read(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());

		thrown.expectMessage("Voucher '" + actual.getCode() + "' expired");
		business.validateVoucher(actual.getCode());
	}

	@Test
	public void testValidate4() throws EpickurException {
		DateTime date = new DateTime();
		date = date.plusWeeks(1);
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.UNTIL, date);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		Voucher actual = business.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(1, actual.getUsedCount().intValue());
	}

	@Test
	public void testValidate5() throws EpickurException {
		DateTime date = new DateTime();
		date = date.plusWeeks(1);
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.UNTIL, date);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		Voucher actual = business.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(1, actual.getUsedCount().intValue());

		Voucher actual2 = business.validateVoucher(voucher.getCode());
		assertNotNull(actual2);
		assertEquals(Status.VALID, actual2.getStatus());
		assertEquals(2, actual2.getUsedCount().intValue());
	}
	
	@Test
	public void testRevert1() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid + "' not found");
		business.revertVoucher(uuid.toString());
	}
	
	@Test
	public void testRevert2() throws EpickurException {
		DateTime date = new DateTime();
		date = date.plusWeeks(1);
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.UNTIL, date);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		Voucher actual = business.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(1, actual.getUsedCount().intValue());
		
		Voucher actual2 = business.revertVoucher(voucher.getCode());
		assertNotNull(actual2);
		assertEquals(Status.VALID, actual2.getStatus());
		assertEquals(0, actual2.getUsedCount().intValue());
	}
	
	@Test
	public void testRevert3() throws EpickurException {
		Set<Voucher> vouchers = business.generate(1, DiscountType.AMOUNT, 5, ExpirationType.ONETIME, null);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		Voucher actual = business.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
		
		Voucher actual2 = business.revertVoucher(voucher.getCode());
		assertNotNull(actual2);
		assertEquals(Status.VALID, actual2.getStatus());
	}
}
