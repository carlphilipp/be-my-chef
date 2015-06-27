package com.epickur.api.business;

import java.util.List;

import com.epickur.api.dao.mongo.DishDaoImpl;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.enumeration.DishType;
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

	/** Dish dao */
	private DishDaoImpl dishDao;

	/** The constructor */
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
	 * @param searchtext
	 *            The address
	 * @param geo
	 *            The geo location
	 * @param distance
	 *            The distance
	 * @return A list of Dish
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public List<Dish> search(final String day, final Integer minutes, final List<DishType> type, final Integer limit, final Geo geo,
			final String searchtext, final int distance)
			throws EpickurException {
		if (geo == null) {
			IGeocoder geocoder = new GeocoderHereImpl();
			Geo geoFound = geocoder.getPosition(searchtext);
			return dishDao.search(day, minutes, type, limit, geoFound, distance);
		} else {
			return dishDao.search(day, minutes, type, limit, geo, distance);
		}
	}
}
