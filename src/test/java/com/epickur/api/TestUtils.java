package com.epickur.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.DishBusiness;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Location;
import com.epickur.api.entity.NutritionFact;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.MeasurementUnit;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.integration.SearchIntegrationTest;
import com.epickur.api.utils.ObjectMapperWrapperAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;

public class TestUtils {
	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(TestUtils.class.getSimpleName());

	private static final String[] pickupdateDays = new String[] { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };

	public static void setupDB() throws IOException {
		InputStreamReader in = new InputStreamReader(SearchIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		in.close();

		String mongoPath = prop.getProperty("mongo.path");
		String mongoAddress = prop.getProperty("mongo.address");
		String mongoPort = prop.getProperty("mongo.port");
		String mongoDbName = prop.getProperty("mongo.db.name");
		String scriptSetupPath = prop.getProperty("script.setup");

		String cmd = mongoPath + " " + mongoAddress + ":" + mongoPort + "/" + mongoDbName + " " + scriptSetupPath;
		TestUtils.runShellCommand(cmd);
	}

	public static void cleanDB() throws IOException {
		InputStreamReader in = new InputStreamReader(SearchIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		in.close();

		String mongoPath = prop.getProperty("mongo.path");
		String mongoAddress = prop.getProperty("mongo.address");
		String mongoPort = prop.getProperty("mongo.port");
		String mongoDbName = prop.getProperty("mongo.db.name");
		String scriptCleanPath = prop.getProperty("script.clean");

		String cmd = mongoPath + " " + mongoAddress + ":" + mongoPort + "/" + mongoDbName + " " + scriptCleanPath;
		TestUtils.runShellCommand(cmd);
	}

	public static Caterer getCaererObject(final String json) throws EpickurException {
		Caterer caterer = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
			caterer = mapper.readValue(json, Caterer.class);
		} catch (IOException e) {
			LOG.error("Error: " + e.getLocalizedMessage(), e);
			throw new EpickurException();
		}
		return caterer;
	}

	public static String convertListToStringIngredients(List<Ingredient> ingredients) {
		final OutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
		try {
			mapper.writeValue(out, ingredients);
			final byte[] data = ((ByteArrayOutputStream) out).toByteArray();
			return new String(data);
		} catch (IOException e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		return null;
	}

	public static String convertListToStringNutritionFacts(List<NutritionFact> nutritionFacts) {
		final OutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
		try {
			mapper.writeValue(out, nutritionFacts);
			final byte[] data = ((ByteArrayOutputStream) out).toByteArray();
			return new String(data);
		} catch (IOException e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		return null;
	}

	public static String convertListToStringSteps(List<String> steps) {
		final OutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
		try {
			mapper.writeValue(out, steps);
			final byte[] data = ((ByteArrayOutputStream) out).toByteArray();
			return new String(data);
		} catch (IOException e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		return null;
	}

	public static void runShellCommand(final String cmd) throws IOException {
		LOG.debug("Executing: " + cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		InputStream is = p.getInputStream();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				LOG.debug(line);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	public static List<NutritionFact> getListObject(final String json) throws EpickurException {
		final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
		try {
			return mapper.readValue(json, new TypeReference<List<NutritionFact>>() {
			});
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public static Caterer generateRandomCatererWithId() {
		Caterer caterer = generateRandomCatererWithoutId();
		caterer.setId(new ObjectId());
		return caterer;
	}

	public static Caterer generateRandomCatererWithoutId() {
		Caterer caterer = new Caterer();
		caterer.setDescription(generateRandomString());
		caterer.setEmail(generateRandomString());
		caterer.setLocation(generateRandomLocation());
		caterer.setManager(generateRandomString());
		caterer.setName(generateRandomString());
		caterer.setPhone(generateRandomString());
		caterer.setCreatedBy(new ObjectId());
		caterer.setWorkingTimes(generateRandomWorkingTime());
		return caterer;
	}

	private static WorkingTimes generateRandomWorkingTime() {
		WorkingTimes workingTimes = new WorkingTimes();
		int num = RandomUtils.nextInt(0, 300);
		workingTimes.setMinimumPreparationTime(num);
		workingTimes.setHours(generateRandomHours());
		return workingTimes;
	}

	private static Hours generateRandomHours() {
		Hours hours = new Hours();
		hours.setMon(generateRandomTimeFrame());
		hours.setFri(generateRandomTimeFrame());
		hours.setSat(generateRandomTimeFrame());
		hours.setSun(generateRandomTimeFrame());
		hours.setThu(generateRandomTimeFrame());
		hours.setTue(generateRandomTimeFrame());
		hours.setWed(generateRandomTimeFrame());
		return hours;
	}

	private static List<TimeFrame> generateRandomTimeFrame() {
		List<TimeFrame> timeFrames = new ArrayList<TimeFrame>();
		TimeFrame timeFrame1 = new TimeFrame();
		timeFrame1.setOpen(RandomUtils.nextInt(350, 600));
		timeFrame1.setClose(RandomUtils.nextInt(700, 900));
		timeFrames.add(timeFrame1);

		TimeFrame timeFrame2 = new TimeFrame();
		timeFrame2.setOpen(RandomUtils.nextInt(1020, 1080));
		timeFrame2.setClose(RandomUtils.nextInt(1320, 1440));
		timeFrames.add(timeFrame2);
		return timeFrames;
	}

	public static String generateRandomPickupDate() {
		DecimalFormat formatter = new DecimalFormat("00");
		int index = RandomUtils.nextInt(0, pickupdateDays.length - 1);
		String selected = pickupdateDays[index];
		int hours = RandomUtils.nextInt(0, 23);
		int minutes = RandomUtils.nextInt(0, 59);
		String hoursFormatted = formatter.format(hours);
		String minutesFormatted = formatter.format(minutes);
		return selected + "-" + hoursFormatted + ":" + minutesFormatted;
	}

	public static Location generateRandomLocation() {
		Location location = new Location();
		location.setAddress(generateRandomAddress());
		location.setGeo(generateGeo());
		return location;
	}

	public static Address generateRandomAddress() {
		Address address = new Address();
		address.setCity(generateRandomString());
		address.setCountry(generateRandomString());
		address.setHouseNumber(generateRandomString());
		address.setLabel(generateRandomString());
		address.setPostalCode(60614);
		address.setState(generateRandomString());
		address.setStreet(generateRandomString());
		return address;
	}

	public static Geo generateGeo() {
		Geo geo = new Geo();
		geo.setLatitude(41.92901);
		geo.setLongitude(-87.650276);
		return geo;
	}

	public static Dish generateRandomDish() {
		Dish dish = new Dish();
		dish.setCaterer(generateRandomCatererWithId());
		dish.setCookingTime(generateRandomInteger());
		dish.setDescription(generateRandomString());
		dish.setDifficultyLevel(generateRandomInteger());
		dish.setIngredients(generateRandomListIngredient());
		dish.setName(generateRandomString());
		dish.setNutritionFacts(generateRandomListNutritionFact());
		dish.setPrice(generateRandomInteger());
		dish.setSteps(generateRandomListString());
		dish.setType(generateRandomDishType());
		dish.setImageAfterUrl(generateRandomString());
		dish.setVideoUrl(generateRandomString());
		dish.setImageAfterUrl(generateRandomString());
		dish.setCreatedBy(new ObjectId());
		return dish;
	}

	public static DishType generateRandomDishType() {
		DishType[] types = DishType.values();
		int max = RandomUtils.nextInt(0, types.length - 1);
		return types[max];
	}

	public static List<NutritionFact> generateRandomListNutritionFact() {
		int max = RandomUtils.nextInt(1, 5);
		List<NutritionFact> res = new ArrayList<NutritionFact>();
		for (int i = 0; i < max; i++) {
			res.add(generateRandomNutritionFact());
		}
		return res;
	}

	public static NutritionFact generateRandomNutritionFact() {
		NutritionFact nu = new NutritionFact();
		nu.setName(generateRandomString());
		nu.setUnit(getRandomMeasurementUnit());
		nu.setValue(generateRandomDouble());
		return nu;
	}

	public static List<Ingredient> generateRandomListIngredient() {
		int max = RandomUtils.nextInt(1, 5);
		List<Ingredient> res = new ArrayList<Ingredient>();
		for (int i = 0; i < max; i++) {
			res.add(generateRandomIngredient());
		}
		return res;
	}

	public static Ingredient generateRandomIngredient() {
		Ingredient ing = new Ingredient();
		ing.setMeasurementUnit(getRandomMeasurementUnit());
		ing.setName(generateRandomString());
		ing.setQuantity(generateRandomInteger());
		ing.setSequence(generateRandomInteger());
		return ing;
	}

	public static MeasurementUnit getRandomMeasurementUnit() {
		int max = MeasurementUnit.values().length;
		int num = RandomUtils.nextInt(0, max);
		return MeasurementUnit.values()[num];
	}

	public static List<String> generateRandomListString() {
		int max = RandomUtils.nextInt(1, 5);
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < max; i++) {
			res.add(generateRandomString());
		}
		return res;
	}

	public static String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	public static Integer generateRandomInteger() {
		return RandomUtils.nextInt(0, 500);
	}

	public static Integer generateRandomStripAmount() {
		return RandomUtils.nextInt(100, 5000);
	}

	public static Double generateRandomDouble() {
		Integer res = generateRandomInteger();
		return res.doubleValue();
	}

	public static Currency generateRandomCurrency() {
		int max = Currency.values().length;
		int num = RandomUtils.nextInt(0, max);
		return Currency.values()[num];
	}

	public static User generateRandomUser() {
		User user = new User();
		user.setAllow(0);
		user.setEmail(generateRandomString());
		user.setName(generateRandomString());
		user.setPassword(generateRandomString());
		return user;
	}

	public static Order generateRandomOrder() {
		Order order = new Order();
		order.setAmount(generateRandomStripAmount());
		order.setCurrency(generateRandomCurrency());
		order.setDescription(generateRandomString());
		order.setDish(generateRandomDish());
		order.setCreatedBy(new ObjectId());
		return order;
	}

	public static Token generateRandomToken() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);

		Token token = Token.create(tokenParams);
		return token;
	}

	public static Key generateRandomKey() {
		Key key = new Key();
		key.setKey(generateRandomString());
		key.setUserId(new ObjectId());
		key.setRole(Role.ADMIN);
		return key;
	}

	public static Caterer createCaterer() throws EpickurException {
		return createCatererWithUserId(new ObjectId());
	}

	public static Caterer createCatererWithUserId(final ObjectId userId) throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		return createCaterer(caterer, userId);
	}

	public static Caterer createCaterer(Caterer caterer, ObjectId userId) throws EpickurException {
		CatererBusiness business = new CatererBusiness();
		caterer.setCreatedBy(userId);
		return business.create(caterer);
	}

	public static User createUser() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		UserBusiness business = new UserBusiness();
		return business.create(user, false, true);
	}

	public static User createUserAndLogin() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = new String(user.getPassword());
		UserBusiness business = new UserBusiness();
		User newUser = business.create(user, false, true);
		newUser.setRole(Role.USER);
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.ADMIN);
		business.update(newUser, key);
		User login = business.login(newUser.getEmail(), password);
		return login;
	}

	public static User createSuperUser() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = new String(user.getPassword());
		UserBusiness business = new UserBusiness();
		User newUser = business.create(user, false, true);
		newUser.setRole(Role.SUPER_USER);
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.ADMIN);
		business.update(newUser, key);
		User login = business.login(newUser.getEmail(), password);
		return login;
	}

	public static Dish createDish() throws EpickurException {
		return createDishWithUserId(new ObjectId());
	}

	public static Dish createDishWithUserId(final ObjectId userId) throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.setCreatedBy(userId);
		DishBusiness business = new DishBusiness();
		Dish newDish = business.create(dish);
		return newDish;
	}

	public static Order createOrder(ObjectId userId) throws EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException,
			CardException, APIException {
		Token token = TestUtils.generateRandomToken();
		Order order = TestUtils.generateRandomOrder();
		OrderBusiness business = new OrderBusiness();
		Order orderRes = business.create(userId.toHexString(), order, token.getId(), false);
		return orderRes;
	}

	public static Order createOrder(ObjectId userId, ObjectId catererId) throws AuthenticationException, InvalidRequestException,
			APIConnectionException,
			CardException, APIException, EpickurException {
		Token token = TestUtils.generateRandomToken();
		Order order = TestUtils.generateRandomOrder();
		order.getDish().getCaterer().setId(catererId);
		OrderBusiness business = new OrderBusiness();
		Order orderRes = business.create(userId.toHexString(), order, token.getId(), false);
		return orderRes;
	}
}
