package com.epickur.api.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.DishBusiness;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.FactoryValidator;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * JAX-RS Dish Service
 * 
 * @author cph
 * @version 1.0
 */
@Path("/dishes")
public final class DishService {

	/** Dish Business **/
	private DishBusiness dishBusiness;
	/** Dish Business **/
	private CatererBusiness catererBusiness;
	/** Service validator **/
	private DishValidator validator;

	/** Constructor **/
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
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final Dish dish, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkCreateData(dish);
		Caterer caterer = catererBusiness.read(dish.getCaterer().getId().toHexString());
		if (caterer == null) {
			return ErrorUtils.notFound(ErrorUtils.CATERER_NOT_FOUND, dish.getCaterer().getId().toHexString());
		}
		validator.checkRightsBefore(key.getRole(), Crud.CREATE, dish, caterer, key);
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
	public Response read(@PathParam("id") final String id) throws EpickurException {
		validator.checkId(id);
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
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") final String id, final Dish dish, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.UPDATE);
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
	 * @param context
	 *            The container context that contains the Key
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The reponse
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") final String id, @Context final ContainerRequestContext context) throws EpickurException {
		Key key = (Key) context.getProperty("key");
		validator.checkRightsBefore(key.getRole(), Crud.DELETE);
		validator.checkId(id);
		boolean isDeleted = dishBusiness.delete(id, key);
		if (isDeleted) {
			DBObject result = BasicDBObjectBuilder.start("id", id).add("deleted", isDeleted).get();
			return Response.ok().entity(result).build();
		} else {
			return ErrorUtils.notFound(ErrorUtils.DISH_NOT_FOUND, id);
		}
	}

	// @formatter:off
	/**
	 * @api {get} /dishes Read all Dishes
	 * @apiVersion 1.0.0
	 * @apiName ReadAllDishes
	 * @apiGroup Dishes
	 * @apiPermission admin, super_user, user
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
	 *	[{
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
	 *	}]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on
	/**
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 * @return The list of Dish
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll() throws EpickurException {
		List<Dish> dishes = dishBusiness.readAll();
		return Response.ok().entity(dishes).build();
	}
}
