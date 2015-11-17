package com.epickur.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotBlank;

import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.KeyService;

/**
 * Logout Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/logout")
public final class LogoutRest {

	/** Key Service */
	private KeyService keyService;

	/** Constructor */
	public LogoutRest() {
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
	 * @param key
	 *            The key
	 * @return The reponse
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@QueryParam("key") @NotBlank(message = "{logout.key}") final String key) throws EpickurException {
		this.keyService.deleteWithKey(key);
		SuccessMessage successMessage = new SuccessMessage();
		return Response.ok().entity(successMessage).build();
	}
}
