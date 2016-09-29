package com.epickur.api.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class that add Access-Control-Allow-Origin in headers of any requests.
 *
 * @author cph
 * @version 1.0
 */
public final class HeaderResponseFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws
			ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		filterChain.doFilter(request, response);
	}
}
