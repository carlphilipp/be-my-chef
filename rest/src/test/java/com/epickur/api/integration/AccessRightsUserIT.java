package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsUserIT extends AccessRights {

	private static final String ENDPOINT = "users";

	// User Administrator
	@Test
	public void testAdministratorUserCreate() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
				.queryParam("key", admin.getKey())
				.build()
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("validate-agent", "false");
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
	public void testAdministratorUserRead() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorUserUpdate() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		User normalUser = integrationTestUtils.createUserAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(normalUser.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		user.setId(normalUser.getId());
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
	public void testAdministratorUserDelete() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(id)
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
	public void testSuperUserCreate() throws IOException, EpickurException {
		// Given
		String key = integrationTestUtils.createUserAndLogin().getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("validate-agent", "false");
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserRead() throws IOException, EpickurException {
		// Given
		// Read another user - should not pass it
		String key = integrationTestUtils.createUserAndLogin().getKey();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserRead2() throws IOException, EpickurException {
		// Given
		// Read its own user - should pass it
		User newUser = integrationTestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

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
	public void testSuperUserUpdate() throws IOException, EpickurException {
		// Given
		// Update another user - should not pass it
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(superUser.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		user.setId(superUser.getId());
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperUserUpdate2() throws IOException, EpickurException {
		// Given
		// Update another user - should not pass it
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		user.setId(new ObjectId(id));
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDelete() throws IOException, EpickurException {
		// Given
		User superUser = integrationTestUtils.createUserAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", superUser.getKey())
				.build()
				.expand(superUser.getId().toHexString())
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
	public void testUserCreate() throws IOException, EpickurException {
		// Given
		String key = integrationTestUtils.createUserAndLogin().getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.addHeader("validate-agent", "false");
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserRead() throws IOException, EpickurException {
		// Given
		// Read another user - should not pass it
		String key = integrationTestUtils.createUserAndLogin().getKey();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserRead2() throws IOException, EpickurException {
		// Given
		// Read its own user - should pass it
		User newUser = integrationTestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpGet request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

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
	public void testUserUpdate() throws IOException, EpickurException {
		// Given
		// Update another user - should not pass it
		User normalUser = integrationTestUtils.createUserAndLogin();
		String key = normalUser.getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(normalUser.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		user.setId(normalUser.getId());
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
	public void testUserUpdate2() throws IOException, EpickurException {
		// Given
		// Update another user - should not pass it
		User normalUser = integrationTestUtils.createUserAndLogin();
		String key = normalUser.getKey();
		String id = integrationTestUtils.createUser().getId().toHexString();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		User user = EntityGenerator.generateRandomUser();
		user.setId(new ObjectId(id));
		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDelete() throws IOException, EpickurException {
		// Given
		User superUser = integrationTestUtils.createUserAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", superUser.getKey())
				.build()
				.expand(superUser.getId().toHexString())
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
