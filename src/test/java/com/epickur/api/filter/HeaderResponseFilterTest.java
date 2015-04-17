package com.epickur.api.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.mockito.Mockito;

public class HeaderResponseFilterTest {
	
	@Test
	public void testCreate() throws IOException {
		ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
		MultivaluedMap<String, Object> map = new MultivaluedHashMap<String, Object>();
		Mockito.when(responseContext.getHeaders()).thenReturn(map);

		HeaderResponseFilter filter = new HeaderResponseFilter();
		filter.filter(null, responseContext);
		assertTrue(!responseContext.getHeaders().isEmpty());
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		List<Object> test = headers.get("Access-Control-Allow-Origin");
		assertEquals(1, test.size());
		String expected = "*";
		String actual = (String)test.get(0);
		assertEquals(expected, actual);
	}
}
