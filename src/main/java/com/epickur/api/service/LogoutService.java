package com.epickur.api.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.KeyBusiness;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Logout Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/logout")
public final class LogoutService {

	/** Key Business **/
	private KeyBusiness keyBusiness;

	/** Constructor **/
	public LogoutService() {
		this.keyBusiness = new KeyBusiness();
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
	public Response logout(@QueryParam("key") final String key) throws EpickurException {
		this.keyBusiness.delete(key);
		DBObject obj = BasicDBObjectBuilder.start("result", "success").get();
		return Response.ok().entity(obj).build();
	}
}
