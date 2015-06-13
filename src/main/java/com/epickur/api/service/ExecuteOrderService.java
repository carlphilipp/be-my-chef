package com.epickur.api.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.OrderBusiness;
import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurException;

@Path("/execute")
public final class ExecuteOrderService {

	/** Order Business **/
	private OrderBusiness orderBusiness;

	/**
	 * Construct a ExecuteOrderService
	 */
	public ExecuteOrderService() {
		this.orderBusiness = new OrderBusiness();
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /execute/users/:userId/orders/:orderId?confirm=:confirm&ordercode=:ordercode Execute an Order
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
	@Path("/users/{id}/orders/{orderId}")
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
}
