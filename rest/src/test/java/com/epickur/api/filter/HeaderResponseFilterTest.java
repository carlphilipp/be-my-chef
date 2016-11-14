package com.epickur.api.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class HeaderResponseFilterTest {

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Test
	public void testCreate() throws IOException, ServletException {
		// Given
		HeaderResponseFilter filter = new HeaderResponseFilter();

		// When
		filter.doFilterInternal(null, response, filterChain);

		// Then
		then(filterChain).should().doFilter(null, response);
	}
}
