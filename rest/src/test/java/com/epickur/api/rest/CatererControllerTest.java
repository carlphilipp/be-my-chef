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
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
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
		given(context.getAttribute("key")).willReturn(key);
		given(context.getContentType()).willReturn(MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		given(catererService.create(isA(Caterer.class))).willReturn(catererAfterCreate);

		// When
		ResponseEntity<?> actual = controller.create(caterer);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualUser = (Caterer) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		given(catererService.read(anyString())).willReturn(Optional.of(catererAfterCreate));

		// When
		ResponseEntity<?> actual = controller.read(catererAfterCreate.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualCaterer = (Caterer) actual.getBody();
		assertNotNull(actualCaterer.getId());
	}

	@Test
	public void testReadCatererNotFound() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		given(catererService.read(anyString())).willReturn(Optional.empty());

		// When
		ResponseEntity<?> actual = controller.read(catererAfterCreate.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		List<Caterer> caterers = new ArrayList<>();
		caterers.add(catererAfterCreate);
		given(catererService.readAll()).willReturn(caterers);

		// When
		ResponseEntity<?> actual = controller.readAll();

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Caterer> actuals = (List<Caterer>) actual.getBody();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readDishes() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Dish dish = EntityGenerator.generateRandomDish();
		List<Dish> dishes = new ArrayList<>();
		dishes.add(dish);
		given(dishService.searchDishesForOneCaterer(anyString())).willReturn(dishes);

		// When
		ResponseEntity<?> actual = controller.readDishes(caterer.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Dish> actuals = (List<Dish>) actual.getBody();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		catererAfterCreate.setDescription("new desc");
		given(catererService.update(isA(Caterer.class))).willReturn(catererAfterCreate);

		// When
		ResponseEntity<?> actual = controller.update(caterer.getId().toHexString(), caterer);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Caterer actualCaterer = (Caterer) actual.getBody();
		assertNotNull(actualCaterer);
		assertNotNull(actualCaterer.getId());
		assertEquals("new desc", actualCaterer.getDescription());
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(catererService.delete(anyString())).willReturn(true);

		// When
		ResponseEntity<?> actual = controller.delete(caterer.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteCatererNotFound() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(catererService.delete(anyString())).willReturn(false);

		// When
		ResponseEntity<?> actual = controller.delete(caterer.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoPdf() throws Exception {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		Order order = EntityGenerator.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<>();
		orders.add(order);
		given(catererService.read(anyString())).willReturn(Optional.of(catererAfterCreate));
		given(orderService.readAllWithCatererId(anyString(), isA(DateTime.class), isA(DateTime.class))).willReturn(orders);
		given(catererService.getTotalAmountSuccessful(isA(List.class))).willReturn(150);
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
		given(context.getContentType()).willReturn(MediaType.APPLICATION_XML.toString());
		given(report.getReport()).willReturn(new byte[10]);

		// When
		ResponseEntity<?> actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), null, null, null);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		assertEquals("attachment; filename =" + catererAfterCreate.getId().toHexString() + ".pdf", actual.getHeaders().getFirst("content-disposition"));
		assertEquals("application/pdf", actual.getHeaders().getContentType().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPaymentInfoJson() throws Exception {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Caterer catererAfterCreate = EntityGenerator.mockCatererAfterCreate(caterer);
		Order order = EntityGenerator.generateRandomOrderWithId();
		List<Order> orders = new ArrayList<>();
		orders.add(order);
		given(catererService.read(anyString())).willReturn(Optional.of(catererAfterCreate));
		given(orderService.readAllWithCatererId(anyString(), isA(DateTime.class), isA(DateTime.class))).willReturn(orders);
		given(catererService.getTotalAmountSuccessful(isA(List.class))).willReturn(150);
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
		given(context.getContentType()).willReturn(MediaType.APPLICATION_JSON.toString());
		given(report.getReport()).willReturn(new byte[10]);

		// When
		ResponseEntity<?> actual = controller.paymentInfo(catererAfterCreate.getId().toHexString(), "01/01/2015", "01/01/2016", "MM/dd/yyyy");

		// Then
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
	}
}
