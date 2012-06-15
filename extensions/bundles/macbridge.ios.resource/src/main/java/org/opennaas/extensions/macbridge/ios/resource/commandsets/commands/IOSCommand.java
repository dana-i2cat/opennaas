package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.Command;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public abstract class IOSCommand extends Command{
	public abstract String getCommand();
	public abstract void updateModel(CLIResponseMessage responseMessage, MACBridge model);
}
