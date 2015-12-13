package com.epickur.api.service;

import com.epickur.api.aop.ValidateComplexAccessRights;
import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.here.IGeocoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epickur.api.enumeration.EndpointType.DISH;
import static com.epickur.api.enumeration.Operation.DELETE;
import static com.epickur.api.enumeration.Operation.UPDATE;

/**
 * {@link Dish} business layer. Execute logic and access {@link DishDAO} layer to update the database.
 *
 * @author cph
 * @version 1.0
 */
@Service
public class DishService {

	@Autowired
	private IGeocoder geocoder;
	/**
	 * The DAO {@link DishDAO}.
	 */
	@Autowired
	private DishDAO dao;

	/**
	 * Create a {@link Dish}
	 *
	 * @param dish the {@link Dish}
	 * @return the {@link Dish} created
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public Dish create(final Dish dish) throws EpickurException {
		dish.prepareForInsertionIntoDB();
		return dao.create(dish);
	}

	/**
	 * Read a {@link Dish}
	 *
	 * @param id the id of the {@link Dish}
	 * @return the {@link Dish}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public Dish read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all Dishes
	 *
	 * @return a list of {@link Dish}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public final List<Dish> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * Update a {@link Dish}
	 *
	 * @param dish The {@link Dish}
	 * @return the updated {@link Dish}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = UPDATE, type = DISH)
	public Dish update(final Dish dish) throws EpickurException {
		dish.prepareForUpdateIntoDB();
		return dao.update(dish);
	}

	/**
	 * Delete a {@link Dish}
	 *
	 * @param id the id of the {@link Dish}
	 * @return true if the {@link Dish} has been deleted
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = DELETE, type = DISH)
	public boolean delete(final String id) throws EpickurException {
		return dao.delete(id);
	}

	/**
	 * Search all dishes for one caterer.
	 *
	 * @param catererId The caterer id
	 * @return A list of {@link Dish}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public List<Dish> searchDishesForOneCaterer(final String catererId) throws EpickurException {
		return dao.searchWithCatererId(catererId);
	}

	/**
	 * Search a list of Dish
	 *
	 * @param day        The day
	 * @param minutes    The minutes
	 * @param type       The type of Dish
	 * @param limit      The number max of result
	 * @param searchtext The address
	 * @param geo        The geo location
	 * @param distance   The distance
	 * @return A list of Dish
	 * @throws EpickurException If an epickur exception occurred
	 */
	public List<Dish> search(final String day, final Integer minutes, final List<DishType> type, final Integer limit, final Geo geo,
			final String searchtext, final int distance) throws EpickurException {
		if (geo == null) {
			final Geo geoFound = geocoder.getPosition(searchtext);
			return dao.search(day, minutes, type, limit, geoFound, distance);
		} else {
			return dao.search(day, minutes, type, limit, geo, distance);
		}
	}
}
