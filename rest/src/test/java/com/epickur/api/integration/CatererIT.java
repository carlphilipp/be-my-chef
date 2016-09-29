package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.*;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class CatererIT {

	private static final String CONTENT_TYPE = "content-type";
	private static final String JSON_MIME_TYPE = "application/json";
	private static final String ENDPOINT = "caterers";

	private String protocol;
	private String host;
	private String port;
	private String path;
	private String apiKey;
	private String name;
	private String id;
	@Autowired
	private IntegrationTestUtils integrationTestUtils;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	protected EpickurProperties properties;

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		IntegrationTestUtils.cleanDB();
	}

	@PostConstruct
	public void postConstruct() throws IOException, EpickurException {
		IntegrationTestUtils.cleanDB();
		protocol = properties.getProtocol();
		host = properties.getHost();
		port = properties.getPort().toString();
		path = properties.getPath();

		User admin = integrationTestUtils.createAdminAndLogin();
		apiKey = admin.getKey();

		// Create
		ObjectNode caterer = mapper.createObjectNode();
		ObjectNode location = mapper.createObjectNode();
		ObjectNode address2 = mapper.createObjectNode();
		ObjectNode geo = mapper.createObjectNode();
		ArrayNode coordinates = mapper.createArrayNode();
		name = RandomStringUtils.randomAlphabetic(10);
		Float[] coord = new Float[2];
		coord[0] = -73.97f;
		coord[1] = 40.77f;
		coordinates.add(coord[0]);
		coordinates.add(coord[1]);
		geo.set("coordinates", coordinates);
		address2.put("label", "carl");
		address2.put("houseNumber", "832");
		address2.put("street", "Wrightwood");
		address2.put("city", "Chicago");
		address2.put("postalCode", "60614");
		address2.put("state", "Illinois");
		address2.put("country", "USA");
		location.set("address", address2);
		location.set("geo", geo);
		caterer.put("name", name);
		caterer.set("location", location);
		caterer.put("description", "Caterer description");
		caterer.put("manager", "Manager name");
		caterer.put("email", "email@email.com");
		caterer.put("phone", "000011222");
		WorkingTimes workingTimes = EntityGenerator.generateRandomWorkingTimes();

		caterer.set("workingTimes", mapper.readTree(workingTimes.toStringAPIView()));

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
				.queryParam("key", apiKey)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(caterer.toString());
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		try (final InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
			 final BufferedReader br = new BufferedReader(in)) {
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.OK.value(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

			// Create result
			id = jsonResult.get("id").asText();
			IntegrationTestUtils.setupDB();
		}
	}

	@After
	public void tearDown() throws IOException {
		if (id != null) {
			// Delete
			deleteCaterer(id);
		}
	}

	private void deleteCaterer(final String id) throws IOException {
		// Delete
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", apiKey)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpDelete request = new HttpDelete(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		HttpClientBuilder.create().build().execute(request);
	}

	@Test
	public void testUnauthorized() throws IOException {
		// Given
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);
	}

	@Test
	public void testCreate() throws IOException, EpickurParsingException {

		// Create
		String name = RandomStringUtils.randomAlphabetic(10);
		Caterer caterer = new Caterer();
		caterer.setName(name);
		caterer.setDescription("Caterer description");
		caterer.setManager("Manager name");
		caterer.setEmail("email@email2.com");
		caterer.setPhone("000011222");
		Location location = new Location();
		Address address = new Address();
		address.setCity("Chicago");
		address.setCountry("USA");
		address.setHouseNumber("832");
		address.setLabel("carl");
		address.setPostalCode(60614);
		address.setState("Illinois");
		address.setStreet("Wrightwood");
		location.setAddress(address);
		Geo geo = new Geo();
		geo.setLongitude(-73.97);
		geo.setLatitude(40.77);
		location.setGeo(geo);
		caterer.setLocation(location);
		caterer.setWorkingTimes(EntityGenerator.generateRandomWorkingTimes());

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT)
				.queryParam("key", apiKey)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

		// Create result
		assertEquals(name, jsonResult.get("name").asText());
		assertNotNull(jsonResult.get("createdAt"));
		assertNotNull(jsonResult.get("updatedAt"));
		assertNotNull(jsonResult.get("location"));
		JsonNode locationRes = jsonResult.get("location");
		assertNotNull(locationRes.get("address"));
		JsonNode addressRes = locationRes.get("address");
		assertEquals("carl", addressRes.get("label").asText());
		assertEquals("832", addressRes.get("houseNumber").asText());
		assertEquals("Wrightwood", addressRes.get("street").asText());
		assertEquals("Chicago", addressRes.get("city").asText());
		assertEquals(new Long(60614).longValue(), addressRes.get("postalCode").longValue());
		assertEquals("Illinois", addressRes.get("state").asText());
		assertEquals("USA", addressRes.get("country").asText());
		assertNotNull(locationRes.get("geo"));
		JsonNode geoRes = locationRes.get("geo");
		assertEquals("Point", geoRes.get("type").asText());
		ArrayNode coordinatesArray = (ArrayNode) geoRes.get("coordinates");
		assertEquals(new Float(-73.97).doubleValue(), coordinatesArray.get(0).doubleValue(), 0.01);
		assertEquals(new Float(40.77).doubleValue(), coordinatesArray.get(1).doubleValue(), 0.01);

		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		// Delete this caterer
		deleteCaterer(id);
	}

	@Test
	public void testReadOneCaterer() throws IOException {
		// Read
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(protocol).host(host).port(port).pathSegment(path, ENDPOINT, "{id}")
				.queryParam("key", apiKey)
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader(CONTENT_TYPE, JSON_MIME_TYPE);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(HttpStatus.OK.value(), statusCode);

		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(JSON_MIME_TYPE, mimeType);

		assertEquals(name, jsonResult.get("name").asText());
		assertNotNull(jsonResult.get("createdAt"));
		assertNotNull(jsonResult.get("updatedAt"));
		assertNotNull(jsonResult.get("location"));
		JsonNode locationRes = jsonResult.get("location");
		assertNotNull(locationRes.get("address"));
		JsonNode addressRes = locationRes.get("address");
		assertEquals("carl", addressRes.get("label").asText());
		assertEquals("832", addressRes.get("houseNumber").asText());
		assertEquals("Wrightwood", addressRes.get("street").asText());
		assertEquals("Chicago", addressRes.get("city").asText());
		assertEquals(new Long(60614).longValue(), addressRes.get("postalCode").longValue());
		assertEquals("Illinois", addressRes.get("state").asText());
		assertEquals("USA", addressRes.get("country").asText());
		assertNotNull(locationRes.get("geo"));
		JsonNode geoRes = locationRes.get("geo");
		assertEquals("Point", geoRes.get("type").asText());
		ArrayNode coordinates = (ArrayNode) geoRes.get("coordinates");
		assertEquals(new Float(-73.97).doubleValue(), coordinates.get(0).doubleValue(), 0.01);
		assertEquals(new Float(40.77).doubleValue(), coordinates.get(1).doubleValue(), 0.01);
	}
}
