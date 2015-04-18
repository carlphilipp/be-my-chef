package com.epickur.api.business;

import java.util.List;

import org.bson.types.ObjectId;

import com.epickur.api.dao.mongo.CatererDaoImpl;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Order;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.FactoryValidator;

/**
 * Caterer business layer. Access Caterer DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public class CatererBusiness {

	/** Caterer dao **/
	private CatererDaoImpl dao;
	/** Caterer validator **/
	private CatererValidator validator;

	/**
	 * The constructor
	 */
	public CatererBusiness() {
		this.dao = new CatererDaoImpl();
		this.validator = (CatererValidator) FactoryValidator.getValidator("caterer");
	}

	/**
	 * Create a Caterer
	 * 
	 * @param caterer
	 *            The Caterer
	 * @return the Caterer created
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Caterer create(final Caterer caterer) throws EpickurException {
		return dao.create(caterer);
	}

	/**
	 * Read a Caterer
	 * 
	 * @param id
	 *            The id of the Caterer
	 * @return a Caterer
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Caterer read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all the Caterers
	 * 
	 * @return a list of Caterer
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final List<Caterer> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * Update a Caterer
	 * 
	 * @param caterer
	 *            The Caterer
	 * @return a Caterer
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Caterer update(final Caterer caterer, final Role role, final ObjectId userId) throws EpickurException {
		Caterer read = dao.read(caterer.getId().toHexString());
		if (read != null) {
			validator.checkRightsAfter(role, userId, read, Crud.UPDATE);
			return dao.update(caterer);
		}
		return read;
	}

	/**
	 * Delete a Caterer
	 * 
	 * @param id
	 *            The id of the Caterer
	 * @return true if the Caterer has been deleted
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final boolean delete(final String id) throws EpickurException {
		return dao.delete(id);
	}

	/**
	 * @param orders
	 *            The orders
	 * @return The addition of all orders amount
	 */
	public final Integer getAmount(final List<Order> orders) {
		Integer amount = new Integer(0);
		for (Order order : orders) {
			amount += order.getAmount();
		}
		return amount;
	}
}
