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
import com.epickur.api.stripe.StripeTestUtils;
import com.epickur.api.utils.Security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stripe.exception.*;
import com.stripe.model.Token;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class UserIT {

	@Autowired
	private IntegrationTestUtils integrationTestUtils;

	private static String URL;
	private static String URL_NO_KEY;
	private static String URL_EXECUTE_ORDER;
	private static String name;
	private static String start;
	private static String end;
	private static String id;
	private static String API_KEY;

	private static ObjectMapper mapper;

	@AfterClass
	public static void afterClass() throws IOException {
		EntityGenerator.cleanDB();
	}

	@Before
	public void setUp() throws IOException, EpickurException {
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			StripeTestUtils.setupStripe();

			mapper = new ObjectMapper();
			in = new InputStreamReader(UserIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			String address = prop.getProperty("address");
			String path = prop.getProperty("api.path");

			URL_NO_KEY = address + path + "/users";
			URL_EXECUTE_ORDER = address + path + "/nokey/execute";

			User admin = integrationTestUtils.createAdminAndLogin();
			API_KEY = admin.getKey();
			URL = URL_NO_KEY + "?key=" + API_KEY;

			name = RandomStringUtils.randomAlphabetic(10);
			String password = RandomStringUtils.randomAlphabetic(10);
			start = RandomStringUtils.randomAlphabetic(5);
			end = RandomStringUtils.randomAlphabetic(3);

			String jsonMimeType = "application/json";

			// Create
			ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("password", password);
			json.put("email", start + "@" + end + ".com");
			json.put("country", "USA");
			json.put("state", "Illinois");
			json.put("zipcode", "60614");

			HttpPost request = new HttpPost(URL);
			StringEntity requestEntity = new StringEntity(json.toString());
			request.addHeader("content-type", jsonMimeType);
			request.setEntity(requestEntity);

			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			JsonNode jsonResult = mapper.readTree(obj);

			// Create result
			id = jsonResult.get("id").asText();
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUnauthorized() throws IOException {
		// Given
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();

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
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testCreateFail() throws IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();

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

			// Create the same user
			json = mapper.createObjectNode();
			json.put("name", name);
			json.put("password", password);
			json.put("email", start + "@" + end + ".com");
			json.put("country", "USA");
			json.put("state", "Illinois");
			json.put("zipcode", "60614");

			request = new HttpPost(URL);
			requestEntity = new StringEntity(json.toString());
			request.addHeader("content-type", jsonMimeType);
			request.setEntity(requestEntity);

			// Create request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();
			int statusCode2 = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.CONFLICT.value(), statusCode2);

			// Delete this user
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testCreatePhoneNumber() throws IOException {
		String jsonMimeType = "application/json";

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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
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
			assertEquals(jsonMimeType, mimeType);

			// Delete this user
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testCreatePhoneNumber2() throws IOException {
		String jsonMimeType = "application/json";

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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
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
			assertEquals(jsonMimeType, mimeType);

			// Delete this user
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testCreatePhoneNumber3Fail() throws IOException {
		String jsonMimeType = "application/json";

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
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			HttpPost request = new HttpPost(URL);
			StringEntity requestEntity = new StringEntity(json.toString());
			request.addHeader("content-type", jsonMimeType);
			request.setEntity(requestEntity);

			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.BAD_REQUEST.value(), statusCode);
			JsonNode jsonResult = mapper.readTree(obj);

			// Create result
			assertTrue(jsonResult.has("error"));
			assertEquals(HttpStatus.BAD_REQUEST.value(), jsonResult.get("error").asInt());
			assertTrue(jsonResult.has("descriptions"));
			assertEquals("The field user.phoneNumber is not valid", jsonResult.get("descriptions").get(0).asText());
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testReadOneUser() throws IOException {
		// Read
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);

			JsonNode jsonResult = mapper.readTree(obj);
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			assertEquals(jsonMimeType, mimeType);

			assertEquals(name, jsonResult.get("name").asText());
			assertEquals(null, jsonResult.get("password"));
			assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
			assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong(), 0.01);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUpdateOneUser() throws IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readTree(obj);

			String id = jsonResult.get("id").asText();

			// Put
			json = mapper.createObjectNode();
			json.put("id", id);
			json.put("email", "epickur_test@epickur_test.com");
			requestEntity = new StringEntity(json.toString());

			HttpPut putRequest = new HttpPut(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			putRequest.addHeader("content-type", jsonMimeType);
			putRequest.setEntity(requestEntity);

			// Put request
			httpResponse = HttpClientBuilder.create().build().execute(putRequest);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();
			int statusCode2 = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode2 + " with " + obj, HttpStatus.OK.value(), statusCode2);
			jsonResult = mapper.readTree(obj);

			assertFalse("Failed request: " + obj, jsonResult.has("error"));

			assertEquals(id, jsonResult.get("id").asText());
			assertEquals(null, jsonResult.get("password"));
			assertEquals("epickur_test@epickur_test.com", jsonResult.get("email").asText());

			// Delete this user
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			putRequest.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testDeleteOneUser() throws IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);

			JsonNode jsonResult = mapper.readTree(obj);

			String id = jsonResult.get("id").asText();

			// Delete
			HttpDelete request3 = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request3.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(request3);

			// Read
			HttpUriRequest request2 = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request2.addHeader("content-type", jsonMimeType);
			// Read request
			httpResponse = HttpClientBuilder.create().build().execute(request2);

			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();
			int statusCode2 = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode2);

			assertFalse("Failed request: " + obj, jsonResult.has("error"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAddOneOrder() throws IOException, EpickurParsingException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create User request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
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

			request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			request.addHeader("charge-agent", "false");
			request.setEntity(requestEntity);

			// Create Order request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();
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
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);

			// Delete this user
			requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testCreateOneOrderWithStripeToken() throws IOException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException, EpickurParsingException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create User request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
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

			request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			request.addHeader("charge-agent", "true");
			request.setEntity(requestEntity);

			// Create Order request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

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

			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);

			// Delete this user

			requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create User request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();

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

			request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			request.setEntity(requestEntity);
			request.addHeader("charge-agent", "false");

			// Create Order request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

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
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);

			// Check if order has been deleted
			HttpGet requestGet = new HttpGet(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			requestGet.addHeader("content-type", jsonMimeType);
			httpResponse = HttpClientBuilder.create().build().execute(requestGet);

			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

			int statusCode3 = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode3 + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode3);
			jsonResult = mapper.readTree(obj);

			assertNotNull(jsonResult.get("error"));
			assertEquals(new Long(404), jsonResult.get("error").asLong(), 0.01);
			assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), jsonResult.get("message").asText());

			// Delete this user
			requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create User request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();

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

			request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			request.setEntity(requestEntity);
			request.addHeader("charge-agent", "false");

			// Create Order request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

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

			HttpPut requestUpdate = new HttpPut(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			requestUpdate.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			requestUpdate.setEntity(requestEntity);
			httpResponse = HttpClientBuilder.create().build().execute(requestUpdate);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

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
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);

			// Delete this user
			requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testExecuteOneOrder() throws IOException, EpickurException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			// Create User request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();

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

			request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			requestEntity = new StringEntity(json.toString());
			request.addHeader("charge-agent", "false");
			request.setEntity(requestEntity);

			// Create Order request
			httpResponse = HttpClientBuilder.create().build().execute(request);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

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

			// Execute order (Caterer choose yes or no)
			String confirm = "false";
			HttpGet httpGet = new HttpGet(
					URL_EXECUTE_ORDER + "/users/" + id + "/orders/" + orderId + "?confirm=" + confirm + "&ordercode=" + orderCode);
			httpGet.addHeader("content-type", jsonMimeType);
			httpGet.addHeader("charge-agent", "false");
			httpResponse = HttpClientBuilder.create().build().execute(httpGet);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			obj = br.readLine();

			int statusCode3 = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode3 + " with " + obj, HttpStatus.OK.value(), statusCode3);
			jsonResult = mapper.readTree(obj);
			assertNotNull(jsonResult.has("status"));
			assertEquals(OrderStatus.DECLINED.toString(), jsonResult.get("status").asText());

			// Delete this order
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "/orders/" + orderId + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);

			// Delete this user
			requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			requestDelete.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}
}
