package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.KeyDAOImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;

/**
 * Key business layer. Access Key DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public final class KeyBusiness {

	/** Key dao */
	private KeyDAOImpl keyDao;

	/**
	 * The constructor
	 */
	public KeyBusiness() {
		this.keyDao = new KeyDAOImpl();
	}

	/**
	 * Create a new Key
	 * 
	 * @param key
	 *            the Key
	 * @return the Key created
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Key create(final Key key) throws EpickurException {
		key.prepareForInsertionIntoDB();
		return this.keyDao.create(key);
	}

	/**
	 * Read a Key with its name
	 * 
	 * @param name
	 *            the name of the Key
	 * @return the Key found
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Key readWithName(final String name) throws EpickurException {
		return this.keyDao.readWithName(name);
	}

	/**
	 * Delete a Key with its id
	 * 
	 * @param id
	 *            the id of the Key
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public void delete(final String id) throws EpickurException {
		this.keyDao.delete(id);
	}
	
	public void deleteWithKey(final String key) throws EpickurException{
		this.keyDao.deleteWithKey(key);
	}

	/**
	 * @return A list of Key
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public List<Key> readAll() throws EpickurException {
		return this.keyDao.readAll();
	}
}
