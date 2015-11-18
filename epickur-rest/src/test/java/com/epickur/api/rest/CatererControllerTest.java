package com.epickur.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.entity.message.PayementInfoMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripeTestUtils;
import com.epickur.api.report.Report;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.service.OrderService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(CatererController.class)
public class CatererControllerTest {

	@Mock
	private CatererService catererService;
	@Mock
	private OrderService orderService;
	@Mock
	private DishService dishService;
	@Mock
	private ContainerRequestContext context;
	@Mock
	private Report report;
	@InjectMocks
	private CatererController controller;

	@BeforeClass
	public static void setUpBeforeClass() {
		StripeTestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StripeTestUtils.resetStripe();
	}

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.create((Caterer) anyObject())).thenReturn(catererAfterCreate);

		Response actual = controller.create(caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualUser = (Caterer) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.read(anyString())).thenReturn(catererAfterCreate);

		Response actual = controller.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualCaterer = (Caterer) actual.getEntity();
		assertNotNull(actualCaterer.getId());
	}

	@Test
	public void testReadCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.read(anyString())).thenReturn(null);

		Response actual = controller.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		List<Caterer> caterers = new ArrayList<Caterer>();
		caterers.add(catererAfterCreate);

		when(catererService.readAll()).thenReturn(caterers);

		Response actual = controller.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<Caterer> actuals = (List<Caterer>) actual.getEntity();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readDishes() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Dish dish = EntityGenerator.generateRandomDish();
		List<Dish> dishes = new ArrayList<Dish>();
		dishes.add(dish);

		when(dishService.searchDishesForOneCaterer(anyString())).thenReturn(dishes);

		Response actual = controller.readDishes(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<Dish> actuals = (List<Dish>) actual.getEntity();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");

		when(catererService.update((Caterer) anyObject(), (Key) anyObject())).thenReturn(catererAfterCreate);

		Response actual = controller.update(caterer.getId().toHexString(), caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualCaterer = (Caterer) actual.getEntity();
		assertNotNull(actualCaterer);
		assertNotNull(actualCaterer.getId());
		assertEquals("new desc", actualCaterer.getDescription());
	}

	@Test
	public void testUpdateCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");

		when(catererService.update((Caterer) anyObject(), (Key) anyObject())).thenReturn(null);

		Response actual = controller.update(caterer.getId().toHexString(), caterer);
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testDelete() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(catererService.delete(anyString())).thenReturn(true);

		Response actual = controller.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(catererService.delete(anyString())).thenReturn(false);

		Response actual = controller.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoPdf() throws Exception {
		try {
			Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
			Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
			Order order = EntityGenerator.generateRandomOrderWithId();
			List<Order> orders = new ArrayList<Order>();
			orders.add(order);

			when(catererService.read(anyString())).thenReturn(catererAfterCreate);
			when(orderService.readAllWithCatererId(anyString(), (DateTime) anyObject(), (DateTime) anyObject())).thenReturn(orders);
			when(catererService.getTotalAmountSuccessful((List<Order>) anyObject())).thenReturn(150);
			Key key = EntityGenerator.generateRandomAdminKey();
			when(context.getProperty("key")).thenReturn(key);
			when(context.getMediaType()).thenReturn(MediaType.APPLICATION_XML_TYPE);
			when(report.getReport()).thenReturn(new byte[10]);
			whenNew(Report.class).withNoArguments().thenReturn(report);

			Response actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), null, null, null);
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			assertEquals("attachment; filename =" + catererAfterCreate.getId().toHexString() + ".pdf", actual.getHeaderString("content-disposition"));
			assertEquals("application/pdf", actual.getMediaType().toString());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(EntityGenerator.STRIPE_MESSAGE);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoJson() throws Exception {
		try {
			Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
			Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
			Order order = EntityGenerator.generateRandomOrderWithId();
			List<Order> orders = new ArrayList<Order>();
			orders.add(order);

			when(catererService.read(anyString())).thenReturn(catererAfterCreate);
			when(orderService.readAllWithCatererId(anyString(), (DateTime) anyObject(), (DateTime) anyObject())).thenReturn(orders);
			when(catererService.getTotalAmountSuccessful((List<Order>) anyObject())).thenReturn(150);
			Key key = EntityGenerator.generateRandomAdminKey();
			when(context.getProperty("key")).thenReturn(key);
			when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
			when(report.getReport()).thenReturn(new byte[10]);
			whenNew(Report.class).withNoArguments().thenReturn(report);

			Response actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			assertEquals("application/json", actual.getMediaType().toString());
			PayementInfoMessage actualMessage = (PayementInfoMessage) actual.getEntity();
			assertNotNull(actualMessage);
			assertEquals(catererAfterCreate.getId().toHexString(), actualMessage.getId());
			assertEquals(catererAfterCreate.getName(), actualMessage.getName());
			assertEquals(150, actualMessage.getAmount().intValue());
			assertEquals("01/01/2015", actualMessage.getStart());
			assertEquals("01/01/2016", actualMessage.getEnd());
			assertEquals("MM/dd/yyyy", actualMessage.getFormat());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(EntityGenerator.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testPaymentInfoCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		Order order = EntityGenerator.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);

		when(catererService.read(anyString())).thenReturn(null);

		Response actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}
}
