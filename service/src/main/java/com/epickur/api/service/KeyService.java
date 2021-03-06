package com.epickur.api.service;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Key business layer. Access Key DAO layer and execute logic.
 *
 * @author cph
 * @version 1.0
 */
@AllArgsConstructor(onConstructor = @_(@Autowired))
@Service
public class KeyService {

	/**
	 * Key dao
	 */
	@NonNull
	private KeyDAO keyDao;

	/**
	 * Create a new Key
	 *
	 * @param key the Key
	 * @return the Key created
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public Key create(final Key key) throws EpickurException {
		key.prepareForInsertionIntoDB();
		return keyDao.create(key);
	}

	/**
	 * Read a Key with its name
	 *
	 * @param name the name of the Key
	 * @return the Key found
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public Key readWithName(final String name) throws EpickurException {
		return keyDao.readWithName(name);
	}

	/**
	 * Delete a Key with its id
	 *
	 * @param id the id of the Key
	 * @return a boolean
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		return keyDao.delete(id);
	}

	public boolean deleteWithKey(final String key) throws EpickurException {
		return keyDao.deleteWithKey(key);
	}

	/**
	 * @return A list of Key
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public List<Key> readAll() throws EpickurException {
		return keyDao.readAll();
	}
}
