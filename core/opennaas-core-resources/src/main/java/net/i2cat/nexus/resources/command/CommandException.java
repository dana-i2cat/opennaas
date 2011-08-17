package net.i2cat.nexus.resources.command;

import net.i2cat.nexus.resources.action.ActionException;

public class CommandException extends ActionException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4678410903548522139L;

	public CommandException(Exception e) {
		super(e);
	}

	public CommandException() {
		super();
	}

	public CommandException(String msg) {
		super(msg);
	}

	public CommandException(String msg, Exception e) {
		super(msg, e);
	}

}
