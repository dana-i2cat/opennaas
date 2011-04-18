package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class GetVlanTaggingCommand extends JunosCommand {

	public static final String	VLANTAGGING	= "getvlantagging";

	// for physical interfaces
	public static final String	TEMPLATE	= "/VM_files/getvlantagging.vm";

	/*
	 * This params have to define in the initialize process. The all necessary information is in the "params" variable.
	 */
	private String				target		= "candidate";

	public GetVlanTaggingCommand() {
		super(VLANTAGGING, TEMPLATE);
	}

	@Override
	public Object sendQuery() {
		// returns the Query with the corresponding command

		return QueryFactory.newGetConfig(target, netconfXML, null);
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// response is an RPCElement -->Reply
	}

}