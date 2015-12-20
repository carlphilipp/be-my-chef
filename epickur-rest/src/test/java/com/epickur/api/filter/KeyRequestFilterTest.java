package com.epickur.api.filter;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KeyRequestFilterTest {

	private static final String KEY_PROPERTY = "key";

	private static final String KEY_VALUE = "keyValue";
	@Mock
	private Utils utils;
	@Mock
	private KeyDAO dao;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;
	@Mock
	private PrintWriter printWriter;
	@InjectMocks
	@Spy
	private KeyRequestFilter filter;

	@Test
	public void testHandlePrivateKey() throws EpickurException, IOException, ServletException {
		Key key = EntityGenerator.generateRandomAdminKey();
		when(dao.read(key.getKey())).thenReturn(key);
		when(utils.isValid(key)).thenReturn(true);

		filter.handlePrivateKey(request, response, filterChain, key.getKey());

		verify(request, times(1)).setAttribute(KEY_PROPERTY, key);
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	public void testHandlePrivateKeyAbort() throws EpickurException, IOException, ServletException {
		Key key = EntityGenerator.generateRandomAdminKey();
		when(dao.read(key.getKey())).thenReturn(key);
		when(utils.isValid(key)).thenReturn(false);
		doNothing().when(filter).abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.INVALID_KEY);

		filter.handlePrivateKey(request, response, filterChain, key.getKey());

		verify(request, never()).setAttribute(KEY_PROPERTY, key);
		verify(filterChain, never()).doFilter(request, response);
	}

	@Test
	public void testHandleAPIKey() throws IOException, ServletException {
		filter.handleAPIKey(request, response, filterChain);

		verify(request, times(1)).setAttribute(eq(KEY_PROPERTY), any(Key.class));
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	public void testHandleKeyPrivate() throws ServletException, IOException, EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		String apiKey = EntityGenerator.generateRandomString();
		when(utils.getAPIKey()).thenReturn(apiKey);
		doNothing().when(filter).handlePrivateKey(request, response, filterChain, key.getKey());

		filter.handleKey(request, response, filterChain, key.getKey());

		verify(filter, times(1)).handlePrivateKey(request, response, filterChain, key.getKey());
	}

	@Test
	public void testHandleKeyAPI() throws ServletException, IOException, EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		when(utils.getAPIKey()).thenReturn(key.getKey());
		doNothing().when(filter).handleAPIKey(request, response, filterChain);

		filter.handleKey(request, response, filterChain, key.getKey());

		verify(filter, times(1)).handleAPIKey(request, response, filterChain);
	}

	@Test
	public void testProcessKey() throws IOException, ServletException, EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		doNothing().when(filter).handleKey(request, response, filterChain, key.getKey());

		filter.processKey(request, response, filterChain, key.getKey());

		verify(filter, times(1)).handleKey(request, response, filterChain, key.getKey());
	}

	@Test
	public void testProcessKeyException() throws IOException, ServletException, EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		doThrow(EpickurException.class).when(filter).handleKey(request, response, filterChain, key.getKey());
		doNothing().when(filter).abortRequest(response, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

		filter.processKey(request, response, filterChain, key.getKey());

		verify(filter, times(1)).abortRequest(response, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	@Test
	public void testAbortRequest() throws IOException {
		when(response.getWriter()).thenReturn(printWriter);

		filter.abortRequest(response, HttpStatus.OK, "error");

		verify(printWriter, times(1)).write(anyString());
	}

	@Test
	public void testDoFilterInternalProcessKey() throws IOException, ServletException {
		String url = "localhost";
		when(request.getRequestURI()).thenReturn(url);
		when(request.getParameter(KEY_PROPERTY)).thenReturn(KEY_VALUE);
		doNothing().when(filter).processKey(request, response, filterChain, KEY_VALUE);

		filter.doFilterInternal(request, response, filterChain);

		verify(filter, times(1)).processKey(request, response, filterChain, KEY_VALUE);
	}

	@Test
	public void testDoFilterInternalAbortRequest() throws IOException, ServletException {
		String url = "localhost";
		when(request.getRequestURI()).thenReturn(url);
		when(request.getParameter(KEY_PROPERTY)).thenReturn(null);
		doNothing().when(filter).abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.MISSING_KEY);

		filter.doFilterInternal(request, response, filterChain);

		verify(filter, times(1)).abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.MISSING_KEY);
	}

	@Test
	public void testDoFilterInternalNoKey() throws IOException, ServletException {
		String url = "localhost/nokey/";
		when(request.getRequestURI()).thenReturn(url);
		doNothing().when(filterChain).doFilter(request, response);

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
	}
}
