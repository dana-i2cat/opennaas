package net.i2cat.mantychore.commandsets.junos.commands;

import java.util.List;

import net.i2cat.mantychore.model.EthernetPort;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class CreateSubInterfaceCommand extends JunosCommand {

	public static final String	CREATESUBINTERFACE	= "CreateSubinterface";

	public static final String	TEMPLATE			= "/configuresubinterface.vm";

	public CreateSubInterfaceCommand() {
		super(CREATESUBINTERFACE, TEMPLATE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createCommand(Object params) {
		List<EthernetPort> listInterfaces = (List<EthernetPort>) params;
		// TODO Auto-generated method stub

	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {
		// TODO Auto-generated method stub

	}

}
