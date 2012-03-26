package org.opennaas.extensions.router.junos.commandsets.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class ReplaceNetconfCommand extends JunosCommand {

	private final String	target				= null;
	private final String	defaultOperation	= "replace";
	private final String	testOption			= null;
	private final String	errorOption			= null;

	public ReplaceNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.EDIT, netconfXML);
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML));
		return query;
	}

}
