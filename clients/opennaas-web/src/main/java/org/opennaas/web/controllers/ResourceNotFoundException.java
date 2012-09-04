package org.opennaas.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jordi
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Long				resourceId;

	/**
	 * @param resourceId
	 */
	public ResourceNotFoundException(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the resourceId
	 */
	public Long getResourceId() {
		return resourceId;
	}

}