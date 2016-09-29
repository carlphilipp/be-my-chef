package com.epickur.api.exception;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.web.ResponseError;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Global exception handler. All failing requests not handled anywhere else should be redirected here.
 *
 * @author cph
 */
@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Context
	 */
	@Autowired
	private HttpServletRequest request;

	@ExceptionHandler({Throwable.class, Exception.class})
	public ResponseEntity<ErrorMessage> handleThrowable(final Throwable throwable) {
		log.error("Fatal Error: {} {}", throwable.getLocalizedMessage(), throwable.getClass(), throwable);
		return ResponseError.error(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EpickurException.class)
	public ResponseEntity<ErrorMessage> handleEpickurException(final EpickurException exception) {
		if (exception instanceof EpickurNotFoundException) {
			return ResponseError.error(HttpStatus.NOT_FOUND);
		} else if (exception instanceof EpickurParsingException) {
			log.error(exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.BAD_REQUEST);
		} else if (exception instanceof EpickurDuplicateKeyException) {
			return ResponseError.error(HttpStatus.CONFLICT, exception.getLocalizedMessage());
		} else if (exception instanceof EpickurDBException) {
			final EpickurDBException ex = (EpickurDBException) exception;
			final StringBuilder stb = new StringBuilder();
			stb.append("Request ").append(ex.getOperation()).append(" failed");
			if (ex.getDocument() != null) {
				stb.append(" with: ").append(ex.getDocument());
			}
			if (ex.getId() != null) {
				stb.append(" - id: ").append(ex.getId());
			}
			if (ex.getUpdate() != null) {
				stb.append(" - update: ").append(ex.getUpdate());
			}
			log.error(exception.getLocalizedMessage() + " - " + stb, ex);
			return ResponseError.error(HttpStatus.BAD_REQUEST);
		} else if (exception instanceof GeoLocationException) {
			log.error("Here exception: {}", exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
		} else if (exception instanceof OrderStatusException) {
			final ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setError(HttpStatus.BAD_REQUEST.value());
			errorMessage.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
			if (!StringUtils.isBlank(exception.getMessage())) {
				errorMessage.addDescription(exception.getMessage());
			}
			return new ResponseEntity<>(errorMessage, getHeaders(), HttpStatus.BAD_REQUEST);
		} else {
			log.error(exception.getLocalizedMessage(), exception);
			return ResponseError.error(HttpStatus.BAD_REQUEST);
		}
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorMessage> handleConstraintViolationException(final ConstraintViolationException exception) {
		final List<String> descriptions = new ArrayList<>();
		final Set<ConstraintViolation<?>> constraints = exception.getConstraintViolations();
		descriptions.addAll(constraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
		log.error("Error: {}", exception.getLocalizedMessage(), exception);
		return ResponseError.error(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), descriptions);
	}

	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<ErrorMessage> handleIllegalArgumentException(final IllegalArgumentException exception) {
		if (!StringUtils.isBlank(exception.getMessage())) {
			log.error("Error: {}", exception.getMessage(), exception);
			return ResponseError.error(HttpStatus.BAD_REQUEST);
		} else {
			log.error("Error", exception);
			return ResponseError.error(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage());
		}
	}

	@ExceptionHandler({EpickurForbiddenException.class})
	public ResponseEntity<ErrorMessage> handleEpickurForbiddenException(final EpickurForbiddenException exception) {
		final Key key = (Key) request.getAttribute("key");
		log.warn("Forbidden : {} {}", exception.getMessage(), key.getId() != null ? " - User Id " + key.getId().toHexString() : "");
		return ResponseError.error(HttpStatus.FORBIDDEN);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers,
																   final HttpStatus status,
																   final WebRequest request) {
		log.warn("Fatal Error: {} {}", ex.getMessage(), ex.getClass(), ex);
		return changeResponseTypeToObject(ResponseError.error(HttpStatus.BAD_REQUEST));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body, final HttpHeaders headers,
															 final HttpStatus status,
															 final WebRequest request) {
		final Key key = (Key) this.request.getAttribute("key");
		log.warn("{} - {} - {} {} {}", ex.getClass().getSimpleName(), ex.getLocalizedMessage(), key.getKey(), key.getUserId(), key.getRole());
		return changeResponseTypeToObject(ResponseError.error(status));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers,
																  final HttpStatus status,
																  final WebRequest request) {
		final BindingResult bidingResult = ex.getBindingResult();
		final List<ObjectError> errors = bidingResult.getAllErrors();
		final List<String> descriptions = errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
		final Key key = (Key) this.request.getAttribute("key");
		log.warn("MethodArgumentNotValidException {} {}", descriptions, key.toString());
		return changeResponseTypeToObject(ResponseError.error(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), descriptions));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
																  final HttpHeaders headers,
																  final HttpStatus status,
																  final WebRequest request) {
		final Key key = (Key) this.request.getAttribute("key");
		log.warn("{} - {} - {} {} {}", ex.getClass().getSimpleName(), ex.getMessage(), key.getKey(), key.getUserId(), key.getRole());
		return changeResponseTypeToObject(ResponseError.error(status, status.getReasonPhrase(), "Required request body is probably missing"));
	}

	private ResponseEntity<Object> changeResponseTypeToObject(final ResponseEntity<ErrorMessage> responseEntity) {
		return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getHeaders(), responseEntity.getStatusCode());
	}

	private HttpHeaders getHeaders() {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
