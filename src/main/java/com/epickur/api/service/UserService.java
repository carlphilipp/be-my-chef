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
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.validator.AccessRights;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JAX-RS User Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/users")
public final class UserService {

	/** User Business */
	private UserBusiness userBusiness;
	/** Order Business */
	private OrderBusiness orderBusiness;
	/** User validator */
	private UserValidator validator;

	/** Constructor */
	public UserService() {
		this.userBusiness = new UserBusiness();
		this.orderBusiness = new OrderBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}
	
	public UserService(final UserBusiness userBusiness, final OrderBusiness orderBusiness){
		this.userBusiness = userBusiness;
		this.orderBusiness = orderBusiness;
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
		AccessRights.check(key.getRole(), Operation.CREATE, EndpointType.USER);
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
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.USER);
		validator.checkId(id);
		User user = userBusiness.read(id, key);
		if (user == null) {
			return ErrorUtils.notFound(ErrorUtils.USER_NOT_FOUND, id);
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
		AccessRights.check(key.getRole(), Operation.UPDATE, EndpointType.USER);
		validator.checkUpdateUser(id, user);
		if (StringUtils.isNotBlank(user.getPassword()) && StringUtils.isNotBlank(user.getNewPassword())) {
			userBusiness.injectNewPassword(user);
			user.setNewPassword(null);
		} else {
			// Set password to null to prevent from updating the field with
			// whatever irrelevant value that could have been sent in request
			// field "password".
			user.setPassword(null);
		}
		User result = userBusiness.update(user, key);
		if (result == null) {
			return ErrorUtils.notFound(ErrorUtils.USER_NOT_FOUND, id);
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
		AccessRights.check(key.getRole(), Operation.DELETE, EndpointType.USER);
		validator.checkId(id);
		boolean isDeleted = userBusiness.delete(id);
		if (isDeleted) {
			DeletedMessage deletedMessage = new DeletedMessage();
			deletedMessage.setId(id);
			deletedMessage.setDeleted(isDeleted);
			return Response.ok().entity(deletedMessage).build();
		} else {
			return ErrorUtils.notFound(ErrorUtils.USER_NOT_FOUND, id);
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
		AccessRights.check(key.getRole(), Operation.READ_ALL, EndpointType.USER);
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
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.ORDER);
		validator.checkReadOneOrder(id, orderId);
		Order order = orderBusiness.read(orderId, key);
		if (order == null) {
			return ErrorUtils.notFound(ErrorUtils.ORDER_NOT_FOUND, id);
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
		AccessRights.check(key.getRole(), Operation.READ_ALL, EndpointType.ORDER);
		validator.checkReadAllOrder(id, key);
		List<Order> orders = orderBusiness.readAllWithUserId(id);
		return Response.ok().entity(orders).build();
	}

	// @formatter:off
	/**
	 * @api {post} /users/:id/orders Create a new Order
	 * @apiVersion 1.0.0
	 * @apiName CreateOrder
	 * @apiGroup Orders
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * 
	 * @apiParam (Request: Header Parameter) {Boolean} charge-agent Set as false to not charge the card. For tests only.
	 * 
	 * @apiParam (Request: JSON Object) {String} derp Has to be determined.
	 *
	 * @apiSuccess (Response: JSON Object) {Order} id Id of the Order.
	 * @apiSuccess (Response: JSON Object) {String} userId Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Order.
	 * @apiSuccess (Response: JSON Object) {Number} quantity Quantity of the related Dish.
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
	 *		"chargeId": "ch_163baS21cpKR0BKmv00GWuLK",
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
	 * @param sendEmail
	 *            The email agent. Can only be true or false
	 * @param order
	 *            The Order
	 * @param context
	 *            The context
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@POST
	@Path("/{id}/orders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createOneOrder(
			@PathParam("id") final String userId,
			@DefaultValue("true") @HeaderParam("email-agent") final boolean sendEmail,
			final Order order,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.CREATE, EndpointType.ORDER);
		validator.checkCreateOneOrder(userId, order);
		Order result = orderBusiness.create(userId, order, sendEmail);
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
		AccessRights.check(key.getRole(), Operation.UPDATE, EndpointType.ORDER);
		validator.checkUpdateOneOrder(id, orderId, order);
		Order result = orderBusiness.update(order, key);
		if (result == null) {
			return ErrorUtils.notFound(ErrorUtils.ORDER_NOT_FOUND, orderId);
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
		AccessRights.check(key.getRole(), Operation.DELETE, EndpointType.ORDER);
		validator.checkDeleteOneOrder(id, orderId);
		boolean isDeleted = orderBusiness.delete(orderId);
		if (isDeleted) {
			DeletedMessage deletedMessage = new DeletedMessage();
			deletedMessage.setId(id);
			deletedMessage.setDeleted(isDeleted);
			return Response.ok().entity(deletedMessage).build();
		} else {
			return ErrorUtils.notFound(ErrorUtils.ORDER_NOT_FOUND, orderId);
		}
	}

	// @formatter:off
	/**
	 * @api {post} /users/reset Send an email to the user to reset his password
	 * @apiVersion 1.0.0
	 * @apiName ResetPassword1
	 * @apiGroup Users
	 * @apiPermission admin only
	 *
	 * @apiSuccess (Response: JSON Object) {email} email Email of the User.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"status" : "email sent"
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param node
	 *            The node containing the user email that needs to be reset
	 * @param context
	 *            The container context that contains the Key
	 * @return The reponse
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@POST
	@Path("/reset")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPasswordFirstStep(
			final ObjectNode node,
			@Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.RESET_PASSWORD, EndpointType.USER);
		validator.checkResetPasswordData(node);
		String email = node.get("email").asText();
		userBusiness.resetPasswordFirstStep(email);
		node.put("status", "email sent");
		return Response.ok().entity(node).build();
	}
}
