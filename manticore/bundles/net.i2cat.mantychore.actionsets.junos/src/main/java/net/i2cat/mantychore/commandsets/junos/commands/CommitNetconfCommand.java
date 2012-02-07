package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class CommitNetconfCommand extends JunosCommand {

	public CommitNetconfCommand() {
		super(CommandNetconfConstants.COMMIT, null);
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newCommit());
		return query;
	}

}
