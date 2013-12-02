package org.opennaas.core.resources;

/**
 * An Exception to tell a lookup in a resource model failed.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ModelElementNotFoundException extends Exception {

	/**
	 * Auto-generated serial version UID for serialization.
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
