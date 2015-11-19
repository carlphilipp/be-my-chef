/**
 * @apiDefine BadRequestError
 * @apiVersion 1.0.0
 *
 * @apiError (Error){Number} error The HTTP error code.
 * @apiError (Error){String} message The error message.
 * @apiError (Error){String} message The description.
 *
 * @apiErrorExample {json} Bad Request:
 *	HTTP/1.1 400 Bad Request
 *	{
 *		"error":403,
 *		"message":"Bad Request",
 *		"description":"Unrecognized field label"
 *	}
 */
/**
 * @apiDefine InternalError
 * @apiVersion 1.0.0
 *
 * @apiError (Error){Number} error The HTTP error code.
 * @apiError (Error){String} message The error message.
 *
 * @apiErrorExample {json} Internal Server Error:
 *	HTTP/1.1 500 Internal server error
 *	{
 *		"error":500,
 *		"message":"Internal server error"
 *	}
 */

/**
 * @apiDefine ForbiddenError
 * @apiVersion 1.0.0
 *
 * @apiError (Error){Number} error The HTTP error code.
 * @apiError (Error){String} message The error message.
 *
 * @apiErrorExample {json} Forbidden:
 *	HTTP/1.1 403 Forbidden
 *	{
 *		"error":403,
 *		"message":"Forbidden"
 *	}
 */