package com.epickur.api.filter;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class KeyRequestFilterTest {

	private static final String KEY_PROPERTY = "key";
	private static final String KEY_VALUE = "keyValue";

	@Mock
	private ObjectMapper mapper;
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
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		given(dao.read(key.getKey())).willReturn(Optional.of(key));
		given(utils.isValid(key)).willReturn(true);

		// When
		filter.handlePrivateKey(request, response, filterChain, key.getKey());

		// Then
		then(request).should().setAttribute(KEY_PROPERTY, key);
		then(filterChain).should().doFilter(request, response);
	}

	@Test
	public void testHandlePrivateKeyAbort() throws EpickurException, IOException, ServletException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		given(dao.read(key.getKey())).willReturn(Optional.of(key));
		given(utils.isValid(key)).willReturn(false);
		willDoNothing().given(filter).abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.INVALID_KEY);

		// When
		filter.handlePrivateKey(request, response, filterChain, key.getKey());

		// Then
		then(request).should(never()).setAttribute(KEY_PROPERTY, key);
		then(filterChain).should(never()).doFilter(request, response);
	}

	@Test
	public void testHandleAPIKey() throws IOException, ServletException {
		// When
		filter.handleAPIKey(request, response, filterChain);

		// Then
		then(request).should().setAttribute(eq(KEY_PROPERTY), any(Key.class));
		then(filterChain).should().doFilter(request, response);
	}

	@Test
	public void testHandleKeyPrivate() throws ServletException, IOException, EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		String apiKey = EntityGenerator.generateRandomString();
		given(utils.getAPIKey()).willReturn(apiKey);
		willDoNothing().given(filter).handlePrivateKey(request, response, filterChain, key.getKey());

		// When
		filter.handleKey(request, response, filterChain, key.getKey());

		// Then
		then(filter).should().handlePrivateKey(request, response, filterChain, key.getKey());
	}

	@Test
	public void testHandleKeyAPI() throws ServletException, IOException, EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		given(utils.getAPIKey()).willReturn(key.getKey());
		willDoNothing().given(filter).handleAPIKey(request, response, filterChain);

		// When
		filter.handleKey(request, response, filterChain, key.getKey());

		// Then
		then(filter).should().handleAPIKey(request, response, filterChain);
	}

	@Test
	public void testProcessKey() throws IOException, ServletException, EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		willDoNothing().given(filter).handleKey(request, response, filterChain, key.getKey());

		// When
		filter.processKey(request, response, filterChain, key.getKey());

		// Then
		then(filter).should().handleKey(request, response, filterChain, key.getKey());
	}

	@Test
	public void testProcessKeyException() throws IOException, ServletException, EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		willThrow(EpickurException.class).given(filter).handleKey(request, response, filterChain, key.getKey());
		willDoNothing().given(filter).abortRequest(response, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

		// When
		filter.processKey(request, response, filterChain, key.getKey());

		// Then
		then(filter).should().abortRequest(response, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	@Test
	public void testAbortRequest() throws IOException {
		// Given
		given(response.getWriter()).willReturn(printWriter);
		given(mapper.writeValueAsString(any(ErrorMessage.class))).willReturn("");

		// When
		filter.abortRequest(response, HttpStatus.OK, "error");

		// Then
		then(printWriter).should().write(anyString());
	}

	@Test
	public void testDoFilterInternalProcessKey() throws IOException, ServletException {
		// Given
		String url = "localhost";
		given(request.getRequestURI()).willReturn(url);
		given(request.getParameter(KEY_PROPERTY)).willReturn(KEY_VALUE);
		willDoNothing().given(filter).processKey(request, response, filterChain, KEY_VALUE);

		// When
		filter.doFilterInternal(request, response, filterChain);

		// Then
		then(filter).should().processKey(request, response, filterChain, KEY_VALUE);
	}

	@Test
	public void testDoFilterInternalAbortRequest() throws IOException, ServletException {
		// Given
		String url = "localhost";
		given(request.getRequestURI()).willReturn(url);
		given(request.getParameter(KEY_PROPERTY)).willReturn(null);
		willDoNothing().given(filter).abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.MISSING_KEY);

		// When
		filter.doFilterInternal(request, response, filterChain);

		// Then
		then(filter).should().abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorConstants.MISSING_KEY);
	}

	@Test
	public void testDoFilterInternalNoKey() throws IOException, ServletException {
		// Given
		String url = "localhost/nokey/";
		given(request.getRequestURI()).willReturn(url);
		willDoNothing().given(filterChain).doFilter(request, response);

		// When
		filter.doFilterInternal(request, response, filterChain);

		// Then
		then(filterChain).should().doFilter(request, response);
	}
}
