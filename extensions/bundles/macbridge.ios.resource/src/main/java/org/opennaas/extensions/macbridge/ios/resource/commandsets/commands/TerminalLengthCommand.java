package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class TerminalLengthCommand extends IOSCommand {

	int length = 0;
	
	public TerminalLengthCommand() {
	}
	
	public TerminalLengthCommand(int length) {
		this.length = length;
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCommand() {
		return "terminal length " + length;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
