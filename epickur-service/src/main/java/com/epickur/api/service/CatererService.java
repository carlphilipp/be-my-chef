package com.epickur.api.service;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.validator.CatererValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Caterer} business layer. Execute logic and access {@link CatererDAO} layer to update the database.
 * 
 * @author cph
 * @version 1.0
 */
@Service
public class CatererService {

	/** The DAO {@link CatererDAO} */
	@Autowired
	private CatererDAO dao;
	/** The validator {@link CatererValidator} */
	@Autowired
	private CatererValidator validator;

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
	 * @param key
	 *            The Key
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
