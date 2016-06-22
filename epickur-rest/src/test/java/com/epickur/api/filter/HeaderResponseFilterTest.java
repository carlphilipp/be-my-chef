package com.epickur.api.filter;

import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class HeaderResponseFilterTest {
	
	@Test
	public void testCreate() throws IOException, ServletException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		HeaderResponseFilter filter = new HeaderResponseFilter();
		filter.doFilterInternal(null, response, filterChain);
		verify(filterChain).doFilter(null, response);
	}
}
