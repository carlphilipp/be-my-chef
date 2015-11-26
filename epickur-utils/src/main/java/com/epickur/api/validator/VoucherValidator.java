package com.epickur.api.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.Utils;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class VoucherValidator extends Validator {

	/**
	 * Construct a voucher validator
	 */
	public VoucherValidator() {
		super("voucher");
	}

	/**
	 * Check vouchers
	 * 
	 * @param voucher
	 *            The voucher
	 * @param prefix
	 *            The prefix
	 */
	public void checkVoucher(final Voucher voucher, final String prefix) {
		String entity = getEntity();
		if (prefix != null) {
			entity = prefix + "." + entity;
		}
		if (voucher == null) {
			throw new EpickurIllegalArgument(NO_VOUCHER_PROVIDED);
		}
		if (StringUtils.isBlank(voucher.getCode())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "code"));
		}
	}

	/**
	 * @param code
	 *            The voucher code
	 */
	public void checkVoucherCode(final String code) {
		if (StringUtils.isBlank(code)) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "code"));
		}
		if (code.length() != 8) {
			throw new EpickurIllegalArgument("The field " + getEntity() + ".code has a wrong format");
		}
		String consonants = "[QWRTPSDFGHJKZXCVBNM]{3}";
		String regex = "^" + consonants + "[2-9]" + consonants + "[2-9]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(code);
		if (!matcher.matches()) {
			throw new EpickurIllegalArgument("The field " + getEntity() + ".code has a wrong format '" + code + "'");
		}
	}

	/**
	 * Check data when generate vouchers
	 * 
	 * @param expirationType
	 *            The expiration type
	 * @param expirationDate
	 *            The expiration date
	 * @param format
	 *            The date format
	 * @throws EpickurParsingException
	 *             If a an epickur parsinf exception occures
	 */
	public void checkVoucherGenerate(
			final ExpirationType expirationType,
			final String expirationDate,
			final String format) throws EpickurParsingException {
		if (expirationType.equals(ExpirationType.UNTIL)) {
			if (StringUtils.isBlank(expirationDate)) {
				throw new EpickurIllegalArgument("The param expirationDate is not allowed to be null or empty when expirationType is until");
			} else {
				if (expirationDate != null) {
					Utils.parseDate(expirationDate, format);
				}
			}
		}
	}
}