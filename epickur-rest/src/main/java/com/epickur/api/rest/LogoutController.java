package com.epickur.api.rest;

import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.KeyService;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Logout Service
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/logout")
public final class LogoutController {

	/**
	 * Key Service
	 */
	private KeyService keyService;

	/**
	 * Constructor
	 */
	public LogoutController() {
		this.keyService = new KeyService();
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /logout Logout
	 * @apiVersion 1.0.0
	 * @apiName Logout
	 * @apiGroup Connection
	 * 
	 * @apiParam (Request: URL Parameter) {String} key API key.
	 *
	 * @apiSuccess (Response: JSON Object) {String} result Success.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"result":"success",
	 *	}
	 */
	// @formatter:on

	/**
	 * @param key The key
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> logout(@RequestParam("key") @NotBlank(message = "{logout.key}") final String key) throws EpickurException {
		keyService.deleteWithKey(key);
		SuccessMessage successMessage = new SuccessMessage();
		return new ResponseEntity<>(successMessage, HttpStatus.OK);
	}
}
