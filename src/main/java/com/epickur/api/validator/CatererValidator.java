package com.epickur.api.validator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Location;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;

/**
 * @author cph
 * @version 1.0
 */
public final class CatererValidator extends Validator {

	/**
	 * Constructor
	 */
	protected CatererValidator() {
		super("caterer");
	}

	/**
	 * @param caterer
	 *            The Caterer
	 */
	public void checkCreateCaterer(final Caterer caterer) {
		checkCaterer(caterer);
	}

	/**
	 * @param id
	 *            The Caterer id
	 */
	public void checkId(final String id) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
	}

	/**
	 * @param id
	 *            The caterer Id
	 * @param caterer
	 *            The Caterer
	 */
	public void checkUpdateCaterer(final String id, final Caterer caterer) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		checkCaterer(caterer);
		if (caterer.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
		if (!caterer.getId().toHexString().equals(id)) {
			throw new EpickurIllegalArgument("The parameter id and the field caterer.id should match");
		}
	}

	/**
	 * @param id
	 *            The Caterer Id
	 * @param start
	 *            The start date
	 * @param end
	 *            The end date
	 */
	public void checkPaymentInfo(final String id, final DateTime start, final DateTime end) {
		checkId(id);
		if (start == null && end != null) {
			throw new EpickurIllegalArgument("Start date missing");
		}
		if (start != null) {
			DateTime today = new DateTime();
			if (start.isAfter(today)) {
				throw new EpickurIllegalArgument("The start date can not be after today");
			}
		}
		if (start != null && end != null && end.isBefore(start)) {
			throw new EpickurIllegalArgument("The end date should be after the start date");
		}
	}

	/**
	 * @param caterer
	 *            The caterer to check
	 */
	protected void checkCaterer(final Caterer caterer) {
		checkCaterer(caterer, null);
	}

	/**
	 * @param caterer
	 *            The caterer to check
	 * @param prefix
	 *            The prefix
	 */
	protected void checkCaterer(final Caterer caterer, final String prefix) {
		String entity = getEntity();
		if (prefix != null) {
			entity = prefix + "." + entity;
		}
		if (caterer == null) {
			throw new EpickurIllegalArgument(NO_CATERER_PROVIDED);
		}
		if (StringUtils.isBlank(caterer.getName())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "name"));
		}
		if (StringUtils.isBlank(caterer.getDescription())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "description"));
		}
		if (StringUtils.isBlank(caterer.getManager())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "manager"));
		}
		if (StringUtils.isBlank(caterer.getEmail())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "email"));
		}
		if (StringUtils.isBlank(caterer.getPhone())) {
			throw new EpickurIllegalArgument(fieldNull(entity, "phone"));
		}
		if (caterer.getLocation() == null) {
			throw new EpickurIllegalArgument(fieldNull(entity, "location"));
		} else {
			Location location = caterer.getLocation();
			if (location.getGeo() == null) {
				throw new EpickurIllegalArgument(fieldNull(entity, "location.geo"));
			} else {
				Geo geo = location.getGeo();
				if (geo.getLatitude() == null) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.geo.latitude"));
				}
				if (geo.getLongitude() == null) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.geo.longitude"));
				}
			}
			if (location.getAddress() == null) {
				throw new EpickurIllegalArgument(fieldNull(entity, "location.address"));
			} else {
				Address address = location.getAddress();
				if (StringUtils.isBlank(address.getCity())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.city"));
				}
				if (StringUtils.isBlank(address.getCountry())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.country"));
				}
				if (StringUtils.isBlank(address.getHouseNumber())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.housenumber"));
				}
				if (StringUtils.isBlank(address.getLabel())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.label"));
				}
				if (address.getPostalCode() == null) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.postalcode"));
				}
				if (StringUtils.isBlank(address.getState())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.state"));
				}
				if (StringUtils.isBlank(address.getStreet())) {
					throw new EpickurIllegalArgument(fieldNull(entity, "location.address.street"));
				}
			}
		}
		checkWorkingHours(entity, caterer.getWorkingTimes());
	}

	private void checkWorkingHours(final String entity, final WorkingTimes workingTimes) {
		if (workingTimes == null) {
			throw new EpickurIllegalArgument(fieldNull(entity, "workingTimes"));
		} else {
			if (workingTimes.getHours() == null) {
				throw new EpickurIllegalArgument(fieldNull(entity, "workingTimes.hours"));
			} else {
				Hours hours = workingTimes.getHours();
				List<TimeFrame> mon = hours.getMon();
				List<TimeFrame> tue = hours.getTue();
				List<TimeFrame> wed = hours.getWed();
				List<TimeFrame> thu = hours.getThu();
				List<TimeFrame> fri = hours.getFri();
				List<TimeFrame> sat = hours.getSat();
				List<TimeFrame> sun = hours.getSun();
				if (mon != null || tue != null || wed != null || thu != null || fri != null || sat != null || sun != null) {
					if (mon != null) {
						checkTimeFrames(entity, ".mon", mon);
					}
					if (tue != null) {
						checkTimeFrames(entity, ".tue", tue);
					}
					if (wed != null) {
						checkTimeFrames(entity, ".wed", wed);
					}
					if (thu != null) {
						checkTimeFrames(entity, ".thu", thu);
					}
					if (fri != null) {
						checkTimeFrames(entity, ".fri", fri);
					}
					if (sat != null) {
						checkTimeFrames(entity, ".sat", sat);
					}
					if (sun != null) {
						checkTimeFrames(entity, ".sun", sun);
					}
				}
			}
		}
	}

	private void checkTimeFrames(final String entity, final String suffix, final List<TimeFrame> timeFrames) {
		int i = 0;
		for (final TimeFrame tf : timeFrames) {
			if (tf.getOpen() > tf.getClose()) {
				throw new EpickurIllegalArgument(fieldNull(entity, "workingTimes.hours" + suffix + "[" + i + "]"));
			}
			i++;
		}
	}

	/**
	 * @param role
	 *            The role
	 * @param userId
	 *            The User Id
	 * @param caterer
	 *            The Caterer
	 * @param action
	 *            The action
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	public void checkRightsAfter(final Role role, final ObjectId userId, final Caterer caterer, final Crud action) throws EpickurException {
		if (role != Role.ADMIN) {
			if (action != Crud.READ) {
				if (action == Crud.UPDATE && role == Role.SUPER_USER) {
					if (!caterer.getCreatedBy().equals(userId)) {
						throw new EpickurForbiddenException();
					}
				} else {
					throw new EpickurException("Rights issue. This case should not happen");
				}
			}
		}
	}
}
