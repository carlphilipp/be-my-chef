package com.epickur.api.service;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.business.SearchBusiness;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
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
	 * @api {get} /search?types=:type1,type2,...,typeN&limit=:limit&at=:lat,:long&searchtext=:searchtext&distance=:distance Search a dish
	 * @apiVersion 1.0.0
	 * @apiName SearchDish
	 * @apiGroup Search
	 * @apiDescription Search a dish.
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} types list of Dish type to search.
	 * @apiParam (Request: URL Parameter) {String} limit Limit of number of result (default is 50).
	 * @apiParam (Request: URL Parameter) {String} at Geocoordinates to use (latitude, longitude).
	 * @apiParam (Request: URL Parameter) {String} searchtext Searchtext to geocode.
	 * @apiParam (Request: URL Parameter) {String} distance Distance from the origin point to search (in meter) (default is 500).
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
	 * @param types
	 *            The list of Dish type
	 * @param limit
	 *            The limit ammount of result
	 * @param searchtext
	 *            The address
	 * @param at
	 *            A geo localisation coordinate. lat,long
	 * @param distance
	 *            The distance
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(
			@QueryParam("pickupdate") final String pickupdate,
			@QueryParam("types") final String types,
			@DefaultValue("50") @QueryParam("limit") final Integer limit,
			@QueryParam("at") final String at,
			@QueryParam("searchtext") final String searchtext,
			@DefaultValue("500") @QueryParam("distance") final Integer distance) throws EpickurException {
		validator.checkSearch(pickupdate, types, at, searchtext);
		List<DishType> dishTypes = Utils.stringToListDishType(types);
		Geo geo = null;
		if (!StringUtils.isBlank(at)) {
			geo = Utils.stringToGeo(at);
		}
		Object[] result = Utils.parsePickupdate(pickupdate);
		String day = (String) result[0];
		Integer minutes = (Integer) result[1];
		List<Dish> dishes = this.searchBusiness.search(day, minutes, dishTypes, limit, geo, searchtext, distance);
		if (dishes.size() != 0) {
			return Response.ok().entity(dishes).build();
		} else {
			return ErrorUtils.noResult();
		}
	}
}
