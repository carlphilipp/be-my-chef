package com.epickur.api.service;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.SearchBusiness;
import com.epickur.api.entity.Dish;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.SearchValidator;

/**
 * JAX-RS Search Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/search")
public final class SearchService {

	/** Search Business **/
	private SearchBusiness searchBusiness;
	/** Service validator **/
	private SearchValidator validator;

	/** Constructor **/
	public SearchService() {
		this.searchBusiness = new SearchBusiness();
		this.validator = (SearchValidator) FactoryValidator.getValidator("search");
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /search?type=:type&limit=:limit&address=:address&distance=:distance Search a dish
	 * @apiVersion 1.0.0
	 * @apiName SearchDish
	 * @apiGroup Search
	 * 
	 * @apiParam (Request: URL Parameter) {String} type Type of Dish to search (case sensitive for now).
	 * @apiParam (Request: URL Parameter) {String} limit Limit of number of result.
	 * @apiParam (Request: URL Parameter) {String} address Address where to search nextby.
	 * @apiParam (Request: URL Parameter) {String} distance Distance from the origin point to search (in meter) (optional).
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} type Type of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} cookingTime Cooking time of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} difficultyLevel Difficulty level of the Dish.
	 * @apiSuccess (Response: JSON Object) {Caterer} caterer Caterer of the Dish.
	 * @apiSuccess (Response: JSON Object) {Ingredient[]} ingredients Ingredients of the Dish.
	 * @apiSuccess (Response: JSON Object) {NutritionFact[]} nutritionFacts Nutrition fact of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} videoUrl Video URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Dish.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	[{
	 *		"id": "54e12a60731e59c612c5fac7",
	 *		"name": "Thai Inbox",
	 *		"description": "Noodles with rice",
	 *		"type": "Meat",
	 *		"caterer": {
	 *			"id": "54e90015b634980ccd05e3bc",
	 *			"name": "Super Thai",
	 *			"description": "Super Thai - Noodles, Curry dishes",
	 *			"manager": "John Lee",
	 *			"email": "jlee@superthai.com",
	 *			"phone": "312-211-8911",
	 *			"location": {
	 *				"address": {
	 *					"label": "House next to the police station",
	 *					"houseNumber": "832",
	 *					"street": "W. Wrightwood Avenue",
	 *					"city": "Chicago",
	 *					"postalCode": 60614,
	 *					"state": "Illinois",
	 *					"country": "USA"
	 *				},
	 *				"geo": {
	 *					"type": "Point",
	 *					"coordinates": [-87.65024,41.928276]
	 *				}
	 *			},
	 *			"createdAt": 1424556053008,
	 *			"updatedAt": 1424556053008
	 *		},
	 *		"price": 500,
	 *		"ingredients": [{
	 *			"name": "Noodles",
	 *			"sequence": 1,
	 *			"quantity": 1.0
	 *		},
	 *		{
	 *			"name": "Rice",
	 *			"sequence": 2,
	 *			"quantity": 1.0
	 *		}],
	 *		"cookingTime": 5,
	 *		"difficultyLevel": 1,
	 *		"nutritionFacts": [{
	 *			"name": "Calories",
	 *			"value": 1250.0,
	 *			"unit": "kJ"
	 *		},
	 *		{
	 *			"name": "Proteins",
	 *			"value": 750.5,
	 *			"unit": "g"
	 *		}],
	 *		"videoUrl": "http://www.google.com",
	 *		"createdAt": 1424042592185,
	 *		"updatedAt": 1424042592185
	 *	}]
	 *
	 * @apiUse InternalError
	 * @apiUse ForbiddenError
	 */
	// @formatter:on
	/**
	 * @param type
	 *            The Dish type
	 * @param limit
	 *            The limit ammount of result
	 * @param address
	 *            The address
	 * @param distance
	 *            The distance
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(
			@QueryParam("type") final String type,
			@DefaultValue("50") @QueryParam("limit") final Integer limit,
			@QueryParam("address") final String address,
			@DefaultValue("500") @QueryParam("distance") final Integer distance) throws EpickurException {
		this.validator.checkSearch(type, address);
		List<Dish> dishes = this.searchBusiness.search(type, limit, address, distance);
		return Response.ok().entity(dishes).build();
	}
}
