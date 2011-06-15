package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class DiscardNetconfCommand extends JunosCommand {

	public DiscardNetconfCommand() {
		super(CommandNetconfConstants.DISCARD, null);
	}

	public Query message() {
		setQuery(QueryFactory.newDiscardChanges());
		return query;
	}

}
