package com.epickur.api.dao;

import java.util.List;

import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurException;

/**
 * @author carl
 * @version 1.0
 * @param <T>
 *            Must be an AbstractEntity
 */
public interface ICrudDAO<T extends AbstractEntity> {

	/**
	 * Create an object
	 * 
	 * @param obj
	 *            The object to create
	 * @return The object created
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	T create(T obj) throws EpickurException;

	/**
	 * Read an object
	 * 
	 * @param id
	 *            The id of the object to read
	 * @return The object found
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	T read(String id) throws EpickurException;

	/**
	 * Update an object
	 * 
	 * @param obj
	 *            The object to update
	 * @return The object updated
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	T update(T obj) throws EpickurException;

	/**
	 * Delete an object
	 * 
	 * @param id
	 *            The id of the object to delete
	 * @return True if the object has been deleted
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	boolean delete(String id) throws EpickurException;

	/**
	 * Read all
	 * 
	 * @return A list containing all the objects
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	List<T> readAll() throws EpickurException;

}