package com.epickur.api.filter;

import com.epickur.api.dao.mongo.LogDAO;
import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Filter that log any single request
 *
 * @author cph
 * @version 1.0
 */
//@Priority(EpickurPriorities.LOG)
//@Provider
public final class LogRequestFilter extends OncePerRequestFilter {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(LogRequestFilter.class.getSimpleName());

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws
			ServletException, IOException {
		Log log = new Log();
		log.setTime(new DateTime());
		log.setUrl(request.getRequestURL().toString());
		log.setMethod(request.getMethod());
		log.setProtocol(request.getProtocol());
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			log.getArgs().put(param, request.getParameter(param));
		}
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		log.setRemoteAddr(ipAddress);
		log.setUserAgent(request.getHeader("User-Agent"));
		LogDAO dao = new LogDAO();
		try {
			dao.create(log);
		} catch (EpickurException e) {
			LOG.warn("Can not put log into DB. " + e.getLocalizedMessage());
		}
		filterChain.doFilter(request, response);
	}
}