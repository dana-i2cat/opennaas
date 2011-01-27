package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class CreateSubInterfaceCommand extends JunosCommand {

	public static final String	CREATESUBINTERFACE	= "CreateSubinterface";

	public static final String	TEMPLATE			= "/VM_files/configuresubinterface.vm";

	private String				target				= "candidate";
	private String				defaultOperation	= null;
	private String				testOption			= null;
	private String				errorOption			= null;

	public CreateSubInterfaceCommand() {
		super(CREATESUBINTERFACE, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	public void initializeCommand(String target, String defaultOperation, String testOption, String errorOption) {
		this.target = (target == null) ? "candidate" : target;
		this.defaultOperation = defaultOperation;
		this.testOption = testOption;
		this.errorOption = errorOption;
	}

	@Override
	public void createCommand() {
		command = QueryFactory.newEditConfig(target, defaultOperation, testOption, errorOption, netconfXML);
	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {
		// TODO Auto-generated method stub
	}

}
