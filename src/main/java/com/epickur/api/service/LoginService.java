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
 * JAX-RS Login Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/login")
public final class LoginService {

	/** User Business */
	private UserBusiness userBusiness;
	/** Service validator */
	private UserValidator validator;

	/** Constructor */
	public LoginService() {
		this.userBusiness = new UserBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param userBusiness
	 *            The user business
	 */
	public LoginService(final UserBusiness userBusiness) {
		this.userBusiness = userBusiness;
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /login Login
	 * @apiVersion 1.0.0
	 * @apiName Login
	 * @apiGroup Connection
	 * 
	 * @apiParam (Request: URL Parameter) {String} email Email of the User.
	 * @apiParam (Request: URL Parameter) {String} password Password of the User.
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
	 */
	// @formatter:on
	/**
	 * @param email
	 *            The email
	 * @param password
	 *            The password
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("email") final String email, @QueryParam("password") final String password) throws EpickurException {
		this.validator.checkLogin(email, password);
		User user = this.userBusiness.login(email, password);
		return Response.ok().entity(user).build();
	}
}
