package net.i2cat.nexus.resources;


public class CorruptStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CorruptStateException() {
		super();
	}

	public CorruptStateException(String msg) {
		super(msg);
	}

	public CorruptStateException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
