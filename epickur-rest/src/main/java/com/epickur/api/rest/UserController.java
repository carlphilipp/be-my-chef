package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.epickur.api.validator.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * JAX-RS User Service
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/users")
public final class UserController {

	/**
	 * Context
	 */
	@Autowired
	private HttpServletRequest context;
	/**
	 * User Service
	 */
	private UserService userService;
	/**
	 * Order Service
	 */
	private OrderService orderService;
	/**
	 * User validator
	 */
	private UserValidator validator;

	/**
	 * Constructor
	 */
	public UserController() {
		this.userService = new UserService();
		this.orderService = new OrderService();
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
	 * Create a User
	 *
	 * @param autoValidate The valide agent. Can onlybe true or false
	 * @param user         The User
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(
			@RequestHeader(value = "validate-agent", defaultValue = "false") final boolean autoValidate,
			@RequestBody @Validated(Create.class) final User user)
			throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.CREATE, EndpointType.USER);
		User result = userService.create(user, autoValidate);
		// We add to the header the check code. Can be useful for tests or developers.
		HttpHeaders headers = new HttpHeaders();
		headers.add("check", result.getCode());
		return new ResponseEntity<>(result, headers, HttpStatus.OK);
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
	 * @param id The User id
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> read(@PathVariable("id") final String id) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.USER);
		User user = userService.read(id, key);
		return new ResponseEntity<>(user, HttpStatus.OK);
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
	 * @param id   The User id
	 * @param user The User
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(
			@PathVariable("id") final String id,
			@RequestBody @Validated(Update.class) final User user) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.UPDATE, EndpointType.USER);
		validator.checkUpdateUser(id, user);
		if (StringUtils.isNotBlank(user.getPassword()) && StringUtils.isNotBlank(user.getNewPassword())) {
			userService.injectNewPassword(user);
			user.setNewPassword(null);
		} else {
			// Set password to null to prevent from updating the field with
			// whatever irrelevant value that could have been sent in request
			// field "password".
			user.setPassword(null);
		}
		User result = userService.update(user, key);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
	 * @param id The User id
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") final String id) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.DELETE, EndpointType.USER);
		boolean isDeleted = userService.delete(id);
		DeletedMessage deletedMessage = new DeletedMessage();
		deletedMessage.setId(id);
		deletedMessage.setDeleted(isDeleted);
		return new ResponseEntity<>(deletedMessage, HttpStatus.OK);
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
	 * Read All users.
	 *
	 * @return A list of User.
	 * @throws EpickurException If an epickur exception occurred.
	 */
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readAll() throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.READ_ALL, EndpointType.USER);
		List<User> users = userService.readAll();
		return new ResponseEntity<>(users, HttpStatus.OK);
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
	 * @param id      The User id
	 * @param orderId The Orderid
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/orders/{orderId:^[0-9a-fA-F]{24}$}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readOneOrder(
			@PathVariable("id") final String id,
			@PathVariable("orderId") final String orderId) throws EpickurException {
		// FIXME Check why id is not used and probably check stuff around users
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.ORDER);
		Order order = orderService.read(orderId, key);
		return new ResponseEntity<>(order, HttpStatus.OK);
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
	 * Read all orders.
	 *
	 * @param id The User id.
	 * @return The list of Order for this User.
	 * @throws EpickurException If an epickur exception occurred.
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/orders", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readAllOrders(@PathVariable("id") final String id) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.READ_ALL, EndpointType.ORDER);
		List<Order> orders = orderService.readAllWithUserId(id);
		return new ResponseEntity<>(orders, HttpStatus.OK);
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
	 * @param userId The User id
	 * @param order  The Order
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createOneOrder(
			@PathVariable("id") final String userId,
			@RequestBody final Order order) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.CREATE, EndpointType.ORDER);
		validator.checkCreateOneOrder(order);
		Order result = orderService.create(userId, order);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
	 * @param id      The User id
	 * @param orderId The Order id
	 * @param order   The Order
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/orders/{orderId:^[0-9a-fA-F]{24}$}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateOneOrder(
			@PathVariable("id") final String id,
			@PathVariable("orderId") final String orderId,
			@RequestBody final Order order) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.UPDATE, EndpointType.ORDER);
		validator.checkUpdateOneOrder(orderId, order);
		Order result = orderService.update(order, key);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
	 * @param id      The User id
	 * @param orderId The Order id
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/orders/{orderId:^[0-9a-fA-F]{24}$}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteOneOrder(
			@PathVariable("id") final String id,
			@PathVariable("orderId") final String orderId) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.DELETE, EndpointType.ORDER);
		boolean isDeleted = orderService.delete(orderId);
		DeletedMessage deletedMessage = new DeletedMessage();
		deletedMessage.setId(id);
		deletedMessage.setDeleted(isDeleted);
		return new ResponseEntity<>(deletedMessage, HttpStatus.OK);
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
	 * @param node The node containing the user email that needs to be reset
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/reset", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPasswordFirstStep(final ObjectNode node) throws EpickurException {
		Key key = (Key) context.getAttribute("key");
		AccessRights.check(key.getRole(), Operation.RESET_PASSWORD, EndpointType.USER);
		validator.checkResetPasswordData(node);
		String email = node.get("email").asText();
		userService.resetPasswordFirstStep(email);
		node.put("status", "email sent");
		return new ResponseEntity<>(node, HttpStatus.OK);
	}
}
