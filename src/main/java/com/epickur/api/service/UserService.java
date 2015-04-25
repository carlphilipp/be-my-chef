package com.epickur.api.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * JAX-RS User Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/users")
public final class UserService {

	/** User Business **/
	private UserBusiness userBusiness;
	/** Order Business **/
	private OrderBusiness orderBusiness;
	/** User validator **/
	private UserValidator validator;

	/** Constructor **/
	public UserService() {
		this.userBusiness = new UserBusiness();
		this.orderBusiness = new OrderBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	// @formatter:off
	/**
	 * @api {post} /users Create a new User
	 * @apiVersion 1.0.0
	 * @apiName CreateUser
	 * @apiGroup Users
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: Header Parameter) {Boolean} email-agent Set as false if no emails should be sent. For tests only.
	 * @apiParam (Request: Header Parameter) {Boolean} validate-agent Set as true for the user to be allowed without verification. For tests only.
	 * 
	 * @apiParam (Request: JSON Object) {String} name Name of the User.
	 * @apiParam (Request: JSON Object) {String} password Password of the User.
	 * @apiParam (Request: JSON Object) {String} email Email of the User.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow 1 if the User is allowed to login, 0 if not.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the User.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the User.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id":"54d7c722d6fe6f71251891a2",
	 *		"name":"Bill Gates",
	 *		"email":"bgates@microsoft.com",
	 *		"allow":1,
	 *		"createdAt":1423427362620,
	 *		"updatedAt":1423427362620
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param sendEmail
	 *            The email agent. Can only be true or false
	 * @param autoValidate
	 *            The valide agent. Can onlybe true or false
	 * @param user
	 *            The User
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			@DefaultValue("true") @HeaderParam("email-agent") final boolean sendEmail,
			@DefaultValue("false") @HeaderParam("validate-agent") final boolean autoValidate,
			final User user,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.CREATE);
		validator.checkCreateUser(user);
		User result = userBusiness.create(user, sendEmail, autoValidate);
		// We add to the header the check code. Can be useful for tests or developers.
		return Response.ok().entity(result).header("check", result.getCode()).build();
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /users/:id Read a User
	 * @apiVersion 1.0.0
	 * @apiName GetUser
	 * @apiGroup Users
	 * @apiPermission admin, super_user (own user), user (own user)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow 1 if the User is allowed to login, 0 if not.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the User.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the User.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id":"54d7a02ed6fee70a27215649",
	 *		"name":"Bill Gates",
	 *		"email":"bgates@microsoft.com",
	 *		"allow":1,
	 *		"createdAt":1423417390991,
	 *		"updatedAt":1423417390991
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The User id
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") final String id, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.READ);
		validator.checkId(id);
		User user = userBusiness.read(id, key);
		if (user == null) {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.USER_NOT_FOUND);
		} else {
			return Response.ok().entity(user).build();
		}
	}

	// @formatter:off
	/**
	 * @api {put} /users/:id Update a User
	 * @apiVersion 1.0.0
	 * @apiName UpdateUser2
	 * @apiGroup Users
	 * @apiPermission admin, super_user (own user), user (own user)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * 
	 * @apiParam (Request: JSON Object) {String} id Id of the User.
	 * @apiParam (Request: JSON Object) {String} name Name of the User. (optional)
	 * @apiParam (Request: JSON Object) {String} email Email of the User. (optional)
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow 1 if the User is allowed to login, 0 if not.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the User.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the User.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id":"54e0f33f731e0e940721f2fb",
	 *		"name":"Bill Gates",
	 *		"email":"bgates@microsoft.com",
	 *		"allow":1,
	 *		"createdAt":1424028479604,
	 *		"updatedAt":1424028479628,
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The User id
	 * @param user
	 *            The User
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(
			@PathParam("id") final String id,
			final User user,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.UPDATE);
		validator.checkUpdateUser(id, user);
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			userBusiness.injectNewPassword(user);
		}
		User result = userBusiness.update(user, key);
		if (result == null) {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.USER_NOT_FOUND);
		} else {
			return Response.ok().entity(result).build();
		}
	}

	// @formatter:off
	/**
	 * @api {delete} /users/:id Delete a User
	 * @apiVersion 1.0.0
	 * @apiName DeleteUser
	 * @apiGroup Users
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
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
	 *            The User id
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") final String id, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.DELETE);
		validator.checkId(id);
		boolean isDeleted = userBusiness.delete(id);
		if (isDeleted) {
			DBObject res = BasicDBObjectBuilder.start("id", id).add("deleted", isDeleted).get();
			return Response.ok().entity(res).build();
		} else {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.USER_NOT_FOUND);
		}
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /users Read all Users
	 * @apiVersion 1.0.0
	 * @apiName GetAllUsers
	 * @apiGroup Users
	 * @apiPermission admin only
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow 1 if the User is allowed to login, 0 if not.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the User.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the User.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	[{
	 *		"id":"54d7a02ed6fee70a27215649",
	 *		"name":"Bill Gates",
	 *		"email":"bgates@microsoft.com",
	 *		"allow":1,
	 *		"createdAt":1423417390991,
	 *		"updatedAt":1423417390991
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
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return A list of User
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.READ);
		List<User> users = userBusiness.readAll();
		return Response.ok().entity(users).build();
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /users/:id/orders/:orderId Read an Order
	 * @apiVersion 1.0.0
	 * @apiName GetOrder
	 * @apiGroup Orders
	 * @apiPermission admin, super_user (own order), user (own order)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * @apiParam (Request: URL Parameter) {String} orderId Id of the Order.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Order.
	 * @apiSuccess (Response: JSON Object) {String} userId Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Order.
	 * @apiSuccess (Response: JSON Object) {Number} amount Price of the Order.
	 * @apiSuccess (Response: JSON Object) {Dish} dish Dish of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Order.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{ 
	 *		"id" : "54e0f996731e1b9f54451ef6",
	 *		"userId" : "54e0f995731e1b9f54451ef5", 
	 *		"description" : "A new order", 
	 *		"amount" : 500 , 
	 *		"currency" : "AUD", 
	 *		"dish" : { 
	 *			"name" : "Dish name", 
	 *			"description" : "A super cool dish", 
	 *			"type" : "Vegan", 
	 *			"price" : 5.0, 
	 *			"cookingTime" : 5, 
	 *			"difficultyLevel" : 8, 
	 *			"videoUrl" : "http://www.google.com/videos"
	 *		}, 
	 *		"createdAt" : 1424030102542, 
	 *		"updatedAt" : 1424030102542
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The User id
	 * @param orderId
	 *            The Orderid
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@GET
	@Path("/{id}/orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readOneOrder(
			@PathParam("id") final String id,
			@PathParam("orderId") final String orderId,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.READ);
		validator.checkReadOneOrder(id, orderId);
		Order order = orderBusiness.read(orderId, key);
		if (order == null) {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.ORDER_NOT_FOUND);
		} else {
			return Response.ok().entity(order).build();
		}
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /users/:id/orders Read a list of Orders
	 * @apiVersion 1.0.0
	 * @apiName GetOrders
	 * @apiGroup Orders
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 *
	 * @apiSuccess (Response: JSON Object) {Order} id Id of the Order.
	 * @apiSuccess (Response: JSON Object) {String} userId Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Order.
	 * @apiSuccess (Response: JSON Object) {Number} amount Price of the Order.
	 * @apiSuccess (Response: JSON Object) {Dish} dish Dish of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Order.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	[{ 
	 *		"id" : "54e0f996731e1b9f54451ef6",
	 *		"userId" : "54e0f995731e1b9f54451ef5", 
	 *		"description" : "A new order", 
	 *		"amount" : 500 , 
	 *		"currency" : "AUD", 
	 *		"dish" : { 
	 *			"name" : "Chicken Kebab", 
	 *			"description" : "Fresh meat, served with fries", 
	 *			"type" : "Vegan", 
	 *			"price" : 5.0, 
	 *			"cookingTime" : 5, 
	 *			"difficultyLevel" : 8, 
	 *			"videoUrl" : "http://www.google.com/videos"
	 *		}, 
	 *		"createdAt" : 1424030102542, 
	 *		"updatedAt" : 1424030102542
	 *	},{ 
	 *		"id" : "54e0f996731e1b9f54451ef7",
	 *		"userId" : "54e0f995731e1b9f54451ef6", 
	 *		"description" : "A new order", 
	 *		"amount" : 500 , 
	 *		"currency" : "AUD", 
	 *		"dish" : { 
	 *			"name" : "Fish and Chips", 
	 *			"description" : "Fresh fish and home made chips", 
	 *			"type" : "Vegan", 
	 *			"price" : 5.0, 
	 *			"cookingTime" : 5, 
	 *			"difficultyLevel" : 8, 
	 *			"videoUrl" : "http://www.google.com/videos"
	 *		}, 
	 *		"createdAt" : 1424030102542, 
	 *		"updatedAt" : 1424030102542
	 *	}]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The User id
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The list of Order for this User
	 */
	@GET
	@Path("/{id}/orders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAllOrders(
			@PathParam("id") final String id,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.READ);
		validator.checkReadAllOrder(id, key);
		List<Order> orders = orderBusiness.readAllWithUserId(id);
		return Response.ok().entity(orders).build();
	}

	// @formatter:off
	/**
	 * @api {post} /users/:id/orders?token=:token Create a new Order
	 * @apiVersion 1.0.0
	 * @apiName CreateOrder
	 * @apiGroup Orders
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * @apiParam (Request: URL Parameter) {String} token Card token.
	 * 
	 * @apiParam (Request: Header Parameter) {Boolean} charge-agent Set as false to not charge the card. For tests only.
	 * 
	 * @apiParam (Request: JSON Object) {String} derp Has to be determined.
	 *
	 * @apiSuccess (Response: JSON Object) {Order} id Id of the Order.
	 * @apiSuccess (Response: JSON Object) {String} userId Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Order.
	 * @apiSuccess (Response: JSON Object) {Number} amount Price of the Order.
	 * @apiSuccess (Response: JSON Object) {Dish} dish Dish of the Order.
	 * @apiSuccess (Response: JSON Object) {String} chargeId Stripe charge id.
	 * @apiSuccess (Response: JSON Object) {Boolean} paid True if has been paid.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Order.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{ 
	 *		"id" : "54e0f996731e1b9f54451ef6",
	 *		"userId" : "54e0f995731e1b9f54451ef5", 
	 *		"description" : "A new order", 
	 *		"amount" : 500 , 
	 *		"currency" : "AUD", 
	 *		"dish" : { 
	 *			"name" : "Chicken Kebab", 
	 *			"description" : "Fresh meat, served with fries", 
	 *			"type" : "Vegan", 
	 *			"price" : 5.0, 
	 *			"cookingTime" : 5, 
	 *			"difficultyLevel" : 8, 
	 *			"videoUrl" : "http://www.google.com/videos"
	 *		}, 
	 *		"chargeId": "ch_15bEfH21cpKR0BKmHimGtkgn",
	 *		"paid": true,
	 *		"createdAt" : 1424030102542, 
	 *		"updatedAt" : 1424030102542
	 *	}
	 *
	 * @apiError (Error){Number} error The HTTP error code.
	 * @apiError (Error){String} message The error message.
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param userId
	 *            The User id
	 * @param cardToken
	 *            The Stripe card token
	 * @param shouldCharge
	 *            The charge header. Can only be true or false
	 * @param sendEmail
	 *            The email agent. Can only be true or false
	 * @param order
	 *            The Order
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@POST
	@Path("/{id}/orders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOneOrder(
			@PathParam("id") final String userId,
			@QueryParam("token") final String cardToken,
			@DefaultValue("true") @HeaderParam("charge-agent") final boolean shouldCharge,
			@DefaultValue("true") @HeaderParam("email-agent") final boolean sendEmail,
			final Order order) throws EpickurException {
		validator.checkCreateOneOrder(userId, cardToken, order);
		Order result = orderBusiness.create(userId, order, cardToken, shouldCharge, sendEmail);
		return Response.ok().entity(result).build();
	}

	// @formatter:off
	/**
	 * @api {put} /user/:id/orders/:orderId Update an Order
	 * @apiVersion 1.0.0
	 * @apiName UpdateOrder
	 * @apiGroup Orders
	 * @apiPermission admin, super_user (own order), user (own order)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * @apiParam (Request: URL Parameter) {String} orderId Id of the Order.
	 * 
	 * @apiParam (Request: JSON Object) {String} id Id of the User.
	 * @apiParam (Request: JSON Object) {String} name Name of the User. (optional)
	 * @apiParam (Request: JSON Object) {String} email Email of the User. (optional)
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Order.
	 * @apiSuccess (Response: JSON Object) {String} userId Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Order.
	 * @apiSuccess (Response: JSON Object) {Number} amount Price of the Order.
	 * @apiSuccess (Response: JSON Object) {Dish} dish Dish of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Order.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Order.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{ 
	 *		"id" : "54e0f996731e1b9f54451ef6",
	 *		"userId" : "54e0f995731e1b9f54451ef5", 
	 *		"description" : "A new order", 
	 *		"amount" : 500 , 
	 *		"currency" : "AUD", 
	 *		"dish" : { 
	 *			"name" : "Chicken Kebab", 
	 *			"description" : "Fresh meat, served with fries", 
	 *			"type" : "Vegan", 
	 *			"price" : 5.0, 
	 *			"cookingTime" : 5, 
	 *			"difficultyLevel" : 8, 
	 *			"videoUrl" : "http://www.google.com/videos"
	 *		}, 
	 *		"createdAt" : 1424030102542, 
	 *		"updatedAt" : 1424030102542
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The User id
	 * @param orderId
	 *            The Order id
	 * @param order
	 *            The Order
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@PUT
	@Path("/{id}/orders/{orderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateOneOrder(
			@PathParam("id") final String id,
			@PathParam("orderId") final String orderId,
			final Order order,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.UPDATE);
		validator.checkUpdateOneOrder(id, orderId, order);
		Order result = orderBusiness.update(order, key);
		if (result == null) {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.ORDER_NOT_FOUND);
		} else {
			return Response.ok().entity(result).build();
		}
	}

	// @formatter:off
	/**
	 * @api {delete} /users/:id/orders/:orderId Delete an Order
	 * @apiVersion 1.0.0
	 * @apiName DeleteOrder
	 * @apiGroup Orders
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * @apiParam (Request: URL Parameter) {String} orderId id Id of the Order.
	 *
	 * @apiSuccess (Response: JSON Object) {String} orderId Id of the Order.
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
	 *            The User id
	 * @param orderId
	 *            The Order id
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@DELETE
	@Path("/{id}/orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteOneOrder(
			@PathParam("id") final String id,
			@PathParam("orderId") final String orderId,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.DELETE);
		validator.checkDeleteOneOrder(id, orderId);
		boolean isDeleted = orderBusiness.delete(orderId);
		if (isDeleted) {
			DBObject result = BasicDBObjectBuilder.start("id", id).add("deleted", isDeleted).get();
			return Response.ok().entity(result).build();
		} else {
			return ErrorUtils.error(Response.Status.NOT_FOUND, ErrorUtils.ORDER_NOT_FOUND);
		}
	}
}
