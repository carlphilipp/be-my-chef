package com.epickur.api.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.dao.mongo.KeyDaoImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Utils;

/**
 * Filter that check if the provided key is valid
 * 
 * @author carl
 * @version 1.0
 */
@Priority(Priorities.AUTHORIZATION)
@Provider
public final class KeyRequestFilter implements ContainerRequestFilter {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(KeyRequestFilter.class.getSimpleName());
	/** Key dao */
	private KeyDaoImpl keyDao;

	/** Constructor */
	public KeyRequestFilter() {
		super();
		this.keyDao = new KeyDaoImpl();
	}

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		String paramKey = requestContext.getUriInfo().getQueryParameters().getFirst("key");
		String urlPath = requestContext.getUriInfo().getPath();
		if (urlPath != null && !urlPath.startsWith("nokey/")) {
			if (paramKey == null) {
				Response response = ErrorUtils.error(Response.Status.UNAUTHORIZED, ErrorUtils.MISSING_KEY);
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(response.getEntity()).build());
			} else {

				String apiKey = Utils.getAPIKey();
				try {
					Key key = keyDao.read(paramKey);
					if (!paramKey.equals(apiKey) && !Utils.isValid(key)) {
						Response response = ErrorUtils.error(Response.Status.UNAUTHORIZED, ErrorUtils.INVALID_KEY);
						requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(response.getEntity()).build());
					} else if (paramKey.equals(apiKey)) {
						Key readKey = new Key();
						readKey.setRole(Role.EPICKUR_WEB);
						requestContext.setProperty("key", readKey);
					} else {
						requestContext.setProperty("key", key);
					}
				} catch (EpickurException e) {
					LOG.error(e.getLocalizedMessage(), e);
					Response response = ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
					requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response.getEntity()).build());
				}
			}
		}
	}

}