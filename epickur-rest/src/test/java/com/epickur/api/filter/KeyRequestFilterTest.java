package com.epickur.api.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.mockito.Mock;

import com.epickur.api.dao.mongo.KeyDAO;

public class KeyRequestFilterTest {

	// TODO create test

	private static final String KEY_PROPERTY = "key";

	private static final String KEY_VALUE = "keyValue";

	private KeyRequestFilter filter;
	@Mock
	private KeyDAO dao;
//	@Mock
//	private ContainerRequestContext context;
//	@Mock
//	private UriInfo uriInfo;
//
//	private Key validKey;
//
//	private Key notValidKey;
//
//	private MultivaluedMap<String, String> params;

//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//
//		filter = new KeyRequestFilter(dao);
//
//		validKey = new Key();
//		DateTime now = new DateTime();
//		validKey.setCreatedAt(now);
//		validKey.setUpdatedAt(now);
//		validKey.setId(new ObjectId());
//		validKey.setRole(Role.EPICKUR_WEB);
//		validKey.setUserId(new ObjectId());
//
//		notValidKey = new Key();
//		now = new DateTime();
//		now = now.minusDays(1 + Utils.SESSION_TIMEOUT);
//		notValidKey.setCreatedAt(now);
//		notValidKey.setUpdatedAt(now);
//		notValidKey.setId(new ObjectId());
//		notValidKey.setRole(Role.EPICKUR_WEB);
//		notValidKey.setUserId(new ObjectId());
//
//		params = new MultivaluedHashMap<>();
//		params.add(KEY_PROPERTY, KEY_VALUE);
//	}
//
//	@Test
//	public void testFilter() throws IOException, EpickurException {
//		when(context.getUriInfo()).thenReturn(uriInfo);
//		when(uriInfo.getPath()).thenReturn("my/path");
//		when(uriInfo.getQueryParameters()).thenReturn(params);
//		when(dao.read(KEY_VALUE)).thenReturn(validKey);
//
//		filter.filter(context);
//
//		verify(context, times(1)).setProperty(KEY_PROPERTY, validKey);
//	}
//
//	@Test
//	public void testFilterKeyNull() throws IOException, EpickurException {
//		params.clear();
//		when(context.getUriInfo()).thenReturn(uriInfo);
//		when(uriInfo.getPath()).thenReturn("my/path");
//		when(uriInfo.getQueryParameters()).thenReturn(params);
//
//		filter.filter(context);
//
//		verify(context, never()).setProperty(anyString(), anyObject());
//	}
//
//	@Test
//	public void testAbortRequest() {
//		filter.abortRequest(context, Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
//
//		verify(context, times(1)).abortWith((Response) anyObject());
//	}
//
//	@Test
//	public void testProcessKey() throws IOException, EpickurException {
//		when(dao.read(KEY_VALUE)).thenReturn(validKey);
//
//		filter.processKey(context, KEY_VALUE);
//
//		verify(context, times(1)).setProperty(KEY_PROPERTY, validKey);
//	}
//
//	@Test
//	public void testProcessKeyException() throws IOException, EpickurException {
//		when(dao.read(KEY_VALUE)).thenThrow(new EpickurException());
//
//		filter.processKey(context, KEY_VALUE);
//
//		verify(context, never()).setProperty(anyString(), anyObject());
//	}
//
//	@Test
//	public void testHandleKeyWithAPIKey() throws EpickurException, IOException {
//		when(context.getProperty(KEY_PROPERTY)).thenReturn(validKey);
//
//		String apiKey = Utils.getAPIKey();
//		filter.handleKey(context, apiKey);
//
//		assertEquals(Role.EPICKUR_WEB, ((Key) context.getProperty(KEY_PROPERTY)).getRole());
//	}
//
//	@Test
//	public void testHandleKeyWithPrivateKey() throws EpickurException, IOException {
//		filter.handleKey(context, KEY_VALUE);
//
//		verify(dao, times(1)).read(KEY_VALUE);
//	}
//
//	@Test
//	public void testHandleAPIKey() {
//		Key key = new Key();
//		key.setRole(Role.EPICKUR_WEB);
//
//		when(context.getProperty(KEY_PROPERTY)).thenReturn(key);
//
//		filter.handleAPIKey(context);
//
//		verify(context, times(1)).setProperty(KEY_PROPERTY, key);
//		assertEquals(key, context.getProperty(KEY_PROPERTY));
//	}
//
//	@Test
//	public void testHandlePrivateKeyValid() throws EpickurException {
//		when(dao.read(KEY_VALUE)).thenReturn(validKey);
//
//		filter.handlePrivateKey(context, KEY_VALUE);
//
//		verify(dao, times(1)).read(KEY_VALUE);
//		verify(context, times(1)).setProperty(KEY_PROPERTY, validKey);
//	}
//
//	@Test
//	public void testHandlePrivateKeyNotValid() throws EpickurException {
//		when(dao.read(KEY_VALUE)).thenReturn(notValidKey);
//
//		filter.handlePrivateKey(context, KEY_VALUE);
//
//		verify(dao, times(1)).read(KEY_VALUE);
//		verify(context, never()).setProperty(KEY_PROPERTY, notValidKey);
//	}
//
//	@Test
//	public void testHandlePrivateKeyNull() throws EpickurException {
//		when(dao.read(KEY_VALUE)).thenReturn(null);
//
//		filter.handlePrivateKey(context, KEY_VALUE);
//
//		verify(dao, times(1)).read(KEY_VALUE);
//		verify(context, never()).setProperty(KEY_PROPERTY, null);
//	}
}
