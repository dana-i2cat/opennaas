package org.opennaas.extensions.router.junos.commandsets.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class DiscardNetconfCommand extends JunosCommand {

	public DiscardNetconfCommand() {
		super(CommandNetconfConstants.DISCARD, null);
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newDiscardChanges());
		return query;
	}

}
