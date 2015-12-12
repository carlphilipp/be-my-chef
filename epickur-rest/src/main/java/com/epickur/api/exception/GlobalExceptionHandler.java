package com.epickur.api.exception;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.web.ResponseError;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(GlobalExceptionHandler.class.getSimpleName());

	/**
	 * Context
	 */
	@Autowired
	private HttpServletRequest context;

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(HttpStatus.BAD_REQUEST.value());
		errorMessage.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		LOG.warn("Fatal Error: {} {}", ex.getMessage(), ex.getClass(), ex);
		return new ResponseEntity<>(errorMessage, getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ Throwable.class, Exception.class })
	public ResponseEntity<?> handleThrowable(final Throwable throwable) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorMessage.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		LOG.error("Fatal Error: {} {}", throwable.getLocalizedMessage(), throwable.getClass(), throwable);
		return new ResponseEntity<Object>(errorMessage, getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EpickurException.class)
	public ResponseEntity<?> handleEpickurException(final EpickurException exception) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (exception instanceof EpickurNotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setError(HttpStatus.NOT_FOUND.value());
			errorMessage.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
			if (!StringUtils.isBlank(exception.getMessage())) {
				errorMessage.addDescription(exception.getMessage());
			}
			return new ResponseEntity<>(errorMessage, headers, HttpStatus.NOT_FOUND);
		} else if (exception instanceof EpickurParsingException) {
			LOG.error(exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		} else if (exception instanceof EpickurDuplicateKeyException) {
			return ResponseError.error(HttpStatus.CONFLICT, exception.getLocalizedMessage());
		} else if (exception instanceof EpickurDBException) {
			EpickurDBException ex = (EpickurDBException) exception;
			StringBuilder stb = new StringBuilder();
			stb.append("Request " + ex.getOperation() + " failed");

			if (ex.getDocument() != null) {
				stb.append(" with: " + ex.getDocument());
			}
			if (ex.getId() != null) {
				stb.append(" - id: " + ex.getId());
			}
			if (ex.getUpdate() != null) {
				stb.append(" - update: " + ex.getUpdate());
			}
			LOG.error(exception.getLocalizedMessage() + " - " + stb, ex);
			return ResponseError.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		} else if (exception instanceof GeoLocationException) {
			LOG.error("Here exception: {}", exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
		} else if (exception instanceof OrderStatusException) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setError(HttpStatus.BAD_REQUEST.value());
			errorMessage.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
			if (!StringUtils.isBlank(exception.getMessage())) {
				errorMessage.addDescription(exception.getMessage());
			}
			return new ResponseEntity<>(errorMessage, getHeaders(), HttpStatus.BAD_REQUEST);
		} else {
			LOG.error(exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		}
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleConstraintViolationException(final ConstraintViolationException exception) {
		ErrorMessage message = new ErrorMessage();
		message.setError(HttpStatus.BAD_REQUEST.value());
		message.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		Set<ConstraintViolation<?>> constraints = exception.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iterator = constraints.iterator();
		while (iterator.hasNext()) {
			ConstraintViolation<?> constraint = iterator.next();
			message.addDescription(constraint.getMessage());
		}
		LOG.error("Error: {}", exception.getLocalizedMessage(), exception);
		return new ResponseEntity<>(message, getHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ IllegalArgumentException.class })
	public ResponseEntity<?> handleIllegalArgumentException(final IllegalArgumentException exception) {
		ErrorMessage mess = new ErrorMessage();
		mess.setError(HttpStatus.BAD_REQUEST.value());
		mess.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
			mess.addDescription(exception.getMessage());
		}
		LOG.error("Error: {}", exception.getMessage(), exception);
		return new ResponseEntity<>(mess, getHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ EpickurForbiddenException.class })
	public ResponseEntity<?> handleEpickurForbiddenException(final EpickurForbiddenException exception) {
		Key key = (Key) context.getAttribute("key");
		LOG.warn("Forbidden : {} {}", exception.getMessage(), key.getId() != null ? " - User Id " + key.getId().toHexString() : "");
		return ResponseError.error(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorMessage message = new ErrorMessage();
		message.setError(status.value());
		message.setMessage(status.getReasonPhrase());
		Key key = (Key) context.getAttribute("key");
		LOG.warn("{} - {} - {} {} {}", ex.getClass().getSimpleName(), ex.getLocalizedMessage(), key.getKey(), key.getUserId(), key.getRole());
		return new ResponseEntity<>(message, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		ErrorMessage message = new ErrorMessage();
		message.setError(HttpStatus.BAD_REQUEST.value());
		message.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		BindingResult bidingResult = ex.getBindingResult();
		List<ObjectError> errors = bidingResult.getAllErrors();
		for (ObjectError error : errors) {
			message.addDescription(error.getDefaultMessage());
		}
		Key key = (Key) context.getAttribute("key");
		LOG.warn("MethodArgumentNotValidException {} {}", message.getDescriptions(), key.toString());
		return new ResponseEntity<>(message, getHeaders(), HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		ErrorMessage message = new ErrorMessage();
		message.setError(status.value());
		message.setMessage(status.getReasonPhrase());
		message.addDescription("Required request body is probably  missing");
		Key key = (Key) context.getAttribute("key");
		LOG.warn("{} - {} - {} {} {}", ex.getClass().getSimpleName(), ex.getMessage(), key.getKey(), key.getUserId(), key.getRole());
		return new ResponseEntity<>(message, headers, status);
	}
}
