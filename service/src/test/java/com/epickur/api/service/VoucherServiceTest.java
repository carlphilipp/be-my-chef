package com.epickur.api.service;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class VoucherServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private VoucherDAO voucherDAO;
	@InjectMocks
	private VoucherService voucherService;

	@Test
	public void testRead() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();

		given(voucherDAO.read(any(String.class))).willReturn(Optional.of(voucher));

		Optional<Voucher> actual = voucherService.read(EntityGenerator.generateRandomString());
		assertTrue(actual.isPresent());
	}


	@Test
	public void testGenerate() throws EpickurException {
		given(voucherDAO.read(any(String.class))).willReturn(Optional.empty());

		int count = 10;
		Set<Voucher> actuals = voucherService.generate(count, DiscountType.AMOUNT, 15, ExpirationType.ONETIME, new DateTime());
		assertNotNull(actuals);
		assertEquals(count, actuals.size());
	}

	@Test
	public void testValidate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfter = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfter.setStatus(Status.VALID);
		voucherAfter.setExpirationType(ExpirationType.ONETIME);
		given(voucherDAO.read(voucher.getCode())).willReturn(Optional.of(voucherAfter));
		given(voucherDAO.update(any(Voucher.class))).willReturn(voucherAfter);

		Voucher actual = voucherService.validateVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.EXPIRED, actual.getStatus());
	}

	@Test
	public void testValidateVoucherNotFound() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid.toString() + "' not found");

		given(voucherDAO.read(uuid.toString())).willReturn(Optional.empty());

		voucherService.validateVoucher(uuid.toString());
	}

	@Test
	public void testValidateVoucherExpired() throws EpickurException {
		thrown.expect(EpickurException.class);
		UUID uuid = UUID.randomUUID();
		thrown.expectMessage("Voucher '" + uuid.toString() + "' expired");

		Voucher voucher = EntityGenerator.generateVoucher();
		Voucher voucherAfter = EntityGenerator.mockVoucherAfterCreate(voucher);
		voucherAfter.setStatus(Status.EXPIRED);

		given(voucherDAO.read(uuid.toString())).willReturn(Optional.of(voucherAfter));

		Voucher actual = voucherService.validateVoucher(uuid.toString());
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

		given(voucherDAO.read(voucher.getCode())).willReturn(Optional.of(voucherAfterRead));
		given(voucherDAO.update(any(Voucher.class))).willReturn(voucherAfterUpdate);

		Voucher actual = voucherService.validateVoucher(voucher.getCode());
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

		given(voucherDAO.read(voucher.getCode())).willReturn(Optional.of(voucherAfterCreate));
		given(voucherDAO.update(any(Voucher.class))).willReturn(voucherAfterUpdate);

		Voucher actual = voucherService.validateVoucher(voucher.getCode());
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

		given(voucherDAO.read(voucher.getCode())).willReturn(Optional.of(voucherAfterRead));
		given(voucherDAO.update(any(Voucher.class))).willReturn(voucherAfterUpdate);

		Voucher actual = voucherService.revertVoucher(voucher.getCode());
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

		given(voucherDAO.read(voucher.getCode())).willReturn(Optional.of(voucherAfterRead));
		given(voucherDAO.update(any(Voucher.class))).willReturn(voucherAfterUpdate);

		Voucher actual = voucherService.revertVoucher(voucher.getCode());
		assertNotNull(actual);
		assertEquals(Status.VALID, actual.getStatus());
		assertEquals(9, actual.getUsedCount().intValue());
	}
}
