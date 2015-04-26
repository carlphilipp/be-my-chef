package com.epickur.api.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.FactoryValidator;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * JAX-RS Caterer service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/caterers")
public final class CatererService {

	/** Caterer Business **/
	private CatererBusiness catererBusiness;
	/** Order Business **/
	private OrderBusiness orderBusiness;
	/** Caterer validator **/
	private CatererValidator validator;

	/** Constructor **/
	public CatererService() {
		this.catererBusiness = new CatererBusiness();
		this.orderBusiness = new OrderBusiness();
		this.validator = (CatererValidator) FactoryValidator.getValidator("caterer");
	}

	// @formatter:off
	/**
	 * @api {post} /caterers Create a new Caterer
	 * @apiVersion 1.0.0
	 * @apiName CreateCaterer
	 * @apiGroup Caterers
	 * @apiDescription Create a Caterer.
	 * @apiPermission admin only
	 * 
	 * 
	 * @apiParam (Request: JSON Object) {String} name Name of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 * @apiParam (Request: JSON Object) {String} description Description of the Caterer.
	 * @apiParam (Request: JSON Object) {String} manager Manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "Adress Label",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param caterer
	 *            The Caterer
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final Caterer caterer, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.CREATE);
		validator.checkCreateCaterer(caterer);
		caterer.setCreatedBy(key.getUserId());
		Caterer result = catererBusiness.create(caterer);
		return Response.ok().entity(result).build();
	}

	// @formatter:off
	/**
	 * @api {get} /caterers/:id Read a Caterer
	 * @apiVersion 1.0.0
	 * @apiName GetCaterer
	 * @apiGroup Caterers
	 * @apiDescription Read a Caterer.
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Caterer id
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") final String id) throws EpickurException {
		validator.checkId(id);
		Caterer caterer = catererBusiness.read(id);
		if (caterer == null) {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		} else {
			return Response.ok().entity(caterer).build();
		}
	}

	// @formatter:off
	/**
	 * @api {put} /caterers/:id Update a Caterer
	 * @apiVersion 1.0.0
	 * @apiName UpdateCaterer
	 * @apiGroup Caterers
	 * @apiDescription Update a Caterer with the data provided in the request.
	 * @apiPermission admin, super_user (own caterer)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 * 
	 * @apiParam (Request: JSON Object) {String} id Id of the Caterer.
	 * @apiParam (Request: JSON Object) {String} name Name of the Caterer.
	 * @apiParam (Request: JSON Object) {String} description Description of the Caterer.
	 * @apiParam (Request: JSON Object) {String} manager Manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{ 
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Caterer id
	 * @param caterer
	 *            The Caterer
	 * @param context
	 *            The container context that contains the Key
	 * @return The response
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(
			@PathParam("id") final String id,
			final Caterer caterer,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.UPDATE);
		validator.checkUpdateCaterer(id, caterer);
		Caterer result = catererBusiness.update(caterer, key.getRole(), key.getUserId());
		if (result == null) {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		} else {
			return Response.ok().entity(result).build();
		}
	}

	// @formatter:off
	/**
	 * @api {delete} /caterers/:id Delete a Caterer
	 * @apiVersion 1.0.0
	 * @apiName DeleteCaterer
	 * @apiGroup Caterers
	 * @apiDescription Delete a Caterer with the provided Id.
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Boolean} deleted True when deleted.
	 * 
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id" : "54e0f713731eff3fe01641d5" , 
	 *		"deleted" : true
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Caterer id
	 * @param context
	 *            The container context that contains the Key
	 * @return The response
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(
			@PathParam("id") final String id,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.DELETE);
		validator.checkId(id);
		boolean resBool = catererBusiness.delete(id);
		if (resBool) {
			DBObject result = BasicDBObjectBuilder.start("id", id).add("deleted", resBool).get();
			return Response.ok().entity(result).build();
		} else {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		}
	}

	// @formatter:off
	/**
	 * @api {get} /caterers Read all Caterers
	 * @apiVersion 1.0.0
	 * @apiName GetAllCaterers
	 * @apiGroup Caterers
	 * @apiDescription Return a list containing all Caterers.
	 * @apiPermission admin, super_user, user
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	[{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param context
	 *            The container context that contains the Key
	 * @return The reponse
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.READ);
		List<Caterer> caterers = catererBusiness.readAll();
		return Response.ok().entity(caterers).build();
	}

	// @formatter:off
	/**
	 * @api {get} /caterers/:id/paymentInfo?startDate=:start&endDate=:end&formatDate=:format Payment amount for a Caterer
	 * @apiVersion 1.0.0
	 * @apiName GetPayement
	 * @apiGroup Caterers
	 * @apiDescription Obtain payement amount for a Caterer within a time period.
	 * @apiPermission admin
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 * 
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *      "id": "553700f7c0651c0e84609a4d",
	 *      "name": "Kebab & company",
	 *      "amount": 0,
	 *      "start": "01/01/2015",
	 *      "end": "01/02/2015",
	 *      "format": "MM/dd/yyyy"
	 * }
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Caterer id
	 * @param start
	 *            The start date to filter on
	 * @param end
	 *            The end date to filter on
	 * @param format
	 *            The date format
	 * @return A Response
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	@GET
	@Path("/{id}/paymentInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response paymentInfo(
			@PathParam("id") final String id,
			@QueryParam("startDate") final String start,
			@QueryParam("endDate") final String end,
			@DefaultValue("MM/dd/yyyy") @QueryParam("formatDate") final String format) throws EpickurException {
		DateTime startDate = null;
		DateTime endDate = null;
		if (start != null) {
			startDate = Utils.parseDate(start, format);
		}
		if (end != null) {
			endDate = Utils.parseDate(start, format);
		}
		validator.checkPaymentInfo(id, startDate, endDate);
		Caterer caterer = catererBusiness.read(id);
		if (caterer == null) {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		} else {
			List<Order> orders = orderBusiness.readAllWithCatererId(caterer.getId().toHexString(), startDate, endDate);
			Integer amount = catererBusiness.getAmount(orders);
			DBObject bdb = BasicDBObjectBuilder.start().get();
			bdb.put("id", caterer.getId().toHexString());
			bdb.put("name", caterer.getName());
			bdb.put("amount", amount);
			if (start != null) {
				bdb.put("start", start);
			}
			if (end != null) {
				bdb.put("end", end);
			}
			bdb.put("format", format);
			return Response.ok().entity(bdb).build();
		}
	}
}
