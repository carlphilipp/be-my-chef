package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;

public class AccessRightsOrderIntegrationTest {
	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;
	private static String jsonMimeType;
	private static String STRIPE_TEST_KEY;
	private static String mongoPath;
	private static String mongoAddress;
	private static String mongoPort;
	private static String mongoDbName;
	private static String scriptCleanPath;
	private static ObjectMapper mapper;
	private static User user;

	@BeforeClass
	public static void beforeClass() throws IOException, EpickurException {
		InputStreamReader in = new InputStreamReader(CatererIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		String address = prop.getProperty("address");
		String path = prop.getProperty("api.path");
		STRIPE_TEST_KEY = prop.getProperty("stripe.key");
		END_POINT = address + path;

		in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/api.key"));
		BufferedReader br = new BufferedReader(in);
		API_KEY = br.readLine();
		in.close();

		jsonMimeType = "application/json";
		mapper = new ObjectMapper();

		mongoPath = prop.getProperty("mongo.path");
		mongoAddress = prop.getProperty("mongo.address");
		mongoPort = prop.getProperty("mongo.port");
		mongoDbName = prop.getProperty("mongo.db.name");
		scriptCleanPath = prop.getProperty("script.clean");

		user = TestUtils.createUserAndLogin();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		String cmd = mongoPath + " " + mongoAddress + ":" + mongoPort + "/" + mongoDbName + " " + scriptCleanPath;
		TestUtils.runShellCommand(cmd);
	}

	// User Administrator
	@Test
	public void testAdministratorOrderCreate() throws ClientProtocolException, IOException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		Order order = TestUtils.generateRandomOrder();

		StringEntity requestEntity = new StringEntity(order.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorOrderRead() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		Order order = TestUtils.createOrder(user.getId());

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		HttpGet getReq = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorOrderRead2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;
		Token token = TestUtils.generateRandomToken();

		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	@Test
	public void testAdministratorOrderUpdate() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testAdministratorOrderUpdate2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		ObjectId id = new ObjectId();
		Token token = TestUtils.generateRandomToken();
		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	@Test
	public void testAdministratorOrderDelete() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserOrderCreate() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createSuperUser();

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		Order order = TestUtils.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserOrderRead() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createSuperUser();

		Order order = TestUtils.createOrder(user.getId());

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet getReq = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserOrderRead2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = TestUtils.createSuperUser();

		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;
		Token token = TestUtils.generateRandomToken();

		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	@Test
	public void testSuperUserOrderRead3() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createSuperUser();

		User otherUser = TestUtils.createUserAndLogin();

		Order order = TestUtils.createOrder(otherUser.getId());

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet getReq = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperUserOrderUpdate() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createSuperUser();
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testSuperUserOrderUpdate2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createSuperUser();
		ObjectId id = new ObjectId();
		Token token = TestUtils.generateRandomToken();
		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	@Test
	public void testSuperUserOrderDelete() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createSuperUser();
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	// User User
	@Test
	public void testUserOrderCreate() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createUserAndLogin();

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		Order order = TestUtils.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.addHeader("email-agent", "false");
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserOrderRead() throws ClientProtocolException, IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createUserAndLogin();

		Order order = TestUtils.createOrder(user.getId());

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet getReq = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	public void testUserOrderRead2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		user = TestUtils.createUserAndLogin();

		// Create Stripe card token
		Stripe.apiKey = STRIPE_TEST_KEY;
		Token token = TestUtils.generateRandomToken();

		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	@Test
	public void testUserOrderRead3() throws ClientProtocolException, IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;

		user = TestUtils.createUserAndLogin();

		User otherUser = TestUtils.createUserAndLogin();

		Order order = TestUtils.createOrder(otherUser.getId());

		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey() + "&token=" + token.getId();

		HttpGet getReq = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	public void testUserOrderUpdate() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createUserAndLogin();
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testUserOrderUpdate2() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createUserAndLogin();
		ObjectId id = new ObjectId();
		Token token = TestUtils.generateRandomToken();
		Order updatedOrder = TestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		StringEntity requestEntity = new StringEntity(updatedOrder.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 404, statusCode);
	}

	public void testUserOrderDelete() throws ClientProtocolException, IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		user = TestUtils.createUserAndLogin();
		Order order = TestUtils.createOrder(user.getId());
		Token token = TestUtils.generateRandomToken();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY + "&token=" + token.getId();

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}
}
