package com.epickur.api.rest;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * JAX-RS Login Service
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/login")
public class LoginController {

	@Autowired
	private UserService userService;

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
	 * @param email    The email
	 * @param password The password
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@JsonView(User.PublicView.class)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(
			@RequestParam("email") @NotBlank(message = "{login.email}") final String email,
			@RequestParam("password") @NotBlank(message = "{login.password}") final String password)
			throws EpickurException {
		final User user = userService.login(email, password);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
