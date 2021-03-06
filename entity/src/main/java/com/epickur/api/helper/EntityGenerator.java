package com.epickur.api.helper;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.*;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.enumeration.*;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ObjectMapperWrapperAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stripe.exception.*;
import com.stripe.model.Token;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Log4j2
public class EntityGenerator {

	public static final String STRIPE_MESSAGE = "Fail while acquiring Stripe token. Internet issue?";

	private static final String[] pickupdateDays = new String[]{"mon", "tue", "wed", "thu", "fri", "sat", "sun"};

	public static Caterer getCatererObject(final String json) throws EpickurException {
		Caterer caterer;
		try {
			final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
			caterer = mapper.readValue(json, Caterer.class);
		} catch (IOException e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
			throw new EpickurException();
		}
		return caterer;
	}

	public static List<NutritionFact> getListObject(final String json) {
		final ObjectMapper mapper = ObjectMapperWrapperAPI.getInstance();
		try {
			return mapper.readValue(json, new TypeReference<List<NutritionFact>>() {
			});
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
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
		caterer.setWorkingTimes(generateRandomWorkingTimes());
		return caterer;
	}

	public static WorkingTimes generateRandomWorkingTimes() {
		WorkingTimes workingTimes = new WorkingTimes();
		int num = RandomUtils.nextInt(0, 60);
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
		List<TimeFrame> timeFrames = new ArrayList<>();
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

	public static Voucher generateVoucher() {
		Voucher voucher = new Voucher();
		voucher.setDiscountType(DiscountType.AMOUNT);
		voucher.setDiscount(5);
		voucher.setCode(generateRandomString());
		voucher.setExpiration(new DateTime());
		voucher.setStatus(Status.VALID);
		return voucher;
	}

	public static Voucher mockVoucherAfterCreate(final Voucher voucher) {
		Voucher mockVoucher = voucher.clone();
		DateTime now = new DateTime();
		mockVoucher.setCreatedAt(now);
		mockVoucher.setUpdatedAt(now);
		return mockVoucher;
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

	public static Dish generateRandomDishWithId() {
		Dish dish = generateRandomDish();
		dish.setId(new ObjectId());
		return dish;
	}

	public static DishType generateRandomDishType() {
		DishType[] types = DishType.values();
		int max = RandomUtils.nextInt(0, types.length - 1);
		return types[max];
	}

	public static List<NutritionFact> generateRandomListNutritionFact() {
		int max = RandomUtils.nextInt(1, 5);
		List<NutritionFact> res = new ArrayList<>();
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
		List<Ingredient> res = new ArrayList<>();
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
		List<String> res = new ArrayList<>();
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
		user.setEmail(generateRandomString() + "@" + generateRandomString() + ".com");
		user.setName(generateRandomString());
		user.setFirst(generateRandomString());
		user.setLast(generateRandomString());
		user.setPassword(generateRandomString());
		user.setPhoneNumber(generateRandomPhoneNumber());
		user.setRole(Role.USER);
		user.setState("Illinois");
		user.setZipcode("60614");
		user.setCountry("USA");
		return user;
	}

	public static User generateRandomUserWithId() {
		User user = generateRandomUser();
		user.setId(new ObjectId());
		return user;
	}

	public static User mockUserAfterCreate(final User user) {
		User userMock = user.clone();
		userMock.setId(new ObjectId());
		DateTime now = new DateTime();
		userMock.setCreatedAt(now);
		userMock.setUpdatedAt(now);
		userMock.setCode(generateRandomString());
		return userMock;
	}

	public static Dish mockDishAfterCreate(final Dish dish) {
		Dish dishMock = dish.clone();
		dishMock.setId(new ObjectId());
		DateTime now = new DateTime();
		dishMock.setCreatedAt(now);
		dishMock.setUpdatedAt(now);
		return dishMock;
	}

	public static Caterer mockCatererAfterCreate(final Caterer caterer) {
		Caterer catererMock = caterer.clone();
		catererMock.setId(new ObjectId());
		DateTime now = new DateTime();
		catererMock.setCreatedAt(now);
		catererMock.setUpdatedAt(now);
		return catererMock;
	}

	public static Order mockOrderAfterCreate(final Order order, final Token token) {
		Order orderAfterCreate = order.clone();
		orderAfterCreate.setId(new ObjectId());
		orderAfterCreate.setCardToken(token.getId());
		DateTime now = new DateTime();
		orderAfterCreate.setCreatedAt(now);
		return orderAfterCreate;
	}

	private static PhoneNumber generateRandomPhoneNumber() {
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setCountryCode(61);
		phoneNumber.setNationalNumber(700000000 + RandomUtils.nextInt(10000000, 99999999));
		// phoneNumber.setNationalNumber(761231585);
		// phoneNumber.setNationalNumber(754944084);
		return phoneNumber;
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

	public static Order generateRandomOrder() {
		Order order = new Order();
		order.setQuantity(2);
		order.setAmount(generateRandomStripAmount());
		order.setCurrency(generateRandomCurrency());
		order.setDescription(generateRandomString());
		order.setDish(generateRandomDish());
		order.setCreatedBy(new ObjectId());

		order.setCardToken(generateRandomString());

		String pickupdate = generateRandomPickupDate();
		order.setPickupdate(pickupdate);
		Optional<Object[]> parsedPickupdateOptional = CommonsUtil.parsePickupdate(pickupdate);
		if (parsedPickupdateOptional.isPresent()) {
			Object[] parsedPickupdate = parsedPickupdateOptional.get();
			String day = (String) parsedPickupdate[0];
			Integer pickupdateMinutes = (Integer) parsedPickupdate[1];

			WorkingTimes workingTimes = order.getDish().getCaterer().getWorkingTimes();
			while (!workingTimes.canBePickup(day, pickupdateMinutes)) {
				WorkingTimes temp = generateRandomWorkingTimes();
				order.getDish().getCaterer().setWorkingTimes(temp);
				workingTimes = order.getDish().getCaterer().getWorkingTimes();

				pickupdate = generateRandomPickupDate();
				order.setPickupdate(pickupdate);
				parsedPickupdateOptional = CommonsUtil.parsePickupdate(pickupdate);
				if (parsedPickupdateOptional.isPresent()) {
					parsedPickupdate = parsedPickupdateOptional.get();
					day = (String) parsedPickupdate[0];
					pickupdateMinutes = (Integer) parsedPickupdate[1];
				}
			}
			order.setStatus(OrderStatus.SUCCESSFUL);
		}
		return order;
	}

	public static Order generateRandomOrderWithId() {
		Order order = generateRandomOrder();
		order.setId(new ObjectId());
		return order;
	}

	public static Order mockOrderAfterCreate(final Order order) {
		Order orderMock = order.clone();
		orderMock.setId(new ObjectId());
		return orderMock;
	}

	public static Token generateRandomToken() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		Map<String, Object> tokenParams = new HashMap<>();
		Map<String, Object> cardParams = new HashMap<>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);

		return Token.create(tokenParams);
	}

	public static Key generateRandomAdminKey() {
		Key key = new Key();
		key.setKey(generateRandomString());
		key.setUserId(new ObjectId());
		key.setRole(Role.ADMIN);
		return key;
	}

	public static Key mockKeyAfterCreate(final Key key) {
		Key keyMock = key.clone();
		keyMock.setId(new ObjectId());
		DateTime now = new DateTime();
		keyMock.setCreatedAt(now);
		keyMock.setUpdatedAt(now);
		return keyMock;
	}

	public static Key generateRandomUserKey() {
		Key key = new Key();
		key.setKey(generateRandomString());
		key.setUserId(new ObjectId());
		key.setRole(Role.USER);
		return key;
	}

	public static Map<String, Object> getTokenParam() {
		Map<String, Object> tokenParams = new HashMap<>();
		Map<String, Object> cardParams = new HashMap<>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		return tokenParams;
	}
}
