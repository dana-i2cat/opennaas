package net.i2cat.nexus.resources;

public class ActivatorException extends Exception {
	public ActivatorException(Exception e) {
		super(e);
	}

	public ActivatorException(String msg) {
		super(msg);
	}

}
