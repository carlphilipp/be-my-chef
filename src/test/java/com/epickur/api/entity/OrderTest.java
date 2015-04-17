package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.TestUtils;

public class OrderTest {

	@Test
	public void testOrder() {
		Order order = TestUtils.generateRandomOrder();
		Order order2 = order.clone();

		assertEquals(order.hashCode(), order2.hashCode());
		assertEquals(order, order2);

		Order key3 = order2;
		assertEquals(order, key3);
		assertFalse(order.equals(null));
		assertFalse(order.equals(new User()));
	}
}
