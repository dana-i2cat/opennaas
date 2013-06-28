package org.opennaas.extensions.gim.controller;

/**
 * An exception indicating a required element is not found in resource model
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class ModelElementNotFoundException extends Exception {

	/**
	 * Auto-generated serial version UID for serialization
	 */
	private static final long	serialVersionUID	= -444323970362594763L;

	public ModelElementNotFoundException() {
		super();
	}

	public ModelElementNotFoundException(String msg) {
		super(msg);
	}

	public ModelElementNotFoundException(Exception e) {
		super(e);
	}

	public ModelElementNotFoundException(String msg, Exception e) {
		super(msg, e);
	}

}
