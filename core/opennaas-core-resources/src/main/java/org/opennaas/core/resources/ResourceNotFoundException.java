package org.opennaas.core.resources;

import org.opennaas.core.resources.descriptor.Information;

public class ResourceNotFoundException extends ResourceException {

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(Exception e) {
		super(e);
	}

	public ResourceNotFoundException(String msg, Exception e) {
		super(msg, e);
	}

	public ResourceNotFoundException(String msg, Information information) {
		super(msg, information);
	}

}
