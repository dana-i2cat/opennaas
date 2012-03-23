package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class EditNetconfCommand extends JunosCommand {

	private String	target				= "candidate";
	private String	defaultOperation	= null;
	private String	testOption			= null;
	private String	errorOption			= null;

	public EditNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.EDIT, netconfXML);
	}

	public EditNetconfCommand(String netconfXML, String defaultOperation) {
		super(CommandNetconfConstants.EDIT, netconfXML);
		this.defaultOperation = defaultOperation;
	}

	@Override
	public Query message() {
		setQuery(QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML));
		return query;
	}

}
