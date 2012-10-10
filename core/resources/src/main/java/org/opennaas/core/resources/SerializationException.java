package org.opennaas.core.resources;

public class SerializationException extends Exception {

	private static final long	serialVersionUID	= 7255301113436167688L;

	public SerializationException() {
		super();
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

}
