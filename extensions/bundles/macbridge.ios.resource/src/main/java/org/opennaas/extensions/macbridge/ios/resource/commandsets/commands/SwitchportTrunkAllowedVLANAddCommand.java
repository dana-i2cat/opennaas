package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class SwitchportTrunkAllowedVLANAddCommand extends IOSCommand {

	private int vlanID = 0;
	
	public SwitchportTrunkAllowedVLANAddCommand(int vlanID) {
		this.vlanID = vlanID;
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCommand() {
		String command = null;
		command = "switchport trunk allowed vlan add " + vlanID;
		return command;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
