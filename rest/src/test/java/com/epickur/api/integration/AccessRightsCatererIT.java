package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.entity.Caterer;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsCatererIT extends AccessRights {

	private static final String ENDPOINT = "caterers";

	// User Administrator
	@Test
	public void testAdministratorCatererCreate() throws EpickurException, IOException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
			.queryParam("key", admin.getKey())
			.build()
			.encode();
		URI uri = uriComponents.toUri();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
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
	public void testAdministratorCatererRead() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createCaterer().getId().toHexString();
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
	public void testAdministratorCatererUpdate() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		Caterer caterer = integrationTestUtils.createCaterer();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", admin.getKey())
			.build()
			.expand(caterer.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		Caterer user = EntityGenerator.generateRandomCatererWithId();
		user.setId(caterer.getId());
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
	public void testAdministratorCatererDelete() throws IOException, EpickurException {
		// Given
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createCaterer().getId().toHexString();
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
	public void testSuperUserCatererCreate() throws IOException, EpickurException {
		// Given
		String key = integrationTestUtils.createSuperUserAndLogin().getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
			.queryParam("key", key).build().encode();
		URI uri = uriComponents.toUri();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(uri);
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
	public void testSuperUserCatererRead() throws IOException, EpickurException {
		// Given
		// Read another caterer - should pass it
		String key = integrationTestUtils.createSuperUserAndLogin().getKey();
		String id = integrationTestUtils.createCaterer().getId().toHexString();
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
	}

	@Test
	public void testSuperUserCatererUpdate() throws IOException, EpickurException {
		// Given
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = integrationTestUtils.createCaterer();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", key)
			.build()
			.expand(caterer.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testSuperUserCatererUpdate2() throws IOException, EpickurException {
		// Given
		// Update a caterer created by current user - should pass it
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(superUser.getId());
		String key = superUser.getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", key)
			.build()
			.expand(caterer.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testSuperUserCatererDelete() throws IOException, EpickurException {
		// Given
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Caterer caterer = integrationTestUtils.createCaterer();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", superUser.getKey())
			.build()
			.expand(caterer.getId().toHexString())
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
	public void testUserCaterCreate() throws IOException, EpickurException {
		// Given
		String key = integrationTestUtils.createUserAndLogin().getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
			.queryParam("key", key).build().encode();
		URI uri = uriComponents.toUri();
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(uri);
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
	public void testUserCatererRead() throws IOException, EpickurException {
		// Given
		// Read another caterer - should pass it
		String key = integrationTestUtils.createUserAndLogin().getKey();
		String id = integrationTestUtils.createCaterer().getId().toHexString();
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
	}

	@Test
	public void testUserCatererUpdate() throws IOException, EpickurException {
		// Given
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = integrationTestUtils.createCaterer();
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", key)
			.build()
			.expand(caterer.getId().toHexString())
			.encode();
		URI uri = uriComponents.toUri();
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testUserCatererDelete() throws IOException, EpickurException {
		// Given
		User superUser = integrationTestUtils.createUserAndLogin();
		Caterer caterer = integrationTestUtils.createCaterer();
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
			.queryParam("key", superUser.getKey())
			.build()
			.expand(caterer.getId().toHexString())
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
