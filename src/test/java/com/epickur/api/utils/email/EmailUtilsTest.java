package com.epickur.api.utils.email;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.dao.mongo.SequenceDAOImpl;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Security;

public class EmailUtilsTest extends InitMocks {

	private static final String EMAIL_TEST = "example@example.com";

	private SequenceDAOImpl dao = new SequenceDAOImpl();

	private EmailUtils emailUtils;
	@Mock
	private MandrillMessagesRequest messagesRequest;

	@Before
	public void setUp() {
		Email email = new Email(messagesRequest, false);
		this.emailUtils = new EmailUtils(email);
	}

	@Test
	public void emailNewRegistrationTest() {
		emailUtils.emailNewRegistration("carl", "Carl-Philipp", "codeeddd", EMAIL_TEST);
	}

	@Test
	public void emailNewOrderTest() throws EpickurException {
		User user = new User();
		user.setName("carl");
		user.setId(new ObjectId());
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		order.setReadableId(dao.getNextId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);
		String orderCode = Security.createOrderCode(new ObjectId(), TestUtils.generateRandomString());
		emailUtils.emailNewOrder(user, order, orderCode);
	}

	@Test
	public void emailDeclineOrderTest() throws EpickurDBException {
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		order.setReadableId(dao.getNextId());
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
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		order.setReadableId(dao.getNextId());
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
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		order.setReadableId(dao.getNextId());
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
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		order.setReadableId(dao.getNextId());
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
		emailUtils.resetPassword("cp.harmant@gmail.com", new ObjectId().toHexString(), Security.generateRandomMd5());
	}
}
