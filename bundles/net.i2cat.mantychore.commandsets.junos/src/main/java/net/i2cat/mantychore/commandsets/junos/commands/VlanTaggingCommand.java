package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class VlanTaggingCommand extends JunosCommand {

	public static final String	VLANTAGGING	= "vlantagging";

	//for physical interfaces
	public static final String	TEMPLATE			= "/VM_files/setvlantagging.vm";

	/*
	 * This params have to define in the initialize process. The all necessary information is in 
	 * the "params" variable.
	 */
	private String				target				= "candidate";
	private String				defaultOperation	= null;
	private String				testOption			= null;
	private String				errorOption			= null;

	public VlanTaggingCommand() {
		super(VLANTAGGING, TEMPLATE);
	}
	
	@Override
	public Object sendQuery() {
		// returns the Query with the corresponding command
		return QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML);
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// response is an RPCElement -->Reply
	}


}