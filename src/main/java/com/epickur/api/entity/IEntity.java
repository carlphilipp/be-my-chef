package com.epickur.api.entity;

import org.bson.Document;

import com.epickur.api.exception.EpickurException;

/**
 * Entity interface
 * 
 * @author cph
 * @version 1.0
 */
interface IEntity extends Cloneable {
	/**
	 * Get an API view of the current object. For instance will provide field id instead of _id
	 * 
	 * @return a DBObject
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	Document getAPIView() throws EpickurException;

	/**
	 * Get an API view of the current object. For instance will provide field _id instead of id
	 * 
	 * @return a DBObject
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	Document getDBView() throws EpickurException;
}
