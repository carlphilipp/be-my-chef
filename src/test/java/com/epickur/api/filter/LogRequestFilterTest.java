package com.epickur.api.filter;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.Mockito;

public class LogRequestFilterTest {

	@Test
	public void testCreate() throws IOException {
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		UriInfo uriInfo = mock(UriInfo.class);
		Request request = mock(Request.class);
		Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
		Mockito.when(requestContext.getHeaders()).thenReturn(map);
		Mockito.when(requestContext.getRequest()).thenReturn(request);
		Mockito.when(uriInfo.getQueryParameters()).thenReturn(map);

		LogRequestFilter filter = new LogRequestFilter();
		filter.filter(requestContext);
	}

}
