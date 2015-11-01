package com.epickur.api.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DeletedMessage {
	/** Id */
	private String id;
	/** Deleted */
	private Boolean deleted;
}
