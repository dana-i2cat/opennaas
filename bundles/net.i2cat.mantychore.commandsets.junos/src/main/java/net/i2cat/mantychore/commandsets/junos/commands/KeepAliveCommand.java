package net.i2cat.mantychore.commandsets.junos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveCommand extends JunosCommand {

	public static final String	KEEPALIVE	= "keepAlive";

	public static final String	TEMPLATE	= "/VM_files/keepalive.vm";

	/** The logger **/
	Logger						log			= LoggerFactory
																.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KEEPALIVE, TEMPLATE);
	}

	@Override
	public Object message() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub

	}

}
