package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class ShutdownCommand extends IOSCommand {

	private boolean flag = false;
	
	public ShutdownCommand(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCommand() {
		String command = null;
		
		if (flag){
			command = "no ";
		}else{
			command = "";
		}

		command = command + "shutdown";
		
		return command;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
