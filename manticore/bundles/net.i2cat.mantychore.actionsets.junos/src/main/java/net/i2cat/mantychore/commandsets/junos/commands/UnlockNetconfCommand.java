package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class UnlockNetconfCommand extends JunosCommand {


	private  String	target				= "candidate";

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
