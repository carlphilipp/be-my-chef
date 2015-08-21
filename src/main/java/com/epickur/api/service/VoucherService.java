package com.epickur.api.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.VoucherBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
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
public final class VoucherService {

	/** User Business */
	private VoucherBusiness voucherBusiness;
	/** User validator */
	private VoucherValidator validator;

	/**
	 * Construct a voucher service
	 */
	public VoucherService() {
		this.voucherBusiness = new VoucherBusiness();
		this.validator = (VoucherValidator) FactoryValidator.getValidator("voucher");
	}
	
	@GET
	@Path("/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("code") final String code, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.VOUCHER);
		validator.checkVoucher(code);
		Voucher voucher = voucherBusiness.read(code);
		if (voucher == null) {
			return ErrorUtils.notFound(ErrorUtils.VOUCHER_NOT_FOUND, code);
		} else {
			return Response.ok().entity(voucher).build();
		}
	}
	//GET <epickur-api>/vouchers/generate?count=12&discountType=amount&discount=5&expirationType=oneTime
	//GET <epickur-api>/vouchers/generate?count=6&discountType=amount&discount=10&expirationType=until&expirationDate=YYYYMMDD
	@GET
	@Path("/generate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generate(	
			@QueryParam("count") final String count,
			@QueryParam("discountType") final String discountType,
			@QueryParam("discount") final String discount, 
			@QueryParam("expirationType") final String expirationType,
			@QueryParam("expirationDate") final String expirationDate,
			@Context final ContainerRequestContext context) throws EpickurException {
		return null;
	}
}
