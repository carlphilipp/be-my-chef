package com.epickur.api.entity.times;

import java.util.HashMap;
import java.util.Map;

import com.epickur.api.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * TimeFrame
 * 
 * @author cph
 * @version 1.0
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class TimeFrame extends AbstractEntity {

	/** Open Time value 0 - 1440 */
	private int open;
	/** Open Time value 0 - 1440 */
	private int close;

	/**
	 * @return The updated map
	 */
	@JsonIgnore
	public Map<String, String> getUpdateMap() {
		Map<String, String> res = new HashMap<>();
		res.put("open", open + "");
		res.put("close", close + "");
		return res;
	}
}
