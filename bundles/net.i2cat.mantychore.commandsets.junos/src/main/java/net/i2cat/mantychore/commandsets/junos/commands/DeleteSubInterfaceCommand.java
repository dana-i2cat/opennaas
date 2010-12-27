package net.i2cat.mantychore.commandsets.junos.commands;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class DeleteSubInterfaceCommand extends JunosCommand {

	public static final String	DELETESUBINTERFACE	= "deleteSubinterface";

	public static final String	TEMPLATE			= "/deletesubinterface.vm";

	public DeleteSubInterfaceCommand() {
		super(DELETESUBINTERFACE, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createCommand(Object params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {
		// TODO Auto-generated method stub

	}

}
