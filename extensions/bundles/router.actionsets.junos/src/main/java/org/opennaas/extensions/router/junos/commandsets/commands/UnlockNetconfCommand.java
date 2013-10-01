package org.opennaas.extensions.router.junos.commandsets.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class UnlockNetconfCommand extends JunosCommand {

	private String	target	= "candidate";

	public UnlockNetconfCommand(String target) {
		super(CommandNetconfConstants.UNLOCK, "");
		this.target = target;
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newUnlock(target));
		return query;
	}

}
