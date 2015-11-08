package com.epickur.api.service;

import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.DishBusiness;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
import com.epickur.api.validator.AccessRights;
import com.epickur.api.validator.CheckId;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.FactoryValidator;

/**
 * JAX-RS Dish Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/dishes")
public final class DishService {

	/** Context */
	@Context
	private ContainerRequestContext context;
	/** Dish Business */
	private DishBusiness dishBusiness;
	/** Dish Business */
	private CatererBusiness catererBusiness;
	/** Service validator */
	private DishValidator validator;

	/** Constructor */
	public DishService() {
		this.dishBusiness = new DishBusiness();
		this.catererBusiness = new CatererBusiness();
		this.validator = (DishValidator) FactoryValidator.getValidator("dish");
	}

	// @formatter:off
	/**
	 * @api {post} /dishes Create a new Dish
	 * @apiVersion 1.0.0
	 * @apiName CreateDish
	 * @apiGroup Dishes
	 * @apiPermission admin, super_user (own caterer)
	 * 
	 * @apiParam (Request: JSON Object) {Dish} dish Dish JSON Object.
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
	 * @apiSuccess (Response: JSON Object) {String} imageAfterUrl Image After URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} videoUrl Video URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Dish.
	 * 
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e12a60731e59c612c5fac7",
 	 *		"name": "Fish and Chips",
	 *		"description": "Fresh fish and chips",
	 *		"type": "Fish",
	 *		"price": 5.0,
	 *		"cookingTime": 5,
	 *		"difficultyLevel": 1,
	 *		"caterer": {
	 *			"id": "54e12a5e731e465f7301d561"
	 *			"name": "FisherMan",
	 *			"location": {
	 *				"address": {
	 *					"label": "",
	 *					"houseNumber": "1090",
	 *					"street": "Michigan avenue",
	 *					"city": "Chicago",
	 *					"postalCode": 60614,
	 *					"state": "Illinois",
	 *					"country": "USA"
	 *				},
	 *				"geo": {
	 *					"type": "Point",
	 *					"coordinates": [-73.97, 40.77]
	 *				},
	 *			},
	 *		},
	 * 		"ingredients": 
	 * 		[{
	 * 			"name": "Fish",
	 *			"sequence": 1,
	 *			"quantity": 1.0
	 *		},
	 *		{
	 *			"name": "Chips",
	 *			"sequence": 2,
	 *			"quantity": 1.0
	 *		}],
	 *		"nutritionFacts": 
	 *		[{
	 *			"name": "Calories",
	 *			"value": 1250.0,
	 *			"unit": "kJ"
	 *		},
	 *		{
	 *			"name": "Proteins",
	 *			"value": 750.5,
	 *			"unit": "g"
	 *		}],
	 *		"imageAfterUrl": "http://www.flickr.com",
	 *		"videoUrl": "http://www.google.com",
	 *		"createdAt": 1424042592185,
	 *		"updatedAt": 1424042592185
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param dish
	 *            The Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final Dish dish) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkCreateData(dish);
		Caterer caterer = catererBusiness.read(dish.getCaterer().getId().toHexString());
		if (caterer == null) {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, dish.getCaterer().getId().toHexString());
		}
		validator.checkRightsBefore(key.getRole(), Operation.CREATE, dish, caterer, key);
		AccessRights.check(key.getRole(), Operation.CREATE, EndpointType.DISH);
		Dish result = dishBusiness.create(dish);
		return Response.ok().entity(result).build();
	}

	// @formatter:off
	/**
	 * @api {get} /dishes/:id Read a Dish
	 * @apiVersion 1.0.0
	 * @apiName ReadDish
	 * @apiGroup Dishes
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Dish.
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
	 * @apiSuccess (Response: JSON Object) {String} imageAfterUrl Image After URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} videoUrl Video URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Dish.
	 * 
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e12a60731e59c612c5fac7",
 	 *		"name": "Fish and Chips",
	 *		"description": "Fresh fish and chips",
	 *		"type": "Fish",
	 *		"price": 5.0,
	 *		"cookingTime": 5,
	 *		"difficultyLevel": 1,
	 *		"caterer": {
	 *			"id": "54e12a5e731e465f7301d561"
	 *			"name": "FisherMan",
	 *			"location": {
	 *				"address": {
	 *					"label": "",
	 *					"houseNumber": "1090",
	 *					"street": "Michigan avenue",
	 *					"city": "Chicago",
	 *					"postalCode": 60614,
	 *					"state": "Illinois",
	 *					"country": "USA"
	 *				},
	 *				"geo": {
	 *					"type": "Point",
	 *					"coordinates": [-73.97, 40.77]
	 *				},
	 *			},
	 *		},
	 * 		"ingredients": 
	 * 		[{
	 * 			"name": "Fish",
	 *			"sequence": 1,
	 *			"quantity": 1.0
	 *		},
	 *		{
	 *			"name": "Chips",
	 *			"sequence": 2,
	 *			"quantity": 1.0
	 *		}],
	 *		"nutritionFacts": 
	 *		[{
	 *			"name": "Calories",
	 *			"value": 1250.0,
	 *			"unit": "kJ"
	 *		},
	 *		{
	 *			"name": "Proteins",
	 *			"value": 750.5,
	 *			"unit": "g"
	 *		}],
	 *		"imageAfterUrl": "http://www.flickr.com",
	 *		"videoUrl": "http://www.google.com",
	 *		"createdAt": 1424042592185,
	 *		"updatedAt": 1424042592185
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Dish id
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") @CheckId final String id) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.READ, EndpointType.DISH);
		Dish dish = dishBusiness.read(id);
		if (dish == null) {
			return ErrorUtils.notFound(ErrorUtils.DISH_NOT_FOUND, id);
		} else {
			return Response.ok().entity(dish).build();
		}
	}

	// @formatter:off
	/**
	 * @api {put} /dishes/:id Update a Dish
	 * @apiVersion 1.0.0
	 * @apiName UpdateDish
	 * @apiGroup Dishes
	 * @apiPermission admin, super_user (own dish)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Dish.
	 *
	 * @apiParam (Request: JSON Object) {String} id Id of the Dish.
	 * @apiParam (Request: JSON Object) {String} name Name of the Dish.
	 * @apiParam (Request: JSON Object) {String} description Description of the Dish.
	 * @apiParam (Request: JSON Object) {String} type Type of the Dish.
	 * @apiParam (Request: JSON Object) {String} cookingTime Cooking time of the Dish.
	 * @apiParam (Request: JSON Object) {String} difficultyLevel Difficulty level of the Dish.
	 * @apiParam (Request: JSON Object) {Caterer} caterer Caterer of the Dish.
	 * @apiParam (Request: JSON Object) {Ingredient[]} ingredients Ingredients of the Dish.
	 * @apiParam (Request: JSON Object) {NutritionFact[]} nutritionFacts Nutrition fact of the Dish.
	 * @apiParam (Request: JSON Object) {String} imageAfterUrl Image After URL of the Dish.
	 * @apiParam (Request: JSON Object) {String} videoUrl Video URL of the Dish.
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
	 * @apiSuccess (Response: JSON Object) {String} imageAfterUrl Image After URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {String} videoUrl Video URL of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Dish.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Dish.
	 * 
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e12a60731e59c612c5fac7",
 	 *		"name": "Fish and Chips",
	 *		"description": "Fresh fish and chips",
	 *		"type": "Fish",
	 *		"price": 5.0,
	 *		"cookingTime": 5,
	 *		"difficultyLevel": 1,
	 *		"caterer": {
	 *			"id": "54e12a5e731e465f7301d561"
	 *			"name": "FisherMan",
	 *			"location": {
	 *				"address": {
	 *					"label": "",
	 *					"houseNumber": "1090",
	 *					"street": "Michigan avenue",
	 *					"city": "Chicago",
	 *					"postalCode": 60614,
	 *					"state": "Illinois",
	 *					"country": "USA"
	 *				},
	 *				"geo": {
	 *					"type": "Point",
	 *					"coordinates": [-73.97, 40.77]
	 *				},
	 *			},
	 *		},
	 * 		"ingredients": 
	 * 		[{
	 * 			"name": "Fish",
	 *			"sequence": 1,
	 *			"quantity": 1.0
	 *		},
	 *		{
	 *			"name": "Chips",
	 *			"sequence": 2,
	 *			"quantity": 1.0
	 *		}],
	 *		"nutritionFacts": 
	 *		[{
	 *			"name": "Calories",
	 *			"value": 1250.0,
	 *			"unit": "kJ"
	 *		},
	 *		{
	 *			"name": "Proteins",
	 *			"value": 750.5,
	 *			"unit": "g"
	 *		}],
	 *		"imageAfterUrl": "http://www.flickr.com",
	 *		"videoUrl": "http://www.google.com",
	 *		"createdAt": 1424042592185,
	 *		"updatedAt": 1424042592185
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Dish id
	 * @param dish
	 *            The Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") @CheckId final String id, final Dish dish) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.UPDATE, EndpointType.DISH);
		validator.checkUpdateData(id, dish);
		Dish result = dishBusiness.update(dish, key);
		if (result == null) {
			return ErrorUtils.notFound(ErrorUtils.DISH_NOT_FOUND, id);
		} else {
			return Response.ok().entity(result).build();
		}
	}

	// @formatter:off
	/**
	 * @api {delete} /dishes/:id Delete a Dish
	 * @apiVersion 1.0.0
	 * @apiName DeleteDish
	 * @apiGroup Dishes
	 * @apiPermission admin, super_user (own dish)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Dish.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id" : "54e0f713731eff3fe01641d5" , 
	 *		"deleted" : true
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @param id
	 *            The Dish id
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") @CheckId final String id) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.DELETE, EndpointType.DISH);
		boolean isDeleted = dishBusiness.delete(id, key);
		if (isDeleted) {
			DeletedMessage message = new DeletedMessage();
			message.setId(id);
			message.setDeleted(isDeleted);
			return Response.ok().entity(message).build();
		} else {
			return ErrorUtils.notFound(ErrorUtils.DISH_NOT_FOUND, id);
		}
	}

	// @formatter:off
	/** 
	 * 
	 * @api {get} /dishes?types=:type1,type2,...,typeN&limit=:limit&at=:lat,:long&searchtext=:searchtext&distance=:distance Search a dish
	 * @apiVersion 1.0.0
	 * @apiName SearchDish
	 * @apiGroup Dishes
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
	 * @param pickupdate
	 *            The pickup date.
	 * @param types
	 *            The list of Dish type.
	 * @param limit
	 *            The limit amount of result.
	 * @param searchtext
	 *            The address.
	 * @param at
	 *            A geo localisation coordinate. lat,long.
	 * @param distance
	 *            The distance.
	 * @throws EpickurException
	 *             If an epickur exception occurred.
	 * @return The response.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(
			@QueryParam("pickupdate") @NotBlank(message = "{dish.search.pickupdate}") final String pickupdate,
			@QueryParam("types") @NotBlank(message = "{dish.search.pickupdate}") final String types,
			@QueryParam("limit") @DefaultValue("50") @Min(value = 1, message = "{dish.search.limit}") final Integer limit,
			@QueryParam("at") final String at,
			@QueryParam("searchtext") final String searchtext,
			@QueryParam("distance") @DefaultValue("500") @Min(value = 50, message = "{dish.search.distance}") final Integer distance) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		AccessRights.check(key.getRole(), Operation.SEARCH_DISH, EndpointType.DISH);
		validator.checkSearch(pickupdate, types, at, searchtext);
		List<DishType> dishTypes = Utils.stringToListDishType(types);
		Geo geo = null;
		if (!StringUtils.isBlank(at)) {
			geo = Utils.stringToGeo(at);
		}
		Object[] result = Utils.parsePickupdate(pickupdate);
		String day = (String) result[0];
		Integer minutes = (Integer) result[1];
		List<Dish> dishes = dishBusiness.search(day, minutes, dishTypes, limit, geo, searchtext, distance);
		return Response.ok().entity(dishes).build();
	}
}
