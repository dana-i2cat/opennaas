package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class EditNetconfCommand extends JunosCommand {

	private final String	target				= "candidate";
	private final String	defaultOperation	= null;
	private final String	testOption			= null;
	private final String	errorOption			= null;

	public EditNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.EDIT, netconfXML);
	}

	public Query message() {
		setQuery(QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML));
		return query;
	}

}
