package com.epickur.api.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public final class ErrorMessage {
	/** Error */
	private Integer error;
	/** Message */
	private String message;
	/** Description */
	private String description;
}
