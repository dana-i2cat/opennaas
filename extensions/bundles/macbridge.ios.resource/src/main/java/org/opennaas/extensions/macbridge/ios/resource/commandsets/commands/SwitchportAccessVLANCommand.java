package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class SwitchportAccessVLANCommand extends IOSCommand {

	private int vlanID = 0;
	private boolean flag = false;
	
	public SwitchportAccessVLANCommand(int vlanID, boolean flag) {
		this.vlanID = vlanID;
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

		command = command + "switchport access vlan " + vlanID;
		return command;
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
