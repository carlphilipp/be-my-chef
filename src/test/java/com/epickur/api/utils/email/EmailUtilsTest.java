package com.epickur.api.utils.email;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;

public class EmailUtilsTest {
	
	private static final String EMAIL_TEST = "cp.harmant@gmail.com";

	@Test
	public void emailNewRegistrationTest() {
		EmailUtils.emailNewRegistration("carl", "codeeddd", EMAIL_TEST);
	}
	
	@Test
	public void emailNewOrderTest(){
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);
		EmailUtils.emailNewOrder(user, order);
	}
	
	@Test
	public void emailDeclineOrderTest(){
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
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
	public void emailSuccessOrderTest(){
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
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
	public void emailFailOrderTest(){
		User user = new User();
		user.setName("carl");
		user.setEmail(EMAIL_TEST);

		Order order = new Order();
		order.setId(new ObjectId());
		Dish dish = new Dish();
		dish.setName("Kebab fries");
		Caterer caterer = new Caterer();
		caterer.setEmail(EMAIL_TEST);
		caterer.setName("Kebab");
		dish.setCaterer(caterer);
		order.setDish(dish);
		EmailUtils.emailFailOrder(user, order);
	}
}