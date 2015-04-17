package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.DishDaoImpl;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.geocoder.IGeocoder;
import com.epickur.api.geocoder.here.GeocoderHereImpl;

/**
 * Search business layer. Access Dish DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public final class SearchBusiness {

	/** Dish dao **/
	private DishDaoImpl dishDao;

	/** The constructor **/
	public SearchBusiness() {
		this.dishDao = new DishDaoImpl();
	}

	/**
	 * Search a list of Dish
	 * 
	 * @param type
	 *            The type of Dish
	 * @param limit
	 *            The number max of result
	 * @param address
	 *            The address
	 * @param distance
	 *            The distance
	 * @return A list of Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public List<Dish> search(final String type, final Integer limit, final String address, final int distance) throws EpickurException {
		IGeocoder geocoder = new GeocoderHereImpl();
		Geo geo = geocoder.getPosition(address);
		return dishDao.search(type, limit, geo, distance);
	}
}
