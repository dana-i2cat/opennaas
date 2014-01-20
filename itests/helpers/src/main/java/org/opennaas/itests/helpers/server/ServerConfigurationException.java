package org.opennaas.itests.helpers.server;

public class ServerConfigurationException extends Exception {

	private static final long	serialVersionUID	= -2503006471288042226L;

	public ServerConfigurationException(String msg) {
		super(msg);
	}

	public ServerConfigurationException(String msg, Throwable e) {
		super(msg, e);
	}

}
