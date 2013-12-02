package org.opennaas.extensions.network.model.virtual;

public class VirtualizationException extends Exception {

	private static final long	serialVersionUID	= -765650491986529933L;

	public VirtualizationException(Exception e) {
		super(e);
	}

	public VirtualizationException() {
		super();
	}

	public VirtualizationException(String msg) {
		super(msg);
	}

	public VirtualizationException(String msg, Exception e) {
		super(msg, e);
	}

}
