package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveCommand extends JunosCommand {

	public static final String KEEPALIVE = "keepAlive";

	public static final String TEMPLATE = "/VM_files/keepalive.vm";

	/** The logger **/
	Logger log = LoggerFactory.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KEEPALIVE);
	}

	@Override
	public Object sendQuery() {
		// TODO Auto-generated method stub
		return QueryFactory.newKeepAlive();
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub

	}

}
