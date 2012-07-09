package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class SwitchportTrunkAllowedVLANRemoveCommand extends IOSCommand {

	private int vlanID = 0;
	
	public SwitchportTrunkAllowedVLANRemoveCommand(int vlanID) {
		this.vlanID = vlanID;
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCommand() {
		String command = null;
		command = "switchport trunk allowed vlan remove " + vlanID;
		return command;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
