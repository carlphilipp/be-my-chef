package com.epickur.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

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
})
public class IntegrationTests {
	
	@BeforeClass
	public static void beforeClass() throws IOException {
		InputStreamReader in = new InputStreamReader(AllTests.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);

		String mongoPath = prop.getProperty("mongo.path");
		String mongoAddress = prop.getProperty("mongo.address");
		String mongoPort = prop.getProperty("mongo.port");
		String mongoDbName = prop.getProperty("mongo.db.name");
		String scriptCleanPath = prop.getProperty("script.clean");
		String cmd = mongoPath + " " + mongoAddress + ":" + mongoPort + "/" + mongoDbName + " " + scriptCleanPath;
		TestUtils.runShellCommand(cmd);
	}
}
