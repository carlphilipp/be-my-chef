package com.epickur.api.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public final class ErrorMessage {
	/**
	 * Error
	 */
	private Integer error;
	/**
	 * Message
	 */
	private String message;
	/**
	 * Description
	 */
	private List<String> descriptions;

	public ErrorMessage() {
	}

	public void addDescription(final String description) {
		if (this.descriptions == null) {
			this.descriptions = new ArrayList<>();
		}
		descriptions.add(description);
	}
}
