package com.epickur.api.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JAX-RS service that handles all the request that does not contain any public key.
 * 
 * @author cph
 * @version 1.0
 *
 */
@Path("/nokey")
public final class NoKeyService {
	/** User Business **/
	private UserBusiness userBusiness;
	/** Order Business **/
	private OrderBusiness orderBusiness;
	/** Service validator **/
	private UserValidator validator;

	/** Construct the service **/
	public NoKeyService() {
		this.userBusiness = new UserBusiness();
		this.orderBusiness = new OrderBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /nokey/check?name=:name&check=:check Verifiy a User
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
	 * @param name
	 *            The User name
	 * @param check
	 *            The code
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@GET
	@Path("/check")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkUser(
			@QueryParam("name") final String name,
			@QueryParam("check") final String check) throws EpickurException {
		this.validator.checkCheckUser(name, check);
		User user = this.userBusiness.checkCode(name, check);
		return Response.ok().entity(user).build();
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
	 * @param userId
	 *            The User id
	 * @param orderId
	 *            The Order id
	 * @param confirm
	 *            If the caterer confirmed the order
	 * @param orderCode
	 *            The Order code
	 * @param sendEmail
	 *            If we want to send the emails
	 * @param shouldCharge
	 *            If we charge the user
	 * @return The reponse
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@GET
	@Path("/execute/users/{id}/orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response executeOrder(
			@PathParam("id") final String userId,
			@PathParam("orderId") final String orderId,
			@QueryParam("confirm") final boolean confirm,
			@QueryParam("ordercode") final String orderCode,
			@DefaultValue("true") @HeaderParam("email-agent") final boolean sendEmail,
			@DefaultValue("true") @HeaderParam("charge-agent") final boolean shouldCharge) throws EpickurException {
		Order result = orderBusiness.executeOrder(userId, orderId, confirm, sendEmail, shouldCharge, orderCode);
		return Response.ok().entity(result).build();
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
	 * @param id
	 *            The User id
	 * @param resetCode
	 *            The reset code
	 * @param node
	 *            The node containing the password
	 * @return The response
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@PUT
	@Path("/reset/users/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPasswordSecondStep(
			@PathParam("id") final String id,
			@QueryParam("token") final String resetCode,
			final ObjectNode node) throws EpickurException {
		validator.checkResetPasswordData(id, node, resetCode);
		User user = userBusiness.resetPasswordSecondStep(id, node, resetCode);
		return Response.ok().entity(user).build();
	}
}
