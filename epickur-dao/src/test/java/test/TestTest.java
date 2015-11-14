package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTest {

	@Test
	public void test() {
		Toto test = new Toto();
		test.setId("der");
		assertEquals("der", test.getId());
	}
}
