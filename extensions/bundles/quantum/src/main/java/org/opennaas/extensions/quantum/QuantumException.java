package org.opennaas.extensions.quantum;

public class QuantumException extends Exception {

	private static final long	serialVersionUID	= 7019386975673834177L;

	public QuantumException(Exception e) {
		super(e);
	}

	public QuantumException() {
		super();
	}

	public QuantumException(String msg) {
		super(msg);
	}

	public QuantumException(String msg, Exception e) {
		super(msg, e);
	}

}
