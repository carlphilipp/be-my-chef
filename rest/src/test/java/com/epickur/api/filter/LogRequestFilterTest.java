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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

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
		// Given
		Enumeration params = mock(Enumeration.class);
		given(params.hasMoreElements()).willReturn(true, false);
		given(params.nextElement()).willReturn("param");
		given(request.getRequestURL()).willReturn(new StringBuffer());
		given(request.getParameterNames()).willReturn(params);
		given(request.getHeader("X-FORWARDED-FOR")).willReturn(null);

		// When
		filter.doFilterInternal(request, response, filterChain);
	}

	@Test
	public void testDoFilterInternalFail() throws IOException, ServletException, EpickurException {
		// Given
		Enumeration params = mock(Enumeration.class);
		given(params.hasMoreElements()).willReturn(true, false);
		given(params.nextElement()).willReturn("param");
		given(request.getRequestURL()).willReturn(new StringBuffer());
		given(request.getParameterNames()).willReturn(params);
		given(request.getHeader("X-FORWARDED-FOR")).willReturn(null);
		given(dao.create(isA(Log.class))).willThrow(new EpickurException());

		// When
		filter.doFilterInternal(request, response, filterChain);
	}
}
