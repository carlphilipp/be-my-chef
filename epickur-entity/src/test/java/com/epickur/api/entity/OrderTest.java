package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class OrderTest {

	@Test
	public void testOrder() {
		Order order = EntityGenerator.generateRandomOrder();
		Order order2 = order.clone();

		assertEquals(order.hashCode(), order2.hashCode());
		assertEquals(order, order2);
	}
}
