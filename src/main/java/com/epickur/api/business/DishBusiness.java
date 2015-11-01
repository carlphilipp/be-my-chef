package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.geocoder.IGeocoder;
import com.epickur.api.geocoder.here.GeocoderHereImpl;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.FactoryValidator;

/**
 * {@link Dish} business layer. Execute logic and access {@link DishDAO} layer to update the database.
 * 
 * @author cph
 * @version 1.0
 */
public class DishBusiness {

	/** The DAO {@link DishDAO}. */
	private DishDAO dao;
	/** The validator {@link DishValidator}. */
	private DishValidator validator;

	/**
	 * Construct a Dish Business.
	 */
	public DishBusiness() {
		this.dao = new DishDAO();
		this.validator = (DishValidator) FactoryValidator.getValidator("dish");
	}
	
	public DishBusiness(final DishDAO dishDAO) {
		this.dao = dishDAO;
		this.validator = (DishValidator) FactoryValidator.getValidator("dish");
	}

	/**
	 * Create a {@link Dish}
	 * 
	 * @param dish
	 *            the {@link Dish}
	 * @return the {@link Dish} created
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Dish create(final Dish dish) throws EpickurException {
		dish.prepareForInsertionIntoDB();
		return dao.create(dish);
	}

	/**
	 * Read a {@link Dish}
	 * 
	 * @param id
	 *            the id of the {@link Dish}
	 * @return the {@link Dish}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Dish read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all Dishes
	 * 
	 * @return a list of {@link Dish}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public final List<Dish> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * Update a {@link Dish}
	 * 
	 * @param dish
	 *            The {@link Dish}
	 * @param key
	 *            The Key
	 * @return the updated {@link Dish}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Dish update(final Dish dish, final Key key) throws EpickurException {
		Dish read = dao.read(dish.getId().toHexString());
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Operation.UPDATE);
		dish.prepareForUpdateIntoDB();
		return dao.update(dish);
	}

	/**
	 * Delete a {@link Dish}
	 * 
	 * @param id
	 *            the id of the {@link Dish}
	 * @param key
	 *            The Key
	 * @return true if the {@link Dish} has been deleted
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public boolean delete(final String id, final Key key) throws EpickurException {
		Dish read = dao.read(id);
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Operation.DELETE);
		return dao.delete(id);
	}

	/**
	 * Search all dishes for one caterer.
	 * 
	 * @param catererId
	 *            The caterer id
	 * @return A list of {@link Dish}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public List<Dish> searchDishesForOneCaterer(final String catererId) throws EpickurException {
		return dao.searchWithCatererId(catererId);
	}

	/**
	 * Search a list of Dish
	 * 
	 * @param day
	 *            The day
	 * @param minutes
	 *            The minutes
	 * @param type
	 *            The type of Dish
	 * @param limit
	 *            The number max of result
	 * @param searchtext
	 *            The address
	 * @param geo
	 *            The geo location
	 * @param distance
	 *            The distance
	 * @return A list of Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public List<Dish> search(final String day, final Integer minutes, final List<DishType> type, final Integer limit, final Geo geo,
			final String searchtext, final int distance) throws EpickurException {
		if (geo == null) {
			IGeocoder geocoder = new GeocoderHereImpl();
			Geo geoFound = geocoder.getPosition(searchtext);
			return dao.search(day, minutes, type, limit, geoFound, distance);
		} else {
			return dao.search(day, minutes, type, limit, geo, distance);
		}
	}
}
