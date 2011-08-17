package org.opennaas.core.resources;

public class ActivatorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivatorException(Exception e) {
		super(e);
	}

	public ActivatorException(String msg) {
		super(msg);
	}

}