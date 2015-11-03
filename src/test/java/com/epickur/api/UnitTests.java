package com.epickur.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.epickur.api.business.CatererBusinessTest;
import com.epickur.api.business.DishBusinessTest;
import com.epickur.api.business.KeyBusinessTest;
import com.epickur.api.business.OrderBusinessTest;
import com.epickur.api.business.UserBusinessTest;
import com.epickur.api.business.VoucherBusinessTest;
import com.epickur.api.dao.mongo.OrderDAOTest;
import com.epickur.api.entity.AddressTest;
import com.epickur.api.entity.CatererTest;
import com.epickur.api.entity.DishTest;
import com.epickur.api.entity.GeoTest;
import com.epickur.api.entity.IngredientTest;
import com.epickur.api.entity.KeyTest;
import com.epickur.api.entity.LocationTest;
import com.epickur.api.entity.NutritionFactTest;
import com.epickur.api.entity.OrderTest;
import com.epickur.api.entity.UserTest;
import com.epickur.api.entity.WorkingTimesTest;
import com.epickur.api.entity.databind.DateSerializerTest;
import com.epickur.api.entity.databind.ObjectIdDeserializerTest;
import com.epickur.api.entity.databind.ObjectIdSerializerTest;
import com.epickur.api.enumeration.CurrencyTest;
import com.epickur.api.enumeration.MeasurementUnitTest;
import com.epickur.api.exception.mapper.EpickurDBExceptionMapperTest;
import com.epickur.api.exception.mapper.EpickurExceptionMapperTest;
import com.epickur.api.exception.mapper.EpickurForbiddenExceptionMapperTest;
import com.epickur.api.exception.mapper.EpickurIOExceptionMapperTest;
import com.epickur.api.exception.mapper.EpickurIllegalArgumentMapperTest;
import com.epickur.api.exception.mapper.IllegalArgumentExceptionMapperTest;
import com.epickur.api.exception.mapper.JsonMappingExceptionMapperTest;
import com.epickur.api.exception.mapper.NotAllowedExceptionMapperTest;
import com.epickur.api.exception.mapper.NotFoundExceptionMapperTest;
import com.epickur.api.exception.mapper.ThrowableExeptionMapperTest;
import com.epickur.api.exception.mapper.UnrecognizedPropertyExceptionMapperTest;
import com.epickur.api.filter.HeaderResponseFilterTest;
import com.epickur.api.filter.KeyRequestFilterTest;
import com.epickur.api.filter.LogRequestFilterTest;
import com.epickur.api.geocoder.here.GeocoderHereImplTest;
import com.epickur.api.geocoder.here.HereTest;
import com.epickur.api.payment.stripe.StripePaymentTest;
import com.epickur.api.service.CatererServiceTest;
import com.epickur.api.service.DishServiceTest;
import com.epickur.api.service.LoginServiceTest;
import com.epickur.api.service.LogoutServiceTest;
import com.epickur.api.service.NoKeyServiceTest;
import com.epickur.api.service.UserServiceTest;
import com.epickur.api.utils.UtilsTest;
import com.epickur.api.utils.email.EmailTest;
import com.epickur.api.utils.email.EmailUtilsTest;
import com.epickur.api.validator.AccessRightsCatererTest;
import com.epickur.api.validator.AccessRightsDishTest;
import com.epickur.api.validator.AccessRightsOrderTest;
import com.epickur.api.validator.AccessRightsUserTest;
import com.epickur.api.validator.AccessRightsVoucherTest;
import com.epickur.api.validator.CatererValidatorTest;
import com.epickur.api.validator.DishValidatorTest;
import com.epickur.api.validator.UserValidatorTest;
import com.epickur.api.validator.VoucherValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({
		WebApplicationTest.class,

		CatererBusinessTest.class,
		DishBusinessTest.class,
		KeyBusinessTest.class,
		OrderBusinessTest.class,
		UserBusinessTest.class,
		VoucherBusinessTest.class,

		AddressTest.class,
		CatererTest.class,
		DishTest.class,
		GeoTest.class,
		IngredientTest.class,
		KeyTest.class,
		LocationTest.class,
		NutritionFactTest.class,
		OrderTest.class,
		UserTest.class,

		DateSerializerTest.class,
		ObjectIdDeserializerTest.class,
		ObjectIdSerializerTest.class,

		CurrencyTest.class,
		MeasurementUnitTest.class,

		EpickurExceptionMapperTest.class,
		EpickurIllegalArgumentMapperTest.class,
		JsonMappingExceptionMapperTest.class,
		NotFoundExceptionMapperTest.class,
		ThrowableExeptionMapperTest.class,
		UnrecognizedPropertyExceptionMapperTest.class,
		EpickurDBExceptionMapperTest.class,
		EpickurForbiddenExceptionMapperTest.class,
		IllegalArgumentExceptionMapperTest.class,
		EpickurIOExceptionMapperTest.class,
		NotAllowedExceptionMapperTest.class,

		HeaderResponseFilterTest.class,
		LogRequestFilterTest.class,
		KeyRequestFilterTest.class,

		GeocoderHereImplTest.class,
		HereTest.class,

		StripePaymentTest.class,

		CatererServiceTest.class,
		NoKeyServiceTest.class,
		DishServiceTest.class,
		LoginServiceTest.class,
		LogoutServiceTest.class,
		UserServiceTest.class,
		
		OrderDAOTest.class,

		// .utils
		UtilsTest.class,

		// .utils.email
		EmailUtilsTest.class,
		EmailTest.class,

		// .validator
		AccessRightsCatererTest.class,
		AccessRightsDishTest.class,
		AccessRightsOrderTest.class,
		AccessRightsUserTest.class,
		AccessRightsVoucherTest.class,
		CatererValidatorTest.class,
		DishValidatorTest.class,
		UserValidatorTest.class,
		VoucherValidatorTest.class,

		WorkingTimesTest.class
})
public class UnitTests {

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
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
