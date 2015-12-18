package com.epickur.api.filter;

import com.epickur.api.dao.mongo.LogDAO;
import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogRequestFilterTest {

	@Mock
	private LogDAO dao;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;
	@InjectMocks
	@Spy
	private LogRequestFilter filter;

	@Test
	public void testDoFilterInternal() throws IOException, ServletException {
		Enumeration params = mock(Enumeration.class);
		when(params.hasMoreElements()).thenReturn(true, false);
		when(params.nextElement()).thenReturn("param");
		when(request.getRequestURL()).thenReturn(new StringBuffer());
		when(request.getParameterNames()).thenReturn(params);
		when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);

		filter.doFilterInternal(request, response, filterChain);
	}

	@Test
	public void testDoFilterInternalFail() throws IOException, ServletException, EpickurException {
		Enumeration params = mock(Enumeration.class);
		when(params.hasMoreElements()).thenReturn(true, false);
		when(params.nextElement()).thenReturn("param");
		when(request.getRequestURL()).thenReturn(new StringBuffer());
		when(request.getParameterNames()).thenReturn(params);
		when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);
		when(dao.create(any(Log.class))).thenThrow(EpickurException.class);

		filter.doFilterInternal(request, response, filterChain);
	}
}
