package com.epickur.api.entity.times;

import com.epickur.api.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hours
 *
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "mon", "tue", "wed", "thu", "fri", "sat", "sun" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class Hours extends AbstractEntity {

	/**
	 * Monday
	 */
	private List<TimeFrame> mon;
	/**
	 * Tuesday
	 */
	private List<TimeFrame> tue;
	/**
	 * Wednesday
	 */
	private List<TimeFrame> wed;
	/**
	 * Thursday
	 */
	private List<TimeFrame> thu;
	/**
	 * Friday
	 */
	private List<TimeFrame> fri;
	/**
	 * Saturday
	 */
	private List<TimeFrame> sat;
	/**
	 * Sunday
	 */
	private List<TimeFrame> sun;

	/**
	 * @param prefix The prefix
	 * @return A updated map
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		final Map<String, Object> res = new HashMap<>();
		if (fri != null) {
			final List<Object> objects = fri.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".fri", objects);
		}
		if (mon != null) {
			final List<Object> objects = mon.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".mon", objects);
		}
		if (tue != null) {
			final List<Object> objects = tue.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".tue", objects);
		}
		if (wed != null) {
			final List<Object> objects = wed.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".wed", objects);
		}
		if (thu != null) {
			List<Object> objects = thu.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".thu", objects);
		}
		if (sat != null) {
			final List<Object> objects = sat.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".sat", objects);
		}
		if (sun != null) {
			final List<Object> objects = sun.stream().map(TimeFrame::getUpdateMap).collect(Collectors.toList());
			res.put(prefix + ".sun", objects);
		}
		return res;
	}

	/**
	 * @param str The day
	 * @return A list of time frame
	 */
	@JsonIgnore
	public List<TimeFrame> get(final String str) {
		if ("mon".equals(str)) {
			return getMon();
		} else if ("tue".equals(str)) {
			return getTue();
		} else if ("wed".equals(str)) {
			return getWed();
		} else if ("thu".equals(str)) {
			return getThu();
		} else if ("fri".equals(str)) {
			return getFri();
		} else if ("sat".equals(str)) {
			return getSat();
		} else if ("sun".equals(str)) {
			return getSun();
		}
		return null;
	}
}
