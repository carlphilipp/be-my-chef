package com.epickur.api.filter;

import com.epickur.api.dao.mongo.LogDAO;
import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Log4j2
@Component("logRequestFilter")
public class LogRequestFilter extends OncePerRequestFilter {

	@Autowired
	private LogDAO logDAO;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
		final Log logEntity = new Log();
		logEntity.setTime(new DateTime());
		logEntity.setUrl(request.getRequestURL().toString());
		logEntity.setMethod(request.getMethod());
		logEntity.setProtocol(request.getProtocol());
		final Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			final String param = params.nextElement();
			logEntity.getArgs().put(param, request.getParameter(param));
		}
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		logEntity.setRemoteAddr(ipAddress);
		logEntity.setUserAgent(request.getHeader("User-Agent"));
		try {
			logDAO.create(logEntity);
		} catch (final EpickurException e) {
			log.warn("Can not put log into DB. {}", e.getLocalizedMessage());
		}
		filterChain.doFilter(request, response);
	}
}
