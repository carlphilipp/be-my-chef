package com.epickur.api.entity;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.helper.EntityGenerator;

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
		Object[] object = EntityGenerator.parsePickupdate(pickupdate);
		assertFalse(workingTimes.canBePickup((String) object[0], (Integer) object[1]));
	}
}
