package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.NutritionFact;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.security.Security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;
import lombok.Cleanup;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class UserIT {

	private static final String ENDPOINT = "users";
	private static final String ENDPOINT_ORDER = "orders";
	private static final String ENDPOINT_NOKEY = "nokey";
	private static final String ENDPOINT_NOKEY_EXECUTE = "execute";
	private static final String JSON_MIME_TYPE = "application/json";
	private static String PROTOCOL;
	private static String HOST;
	private static String PORT;
	private static String PATH;
	private static String API_KEY;
	private static String NAME;
	private static String EMAIL;
	private static String ID;

	@Autowired
	private IntegrationTestUtils integrationTestUtils;
	@Autowired
	private ObjectMapper mapper;

	@AfterClass
	public static void afterClass() throws IOException {
		IntegrationTestUtils.cleanDB();
	}

	@Before
	public void setUp() throws IOException, EpickurException {
		@Cleanup InputStreamReader in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		PROTOCOL = prop.getProperty("protocol");
		HOST = prop.getProperty("host");
		PORT = prop.getProperty("port");
		PATH = prop.getProperty("api.path");

		User admin = integrationTestUtils.createAdminAndLogin();
		API_KEY = admin.getKey();

		User user = EntityGenerator.generateRandomUser();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		ID = jsonResult.get("id").asText();
		NAME = jsonResult.get("name").asText();
		EMAIL = jsonResult.get("email").asText();
	}

	@Test
	public void testUnauthorized() throws IOException {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		// Given
		HttpUriRequest request = new HttpGet(uri);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);
	}

	@Test
	public void testCreate() throws IOException {
		String jsonMimeType = "application/json";

		// Create
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		// Delete this user
		deleteUser(id);
	}

	private void deleteUser(final String id) throws IOException {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", JSON_MIME_TYPE);
		HttpClientBuilder.create().build().execute(requestDelete);
	}

	@Test
	public void testCreateFail() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Create the same user
		json = mapper.createObjectNode();
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.CONFLICT.value(), statusCode2);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testCreatePhoneNumber() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		ObjectNode phoneNumber = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		phoneNumber.put("nationalNumber", 383400775);
		phoneNumber.put("countryCode", 33);
		json.set("phoneNumber", phoneNumber);
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);
		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		assertTrue(jsonResult.has("phoneNumber"));
		JsonNode phoneNumberNode = jsonResult.get("phoneNumber");
		assertTrue(phoneNumberNode.has("nationalNumber"));
		assertTrue(phoneNumberNode.has("countryCode"));
		assertEquals(383400775, phoneNumberNode.get("nationalNumber").asLong());
		assertEquals(33, phoneNumberNode.get("countryCode").asInt());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testCreatePhoneNumber2() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		ObjectNode phoneNumber = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		phoneNumber.put("nationalNumber", "+33383400775");
		json.set("phoneNumber", phoneNumber);
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);
		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		assertTrue(jsonResult.has("phoneNumber"));
		JsonNode phoneNumberNode = jsonResult.get("phoneNumber");
		assertTrue(phoneNumberNode.has("nationalNumber"));
		assertTrue(phoneNumberNode.has("countryCode"));
		assertEquals(383400775, phoneNumberNode.get("nationalNumber").asLong());
		assertEquals(33, phoneNumberNode.get("countryCode").asInt());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testCreatePhoneNumber3Fail() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		ObjectNode phoneNumber = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		phoneNumber.put("nationalNumber", "00775");
		json.set("phoneNumber", phoneNumber);
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.BAD_REQUEST.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertTrue(jsonResult.has("error"));
		assertEquals(HttpStatus.BAD_REQUEST.value(), jsonResult.get("error").asInt());
		assertTrue(jsonResult.has("descriptions"));
		assertEquals("The field user.phoneNumber is not valid", jsonResult.get("descriptions").get(0).asText());
	}

	@Test
	public void testReadOneUser() throws IOException {
		// Read
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", API_KEY)
				.build()
				.expand(ID)
				.encode();
		URI uri = uriComponents.toUri();

		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("content-type", JSON_MIME_TYPE);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());

		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);

		JsonNode jsonResult = mapper.readTree(obj);
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		assertEquals(NAME, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(EMAIL, jsonResult.get("email").asText());
		assertEquals(0, jsonResult.get("allow").asLong(), 0.01);
	}

	@Test
	public void testUpdateOneUser() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		String id = jsonResult.get("id").asText();

		// Put
		json = mapper.createObjectNode();
		json.put("id", id);
		json.put("email", "epickur_test@epickur_test.com");
		requestEntity = new StringEntity(json.toString());

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		HttpPut putRequest = new HttpPut(uri);
		putRequest.addHeader("content-type", JSON_MIME_TYPE);
		putRequest.setEntity(requestEntity);

		// Put request
		httpResponse = HttpClientBuilder.create().build().execute(putRequest);
		obj = integrationTestUtils.readResult(httpResponse);
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertEquals(id, jsonResult.get("id").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals("epickur_test@epickur_test.com", jsonResult.get("email").asText());

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testDeleteOneUser() throws IOException {
		// Create
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);

		JsonNode jsonResult = mapper.readTree(obj);

		String id = jsonResult.get("id").asText();

		// Delete
		deleteUser(id);

		// Read
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();
		HttpUriRequest request2 = new HttpGet(uri);
		request2.addHeader("content-type", JSON_MIME_TYPE);
		// Read request
		httpResponse = HttpClientBuilder.create().build().execute(request2);

		obj = integrationTestUtils.readResult(httpResponse);
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode2);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));
	}

	@Test
	public void testAddOneOrder() throws IOException, EpickurParsingException {
		// Create User
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Create one Order
		json = mapper.createObjectNode();
		Dish dish = EntityGenerator.generateRandomDish();
		dish.setId(new ObjectId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("quantity", 2);
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = integrationTestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = EntityGenerator.generateRandomString();
		json.put("cardToken", cardToken);

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER)
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		request.addHeader("content-type", JSON_MIME_TYPE);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("charge-agent", "false");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		String orderId = jsonResult.get("id").asText();
		assertEquals(new Long(500).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt").asText());
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));

		// Delete this order
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();

		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", JSON_MIME_TYPE);
		HttpClientBuilder.create().build().execute(requestDelete);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testCreateOneOrderWithStripeToken() throws IOException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException, EpickurParsingException {
		// Create User
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Create one Order
		json = mapper.createObjectNode();
		Dish dish = new Dish();
		dish.setCaterer(EntityGenerator.generateRandomCatererWithId());
		dish.setName("Dish name");
		dish.setDescription("A super cool dish");
		dish.setType(DishType.MAIN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		List<String> steps = EntityGenerator.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = EntityGenerator.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = EntityGenerator.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setImageAfterUrl("url");
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("quantity", 2);
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = integrationTestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);

		Map<String, Object> tokenParams = new HashMap<>();
		Map<String, Object> cardParams = new HashMap<>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);

		Token token = Token.create(tokenParams);

		json.put("cardToken", token.getId());

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER)
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		request.addHeader("content-type", JSON_MIME_TYPE);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		String orderId = jsonResult.get("id").asText();
		assertNotNull(orderId);
		assertEquals(new Long(500).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt").asText());
		assertNotNull(jsonResult.get("updatedAt").asText());
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));
		assertNotNull(jsonResult.get("cardToken"));

		// Delete this order
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();
		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", JSON_MIME_TYPE);
		HttpClientBuilder.create().build().execute(requestDelete);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testDeleteOneOrder() throws IOException, EpickurParsingException {
		String jsonMimeType = "application/json";

		// Create User
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);
		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		// Create one Order
		json = mapper.createObjectNode();
		Dish dish = new Dish();
		dish.setName("Dish name");
		dish.setDescription("A super cool dish");
		dish.setType(DishType.MAIN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		dish.setImageAfterUrl("url");
		List<String> steps = EntityGenerator.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = EntityGenerator.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = EntityGenerator.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setCaterer(EntityGenerator.generateRandomCatererWithId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("quantity", 2);
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = integrationTestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = EntityGenerator.generateRandomString();
		json.put("cardToken", cardToken);

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER)
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.setEntity(requestEntity);
		request.addHeader("charge-agent", "false");

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		String orderId = jsonResult.get("id").asText();
		assertEquals(new Long(500).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt"));
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));

		// Delete this order
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();

		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);

		// Check if order has been deleted
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();
		HttpGet requestGet = new HttpGet(uri);
		requestGet.addHeader("content-type", jsonMimeType);
		httpResponse = HttpClientBuilder.create().build().execute(requestGet);

		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode3);
		jsonResult = mapper.readTree(obj);

		assertNotNull(jsonResult.get("error"));
		assertEquals(404, jsonResult.get("error").asLong(), 0.01);
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), jsonResult.get("message").asText());

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testUpdateOneOrder() throws IOException, EpickurParsingException {
		String jsonMimeType = "application/json";

		// Create User
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		// Create one Order
		json = mapper.createObjectNode();
		Dish dish = new Dish();
		dish.setName("Dish name");
		dish.setDescription("A super cool dish");
		dish.setType(DishType.MAIN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		List<String> steps = EntityGenerator.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = EntityGenerator.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = EntityGenerator.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setImageAfterUrl("url");
		dish.setCaterer(EntityGenerator.generateRandomCatererWithId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("quantity", 6);
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = integrationTestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = EntityGenerator.generateRandomString();
		json.put("cardToken", cardToken);

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER)
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.setEntity(requestEntity);
		request.addHeader("charge-agent", "false");

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		String orderId = jsonResult.get("id").asText();
		assertEquals(new Long(500).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt").asText());
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));
		assertNotNull(jsonResult.get("status"));
		assertEquals("PENDING", jsonResult.get("status").asText());

		// Update order
		json = mapper.createObjectNode();
		json.put("description", "A new order modified");
		json.put("amount", 600);
		json.put("currency", "AUD");
		json.put("id", orderId);
		json.put("createdBy", id);

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();

		HttpPut requestUpdate = new HttpPut(uri);
		requestUpdate.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		requestUpdate.setEntity(requestEntity);
		httpResponse = HttpClientBuilder.create().build().execute(requestUpdate);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, HttpStatus.OK.value(), statusCode3);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		assertEquals(new Long(600).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order modified", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt").asText());
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));

		// Delete this order
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();
		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);

		// Delete this user
		deleteUser(id);
	}

	@Test
	public void testExecuteOneOrder() throws IOException, EpickurException {
		// Create User
		ObjectNode json = mapper.createObjectNode();
		String name = RandomStringUtils.randomAlphabetic(10);
		String password = RandomStringUtils.randomAlphabetic(10);
		String start = RandomStringUtils.randomAlphabetic(5);
		String end = RandomStringUtils.randomAlphabetic(3);
		json.put("name", name);
		json.put("password", password);
		json.put("email", start + "@" + end + ".com");
		json.put("country", "USA");
		json.put("state", "Illinois");
		json.put("zipcode", "60614");

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", API_KEY)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong());
		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Create one Order
		json = mapper.createObjectNode();
		Dish dish = EntityGenerator.generateRandomDish();
		dish.setId(new ObjectId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("quantity", 2);
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = integrationTestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = EntityGenerator.generateRandomString();
		json.put("cardToken", cardToken);

		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER)
				.queryParam("key", API_KEY)
				.build()
				.expand(id)
				.encode();
		uri = uriComponents.toUri();

		request = new HttpPost(uri);
		request.addHeader("content-type", JSON_MIME_TYPE);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("charge-agent", "false");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertNotNull(jsonResult.get("id"));
		String orderId = jsonResult.get("id").asText();
		assertEquals(new Long(500).longValue(), jsonResult.get("amount").asLong());
		assertEquals("A new order", jsonResult.get("description").asText());
		assertEquals(id, jsonResult.get("createdBy").asText());
		assertNotNull(jsonResult.get("createdAt").asText());
		assertEquals("AUD", jsonResult.get("currency").asText());
		assertNotNull(jsonResult.get("dish"));
		assertNotNull(jsonResult.get("cardToken"));

		String orderCode = Security.createOrderCode(new ObjectId(orderId), jsonResult.get("cardToken").asText());
		String confirm = "false";
		// Execute order (Caterer choose yes or no)
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT)
				.pathSegment(PATH, ENDPOINT_NOKEY, ENDPOINT_NOKEY_EXECUTE, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("confirm", confirm)
				.queryParam("ordercode", orderCode)
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("content-type", JSON_MIME_TYPE);
		httpGet.addHeader("charge-agent", "false");
		httpResponse = HttpClientBuilder.create().build().execute(httpGet);
		obj = integrationTestUtils.readResult(httpResponse);

		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, HttpStatus.OK.value(), statusCode3);
		jsonResult = mapper.readTree(obj);
		assertNotNull(jsonResult.has("status"));
		assertEquals(OrderStatus.DECLINED.toString(), jsonResult.get("status").asText());

		// Delete this order
		uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}", ENDPOINT_ORDER, "{orderId}")
				.queryParam("key", API_KEY)
				.build()
				.expand(id, orderId)
				.encode();
		uri = uriComponents.toUri();
		HttpDelete requestDelete = new HttpDelete(uri);
		requestDelete.addHeader("content-type", JSON_MIME_TYPE);
		HttpClientBuilder.create().build().execute(requestDelete);

		// Delete this user
		deleteUser(id);
	}
}
