package com.epickur.api.rest;

import com.epickur.api.annotation.ValidateSimpleAccessRights;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import com.epickur.api.web.ResponseError;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

import static com.epickur.api.enumeration.EndpointType.VOUCHER;
import static com.epickur.api.enumeration.Operation.GENERATE_VOUCHER;
import static com.epickur.api.enumeration.Operation.READ;

/**
 * JAX-RS Voucher Service
 *
 * @author cph
 * @version 1.0
 */
@AllArgsConstructor(onConstructor = @_(@Autowired))
@RestController
@RequestMapping(value = "/vouchers")
public class VoucherController {

	@NonNull
	private HttpServletRequest context;
	@NonNull
	private VoucherService voucherService;
	@NonNull
	private Utils utils;

	// @formatter:off
	/**
	 * @api {get} /vouchers/:code Get a voucher
	 * @apiVersion 1.0.0
	 * @apiName GetVoucher
	 * @apiGroup Vouchers
	 * @apiDescription Get Voucher details.
	 * @apiPermission admin, super_user, user, website
	 *
	 * @apiParam (Request: URL Parameter) {String} code Voucher code.
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *      "id": "55d8aa258b5d5d2f94236e97",
	 *      "code": "KJS8SCT5",
	 *      "discount": 5,
	 *      "discountType" : "amount",
	 *      "expirationType" : "onetime",
	 *      "status" : "valid",
	 *      "usedCount" : 0,
	 *      "createdAt" : 1440262693104,
	 *      "updatedAt" : 1440262693104
	 * }
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param code The voucher code
	 * @return A response
	 * @throws EpickurException If an EpickurException occured
	 */
	@ValidateSimpleAccessRights(operation = READ, endpoint = VOUCHER)
	@RequestMapping(value = "/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> read(@PathVariable("code") final String code) throws EpickurException {
		final Optional<Voucher> voucher = voucherService.read(code);
		return voucher.isPresent()
			? new ResponseEntity<>(voucher.get(), HttpStatus.OK)
			: ResponseError.notFound(ErrorConstants.VOUCHER_NOT_FOUND, code);
	}

	// @formatter:off
	/**
	 * @api {get} /vouchers/generate?count=:c&discountType=:dt&discount=:d&expirationType=:et&expiration=:e&format=:f Generate vouchers
	 * @apiVersion 1.0.0
	 * @apiName GenerateVoucher
	 * @apiGroup Vouchers
	 * @apiDescription Generate a list of vouchers.
	 * @apiPermission admin
	 *
	 * @apiParam (Request: URL Parameter) {Integer} count Number of voucher to generate.
	 * @apiParam (Request: URL Parameter) {String} discountType Discount type. Can be amount or percentage
	 * @apiParam (Request: URL Parameter) {Double} discount Discount amount.
	 * @apiParam (Request: URL Parameter) {Double} expirationType Expiration type. Can be onetime or until
	 * @apiParam (Request: URL Parameter) {Date} expiration Expiration date. (not mandatory)
	 * @apiParam (Request: URL Parameter) {String} format Format date. Default is MM/dd/yyyy. (not mandatory)
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * [{
	 *      "id": "55d8aa258b5d5d2f94236e97",
	 *      "code": "KJS8SCT5",
	 *      "discount": 5,
	 *      "discountType" : "amount",
	 *      "expirationType" : "onetime",
	 *      "status" : "valid",
	 *      "usedCount" : 0,
	 *      "createdAt" : 1440262693104,
	 *      "updatedAt" : 1440262693104
	 * }
	 * {
	 *      "id": "55d8aa258b5d5d2f94236e97",
	 *      "code": "KJS8SCT7",
	 *      "discount": 5,
	 *      "discountType" : "amount",
	 *      "expirationType" : "onetime",
	 *      "status" : "valid",
	 *      "usedCount" : 0,
	 *      "createdAt" : 1440262693104,
	 *      "updatedAt" : 1440262693104
	 * }]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param count          The number of voucher we want to generate
	 * @param discountType   The discount type. Can be amount or percentage
	 * @param discount       The actual discount
	 * @param expirationType The expiraton type. Can be onetime or until
	 * @param expiration     The expiration date. Only relevant if expiration type is until
	 * @param format         The expiration date format. If not provided MM/dd/yyyy
	 * @return The response
	 * @throws EpickurException If an EpickurException occured
	 */
	@ValidateSimpleAccessRights(operation = GENERATE_VOUCHER, endpoint = VOUCHER)
	@RequestMapping(value = "/generate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> generate(
		@RequestParam("count") @NotBlank(message = "{voucher.generate.count.blank}") @Min(value = 0, message = "{voucher.generate.count.positive}") final Integer count,
		@RequestParam("discountType") @NotBlank(message = "{voucher.generate.discounttype}") final DiscountType discountType,
		@RequestParam("discount") @NotBlank(message = "{voucher.generate.discount.blank}") @Min(value = 0, message = "{voucher.generate.discount.positive}") final Integer discount,
		@RequestParam("expirationType") @NotNull(message = "{voucher.generate.expirationtype}") final ExpirationType expirationType,
		@RequestParam("expiration") final String expiration,
		@RequestParam(value = "formatDate", required = false, defaultValue = "MM/dd/yyyy") final String format) throws EpickurException {
		DateTime date = null;
		if (expiration != null) {
			date = utils.parseDate(expiration, format);
		}
		final Set<Voucher> vouchers = voucherService.generate(count, discountType, discount, expirationType, date);
		return new ResponseEntity<>(vouchers, HttpStatus.OK);
	}
}
