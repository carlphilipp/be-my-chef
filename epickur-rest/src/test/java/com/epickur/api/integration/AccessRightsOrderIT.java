package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripeTestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsOrderIT {

	@Autowired
	private IntegrationTestUtils integrationTestUtils;

	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String jsonMimeType;
	private static ObjectMapper mapper;
	private static User user;

	@Before
	public void setUp() throws IOException, EpickurException {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			IOUtils.closeQuietly(in);
			String address = prop.getProperty("address");
			String path = prop.getProperty("api.path");
			StripeTestUtils.setupStripe();
			END_POINT = address + path;
			in = new InputStreamReader(UserIT.class.getClass().getResourceAsStream("/api.key"));
			BufferedReader br = new BufferedReader(in);
			String API_KEY = br.readLine();
			IOUtils.closeQuietly(in);
			jsonMimeType = "application/json";
			mapper = new ObjectMapper();
			user = integrationTestUtils.createUserAndLogin();
			EntityGenerator.setupDB();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StripeTestUtils.resetStripe();
		EntityGenerator.cleanDB();
	}

	// User Administrator
	@Test
	public void testAdministratorOrderCreate() throws IOException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Order order = EntityGenerator.generateRandomOrder();

		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorOrderRead() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Order order = integrationTestUtils.createOrder(user.getId());

		User admin = integrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpGet getReq = new HttpGet(URL);
		getReq.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorOrderRead2() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {

		User admin = integrationTestUtils.createAdminAndLogin();

		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorOrderUpdate() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Order order = integrationTestUtils.createOrder(user.getId());

		User admin = integrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		User admin = integrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorOrderDelete() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		Order order = integrationTestUtils.createOrder(user.getId());

		User admin = integrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	// User Super_User
	@Test
	public void testSuperUserOrderCreate() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + user.getKey();

		Order order = EntityGenerator.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderRead() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();

		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet getReq = new HttpGet(URL);
		getReq.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderRead2() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();

		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderRead3() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();

		User otherUser = integrationTestUtils.createUserAndLogin();

		Order order = integrationTestUtils.createOrder(otherUser.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet getReq = new HttpGet(URL);
		getReq.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderUpdate() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		User sUser = integrationTestUtils.createSuperUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + sUser.getKey();

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserOrderDelete() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createSuperUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	// User User
	@Test
	public void testUserOrderCreate() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders";
		URL = URL_NO_KEY + "?key=" + user.getKey();

		Order order = EntityGenerator.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderRead() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();

		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet getReq = new HttpGet(URL);
		getReq.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderRead2() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();
		String id = new ObjectId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + id;
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderRead3() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();

		User otherUser = integrationTestUtils.createUserAndLogin();

		Order order = integrationTestUtils.createOrder(otherUser.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpGet getReq = new HttpGet(URL);
		getReq.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderUpdate() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + updatedOrder.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserOrderDelete() throws IOException, EpickurException, AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());

		URL_NO_KEY = END_POINT + "/users/" + user.getId().toHexString() + "/orders/" + order.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + user.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}
}
