package com.epickur.api.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurForbiddenException;

public class AccessRights {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(AccessRights.class.getSimpleName());

	private static boolean[][] MATRIX = new boolean[][] {
// @formatter:off

	// Endpoint	    // 	-------------------------- USER --------------------------|------------------ ORDER ------------------|------------------------------- CATERER -------------------------------|------------------ DISH ------------------
	// Method		// 	create - read - update - delete - readAll - resetPassword - create - read - update - delete - readAll - create - read - update - delete - readAll - readDishes - payementInfo - create - read - update - delete - search
	// Column		//   0     - 1    - 2      - 3      - 4       - 5             - 6      - 7    - 8      - 9      - 10      - 11     - 12   - 13     - 14     - 15      - 16         - 17           - 18     - 19   - 20     - 21     - 22
/* Administrator */		{true  , true , true   , true   , true    , true          , true   , true , true   , true   , true    , true   , true , true   , true   , true    , true       , true         , true   , true , true   , true   , true}, 	/* Administrator */
/* Super User */		{false , true , true   , false  , false   , false         , true   , true , true   , false  , true    , false  , true , true   , false  , false   , true       , false        , true   , true , true   , true   , true}, 	/* Super User */
/* User */				{false , true , true   , false  , false   , false         , true   , true , true   , false  , false   , false  , true , false  , false  , false   , true       , false        , false  , true , false  , false  , true}, 	/* User */
/* Epickur-Web */		{true  , false, false  , false  , false   , false         , false  , false, false  , false  , false   , false  , false, false  , false  , false   , false      , false        , false  , false, false  , false  , false}, 	/* Epickur-Web */

// @formatter:on
	};

	/**
	 * Avoid constuction
	 */
	private AccessRights() {
	}

	public static void check(final Role role, final Operation operation, final EndpointType endpoint) {
		int line = getLine(role);
		int column = getColumn(operation, endpoint);
		LOG.info("[LINE] Operation: " + role + " " + line);
		LOG.info("[COLUMN] Role: " + operation + " with " + endpoint + " " + column);
		if (line == -1 || column == -1) {
			LOG.error("Unable to find the access rights (" + line + ";" + column + ") with " + role + ", " + operation + " and " + endpoint);
			throw new EpickurForbiddenException();
		}
		if (!MATRIX[line][column]) {
			throw new EpickurForbiddenException();
		}
	}

	private static int getLine(final Role role) {
		int line = -1;
		switch (role) {
		case ADMIN:
			line = 0;
			break;
		case SUPER_USER:
			line = 1;
			break;
		case USER:
			line = 2;
			break;
		case EPICKUR_WEB:
			line = 3;
			break;
		default:
			break;
		}
		return line;
	}

	private static int getColumn(final Operation operation, final EndpointType endpoint) {
		int line = -1;
		int offset = getOffset(endpoint);
		switch (operation) {
		case CREATE:
			line = offset;
			break;
		case READ:
			line = offset + 1;
			break;
		case UPDATE:
			line = offset + 2;
			break;
		case DELETE:
			line = offset + 3;
			break;
		case READ_ALL:
			if (endpoint.equals(EndpointType.DISH)) {
				throw new EpickurForbiddenException();
			}
			line = offset + 4;
			break;
		case RESET_PASSWORD:
			if (!endpoint.equals(EndpointType.USER)) {
				throw new EpickurForbiddenException();
			}
			line = offset + 5;
			break;
		case READ_DISHES:
			if (!endpoint.equals(EndpointType.CATERER)) {
				throw new EpickurForbiddenException();
			}
			line = offset + 5;
			break;
		case PAYEMENT_INFO:
			if (!endpoint.equals(EndpointType.CATERER)) {
				throw new EpickurForbiddenException();
			}
			line = offset + 6;
			break;
		case SEARCH_DISH:
			if (!endpoint.equals(EndpointType.DISH)) {
				throw new EpickurForbiddenException();
			}
			line = offset + 4;
			break;
		default:
			break;
		}
		return line;
	}

	private static int getOffset(final EndpointType endpoint) {
		int offset = -1;
		switch (endpoint) {
		case USER:
			offset = 0;
			break;
		case ORDER:
			offset = 6;
			break;
		case CATERER:
			offset = 11;
			break;
		case DISH:
			offset = 18;
			break;
		default:
			break;
		}
		return offset;
	}

	public static void main(String[] args) {
		AccessRights.check(Role.USER, Operation.CREATE, EndpointType.DISH);
	}
}
