package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class CreateSubInterfaceCommand extends JunosCommand {

	public static final String	CREATESUBINTERFACE	= "CreateSubinterface";

	public static final String	TEMPLATE			= "/VM_files/replacesubinterface.vm";
	// TODO must target be defined here??? or it is a configuration parameter.
	private String				target				= "candidate";
	private String				defaultOperation	= null;
	private String				testOption			= null;
	private String				errorOption			= null;

	public CreateSubInterfaceCommand() {
		super(CREATESUBINTERFACE, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	// TODO how it is defined the target configuration to be configured

	public void initializeCommand(String target) {
		this.target = target;
	}

	@Override
	public Object sendQuery() {
		// returns the Query with the corresponding command
		return QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML);
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub
		// response is an RPCElement -->Reply
	}

}
