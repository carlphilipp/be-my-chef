package com.epickur.api.entity.message;

import java.util.ArrayList;
import java.util.List;

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
	private List<String> descriptions;

	public ErrorMessage() {
		this.descriptions = new ArrayList<>();
	}

	public void addDescription(final String description) {
		descriptions.add(description);
	}
}
