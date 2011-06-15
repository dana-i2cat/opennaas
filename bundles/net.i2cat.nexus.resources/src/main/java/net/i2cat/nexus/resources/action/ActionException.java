package net.i2cat.nexus.resources.action;

import net.i2cat.nexus.resources.capability.CapabilityException;

public class ActionException extends CapabilityException {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6179279998351947424L;

	public ActionException(Exception e) {
		super(e);
	}

	public ActionException() {
		super();
	}

	public ActionException(String msg) {
		super(msg);
	}

	public ActionException(String msg, Exception e) {
		super(msg, e);
	}

}
