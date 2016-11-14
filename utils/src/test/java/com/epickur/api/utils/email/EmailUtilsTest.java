package com.epickur.api.utils.email;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.security.Security;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

@RunWith(MockitoJUnitRunner.class)
public class EmailUtilsTest {

	private static final String EMAIL_TEST = "example@example.com";
	@Mock
	private Email email;
	@Inject
	private String[] admins = new String[0];
	@Mock
	private MandrillMessagesRequest messagesRequest;
	@Mock
	private EmailTemplate emailTemplate;
	@Mock
	private EpickurProperties epickurProperties;
	@InjectMocks
	private EmailUtils emailUtils;

	@Test
	public void emailNewRegistrationTest() {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());

		// When
		// When
		emailUtils.emailNewRegistration(user, user.getCode());
	}

	@Test
	public void emailNewOrderTest() throws EpickurException {
		// Given
		User user = new User();
		user.setName("carl");
		user.setId(new ObjectId());
		user.setEmail(EMAIL_TEST);
		Order order = new Order();
		order.setId(new ObjectId());
		order.setQuantity(1);
		order.setAmount(15);
		order.setCurrency(Currency.AUD);
		// order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);
		String orderCode = Security.createOrderCode(new ObjectId(), EntityGenerator.generateRandomString());

		// When
		emailUtils.emailNewOrder(user, order, orderCode);
	}

	@Test
	public void emailDeclineOrderTest() throws EpickurDBException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		// order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);

		// When
		emailUtils.emailDeclineOrder(user, order);
	}

	@Test
	public void emailSuccessOrderTest() throws EpickurDBException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		// order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);

		// When
		emailUtils.emailSuccessOrder(user, order);
	}

	@Test
	public void emailFailOrderTest() throws EpickurDBException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		// order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);

		// When
		emailUtils.emailFailOrder(user, order);
	}

	@Test
	public void emailCancelOrder() throws EpickurDBException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		// order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);

		// When
		emailUtils.emailCancelOrder(user, order);
	}

	@Test
	public void emailResetPassword() throws EpickurException {
		// Given
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		user.setId(new ObjectId());

		// When
		emailUtils.resetPassword(user, Security.generateRandomMd5());
	}
}
