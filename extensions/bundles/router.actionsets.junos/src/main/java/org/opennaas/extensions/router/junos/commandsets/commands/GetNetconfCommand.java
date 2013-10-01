package org.opennaas.extensions.router.junos.commandsets.commands;

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants.TargetConfiguration;

public class GetNetconfCommand extends JunosCommand {

	private static final TargetConfiguration	DEFAULT_SOURCE	= TargetConfiguration.RUNNING;
	private TargetConfiguration					source			= DEFAULT_SOURCE;
	private String								attrFilter		= null;

	public GetNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.GET, netconfXML);
	}

	public GetNetconfCommand(String netconfXML, TargetConfiguration source) {
		super(CommandNetconfConstants.GET, netconfXML);
		this.source = source;
	}

	@Override
	public Query message() {
		// setQuery(QueryFactory.newGetInterfaceInformation());
		setQuery(QueryFactory.newGetConfig(source.toString(), netconfXML, attrFilter));
		return query;
	}

}
