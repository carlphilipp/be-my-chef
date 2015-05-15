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
 * {@link Caterer} business layer. Execute logic and access {@link CatererDaoImpl} layer to update the database.
 * 
 * @author cph
 * @version 1.0
 */
public class CatererBusiness {

	/** The DAO {@link CatererDaoImpl} **/
	private CatererDaoImpl dao;
	/** The validator {@link CatererValidator} **/
	private CatererValidator validator;

	/**
	 * Construct a Caterer Business
	 */
	public CatererBusiness() {
		this.dao = new CatererDaoImpl();
		this.validator = (CatererValidator) FactoryValidator.getValidator("caterer");
	}

	/**
	 * Create a {@link Caterer}
	 * 
	 * @param caterer
	 *            The {@link Caterer}
	 * @return The {@link Caterer} created
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public final Caterer create(final Caterer caterer) throws EpickurException {
		return dao.create(caterer);
	}

	/**
	 * Read a {@link Caterer}
	 * 
	 * @param id
	 *            The id of the {@link Caterer}
	 * @return a {@link Caterer}
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final Caterer read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all the Caterers
	 * 
	 * @return a list of {@link Caterer}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public final List<Caterer> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * @param caterer
	 *            The {@link Caterer}
	 * @param role
	 *            The Role
	 * @param userId
	 *            The User Id
	 * @return The updated {@link Caterer}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
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
	 * Delete a {@link Caterer}
	 * 
	 * @param id
	 *            The id of the {@link Caterer}
	 * @return true if the {@link Caterer} has been deleted
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
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
		Integer amount = Integer.valueOf(0);
		for (Order order : orders) {
			amount += order.getAmount();
		}
		return amount;
	}
}
