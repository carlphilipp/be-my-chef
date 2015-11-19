package com.epickur.api.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Class that add Access-Control-Allow-Origin in headers of any requests.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
public final class HeaderResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
		responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
	}
}
