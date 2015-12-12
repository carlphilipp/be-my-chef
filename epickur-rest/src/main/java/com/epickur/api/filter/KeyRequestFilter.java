package com.epickur.api.filter;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that check if the provided key is valid
 *
 * @author carl
 * @version 1.0
 */
@Component("keyRequestFilter")
public final class KeyRequestFilter extends OncePerRequestFilter {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(KeyRequestFilter.class.getSimpleName());
	/**
	 * Key dao
	 */
	@Autowired
	private KeyDAO keyDAO;
	@Autowired
	private Utils utils;

	public KeyRequestFilter() {
		super();
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		final String urlPath = request.getRequestURI();
		if (urlPath != null && !StringUtils.contains(urlPath, "/nokey/")) {
			final String paramKey = request.getParameter("key");
			if (paramKey == null) {
				abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorUtils.MISSING_KEY);
			} else {
				processKey(request, response, filterChain, paramKey);
			}
		} else if (StringUtils.contains(urlPath, "/nokey/")) {
			filterChain.doFilter(request, response);
		}
	}

	protected void abortRequest(final HttpServletResponse response, final HttpStatus status, final String errorType) throws IOException {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(status.getReasonPhrase());
		error.addDescription(errorType);
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		final ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(error));
	}

	protected void processKey(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain,
			final String paramKey) throws IOException, ServletException {
		try {
			handleKey(request, response, filterChain, paramKey);
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
			abortRequest(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		}
	}

	protected void handleKey(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain,
			final String paramKey) throws EpickurException, IOException, ServletException {
		String apiKey = utils.getAPIKey();
		if (paramKey.equals(apiKey)) {
			handleAPIKey(request, response, filterChain);
		} else {
			handlePrivateKey(request, response, filterChain, paramKey);
		}
	}

	protected void handleAPIKey(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		Key readKey = new Key();
		readKey.setRole(Role.EPICKUR_WEB);
		request.setAttribute("key", readKey);
		filterChain.doFilter(request, response);
	}

	protected void handlePrivateKey(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain,
			final String paramKey) throws EpickurException, IOException, ServletException {
		Key key = keyDAO.read(paramKey);
		if (!utils.isValid(key)) {
			abortRequest(response, HttpStatus.UNAUTHORIZED, ErrorUtils.INVALID_KEY);
		} else {
			request.setAttribute("key", key);
			filterChain.doFilter(request, response);
		}
	}
}