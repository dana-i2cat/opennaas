package org.opennaas.extensions.router.junos.commandsets.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class LockNetconfCommand  extends JunosCommand {

	private  String	target				= "candidate";

	public LockNetconfCommand(String target) {
		super(CommandNetconfConstants.LOCK, "");
		this.target = target;

	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newLock(target));
		return query;
	}

}
