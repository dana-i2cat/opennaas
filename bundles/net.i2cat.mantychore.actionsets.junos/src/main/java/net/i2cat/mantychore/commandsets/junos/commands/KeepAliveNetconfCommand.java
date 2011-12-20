package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class KeepAliveNetconfCommand extends JunosCommand {

	public KeepAliveNetconfCommand() {
		super(CommandNetconfConstants.KEEPALIVE, null);
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newKeepAlive());
		return query;
	}

}
