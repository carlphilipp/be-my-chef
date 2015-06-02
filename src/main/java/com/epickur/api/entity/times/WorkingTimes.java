package com.epickur.api.entity.times;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Working times
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class WorkingTimes extends AbstractEntity {

	/** Hours **/
	private Hours hours;
	/** Minimum preparation time in minutes **/
	private int minimumPreparationTime;

	/**
	 * Getter
	 * 
	 * @return The hours
	 */
	public final Hours getHours() {
		return hours;
	}

	/**
	 * Setter
	 * 
	 * @param hours
	 *            The hours
	 */
	public final void setHours(final Hours hours) {
		this.hours = hours;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public final int getMinimumPreparationTime() {
		return minimumPreparationTime;
	}

	public static WorkingTimes getObject(final Document obj) throws EpickurParsingException {
		return WorkingTimes.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	public static WorkingTimes getObject(final String json) throws EpickurParsingException {
		WorkingTimes wt = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			wt = mapper.readValue(json, WorkingTimes.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to WorkingTimes: " + json, e);
		}
		return wt;
	}

	public Map<String, Object> getUpdateMapObject(final String prefix) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (hours != null) {
			res.putAll(hours.getUpdateMap(prefix + ".hours"));
		}
		res.put(prefix + ".minimumPreparationTime", minimumPreparationTime);
		return res;
	}

	/**
	 * Setter
	 * 
	 * @param minimumPreparationTime
	 *            The minimun preparation time
	 */
	public final void setMinimumPreparationTime(final int minimumPreparationTime) {
		this.minimumPreparationTime = minimumPreparationTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hours == null) ? 0 : hours.hashCode());
		result = prime * result + minimumPreparationTime;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof WorkingTimes)) {
			return false;
		}
		WorkingTimes other = (WorkingTimes) obj;
		if (hours == null) {
			if (other.hours != null) {
				return false;
			}
		} else if (!hours.equals(other.hours)) {
			return false;
		}
		if (minimumPreparationTime != other.minimumPreparationTime) {
			return false;
		}
		return true;
	}
}
