package com.epickur.api.rest;

import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
import com.epickur.api.validator.AccessRights;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.VoucherValidator;

/**
 * JAX-RS Voucher Service
 * 
 * @author cph
 * @version 1.0
 *
 */
@Path("/vouchers")
public final class VoucherController {

	/** Context */
	@Context
	private ContainerRequestContext context;
	/** User Service */
	private VoucherService voucherService;
	/** User validator */
	private VoucherValidator validator;

	/**
	 * Construct a voucher service
	 */
	public VoucherController() {
		this.voucherService = new VoucherService();
		this.validator = (VoucherValidator) FactoryValidator.getValidator("voucher");
	}

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
	 * @param code
	 *            The voucher code
	 * @return A response
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	@GET
	@Path("/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("code") final String code) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.VOUCHER);
		validator.checkVoucherCode(code);
		Voucher voucher = voucherService.read(code);
		if (voucher == null) {
			return ErrorUtils.notFound(ErrorUtils.VOUCHER_NOT_FOUND, code);
		} else {
			return Response.ok().entity(voucher).build();
		}
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
	 * @param count
	 *            The number of voucher we want to generate
	 * @param discountType
	 *            The discount type. Can be amount or percentage
	 * @param discount
	 *            The actual discount
	 * @param expirationType
	 *            The expiraton type. Can be onetime or until
	 * @param expiration
	 *            The expiration date. Only relevant if expiration type is until
	 * @param format
	 *            The expiration date format. If not provided MM/dd/yyyy
	 * @return The response
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	@GET
	@Path("/generate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generate(
			@QueryParam("count") @NotBlank(message = "{voucher.generate.count.blank}") @Min(value = 0, message = "{voucher.generate.count.positive}") final Integer count,
			@QueryParam("discountType") @NotBlank(message = "{voucher.generate.discounttype}") final DiscountType discountType,
			@QueryParam("discount") @NotBlank(message = "{voucher.generate.discount.blank}") @Min(value = 0, message = "{voucher.generate.discount.positive}") final Integer discount,
			@QueryParam("expirationType") @NotNull(message = "{voucher.generate.expirationtype}") final ExpirationType expirationType,
			@QueryParam("expiration") final String expiration,
			@DefaultValue("MM/dd/yyyy") @QueryParam("formatDate") final String format) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.GENERATE_VOUCHER, EndpointType.VOUCHER);
		validator.checkVoucherGenerate(expirationType, expiration, format);
		DateTime date = null;
		if (expiration != null) {
			date = Utils.parseDate(expiration, format);
		}
		Set<Voucher> vouchers = voucherService.generate(count, discountType, discount, expirationType, date);
		return Response.ok().entity(vouchers).build();
	}
}