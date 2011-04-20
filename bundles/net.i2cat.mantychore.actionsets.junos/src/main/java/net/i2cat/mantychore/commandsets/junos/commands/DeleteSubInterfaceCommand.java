package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

public class DeleteSubInterfaceCommand extends JunosCommand {

	public static final String DELETESUBINTERFACE = "deleteSubinterface";

	public static final String TEMPLATE = "/VM_files/deletesubinterface.vm";

	private String target = null;
	private String defaultOperation = null;
	private String testOption = null;
	private String errorOption = null;

	public DeleteSubInterfaceCommand() {
		super(DELETESUBINTERFACE, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	public void initializeCommand(String target) {
		this.target = target;
		this.defaultOperation = "none";
	}

	@Override
	public Object sendQuery() {
		// TODO Auto-generated method stub
		return QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML);
	}

	@Override
	public void parseResponse(Object response, Object model) {

	}


}
