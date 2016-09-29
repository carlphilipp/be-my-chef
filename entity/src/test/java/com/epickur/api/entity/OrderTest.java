package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderTest {

	@Test
	public void testOrder() {
		Order order = EntityGenerator.generateRandomOrder();
		Order order2 = order.clone();

		assertEquals(order.hashCode(), order2.hashCode());
		assertEquals(order, order2);
	}
}
