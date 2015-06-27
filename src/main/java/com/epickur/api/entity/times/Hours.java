package com.epickur.api.entity.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.epickur.api.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Hours
 * 
 * @author cph
 * @version 1.0
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"})
public final class Hours extends AbstractEntity {

	/** Monday */
	private List<TimeFrame> mon;
	/** Tuesday */
	private List<TimeFrame> tue;
	/** Wednesday */
	private List<TimeFrame> wed;
	/** Thursday */
	private List<TimeFrame> thu;
	/** Friday */
	private List<TimeFrame> fri;
	/** Saturday */
	private List<TimeFrame> sat;
	/** Sunday */
	private List<TimeFrame> sun;

	/**
	 * @return
	 */
	public final List<TimeFrame> getMon() {
		return mon;
	}

	/**
	 * @param mon
	 */
	public final void setMon(final List<TimeFrame> mon) {
		this.mon = mon;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getTue() {
		return tue;
	}

	/**
	 * @param tue
	 */
	public final void setTue(final List<TimeFrame> tue) {
		this.tue = tue;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getWed() {
		return wed;
	}

	/**
	 * @param wed
	 */
	public final void setWed(final List<TimeFrame> wed) {
		this.wed = wed;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getThu() {
		return thu;
	}

	/**
	 * @param thu
	 */
	public final void setThu(final List<TimeFrame> thu) {
		this.thu = thu;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getFri() {
		return fri;
	}

	/**
	 * @param fri
	 */
	public final void setFri(final List<TimeFrame> fri) {
		this.fri = fri;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getSat() {
		return sat;
	}

	/**
	 * @param sat
	 */
	public final void setSat(final List<TimeFrame> sat) {
		this.sat = sat;
	}

	/**
	 * @return
	 */
	public final List<TimeFrame> getSun() {
		return sun;
	}

	/**
	 * @param sun
	 */
	public final void setSun(final List<TimeFrame> sun) {
		this.sun = sun;
	}

	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (fri != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : fri) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".fri", objects);
		}
		if (mon != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : mon) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".mon", objects);
		}
		if (tue != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : tue) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".tue", objects);
		}
		if (wed != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : wed) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".wed", objects);
		}
		if (thu != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : thu) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".thu", objects);
		}
		if (sat != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : sat) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".sat", objects);
		}
		if (sun != null) {
			List<Object> objects = new ArrayList<Object>();
			for (TimeFrame tf : sun) {
				objects.add(tf.getUpdateMap());
			}
			res.put(prefix + ".sun", objects);
		}
		return res;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fri == null) ? 0 : fri.hashCode());
		result = prime * result + ((mon == null) ? 0 : mon.hashCode());
		result = prime * result + ((sat == null) ? 0 : sat.hashCode());
		result = prime * result + ((sun == null) ? 0 : sun.hashCode());
		result = prime * result + ((thu == null) ? 0 : thu.hashCode());
		result = prime * result + ((tue == null) ? 0 : tue.hashCode());
		result = prime * result + ((wed == null) ? 0 : wed.hashCode());
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
		if (!(obj instanceof Hours)) {
			return false;
		}
		Hours other = (Hours) obj;
		if (fri == null) {
			if (other.fri != null) {
				return false;
			}
		} else if (!fri.equals(other.fri)) {
			return false;
		}
		if (mon == null) {
			if (other.mon != null) {
				return false;
			}
		} else if (!mon.equals(other.mon)) {
			return false;
		}
		if (sat == null) {
			if (other.sat != null) {
				return false;
			}
		} else if (!sat.equals(other.sat)) {
			return false;
		}
		if (sun == null) {
			if (other.sun != null) {
				return false;
			}
		} else if (!sun.equals(other.sun)) {
			return false;
		}
		if (thu == null) {
			if (other.thu != null) {
				return false;
			}
		} else if (!thu.equals(other.thu)) {
			return false;
		}
		if (tue == null) {
			if (other.tue != null) {
				return false;
			}
		} else if (!tue.equals(other.tue)) {
			return false;
		}
		if (wed == null) {
			if (other.wed != null) {
				return false;
			}
		} else if (!wed.equals(other.wed)) {
			return false;
		}
		return true;
	}
}
