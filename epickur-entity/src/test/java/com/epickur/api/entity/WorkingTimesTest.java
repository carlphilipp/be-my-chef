package com.epickur.api.entity;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class WorkingTimesTest {

	@Test
	public void testWorkingTimes() {
		WorkingTimes workingTimes = new WorkingTimes();
		workingTimes.setMinimumPreparationTime(26);
		Hours hours = new Hours();
		TimeFrame timeFrame1 = new TimeFrame();
		timeFrame1.setOpen(588);
		timeFrame1.setClose(825);
		TimeFrame timeFrame2 = new TimeFrame();
		timeFrame2.setOpen(1058);
		timeFrame2.setClose(1332);
		List<TimeFrame> sat = new ArrayList<>();
		sat.add(timeFrame1);
		sat.add(timeFrame2);
		hours.setSat(sat);
		workingTimes.setHours(hours);

		String pickupdate = "sat-07:12";
		Optional<Object[]> objectOptional = CommonsUtil.parsePickupdate(pickupdate);
		if (objectOptional.isPresent()) {
			Object[] object = objectOptional.get();
			assertFalse(workingTimes.canBePickup((String) object[0], (Integer) object[1]));
		}
	}
}
