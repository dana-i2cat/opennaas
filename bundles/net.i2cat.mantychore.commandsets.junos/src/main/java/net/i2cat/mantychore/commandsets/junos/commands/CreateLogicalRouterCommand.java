package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class CreateLogicalRouterCommand extends JunosCommand {
	public static final String CREATELOGICALROUTER = "createLogicalRouter";

	public static final String TEMPLATE = "/VM_files/createLogicalRouter.vm";

	private String target = null;
	private String defaultOperation = null;
	private String testOption = null;
	private String errorOption = null;

	protected CreateLogicalRouterCommand(String target) {
		super(CREATELOGICALROUTER, TEMPLATE);
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
