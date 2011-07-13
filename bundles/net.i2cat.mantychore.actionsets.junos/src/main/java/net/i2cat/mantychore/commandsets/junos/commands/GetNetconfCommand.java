package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

public class GetNetconfCommand extends JunosCommand {

	private String	source		= "running";
	private String	attrFilter	= null;

	public GetNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.GET, netconfXML);
	}

	@Override
	public Query message() {
		// setQuery(QueryFactory.newGetInterfaceInformation());
		setQuery(QueryFactory.newGetConfig(source, netconfXML, attrFilter));
		return query;
	}

}
