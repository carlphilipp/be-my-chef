package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.DishDaoImpl;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.FactoryValidator;

/**
 * Dish business layer. Access Dish DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public class DishBusiness {

	/** Dish dao **/
	private DishDaoImpl dao;
	/** Service validator **/
	private DishValidator validator;

	/**
	 * The constructor
	 */
	public DishBusiness() {
		this.dao = new DishDaoImpl();
		this.validator = (DishValidator) FactoryValidator.getValidator("dish");
	}

	/**
	 * Create a Dish
	 * 
	 * @param dish
	 *            the Dish
	 * @return the Dish created
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Dish create(final Dish dish) throws EpickurException {
		return dao.create(dish);
	}

	/**
	 * Read a Dish
	 * 
	 * @param id
	 *            the id of the Dish
	 * @return the Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Dish read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all Dishes
	 * 
	 * @return a list of Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final List<Dish> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * Update a Dish
	 * 
	 * @param dish
	 *            the Dish
	 * @return the updated Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Dish update(final Dish dish, final Key key) throws EpickurException {
		Dish read = dao.read(dish.getId().toHexString());
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Crud.UPDATE);
		return dao.update(dish);
	}

	/**
	 * Delete a Dish
	 * 
	 * @param id
	 *            the id of the Dish
	 * @return true if the Dish has been deleted
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final boolean delete(final String id, final Key key) throws EpickurException {
		Dish read = dao.read(id);
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Crud.DELETE);
		return dao.delete(id);
	}
}
