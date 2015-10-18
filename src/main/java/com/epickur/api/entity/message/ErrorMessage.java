package com.epickur.api.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public final class ErrorMessage {
	private Integer error;
	private String message;
	private String description;
}
