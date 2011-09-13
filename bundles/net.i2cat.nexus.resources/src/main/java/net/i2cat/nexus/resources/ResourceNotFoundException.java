package net.i2cat.nexus.resources;

import net.i2cat.nexus.resources.descriptor.Information;

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
