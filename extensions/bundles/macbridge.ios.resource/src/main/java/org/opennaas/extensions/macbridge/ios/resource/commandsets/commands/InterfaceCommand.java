package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class InterfaceCommand extends IOSCommand {
	
	private final String GIGABITETHERNET = "gigabitEthernet";
	private final String FASTETHERNET = "fastEthernet";
	
	/** Constant value to specify Gigabit Ethernet Card */
	private final String GI = "Gi";

	/** Constant value to specify Fast Ethernet Card */
	private final String FA = "Fa";
	
	private String port = null;
	private String type = GIGABITETHERNET;
	
	public InterfaceCommand(String interfaceName) {
		if (interfaceName.startsWith(GI)){
			this.type = GIGABITETHERNET;
			this.port = interfaceName.split(GI)[1];
		}else if(interfaceName.startsWith(FA)){
			this.type = FASTETHERNET;
			this.port = interfaceName.split(FA)[1];
		}
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCommand() {
		return "interface " + type + " " + port;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
