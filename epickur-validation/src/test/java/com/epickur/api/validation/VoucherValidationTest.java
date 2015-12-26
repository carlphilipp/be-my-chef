package com.epickur.api.validation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurParsingException;

/**
 * @author cph
 * @version 1.0
 *
 */
public class VoucherValidationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckVouchValidator() {
		VoucherValidation validator = new VoucherValidation();
		String code = "KJS8SCT5";
		validator.checkVoucherCode(code);
	}

	@Test
	public void testCheckVouchValidator2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field voucher.code has a wrong format");

		VoucherValidation validator = new VoucherValidation();
		String code = "KJS8SCT5T";
		validator.checkVoucherCode(code);
	}

	@Test
	public void testCheckVouchValidator3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field voucher.code has a wrong format");

		VoucherValidation validator = new VoucherValidation();
		String code = "KJS8SCTY";
		validator.checkVoucherCode(code);
	}

	@Test
	public void testCheckVouchValidator4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field voucher.code has a wrong format");

		VoucherValidation validator = new VoucherValidation();
		String code = "KJS8ACT6";
		validator.checkVoucherCode(code);
	}

	@Test
	public void testCheckVouchValidator5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field voucher.code is not allowed to be null or empty");

		VoucherValidation validator = new VoucherValidation();
		validator.checkVoucherCode(null);
	}

	@Test
	public void testCheckVouchValidator6() throws EpickurParsingException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The param expirationDate is not allowed to be null or empty when expirationType is until");

		VoucherValidation validator = new VoucherValidation();
		ExpirationType expirationType = ExpirationType.UNTIL;
		String format = "MM/dd/yyyy";
		validator.checkVoucherGenerate(expirationType, null, format);
	}

	@Test
	public void testCheckVouchValidator7() throws EpickurParsingException {
		thrown.expect(EpickurParsingException.class);
		thrown.expectMessage("Error while parsing date 'derp' with format 'MM/dd/yyyy'");

		VoucherValidation validator = new VoucherValidation();
		ExpirationType expirationType = ExpirationType.UNTIL;
		String date = "derp";
		String format = "MM/dd/yyyy";
		validator.checkVoucherGenerate(expirationType, date, format);
	}

	@Test
	public void testCheckVouchValidator8() throws EpickurParsingException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("No voucher has been provided");

		VoucherValidation validator = new VoucherValidation();
		validator.checkVoucher(null, null);
	}

	@Test
	public void testCheckVouchValidator9() throws EpickurParsingException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field voucher.code is not allowed to be null or empty");

		VoucherValidation validator = new VoucherValidation();
		Voucher voucher = new Voucher();
		validator.checkVoucher(voucher, null);
	}

	@Test
	public void testCheckVouchValidator10() throws EpickurParsingException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.voucher.code is not allowed to be null or empty");

		VoucherValidation validator = new VoucherValidation();
		Voucher voucher = new Voucher();
		validator.checkVoucher(voucher, "order");
	}
}
