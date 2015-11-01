package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.CatererDAOImpl;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.FactoryValidator;

/**
 * {@link Caterer} business layer. Execute logic and access {@link CatererDAOImpl} layer to update the database.
 * 
 * @author cph
 * @version 1.0
 */
public class CatererBusiness {

	/** The DAO {@link CatererDAOImpl} */
	private CatererDAOImpl dao;
	/** The validator {@link CatererValidator} */
	private CatererValidator validator;

	/**
	 * Construct a Caterer Business
	 */
	public CatererBusiness() {
		this.dao = new CatererDAOImpl();
		this.validator = (CatererValidator) FactoryValidator.getValidator("caterer");
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param dao
	 *            the dao.
	 */
	public CatererBusiness(final CatererDAOImpl dao) {
		this.dao = dao;
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
	public Caterer create(final Caterer caterer) throws EpickurException {
		caterer.prepareForInsertionIntoDB();
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
	public Caterer read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all the Caterers
	 * 
	 * @return a list of {@link Caterer}
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public List<Caterer> readAll() throws EpickurException {
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
	public Caterer update(final Caterer caterer, final Key key) throws EpickurException {
		Caterer read = dao.read(caterer.getId().toHexString());
		if (read != null) {
			validator.checkRightsAfter(key.getRole(), key.getUserId(), read, Operation.UPDATE);
			caterer.prepareForUpdateIntoDB();
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
	public boolean delete(final String id) throws EpickurException {
		return dao.delete(id);
	}

	/**
	 * @param orders
	 *            The orders
	 * @return The addition of all orders amount
	 */
	public Integer getTotalAmountSuccessful(final List<Order> orders) {
		Integer amount = Integer.valueOf(0);
		for (Order order : orders) {
			if (order.getStatus() == OrderStatus.SUCCESSFUL) {
				amount += order.getAmount();
			}
		}
		return amount;
	}
}
