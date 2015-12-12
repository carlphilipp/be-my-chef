package com.epickur.api.rest;

import com.epickur.api.aop.ValidateSimpleAccessRights;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.PayementInfoMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.report.Report;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.service.OrderService;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
import com.epickur.api.web.ResponseError;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.epickur.api.enumeration.EndpointType.CATERER;
import static com.epickur.api.enumeration.Operation.*;

/**
 * JAX-RS Caterer service
 *
 * @author cph
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/caterers")
public class CatererController {

	/**
	 * Context
	 */
	@Autowired
	private HttpServletRequest request;
	/**
	 * Caterer Service
	 */
	@Autowired
	private CatererService catererService;
	/**
	 * Order Service
	 */
	@Autowired
	private OrderService orderService;
	/**
	 * Dish Service
	 */
	@Autowired
	private DishService dishService;
	@Autowired
	private Utils utils;

	// @formatter:off
	/**
	 * @api {post} /caterers Create a new Caterer
	 * @apiVersion 1.0.0
	 * @apiName CreateCaterer
	 * @apiGroup Caterers
	 * @apiDescription Create a Caterer.
	 * @apiPermission admin only
	 * 
	 * 
	 * @apiParam (Request: JSON Object) {String} name Name of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 * @apiParam (Request: JSON Object) {String} description Description of the Caterer.
	 * @apiParam (Request: JSON Object) {String} manager Manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "Adress Label",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param caterer The Caterer
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = CREATE, endpoint = CATERER)
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@RequestBody final Caterer caterer) throws EpickurException {
		Key key = (Key) request.getAttribute("key");
		caterer.setCreatedBy(key.getUserId());
		Caterer result = catererService.create(caterer);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {get} /caterers/:id Read a Caterer
	 * @apiVersion 1.0.0
	 * @apiName GetCaterer
	 * @apiGroup Caterers
	 * @apiDescription Read a Caterer.
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param id The Caterer id
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = READ, endpoint = CATERER)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> read(@PathVariable("id") final String id) throws EpickurException {
		Caterer caterer = catererService.read(id);
		if (caterer == null) {
			return ResponseError.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		} else {
			return new ResponseEntity<>(caterer, HttpStatus.OK);
		}
	}

	// @formatter:off
	/**
	 * @api {put} /caterers/:id Update a Caterer
	 * @apiVersion 1.0.0
	 * @apiName UpdateCaterer
	 * @apiGroup Caterers
	 * @apiDescription Update a Caterer with the data provided in the request.
	 * @apiPermission admin, super_user (own caterer)
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 * 
	 * @apiParam (Request: JSON Object) {String} id Id of the Caterer.
	 * @apiParam (Request: JSON Object) {String} name Name of the Caterer.
	 * @apiParam (Request: JSON Object) {String} description Description of the Caterer.
	 * @apiParam (Request: JSON Object) {String} manager Manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiParam (Request: JSON Object) {Location} location Location of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	{ 
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param id      The Caterer id
	 * @param caterer The Caterer
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = UPDATE, endpoint = CATERER)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(
			@PathVariable("id") final String id,
			@RequestBody final Caterer caterer) throws EpickurException {
		Caterer result = catererService.update(caterer);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {delete} /caterers/:id Delete a Caterer
	 * @apiVersion 1.0.0
	 * @apiName DeleteCaterer
	 * @apiGroup Caterers
	 * @apiDescription Delete a Caterer with the provided Id.
	 * @apiPermission admin only
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Boolean} deleted True when deleted.
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
	 * @param id The Caterer id
	 * @return The response
	 * @throws EpickurException If an epickur exception occurred
	 */
	@ValidateSimpleAccessRights(operation = DELETE, endpoint = CATERER)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(
			@PathVariable("id") final String id) throws EpickurException {
		boolean resBool = catererService.delete(id);
		if (resBool) {
			DeletedMessage deletedMessage = new DeletedMessage();
			deletedMessage.setId(id);
			deletedMessage.setDeleted(true);
			return new ResponseEntity<>(deletedMessage, HttpStatus.OK);
		} else {
			return ResponseError.notFound(ErrorUtils.CATERER_NOT_FOUND, id);
		}
	}

	// @formatter:off
	/**
	 * @api {get} /caterers Read all Caterers
	 * @apiVersion 1.0.0
	 * @apiName GetAllCaterers
	 * @apiGroup Caterers
	 * @apiDescription Return a list containing all Caterers.
	 * @apiPermission admin, super_user, user
	 *
	 * @apiSuccess (Response: JSON Object) {String} id Id of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} name Name of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} description Description of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} manager Manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} email Email of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {String} phone Phone of the manager of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Location} location Location of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} createdAt Creation date of the Caterer.
	 * @apiSuccess (Response: JSON Object) {Date} updatedAt Last update of the Caterer.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 *	[{
	 *		"id": "54e13813731ec70befb77ce6",
	 *		"name": "Super Thai",
	 *		"description": "Super Thai - Noodles, Curry dishes",
	 *		"manager": "John Lee",
	 *		"email": "jlee@superthai.com",
	 *		"phone": "312-211-8911",
	 *		"location": {
	 *			"geo": {
	 *				"type": "Point",
	 *				"coordinates": [-73.97, 40.77]
	 *			},
	 *			"address": {
	 *				"postalCode": 60614,
	 *				"street": "Wrightwood",
	 *				"state": "Illinois",
	 *				"houseNumber": "1200",
	 *				"label": "carl",
	 *				"country": "USA",
	 *				"city": "Chicago"
	 *			}
	 *		},
	 *		"createdAt": 1424046099768,
	 *		"updatedAt": 1424046099768
	 *	}]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * Read all caterers.
	 *
	 * @return The response.
	 * @throws EpickurException If an epickur exception occurred.
	 */
	@ValidateSimpleAccessRights(operation = READ_ALL, endpoint = CATERER)
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readAll() throws EpickurException {
		List<Caterer> caterers = catererService.readAll();
		return new ResponseEntity<>(caterers, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {get} /caterers/:id/dishes Search Dishes for a Caterer
	 * @apiVersion 1.0.0
	 * @apiName SearchDishesForOneCaterer
	 * @apiGroup Caterers
	 * @apiDescription Return a list containing all Dishes.
	 * @apiPermission admin, super_user, user
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 *
	 * @apiSuccess (Response: List of JSON Object) {String} id Id of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} name Name of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} description Description of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} type Type of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} cookingTime Cooking time of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} difficultyLevel Difficulty level of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {Caterer} caterer Caterer of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {Ingredient[]} ingredients Ingredients of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {NutritionFact[]} nutritionFacts Nutrition fact of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} imageAfterUrl Image After URL of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {String} videoUrl Video URL of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {Date} createdAt Creation date of the Dish.
	 * @apiSuccess (Response: List of JSON Object) {Date} updatedAt Last update of the Dish.
	 *
	 * @apiSuccessExample Success-Response:
	 *	HTTP/1.1 200 OK
	 * [{
	 *	"id": "558f2b85a557dbd2cb95d7f5",
	 *	"name": "Fish and Chips",
	 *	"description": "Fresh fish and chips",
	 *	"type": "fish",
	 *	"price": 500,
	 *	"cookingTime": 5,
	 *	"difficultyLevel": 1,
	 *	"videoUrl": "http://www.google.com",
	 *	"nutritionFacts": [{
	 * 	    "name": "Calories",
	 * 	    "value": 1250.0,
	 * 	    "unit": "KJ"
	 *	},
	 *	{
	 * 	    "name": "Proteins",
	 * 	    "value": 750.5,
	 * 	    "unit": "G"
	 *	}],
	 *	"ingredients": [{
	 * 	    "name": "Fish",
	 * 	    "sequence": 1,
	 * 	    "quantity": 1.0,
	 * 	    "measurementUnit": "G"
	 *	},
	 *	{
	 * 	    "name": "Chips",
	 * 	    "sequence": 2,
	 * 	    "quantity": 1.0,
	 * 	    "measurementUnit": "G"
	 *	}],
	 *	"caterer": {
	 *		"id": "54e90015b634980ccd05e3be",
	 *		"name": "Fish & Chips",
	 *		"description": "The bast Fish & Chips down under",
	 *		"manager": "Dean Prob",
	 *		"email": "dprob@fishchips.com",
	 *		"phone": "312-211-8913",
	 *		"location": {
	 * 			"address": {
	 * 				"label": "Downtown area",
	 * 				"houseNumber": "1",
	 * 				"street": "Elizabeth Street",
	 * 				"city": "Melbourne",
	 * 				"postalCode": 32901,
	 * 				"state": "Victoria",
	 * 				"country": "Australia"
	 * 			},
	 * 			"geo": {
	 * 				"type": "Point",
	 * 				"coordinates": [144.96328, -37.814107]
	 *  		}
	 * 		},
	 * 		"workingTimes": {
	 * 			"hours": {
	 * 				"mon": [{"open": 492,"close": 868},{"open": 1074,"close": 1395}],
	 * 				"tue": [{"open": 517,"close": 831},{"open": 1059,"close": 1433}],
	 * 				"wed": [{"open": 428,"close": 711},{"open": 1052,"close": 1397}],
	 * 				"thu": [{"open": 529,"close": 889},{"open": 1034,"close": 1349}],
	 * 				"fri": [{"open": 449,"close": 810},{"open": 1076,"close": 1373}]
	 * 			},
	 * 			"minimumPreparationTime": 30
	 * 		},
	 * 		"createdAt": 1424042592185,
	 * 		"updatedAt": 1424042592185
	 *  	},
	 *	"createdAt": 1424042592185,
	 *	"updatedAt": 1424042592185,
	 *	"imageAfterUrl": "http://www.google.com"
	 *}]
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param catererId The {@link Caterer} id.
	 * @return The response.
	 * @throws EpickurException If an Epickur exception occurred.
	 */
	@ValidateSimpleAccessRights(operation = READ_DISHES, endpoint = CATERER)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/dishes", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> readDishes(@PathVariable("id") final String catererId) throws EpickurException {
		List<Dish> dishes = dishService.searchDishesForOneCaterer(catererId);
		return new ResponseEntity<>(dishes, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {get} /caterers/:id/paymentInfo?startDate=:start&endDate=:end&formatDate=:format Payment amount for a Caterer
	 * @apiVersion 1.0.0
	 * @apiName GetPayement
	 * @apiGroup Caterers
	 * @apiDescription Obtain payement amount for a Caterer within a time period.
	 * @apiPermission admin
	 * 
	 * @apiParam (Request: URL Parameter) {String} id Id of the Caterer.
	 * 
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *      "id": "553700f7c0651c0e84609a4d",
	 *      "name": "Kebab & company",
	 *      "amount": 0,
	 *      "start": "01/01/2015",
	 *      "end": "01/02/2015",
	 *      "format": "MM/dd/yyyy"
	 * }
	 *
	 * @apiUse BadRequestError
	 * @apiUse ForbiddenError
	 * @apiUse InternalError
	 */
	// @formatter:on

	/**
	 * @param id     The Caterer id
	 * @param start  The start date to filter on
	 * @param end    The end date to filter on
	 * @param format The date format
	 * @return A Response
	 * @throws EpickurException If an EpickurException occured
	 */
	@ValidateSimpleAccessRights(operation = PAYEMENT_INFO, endpoint = CATERER)
	@RequestMapping(value = "/{id:^[0-9a-fA-F]{24}$}/paymentInfo", method = RequestMethod.GET, consumes = { MediaType.APPLICATION_JSON_VALUE,
			"application/pdf" }, produces = { MediaType.APPLICATION_JSON_VALUE, "application/pdf" })
	public ResponseEntity<?> paymentInfo(
			@PathVariable("id") final String id,
			@RequestParam("startDate") final String start,
			@RequestParam("endDate") final String end,
			@RequestParam(value = "formatDate", defaultValue = "MM/dd/yyyy") final String format) throws EpickurException {
		DateTime startDate = null;
		DateTime endDate = null;
		if (start != null) {
			startDate = utils.parseDate(start, format);
		}
		if (end != null) {
			endDate = utils.parseDate(start, format);
		}
		Caterer caterer = catererService.read(id);
		List<Order> orders = orderService.readAllWithCatererId(caterer.getId().toHexString(), startDate, endDate);
		Integer amount = catererService.getTotalAmountSuccessful(orders);
		if (request.getContentType() != null && request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			PayementInfoMessage payementInfoMessage = new PayementInfoMessage();
			payementInfoMessage.setId(caterer.getId().toHexString());
			payementInfoMessage.setName(caterer.getName());
			payementInfoMessage.setAmount(amount);
			payementInfoMessage.setStart(start);
			payementInfoMessage.setEnd(end);
			payementInfoMessage.setFormat(format);
			//				List<String> list = new ArrayList<>();
			//				for (Order order : orders) {
			//					order.setDish(null);
			//					list.add(order.getDocumentAPIView().toJson());
			//				}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity<>(payementInfoMessage, headers, HttpStatus.OK);
			//return Response.ok().entity(payementInfoMessage).type(MediaType.APPLICATION_JSON).build();
		} else {
			Report report = new Report();
			report.addParam("caterer", caterer);
			report.addParam("orders", orders);
			report.addParam("amount", amount);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			headers.add("content-disposition", "attachment; filename =" + caterer.getId().toHexString() + ".pdf");
			return new ResponseEntity<>(report.getReport(), headers, HttpStatus.OK);
		}
	}
}
