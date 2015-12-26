package com.epickur.api.validation;

import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Location;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author cph
 * @version 1.0
 */
public class CatererValidation extends Validation {

	/**
	 * Constructor
	 */
	protected CatererValidation() {
		super("caterer");
	}

	/**
	 * @param caterer The Caterer
	 */
	public void checkCreateCaterer(final Caterer caterer) {
		checkCaterer(caterer);
	}

	/**
	 * @param id      The caterer Id
	 * @param caterer The Caterer
	 */
	public void checkUpdateCaterer(final String id, final Caterer caterer) {
		checkCaterer(caterer);
		if (caterer.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
		if (!caterer.getId().toHexString().equals(id)) {
			throw new EpickurIllegalArgument("The parameter id and the field caterer.id should match");
		}
	}

	/**
	 * @param start The start date
	 * @param end   The end date
	 */
	public void checkPaymentInfo(final DateTime start, final DateTime end) {
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
	 * @param caterer The caterer to check
	 */
	protected void checkCaterer(final Caterer caterer) {
		checkCaterer(caterer, null);
	}

	/**
	 * @param caterer The caterer to check
	 * @param prefix  The prefix
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
			final Location location = caterer.getLocation();
			if (location.getGeo() == null) {
				throw new EpickurIllegalArgument(fieldNull(entity, "location.geo"));
			} else {
				final Geo geo = location.getGeo();
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
				final Address address = location.getAddress();
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

	/**
	 * @param entity       The entity
	 * @param workingTimes The working times
	 */
	private void checkWorkingHours(final String entity, final WorkingTimes workingTimes) {
		if (workingTimes == null) {
			throw new EpickurIllegalArgument(fieldNull(entity, "workingTimes"));
		} else {
			if (workingTimes.getHours() == null) {
				throw new EpickurIllegalArgument(fieldNull(entity, "workingTimes.hours"));
			} else {
				final Hours hours = workingTimes.getHours();
				final List<TimeFrame> mon = hours.getMon();
				final List<TimeFrame> tue = hours.getTue();
				final List<TimeFrame> wed = hours.getWed();
				final List<TimeFrame> thu = hours.getThu();
				final List<TimeFrame> fri = hours.getFri();
				final List<TimeFrame> sat = hours.getSat();
				final List<TimeFrame> sun = hours.getSun();
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

	/**
	 * @param entity     The entity
	 * @param suffix     The suffix
	 * @param timeFrames The time frame
	 */
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
	 * @param role    The role
	 * @param userId  The User Id
	 * @param caterer The Caterer
	 * @param action  The action
	 * @throws EpickurException If an EpickurException occured
	 */
	public void checkRightsAfter(final Role role, final ObjectId userId, final Caterer caterer, final Operation action) throws EpickurException {
		if (role != Role.ADMIN && action != Operation.READ) {
			if (action == Operation.UPDATE && role == Role.SUPER_USER) {
				if (!caterer.getCreatedBy().equals(userId)) {
					throw new EpickurForbiddenException();
				}
			} else {
				throw new EpickurException("Rights issue. This case should not happen");
			}
		}
	}
}
