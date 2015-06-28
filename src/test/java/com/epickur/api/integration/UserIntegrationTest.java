package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.NutritionFact;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.Security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;

public class UserIntegrationTest {

	private static String URL;
	private static String URL_NO_KEY;
	private static String URL_EXECUTE_ORDER;
	private static String name;
	private static String password;
	private static String start;
	private static String end;
	private static String id;
	private static String API_KEY;
	private static String STRIPE_TEST_KEY;

	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass() {
		try {
			mapper = new ObjectMapper();
			InputStreamReader in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			String address = prop.getProperty("address");
			String path = prop.getProperty("api.path");
			STRIPE_TEST_KEY = prop.getProperty("stripe.key");
			URL_NO_KEY = address + path + "/users";
			URL_EXECUTE_ORDER = address + path + "/nokey/execute";

			in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/api.key"));
			BufferedReader br = new BufferedReader(in);
			API_KEY = br.readLine();
			in.close();
			URL = URL_NO_KEY + "?key=" + API_KEY;

			name = RandomStringUtils.randomAlphabetic(10);
			password = RandomStringUtils.randomAlphabetic(10);
			start = RandomStringUtils.randomAlphabetic(5);
			end = RandomStringUtils.randomAlphabetic(3);

			String jsonMimeType = "application/json";

			// Create
			ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("password", password);
			json.put("email", start + "@" + end + ".com");

			HttpPost request = new HttpPost(URL);
			StringEntity requestEntity = new StringEntity(json.toString());
			request.addHeader("content-type", jsonMimeType);
			request.addHeader("email-agent", "false");
			request.setEntity(requestEntity);

			// Create request
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			JsonNode jsonResult = mapper.readTree(obj);
			in.close();

			// Create result
			id = jsonResult.get("id").asText();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() throws ClientProtocolException, IOException {
		String jsonMimeType = "application/json";
		// Delete
		HttpDelete request = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(request);
	}

	@Test
	public void testUnauthorized() throws ClientProtocolException, IOException {
		// Given
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
	}

	@Test
	public void testCreateFail() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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

		request = new HttpPost(URL);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.CONFLICT.getStatusCode(), statusCode2);
		jsonResult = mapper.readTree(obj);

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);
	}
	
	
	@Test
	public void testCreatePhoneNumber() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
	}
	
	@Test
	public void testCreatePhoneNumber2() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
	}
	
	@Test
	public void testCreatePhoneNumber3Fail() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		// Create result
		assertTrue(jsonResult.has("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), jsonResult.get("error").asInt());
		assertTrue(jsonResult.has("description"));
		assertEquals("The field user.phoneNumber is not valid", jsonResult.get("description").asText());
	}


	@Test
	public void testReadOneUser() throws ClientProtocolException, IOException {
		// Read
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);

		JsonNode jsonResult = mapper.readTree(obj);
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		assertEquals(name, jsonResult.get("name").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals(start + "@" + end + ".com", jsonResult.get("email").asText());
		assertEquals(new Long(0).longValue(), jsonResult.get("allow").asLong(), 0.01);
	}

	@Test
	public void testUpdateOneUser() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		String id = jsonResult.get("id").asText();

		// Put
		json = mapper.createObjectNode();
		json.put("id", id);
		json.put("email", "epickur_test@epickur_test.com");
		requestEntity = new StringEntity(json.toString());

		HttpPut putRequest = new HttpPut(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		putRequest.addHeader("content-type", jsonMimeType);
		putRequest.addHeader("email-agent", "false");
		putRequest.setEntity(requestEntity);

		// Put request
		httpResponse = HttpClientBuilder.create().build().execute(putRequest);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
		jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertEquals(id, jsonResult.get("id").asText());
		assertEquals(null, jsonResult.get("password"));
		assertEquals("epickur_test@epickur_test.com", jsonResult.get("email").asText());

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		putRequest.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);
	}

	@Test
	public void testDeleteOneUser() throws ClientProtocolException, IOException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		
		JsonNode jsonResult = mapper.readTree(obj);

		String id = jsonResult.get("id").asText();

		// Delete
		HttpDelete request3 = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request3.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(request3);

		// Read
		HttpUriRequest request2 = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		// Read request
		httpResponse = HttpClientBuilder.create().build().execute(request2);

		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.NOT_FOUND.getStatusCode(), statusCode2);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));
	}

	@Test
	public void testAddOneOrder() throws ClientProtocolException, IOException, EpickurParsingException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(new ObjectId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = TestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = TestUtils.generateRandomString();
		json.put("cardToken", cardToken);

		request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("charge-agent", "false");
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
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
	}

	@Test
	public void testCreateOneOrderWithStripeToken() throws ClientProtocolException, IOException, AuthenticationException, InvalidRequestException,
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
		dish.setCaterer(TestUtils.generateRandomCatererWithId());
		dish.setName("Dish name");
		dish.setDescription("A super cool dish");
		dish.setType(DishType.VEGAN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		List<String> steps = TestUtils.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = TestUtils.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = TestUtils.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setImageAfterUrl("url");
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = TestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);

		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
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
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
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

	}

	@Test
	public void testDeleteOneOrder() throws ClientProtocolException, IOException, EpickurParsingException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
		dish.setType(DishType.VEGAN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		dish.setImageAfterUrl("url");
		List<String> steps = TestUtils.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = TestUtils.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = TestUtils.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setCaterer(TestUtils.generateRandomCatererWithId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = TestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = TestUtils.generateRandomString();
		json.put("cardToken", cardToken);

		request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.setEntity(requestEntity);
		request.addHeader("charge-agent", "false");
		request.addHeader("email-agent", "false");

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
		jsonResult = mapper.readTree(obj);
		in.close();

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

		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, Response.Status.NOT_FOUND.getStatusCode(), statusCode3);
		jsonResult = mapper.readTree(obj);

		assertNotNull(jsonResult.get("error"));
		assertEquals(new Long(404), jsonResult.get("error").asLong(), 0.01);
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), jsonResult.get("message").asText());

		// Delete this user
		requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		requestDelete.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);
	}

	@Test
	public void testUpdateOneOrder() throws ClientProtocolException, IOException, EpickurParsingException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
		dish.setType(DishType.VEGAN);
		dish.setPrice(500);
		dish.setCookingTime(5);
		dish.setDifficultyLevel(8);
		List<String> steps = TestUtils.generateRandomListString();
		dish.setSteps(steps);
		List<NutritionFact> nutritionFacts = TestUtils.generateRandomListNutritionFact();
		dish.setNutritionFacts(nutritionFacts);
		List<Ingredient> ingredients = TestUtils.generateRandomListIngredient();
		dish.setIngredients(ingredients);
		dish.setVideoUrl("google.com");
		dish.setImageAfterUrl("url");
		dish.setCaterer(TestUtils.generateRandomCatererWithId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = TestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = TestUtils.generateRandomString();
		json.put("cardToken", cardToken);

		request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.setEntity(requestEntity);
		request.addHeader("charge-agent", "false");
		request.addHeader("email-agent", "false");

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
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
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode3);
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
	}
	
	
	
	@Test
	public void testExecuteOneOrder() throws ClientProtocolException, IOException, EpickurException {
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

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(json.toString());
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create User request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
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
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(new ObjectId());
		json.set("dish", mapper.readTree(dish.toStringAPIView()));
		json.put("description", "A new order");
		json.put("amount", 500);
		json.put("currency", "AUD");
		String pickupdate = TestUtils.generateRandomCorrectPickupDate(dish.getCaterer().getWorkingTimes());
		json.put("pickupdate", pickupdate);
		String cardToken = TestUtils.generateRandomString();
		json.put("cardToken", cardToken);

		request = new HttpPost(URL_NO_KEY + "/" + id + "/orders?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		requestEntity = new StringEntity(json.toString());
		request.addHeader("charge-agent", "false");
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		// Create Order request
		httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode2 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode2);
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
		HttpGet httpGet = new HttpGet(URL_EXECUTE_ORDER + "/users/" + id + "/orders/" + orderId + "?confirm=" + confirm + "&ordercode=" + orderCode);
		httpGet.addHeader("content-type", jsonMimeType);
		httpGet.addHeader("charge-agent", "false");
		httpGet.addHeader("email-agent", "false");
		httpResponse = HttpClientBuilder.create().build().execute(httpGet);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode3 = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode3 + " with " + obj, Response.Status.OK.getStatusCode(), statusCode3);
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
	}
}
