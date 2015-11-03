package com.epickur.api;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.epickur.api.integration.AccessRightsCatererIT;
import com.epickur.api.integration.AccessRightsDishIT;
import com.epickur.api.integration.AccessRightsOrderIT;
import com.epickur.api.integration.AccessRightsUserIT;
import com.epickur.api.integration.CatererIT;
import com.epickur.api.integration.DishIT;
import com.epickur.api.integration.OrderDAOIT;
import com.epickur.api.integration.StripePaymentIT;
import com.epickur.api.integration.UserIT;

@RunWith(Suite.class)
@SuiteClasses({
		AccessRightsUserIT.class,
		AccessRightsCatererIT.class,
		AccessRightsDishIT.class,
		AccessRightsOrderIT.class,
		CatererIT.class,
		DishIT.class,
		StripePaymentIT.class,
		UserIT.class,
		OrderDAOIT.class
})
public class IntegrationTests {
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		TestUtils.cleanDB();
	}
}
