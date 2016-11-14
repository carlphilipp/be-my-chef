package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.exception.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsOrderIT extends AccessRights {

	private static final String ENDPOINT = "users";
	private static final String ORDER_EXT = "orders";

	private User user;

	@PostConstruct
	public void postConstruct() throws IOException, EpickurException {
		super.postConstruct();
		user = integrationTestUtils.createUserAndLogin();
	}

	// User Administrator
	@Test
	public void testAdministratorOrderCreate() throws IOException, AuthenticationException, InvalidRequestException,
		APIConnectionException, CardException, APIException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}", ORDER_EXT)
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order order = EntityGenerator.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorOrderRead() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		Order order = integrationTestUtils.createOrder(user.getId());
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet getReq = new HttpGet(uri);
		getReq.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorOrderRead2() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = new ObjectId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString(), id)
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testAdministratorOrderUpdate() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		Order order = integrationTestUtils.createOrder(user.getId());
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString(), updatedOrder.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testAdministratorOrderDelete() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		Order order = integrationTestUtils.createOrder(user.getId());
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpDelete request = new HttpDelete(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserOrderCreate() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT)
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order order = EntityGenerator.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserOrderRead() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, order.getId().toHexString())
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet getReq = new HttpGet(uri);
		getReq.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserOrderRead2() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		String id = new ObjectId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), id)
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testSuperUserOrderRead3() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		User otherUser = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(otherUser.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet getReq = new HttpGet(uri);
		getReq.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testSuperUserOrderUpdate() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testSuperUserOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), updatedOrder.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testSuperUserOrderDelete() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createSuperUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpDelete request = new HttpDelete(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	// User User
	@Test
	public void testUserOrderCreate() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}", ORDER_EXT)
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order order = EntityGenerator.generateRandomOrder();
		StringEntity requestEntity = new StringEntity(order.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("charge-agent", "true");
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserOrderRead() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
		APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet getReq = new HttpGet(uri);
		getReq.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserOrderRead2() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
		APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		String id = new ObjectId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), id)
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testUserOrderRead3() throws IOException, EpickurException, AuthenticationException, InvalidRequestException,
		APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		User otherUser = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(otherUser.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpGet getReq = new HttpGet(uri);
		getReq.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getReq);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testUserOrderUpdate() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(order.getId());
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testUserOrderUpdate2() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		ObjectId id = new ObjectId();
		Order updatedOrder = integrationTestUtils.createOrder(user.getId());
		updatedOrder.setId(id);
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), updatedOrder.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(updatedOrder.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.NOT_FOUND.value(), statusCode);
	}

	@Test
	public void testUserOrderDelete() throws IOException, EpickurException, AuthenticationException,
		InvalidRequestException, APIConnectionException, CardException, APIException {
		// Given
		user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port)
			.pathSegment(path, ENDPOINT, "{id}", ORDER_EXT, "{orderId}")
			.queryParam("key", user.getKey())
			.build()
			.expand(user.getId().toHexString(), order.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		HttpDelete request = new HttpDelete(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}
}
