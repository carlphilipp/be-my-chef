package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.epickur.api.TestUtils;
import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.DishBusiness;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.entity.message.PayementInfoMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.report.Report;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(CatererService.class)
public class CatererServiceTest {

	private CatererService service;
	@Mock
	private CatererBusiness catererBusiness;
	@Mock
	private OrderBusiness orderBusiness;
	@Mock
	private DishBusiness dishBusiness;
	@Mock
	private ContainerRequestContext context;
	@Mock
	private Report report;

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtils.setupStripe();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestUtils.resetStripe();
	}

	@Before
	public void setUp() {
		service = new CatererService(catererBusiness, orderBusiness, dishBusiness, context);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
	}
	
	@After
	public void tearDown() {
		service = null;
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);

		when(catererBusiness.create((Caterer) anyObject())).thenReturn(catererAfterCreate);

		Response actual = service.create(caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualUser = (Caterer) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);

		when(catererBusiness.read(anyString())).thenReturn(catererAfterCreate);

		Response actual = service.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualCaterer = (Caterer) actual.getEntity();
		assertNotNull(actualCaterer.getId());
	}

	@Test
	public void testReadCatererNotFound() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);

		when(catererBusiness.read(anyString())).thenReturn(null);

		Response actual = service.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		List<Caterer> caterers = new ArrayList<Caterer>();
		caterers.add(catererAfterCreate);

		when(catererBusiness.readAll()).thenReturn(caterers);

		Response actual = service.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<Caterer> actuals = (List<Caterer>) actual.getEntity();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readDishes() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Dish dish = TestUtils.generateRandomDish();
		List<Dish> dishes = new ArrayList<Dish>();
		dishes.add(dish);

		when(dishBusiness.searchDishesForOneCaterer(anyString())).thenReturn(dishes);

		Response actual = service.readDishes(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<Dish> actuals = (List<Dish>) actual.getEntity();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");

		when(catererBusiness.update((Caterer) anyObject(), (Key) anyObject())).thenReturn(catererAfterCreate);

		Response actual = service.update(caterer.getId().toHexString(), caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Caterer actualCaterer = (Caterer) actual.getEntity();
		assertNotNull(actualCaterer);
		assertNotNull(actualCaterer.getId());
		assertEquals("new desc", actualCaterer.getDescription());
	}

	@Test
	public void testUpdateCatererNotFound() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");

		when(catererBusiness.update((Caterer) anyObject(), (Key) anyObject())).thenReturn(null);

		Response actual = service.update(caterer.getId().toHexString(), caterer);
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testDelete() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();

		when(catererBusiness.delete(anyString())).thenReturn(true);

		Response actual = service.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteCatererNotFound() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();

		when(catererBusiness.delete(anyString())).thenReturn(false);

		Response actual = service.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoPdf() throws Exception {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		Order order = TestUtils.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);

		when(catererBusiness.read(anyString())).thenReturn(catererAfterCreate);
		when(orderBusiness.readAllWithCatererId(anyString(), (DateTime) anyObject(), (DateTime) anyObject())).thenReturn(orders);
		when(catererBusiness.getTotalAmountSuccessful((List<Order>) anyObject())).thenReturn(150);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_XML_TYPE);
		when(report.getReport()).thenReturn(new byte[10]);
		whenNew(Report.class).withNoArguments().thenReturn(report);

		Response actual = service.paymentInfo(catererAfterCreate.getId().toHexString(), null, null, null);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		assertEquals("attachment; filename =" + catererAfterCreate.getId().toHexString() + ".pdf", actual.getHeaderString("content-disposition"));
		assertEquals("application/pdf", actual.getMediaType().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoJson() throws Exception {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		Order order = TestUtils.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);

		when(catererBusiness.read(anyString())).thenReturn(catererAfterCreate);
		when(orderBusiness.readAllWithCatererId(anyString(), (DateTime) anyObject(), (DateTime) anyObject())).thenReturn(orders);
		when(catererBusiness.getTotalAmountSuccessful((List<Order>) anyObject())).thenReturn(150);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		when(context.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		when(report.getReport()).thenReturn(new byte[10]);
		whenNew(Report.class).withNoArguments().thenReturn(report);

		Response actual = service.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");
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
	}

	@Test
	public void testPaymentInfoCatererNotFound() throws Exception {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = TestUtils.mockCatererAfterCreate(caterer);
		Order order = TestUtils.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);

		when(catererBusiness.read(anyString())).thenReturn(null);

		Response actual = service.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());

	}
}
