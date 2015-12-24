package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.*;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Cleanup;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class CatererIT {

	@Autowired
	private IntegrationTestUtils integrationTestUtils;

	private static final String jsonMimeType = "application/json";

	private static final String ENDPOINT = "caterers";

	private static String PROTOCOL;
	private static String HOST;
	private static String PORT;
	private static String PATH;

	private static String API_KEY;
	private static String NAME;
	private static String ID;

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
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

		// Create
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode caterer = mapper.createObjectNode();
		ObjectNode location = mapper.createObjectNode();
		ObjectNode address2 = mapper.createObjectNode();
		ObjectNode geo = mapper.createObjectNode();
		ArrayNode coordinates = mapper.createArrayNode();
		NAME = RandomStringUtils.randomAlphabetic(10);
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
		caterer.put("name", NAME);
		caterer.set("location", location);
		caterer.put("description", "Caterer description");
		caterer.put("manager", "Manager name");
		caterer.put("email", "email@email.com");
		caterer.put("phone", "000011222");
		WorkingTimes workingTimes = EntityGenerator.generateRandomWorkingTimes();

		caterer.set("workingTimes", mapper.readTree(workingTimes.toStringAPIView()));

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key",API_KEY )
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(caterer.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

		// Create result
		ID = jsonResult.get("id").asText();
	}

	@After
	public void tearDown() throws IOException {
		if (ID != null) {
			// Delete
			deleteCaterer(ID);
		}
	}

	private void deleteCaterer(final String id) throws IOException {
		// Delete
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key",API_KEY )
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();
		HttpDelete request = new HttpDelete(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(request);
	}

	@Test
	public void testUnauthorized() throws IOException {
		// Given
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.build()
				.expand(ID)
				.encode();
		URI uri = uriComponents.toUri();
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("content-type", jsonMimeType);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
	}

	@Test
	public void testCreate() throws IOException, EpickurParsingException {

		// Create
		ObjectMapper mapper = new ObjectMapper();
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
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key",API_KEY )
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		HttpPost request = new HttpPost(uri);
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		request.addHeader("content-type", jsonMimeType);
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
		assertEquals(jsonMimeType, mimeType);

		// Delete this caterer
		deleteCaterer(id);
	}

	@Test
	public void testReadOneCaterer() throws IOException {
		// Read
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key",API_KEY )
				.build()
				.expand(ID)
				.encode();
		URI uri = uriComponents.toUri();
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("content-type", jsonMimeType);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
		String obj = integrationTestUtils.readResult(httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(HttpStatus.OK.value(), statusCode);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		assertEquals(NAME, jsonResult.get("name").asText());
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
