package com.epickur.api.rest;

import com.epickur.api.annotation.ValidateSimpleAccessRights;
import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import com.epickur.api.web.ResponseError;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.enumeration.EndpointType.DISH;
import static com.epickur.api.enumeration.Operation.*;

/**
 * JAX-RS Dish Service
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/dishes")
public class DishController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private DishService dishService;
	@Autowired
	private CatererService catererService;
	@Autowired
	private Utils utils;

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
	 * @param dish The Dish
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = CREATE, endpoint = DISH)
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@RequestBody final Dish dish) throws EpickurException {
		final Dish result = dishService.create(dish);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
	 * @param id The Dish id
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = READ, endpoint = DISH)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> read(@PathVariable("id") final String id) throws EpickurException {
		final Optional<Dish> dish = dishService.read(id);
		if (!dish.isPresent()) {
			return ResponseError.notFound(ErrorConstants.DISH_NOT_FOUND, id);
		} else {
			return new ResponseEntity<>(dish.get(), HttpStatus.OK);
		}
	}

	// @formatter:off
	/**
	 *
	 * @api {get} /dishes/all Get all dishes.
	 * @apiVersion 1.0.0
	 * @apiName ReadAllDishes
	 * @apiGroup Dishes
	 * @apiDescription Read all dishes.
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
	 */
	// @formatter:on
	/**
	 * Read all dishes.
	 *
	 * @return a list of dishes
	 * @throws EpickurException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readAll() throws EpickurException {
		final List<Dish> dishes = dishService.readAll();
		return new ResponseEntity<>(dishes, HttpStatus.OK);
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
	 * @param id   The Dish id
	 * @param dish The Dish
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = UPDATE, endpoint = DISH)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(
			@PathVariable("id") final String id,
			@RequestBody final Dish dish) throws EpickurException {
		final Dish result = dishService.update(dish);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
	 * @param id The Dish id
	 * @return The reponse
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = DELETE, endpoint = DISH)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") final String id) throws EpickurException {
		final boolean isDeleted = dishService.delete(id);
		if (isDeleted) {
			final DeletedMessage message = new DeletedMessage();
			message.setId(id);
			message.setDeleted(true);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} else {
			return ResponseError.notFound(ErrorConstants.DISH_NOT_FOUND, id);
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
	 * @param pickupdate The pickup date.
	 * @param types      The list of Dish type.
	 * @param limit      The limit amount of result.
	 * @param searchtext The address.
	 * @param at         A geo localisation coordinate. lat,long.
	 * @param distance   The distance.
	 * @return The response.
	 * @throws EpickurException If an epickur exception occurred.
	 */
	@ValidateSimpleAccessRights(operation = SEARCH_DISH, endpoint = DISH)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> search(
			@RequestParam("pickupdate") @NotBlank(message = "{dish.search.pickupdate}") final String pickupdate,
			@RequestParam("types") @NotBlank(message = "{dish.search.types}") final String types,
			@RequestParam(value = "limit", defaultValue = "50") @Min(value = 1, message = "{dish.search.limit}") final Integer limit,
			@RequestParam(value = "at", required = false) final String at,
			@RequestParam(value = "searchtext", required = false) final String searchtext,
			@RequestParam(value = "distance", defaultValue = "500") @Min(value = 50, message = "{dish.search.distance}") final Integer distance)
			throws EpickurException {
		final List<DishType> dishTypes = utils.stringToListDishType(types);
		Geo geo = null;
		if (!StringUtils.isBlank(at)) {
			geo = utils.stringToGeo(at);
		}
		final Object[] result = CommonsUtil.parsePickupdate(pickupdate);
		final String day = (String) result[0];
		final Integer minutes = (Integer) result[1];
		final List<Dish> dishes = dishService.search(day, minutes, dishTypes, limit, geo, searchtext, distance);
		return new ResponseEntity<>(dishes, HttpStatus.OK);
	}
}
