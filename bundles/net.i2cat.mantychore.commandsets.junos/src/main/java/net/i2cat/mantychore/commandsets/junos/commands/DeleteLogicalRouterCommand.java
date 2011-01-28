package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class DeleteLogicalRouterCommand extends JunosCommand {
	public static final String	DELETELOGICALROUTER	= "deleteLogicalRouter";

	public static final String	TEMPLATE			= "/VM_files/createLogicalRouter.vm";

	private String				target				= null;
	private String				defaultOperation	= null;
	private String				testOption			= null;
	private String				errorOption			= null;

	protected DeleteLogicalRouterCommand() {
		super(DELETELOGICALROUTER, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	public void initializeCommand(String target) {
		this.target = target;
		this.defaultOperation = "none";
	}

	@Override
	public Object message() {
		// returns the Query with the corresponding command
		return QueryFactory.newEditConfig(target, defaultOperation, testOption, errorOption, netconfXML);

	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub
		// response is an RPCElement -->Reply
	}

}
