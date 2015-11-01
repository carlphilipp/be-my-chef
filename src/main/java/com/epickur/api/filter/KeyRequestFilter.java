package com.epickur.api.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.dao.mongo.KeyDAOImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.EpickurPriorities;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;

/**
 * Filter that check if the provided key is valid
 * 
 * @author carl
 * @version 1.0
 */
@Priority(EpickurPriorities.AUTHORIZATION)
@Provider
public final class KeyRequestFilter implements ContainerRequestFilter {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(KeyRequestFilter.class.getSimpleName());
	/** Key dao */
	private KeyDAOImpl keyDAO;

	/** Constructor */
	public KeyRequestFilter() {
		super();
		this.keyDAO = new KeyDAOImpl();
	}

	public KeyRequestFilter(final KeyDAOImpl keyDao) {
		this.keyDAO = keyDao;
	}

	@Override
	public void filter(final ContainerRequestContext context) throws IOException {
		String urlPath = context.getUriInfo().getPath();
		if (urlPath!= null && !StringUtils.startsWith(urlPath, "nokey/")) {
			String paramKey = context.getUriInfo().getQueryParameters().getFirst("key");
			if (paramKey == null) {
				abortRequest(context, Response.Status.UNAUTHORIZED, ErrorUtils.MISSING_KEY);
			} else {
				processKey(context, paramKey);
			}
		}
	}

	protected void abortRequest(final ContainerRequestContext context, final Response.Status status, final String errorType) {
		Response response = ErrorUtils.error(status, errorType);
		context.abortWith(Response.status(status).entity(response.getEntity()).build());
	}

	protected void processKey(final ContainerRequestContext context, final String paramKey) throws IOException {
		try {
			handleKey(context, paramKey);
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
			abortRequest(context, Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		}
	}
	
	protected void handleKey(final ContainerRequestContext context, final String paramKey) throws EpickurException, IOException{
		String apiKey = Utils.getAPIKey();
		if (paramKey.equals(apiKey)) {
			handleAPIKey(context);
		} else {
			handlePrivateKey(context, paramKey);
		}
	}

	protected void handleAPIKey(final ContainerRequestContext context) {
		Key readKey = new Key();
		readKey.setRole(Role.EPICKUR_WEB);
		context.setProperty("key", readKey);
	}

	protected void handlePrivateKey(final ContainerRequestContext context, final String paramKey) throws EpickurException {
		Key key = keyDAO.read(paramKey);
		if (!Utils.isValid(key)) {
			abortRequest(context, Response.Status.UNAUTHORIZED, ErrorUtils.INVALID_KEY);
		} else {
			context.setProperty("key", key);
		}
	}
}