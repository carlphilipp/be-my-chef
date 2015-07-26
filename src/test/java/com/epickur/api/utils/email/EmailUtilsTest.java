package com.epickur.api.utils.email;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.dao.mongo.SequenceDaoImpl;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Security;

public class EmailUtilsTest {

	private static final String EMAIL_TEST = "cp.harmant@gmail.com";
	
	private SequenceDaoImpl dao = new SequenceDaoImpl();

	@Test
	public void emailNewRegistrationTest() {
		EmailUtils.emailNewRegistration("carl", "Carl-Philipp", "codeeddd", EMAIL_TEST);
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
		EmailUtils.emailNewOrder(user, order, orderCode);
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
		EmailUtils.emailDeclineOrder(user, order);
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
		EmailUtils.emailSuccessOrder(user, order);
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
		EmailUtils.emailFailOrder(user, order);
	}
	
	@Test
	public void emailCancelOrder() throws EpickurDBException{
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
		EmailUtils.emailCancelOrder(user, order);
	}
	
	@Test
	public void emailResetPassword() throws EpickurException{
		EmailUtils.resetPassword("cp.harmant@gmail.com", new ObjectId().toHexString(), Security.generateRandomMd5());
	}
}
