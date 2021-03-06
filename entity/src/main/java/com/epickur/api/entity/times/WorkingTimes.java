package com.epickur.api.entity.times;

import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Working times
 *
 * @author cph
 * @version 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class WorkingTimes extends AbstractEntity {

	/**
	 * Hours
	 */
	private Hours hours;
	/**
	 * Minimum preparation time in minutes
	 */
	private int minimumPreparationTime;

	/**
	 * @param day               The day
	 * @param pickupdateMinutes The pickup date in minutes
	 * @return true or false
	 */
	@JsonIgnore
	public boolean canBePickup(final String day, final Integer pickupdateMinutes) {
		// Removed preparation time #API-76
		//Integer openTime = null;
		final List<TimeFrame> timeFrames = getHours().get(day);
		assert timeFrames != null;
		return timeFrames
				.parallelStream()
				.anyMatch(timeFrame -> timeFrame.getOpen() <= pickupdateMinutes && timeFrame.getClose() >= pickupdateMinutes);
	}

	/**
	 * @param obj The document
	 * @return The working times
	 * @throws EpickurParsingException If a parsing exception occured
	 */
	public static WorkingTimes getObject(final Document obj) throws EpickurParsingException {
		return WorkingTimes.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json The json
	 * @return The working times
	 * @throws EpickurParsingException If a parsing exception occured
	 */
	public static WorkingTimes getObject(final String json) throws EpickurParsingException {
		WorkingTimes wt;
		try {
			final ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			wt = mapper.readValue(json, WorkingTimes.class);
		} catch (final IOException e) {
			throw new EpickurParsingException("Can not convert string to WorkingTimes: " + json, e);
		}
		return wt;
	}

	/**
	 * @param prefix The prifix
	 * @return The updated map
	 */
	public Map<String, Object> getUpdateMapObject(final String prefix) {
		Map<String, Object> res = new HashMap<>();
		if (hours != null) {
			res.putAll(hours.getUpdateMap(prefix + ".hours"));
		}
		res.put(prefix + ".minimumPreparationTime", minimumPreparationTime);
		return res;
	}

	/**
	 * Setter
	 *
	 * @param minimumPreparationTime The minimun preparation time
	 */
	public void setMinimumPreparationTime(final int minimumPreparationTime) {
		this.minimumPreparationTime = minimumPreparationTime;
	}
}
