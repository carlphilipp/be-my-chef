package com.epickur.api.rest;

import com.epickur.api.aop.ValidateSimpleAccessRights;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

import static com.epickur.api.enumeration.EndpointType.NO_KEY;
import static com.epickur.api.enumeration.Operation.RESET_PASSWORD;

/**
 * JAX-RS service that handles all the request that does not contain any public key.
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/nokey")
public class NoKeyController {

	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;

	// @formatter:off
	/** 
	 * 
	 * @api {get} /nokey/check?email=:email&check=:check Verifiy a User
	 * @apiVersion 1.0.0
	 * @apiName CheckUser
	 * @apiGroup Users
	 * @apiDescription Service called to allow or not a user to connect to the website (via the "allow" flag).
	 * The temporary key is received by email and sent as param in the request that called this service.
	 * 
	 * @apiParam (Request: URL Parameter) {String} name Name of User.
	 * @apiParam (Request: URL Parameter) {String} check The key given by email.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow  1 if the User is allowed to login, 0 if not.
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
	 * @param email The User email
	 * @param check The code
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@JsonView(User.PublicView.class)
	@RequestMapping(value = "/check", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkUser(
			@RequestParam("email") @NotBlank(message = "{nokey.check.user.email}") final String email,
			@RequestParam("check") @NotBlank(message = "{nokey.check.user.check}") final String check)
			throws EpickurException {
		final User user = userService.checkCode(email, check);
		userService.suscribeToNewsletter(user);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /nokey/execute/users/:userId/orders/:orderId?confirm=:confirm&ordercode=:ordercode Execute an Order
	 * @apiVersion 1.0.0
	 * @apiName ExecuteOrder
	 * @apiGroup Orders
	 * @apiPermission none
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the User.
	 * @apiParam (Request: URL Parameter) {String} orderId Id of the Order.
	 * @apiParam (Request: URL Parameter) {Boolean} confirm If the caterer accept the order or not
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
	 *		"paid": true,
	 *		"chargeId": "ch_163baS21cpKR0BKmv00GWuLK",
	 *		"cardToken": "tok_163baP21cpKR0BKmxFStdlIc",
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
	 * @param userId       The User id
	 * @param orderId      The Order id
	 * @param confirm      If the caterer confirmed the order
	 * @param orderCode    The Order code
	 * @param shouldCharge If we charge the user
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(value = "/execute/users/{id:^[0-9a-fA-F]{24}$}/orders/{orderId:^[0-9a-fA-F]{24}$}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> executeOrder(
			@PathVariable("id") final String userId,
			@PathVariable("orderId") final String orderId,
			@RequestParam("confirm") @NotNull(message = "{nokey.execute.confirm}") final boolean confirm,
			@RequestParam("ordercode") @NotBlank(message = "{nokey.execute.ordercode}") final String orderCode,
			@RequestHeader(value = "charge-agent", defaultValue = "true") final boolean shouldCharge) throws EpickurException {
		final Order result = orderService.executeOrder(userId, orderId, confirm, shouldCharge, orderCode);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// @formatter:off
	/** 
	 * 
	 * @api {put} /nokey/reset/users/:userId Reset user password
	 * @apiVersion 1.0.0
	 * @apiName ResetPassword2
	 * @apiGroup Users
	 * @apiDescription Reset the current password of the user with the provided new one.
	 * 
	 * @apiParam (Request: URL Parameter) {String} userId User id.
	 * 
	 * @apiParam (Request: JSON Object) {String} password New user password.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the User.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the User.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the User.
	 * @apiSuccess (Response: JSON Object) {Number} allow  1 if the User is allowed to login, 0 if not.
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
	 * @param id        The User id
	 * @param resetCode The reset code
	 * @param node      The node containing the password
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@JsonView(User.PublicView.class)
	@ValidateSimpleAccessRights(operation = RESET_PASSWORD, endpoint = NO_KEY)
	@RequestMapping(value = "/reset/users/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPasswordSecondStep(
			@PathVariable("id") final String id,
			@RequestParam("token") @NotBlank(message = "{nokey.reset.token}") final String resetCode,
			@RequestBody final ObjectNode node) throws EpickurException {
		final String newPassword = node.get("password").asText();
		final User user = userService.resetPasswordSecondStep(id, newPassword, resetCode);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
