package com.epickur.api.utils.email;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

public class EmailUtilsTest {

	private static final String EMAIL_TEST = "example@example.com";
	@Mock
	private Email email;
	@Inject
	private String[] admins = new String [0];
	@Mock
	private MandrillMessagesRequest messagesRequest;
	@Mock
	private EmailTemplate emailTemplate;
	@InjectMocks
	private EmailUtils emailUtils;

	@Before
	public void setUp() throws EpickurDBException {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void emailNewRegistrationTest() {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());
		emailUtils.emailNewRegistration(user, user.getCode());
	}

	@Test
	public void emailNewOrderTest() throws EpickurException {
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
		emailUtils.emailNewOrder(user, order, orderCode);
	}

	@Test
	public void emailDeclineOrderTest() throws EpickurDBException {
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
		emailUtils.emailDeclineOrder(user, order);
	}

	@Test
	public void emailSuccessOrderTest() throws EpickurDBException {
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
		emailUtils.emailSuccessOrder(user, order);
	}

	@Test
	public void emailFailOrderTest() throws EpickurDBException {
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
		emailUtils.emailFailOrder(user, order);
	}

	@Test
	public void emailCancelOrder() throws EpickurDBException {
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
		emailUtils.emailCancelOrder(user, order);
	}

	@Test
	public void emailResetPassword() throws EpickurException {
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);
		user.setId(new ObjectId());
		
		emailUtils.resetPassword(user, Security.generateRandomMd5());
	}
}
