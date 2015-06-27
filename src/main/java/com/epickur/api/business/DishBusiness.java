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
 * {@link Dish} business layer. Execute logic and access {@link DishDaoImpl} layer to update the database.
 * 
 * @author cph
 * @version 1.0
 */
public class DishBusiness {

	/** The DAO {@link DishDaoImpl}. */
	private DishDaoImpl dao;
	/** The validator {@link DishValidator}. */
	private DishValidator validator;

	/**
	 * Construct a Dish Business.
	 */
	public DishBusiness() {
		this.dao = new DishDaoImpl();
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
	public final Dish create(final Dish dish) throws EpickurException {
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
	public final Dish read(final String id) throws EpickurException {
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
	public final Dish update(final Dish dish, final Key key) throws EpickurException {
		Dish read = dao.read(dish.getId().toHexString());
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Crud.UPDATE);
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
	public final boolean delete(final String id, final Key key) throws EpickurException {
		Dish read = dao.read(id);
		validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Crud.DELETE);
		return dao.delete(id);
	}
}
