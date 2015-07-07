package com.epickur.api.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.epickur.api.dao.mongo.LogDaoImpl;
import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;

/**
 * Filter that log any single request
 * 
 * @author cph
 * @version 1.0
 */
@Priority(Priorities.AUTHENTICATION)
@Provider
public final class LogRequestFilter implements ContainerRequestFilter {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(LogRequestFilter.class.getSimpleName());

	@Context
	private HttpServletRequest servletRequest;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		if (LOG.isDebugEnabled()) {
			UriInfo uriInfo = requestContext.getUriInfo();
			StringBuilder stb = new StringBuilder();
			stb.append(requestContext.getRequest().getMethod());
			stb.append("|");
			stb.append(uriInfo.getBaseUri());
			stb.append(uriInfo.getPath() + "?");
			MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
			for (Entry<String, List<String>> s : params.entrySet()) {
				stb.append(s.getKey() + "=" + s.getValue().get(0) + "&");
			}
			stb.deleteCharAt(stb.length() - 1);
			stb.append("|");
			stb.append(requestContext.getHeaders());
			LOG.trace(stb.toString());
		}
		Log log = new Log();
		log.setTime(new DateTime());
		log.setUrl(servletRequest.getRequestURL().toString());
		log.setMethod(requestContext.getMethod());
		log.setProtocol(servletRequest.getProtocol());

		String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = servletRequest.getRemoteAddr();
		}
		log.setRemoteAddr(ipAddress);
		log.setUserAgent(requestContext.getHeaderString("User-Agent"));
		LogDaoImpl dao = new LogDaoImpl();
		try {
			dao.create(log);
		} catch (EpickurException e) {
			LOG.warn("Can not put log into DB. " + e.getLocalizedMessage());
		}
	}
}