package com.epickur.api.service;

import com.epickur.api.annotation.ValidateComplexAccessRights;
import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Order;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.epickur.api.enumeration.EndpointType.CATERER;
import static com.epickur.api.enumeration.Operation.UPDATE;

/**
 * {@link Caterer} business layer. Execute logic and access {@link CatererDAO} layer to update the database.
 *
 * @author cph
 * @version 1.0
 */
@AllArgsConstructor(onConstructor = @_(@Autowired))
@Service
public class CatererService {

	/**
	 * The DAO {@link CatererDAO}
	 */
	@NonNull
	private CatererDAO dao;

	/**
	 * Create a {@link Caterer}
	 *
	 * @param caterer The {@link Caterer}
	 * @return The {@link Caterer} created
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public Caterer create(final Caterer caterer) throws EpickurException {
		caterer.prepareForInsertionIntoDB();
		return dao.create(caterer);
	}

	/**
	 * Read a {@link Caterer}
	 *
	 * @param id The id of the {@link Caterer}
	 * @return a {@link Caterer}
	 * @throws EpickurException If an epickur exception occurred
	 */
	public Optional<Caterer> read(final String id) throws EpickurException {
		return dao.read(id);
	}

	/**
	 * Read all the Caterers
	 *
	 * @return a list of {@link Caterer}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public List<Caterer> readAll() throws EpickurException {
		return dao.readAll();
	}

	/**
	 * @param caterer The {@link Caterer}
	 * @return The updated {@link Caterer}
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = UPDATE, type = CATERER)
	public Caterer update(final Caterer caterer) throws EpickurException {
		caterer.prepareForUpdateIntoDB();
		return dao.update(caterer);
	}

	/**
	 * Delete a {@link Caterer}
	 *
	 * @param id The id of the {@link Caterer}
	 * @return true if the {@link Caterer} has been deleted
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		return dao.delete(id);
	}

	/**
	 * @param orders The orders
	 * @return The addition of all orders amount
	 */
	public Integer getTotalAmountSuccessful(final List<Order> orders) {
		final AtomicInteger amount = new AtomicInteger();
		orders.stream()
			.filter(order -> order.getStatus() == OrderStatus.SUCCESSFUL)
			.forEach(order -> amount.addAndGet(order.getAmount()));
		return amount.get();
	}
}
