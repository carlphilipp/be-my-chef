package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.VoucherService;

public class VoucherServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private VoucherDAO voucherDAO;
	@InjectMocks
	private VoucherService voucherBusiness;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testRead() throws EpickurException{
		Voucher voucher = EntityGenerator.generateVoucher();
		
		when(voucherDAO.read(anyString())).thenReturn(voucher);
		
		Voucher actual = voucherBusiness.read(EntityGenerator.generateRandomString());
		assertNotNull(actual);
	}
	
	@Test
	public void testGenerate() throws EpickurException{
		when(voucherDAO.read(anyString())).thenReturn(null);
		
		int count = 10;
		Set<Voucher> actuals = voucherBusiness.generate(count, DiscountType.AMOUNT, 15, ExpirationType.ONETIME, new DateTime());
		assertNotNull(actuals);
		assertEquals(count, actuals.size());
	}

	@Test
	public void testClean() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		List<Voucher> vouchers = new ArrayList<>();
		vouchers.add(voucher);
		
		when(voucherDAO.readToClean()).thenReturn(vouchers);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(null);
		
		List<Voucher> actuals = voucherBusiness.clean();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		assertEquals(Status.EXPIRED, actuals.get(0).getStatus());
	}

	@Test
	public void testValidate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfter = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfter.setStatus(Status.VALID);
		voucherAfter.setExpirationType(ExpirationType.ONETIME);
		
		when(voucherDAO.read(voucher.getCode())).thenReturn(voucherAfter);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(voucherAfter);
		
		Voucher actual = voucherBusiness.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
	}

	@Test
	public void testValidateVoucherNotFound() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid.toString() + "' not found");
		
		when(voucherDAO.read(uuid.toString())).thenReturn(null);
		
		voucherBusiness.validateVoucher(uuid.toString());
	}

	@Test
	public void testValidateVoucherExpired() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid.toString() + "' expired");
		
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfter = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfter.setStatus(Status.EXPIRED);
		
		when(voucherDAO.read(uuid.toString())).thenReturn(voucherAfter);
		
		Voucher actual = voucherBusiness.validateVoucher(uuid.toString());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
	}

	@Test
	public void testValidateUntil() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterRead = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterRead.setStatus(Status.VALID);
		voucherAfterRead.setExpirationType(ExpirationType.UNTIL);
		voucherAfterRead.setDiscountType(DiscountType.AMOUNT);
		Voucher voucherAfterUpdate = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterUpdate.setStatus(Status.VALID);
		voucherAfterUpdate.setUsedCount(1);
		DateTime now = new DateTime();
		voucherAfterUpdate.setCreatedAt(now);
		voucherAfterUpdate.setUpdatedAt(now);
		
		when(voucherDAO.read(voucher.getCode())).thenReturn(voucherAfterRead);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(voucherAfterUpdate);
		
		Voucher actual = voucherBusiness.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(1, actual.getUsedCount().intValue());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
	}
	
	@Test
	public void testValidateUntil2() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterCreate = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterCreate.setStatus(Status.VALID);
		voucherAfterCreate.setExpirationType(ExpirationType.UNTIL);
		voucherAfterCreate.setDiscountType(DiscountType.AMOUNT);
		Voucher voucherAfterUpdate = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterUpdate.setStatus(Status.VALID);
		voucherAfterUpdate.setUsedCount(1);
		DateTime now = new DateTime();
		voucherAfterUpdate.setCreatedAt(now);
		voucherAfterUpdate.setUpdatedAt(now);
		voucherAfterUpdate.setUsedCount(4);
		
		when(voucherDAO.read(voucher.getCode())).thenReturn(voucherAfterCreate);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(voucherAfterUpdate);
		
		Voucher actual = voucherBusiness.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(4, actual.getUsedCount().intValue());
		assertNotNull(actual.getCreatedAt());
		assertNotNull(actual.getUpdatedAt());
	}
	
	@Test
	public void testRevertOneTime() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterRead = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterRead.setExpirationType(ExpirationType.ONETIME);
		Voucher voucherAfterUpdate = EntityGenerator.mockVoucherAfterCreate(voucherAfterRead);
		voucherAfterUpdate.setStatus(Status.VALID);
		voucherAfterUpdate.setUsedCount(0);
		
		when(voucherDAO.read(voucher.getCode())).thenReturn(voucherAfterRead);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(voucherAfterUpdate);
	
		Voucher actual = voucherBusiness.revertVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(0, actual.getUsedCount().intValue());
	}
	
	@Test
	public void testRevertUntil() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfterRead = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfterRead.setExpirationType(ExpirationType.UNTIL);
		voucherAfterRead.setUsedCount(10);
		Voucher voucherAfterUpdate = EntityGenerator.mockVoucherAfterCreate(voucherAfterRead);
		voucherAfterUpdate.setStatus(Status.VALID);
		voucherAfterUpdate.setUsedCount(9);
		
		when(voucherDAO.read(voucher.getCode())).thenReturn(voucherAfterRead);
		when(voucherDAO.update((Voucher) anyObject())).thenReturn(voucherAfterUpdate);
	
		Voucher actual = voucherBusiness.revertVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(9, actual.getUsedCount().intValue());
	}
}