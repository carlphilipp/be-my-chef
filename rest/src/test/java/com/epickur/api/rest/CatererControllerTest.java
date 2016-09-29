package com.epickur.api.rest;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.entity.message.PayementInfoMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.service.OrderService;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.report.Report;
import com.stripe.exception.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
	private HttpServletRequest context;
	@Mock
	private Report report;
	@Mock
	private Utils utils;
	@InjectMocks
	private CatererController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
		when(context.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.create(isA(Caterer.class))).thenReturn(catererAfterCreate);

		ResponseEntity<?> actual = controller.create(caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualUser = (Caterer) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.read(anyString())).thenReturn(Optional.of(catererAfterCreate));

		ResponseEntity<?> actual = controller.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualCaterer = (Caterer) actual.getBody();
		assertNotNull(actualCaterer.getId());
	}

	@Test
	public void testReadCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);

		when(catererService.read(anyString())).thenReturn(Optional.empty());

		ResponseEntity<?> actual = controller.read(catererAfterCreate.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		List<Caterer> caterers = new ArrayList<>();
		caterers.add(catererAfterCreate);

		when(catererService.readAll()).thenReturn(caterers);

		ResponseEntity<?> actual = controller.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Caterer> actuals = (List<Caterer>) actual.getBody();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readDishes() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Dish dish = EntityGenerator.generateRandomDish();
		List<Dish> dishes = new ArrayList<>();
		dishes.add(dish);

		when(dishService.searchDishesForOneCaterer(anyString())).thenReturn(dishes);

		ResponseEntity<?> actual = controller.readDishes(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Dish> actuals = (List<Dish>) actual.getBody();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");

		when(catererService.update(isA(Caterer.class))).thenReturn(catererAfterCreate);

		ResponseEntity<?> actual = controller.update(caterer.getId().toHexString(), caterer);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualCaterer = (Caterer) actual.getBody();
		assertNotNull(actualCaterer);
		assertNotNull(actualCaterer.getId());
		assertEquals("new desc", actualCaterer.getDescription());
	}

	@Test
	public void testDelete() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(catererService.delete(anyString())).thenReturn(true);

		ResponseEntity<?> actual = controller.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteCatererNotFound() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(catererService.delete(anyString())).thenReturn(false);

		ResponseEntity<?> actual = controller.delete(caterer.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoPdf() throws Exception {
		try {
			Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
			Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
			Order order = EntityGenerator.generateRandomOrderWithId();
			List<Order> orders = new ArrayList<>();
			orders.add(order);

			when(catererService.read(anyString())).thenReturn(Optional.of(catererAfterCreate));
			when(orderService.readAllWithCatererId(anyString(), isA(DateTime.class), isA(DateTime.class))).thenReturn(orders);
			when(catererService.getTotalAmountSuccessful(isA(List.class))).thenReturn(150);
			Key key = EntityGenerator.generateRandomAdminKey();
			when(context.getAttribute("key")).thenReturn(key);
			when(context.getContentType()).thenReturn(MediaType.APPLICATION_XML.toString());
			when(report.getReport()).thenReturn(new byte[10]);
			whenNew(Report.class).withNoArguments().thenReturn(report);

			ResponseEntity<?> actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), null, null, null);
			assertNotNull(actual);
			assertEquals(200, actual.getStatusCode().value());
			assertEquals("attachment; filename =" + catererAfterCreate.getId().toHexString() + ".pdf",
					actual.getHeaders().getFirst("content-disposition"));
			assertEquals("application/pdf", actual.getHeaders().getContentType().toString());
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
			List<Order> orders = new ArrayList<>();
			orders.add(order);

			when(catererService.read(anyString())).thenReturn(Optional.of(catererAfterCreate));
			when(orderService.readAllWithCatererId(anyString(), isA(DateTime.class), isA(DateTime.class))).thenReturn(orders);
			when(catererService.getTotalAmountSuccessful(isA(List.class))).thenReturn(150);
			Key key = EntityGenerator.generateRandomAdminKey();
			when(context.getAttribute("key")).thenReturn(key);
			when(context.getContentType()).thenReturn(MediaType.APPLICATION_JSON.toString());
			when(report.getReport()).thenReturn(new byte[10]);
			whenNew(Report.class).withNoArguments().thenReturn(report);

			ResponseEntity<?> actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");
			assertNotNull(actual);
			assertEquals(200, actual.getStatusCode().value());
			assertEquals("application/json", actual.getHeaders().getContentType().toString());
			PayementInfoMessage actualMessage = (PayementInfoMessage) actual.getBody();
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
}
