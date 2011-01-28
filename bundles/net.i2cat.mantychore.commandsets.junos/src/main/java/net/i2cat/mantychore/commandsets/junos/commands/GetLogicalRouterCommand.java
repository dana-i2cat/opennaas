package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class GetLogicalRouterCommand extends JunosCommand {

	public static final String	GETLOGICALROUTER	= "GetLogicalRouter";

	public static final String	TEMPLATE			= "/VM_files/getLogicalRouters.vm";

	private String				source				= null;
	private String				filter				= null;
	private String				attrFilter			= null;

	public GetLogicalRouterCommand() {
		super(GETLOGICALROUTER, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	public void initializeCommand(String source) {
		this.source = source;
	}

	@Override
	public Object message() {
		// TODO Auto-generated method stub
		return QueryFactory.newGetConfig(source, netconfXML, attrFilter);

	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub

	}

}
