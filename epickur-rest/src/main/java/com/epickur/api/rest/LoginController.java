package com.epickur.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotBlank;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.UserService;

/**
 * JAX-RS Login Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/login")
public final class LoginController {

	/** User Service */
	private UserService userService;

	/** Constructor */
	public LoginController() {
		this.userService = new UserService();
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
	public Response login(
			@QueryParam("email") @NotBlank(message = "{login.email}") final String email,
			@QueryParam("password") @NotBlank(message = "{login.password}") final String password)
					throws EpickurException {
		User user = userService.login(email, password);
		return Response.ok().entity(user).build();
	}
}
