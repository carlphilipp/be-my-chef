package com.epickur.api.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;

/**
 * JAX-RS Check user service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/check")
public final class CheckUserService {

	/** User Business **/
	private UserBusiness userBusiness;
	/** Service validator **/
	private UserValidator validator;

	/** Constructor **/
	public CheckUserService() {
		this.userBusiness = new UserBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /check?name=:name&check=:check Verifiy a User
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response check(@QueryParam("name") final String name, @QueryParam("check") final String check) throws EpickurException {
		this.validator.checkCheckUser(name, check);
		User user = this.userBusiness.checkCode(name, check);
		return Response.ok().entity(user).build();
	}
}
