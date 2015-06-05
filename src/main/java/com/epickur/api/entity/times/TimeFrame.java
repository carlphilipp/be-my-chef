package com.epickur.api.entity.times;

import java.util.HashMap;
import java.util.Map;

import com.epickur.api.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * TimeFrame
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class TimeFrame extends AbstractEntity {

	/** Open Time value 0 - 1440 **/
	private int open;
	/** Open Time value 0 - 1440 **/
	private int close;

	/**
	 * @return
	 */
	public final int getOpen() {
		return open;
	}

	/**
	 * @param open
	 */
	public final void setOpen(final int open) {
		this.open = open;
	}

	/**
	 * @return
	 */
	public final int getClose() {
		return close;
	}

	/**
	 * @param close
	 */
	public final void setClose(final int close) {
		this.close = close;
	}

	@JsonIgnore
	public Map<String, String> getUpdateMap() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("open", open + "");
		res.put("close", close + "");
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + close;
		result = prime * result + open;
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
		if (!(obj instanceof TimeFrame)) {
			return false;
		}
		TimeFrame other = (TimeFrame) obj;
		if (close != other.close) {
			return false;
		}
		if (open != other.open) {
			return false;
		}
		return true;
	}
}
