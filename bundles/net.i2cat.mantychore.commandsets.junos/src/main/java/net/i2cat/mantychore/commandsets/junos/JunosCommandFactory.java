package net.i2cat.mantychore.commandsets.junos;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;
import net.i2cat.mantychore.commons.Command;

import com.iaasframework.capabilities.commandset.CommandException;

public class JunosCommandFactory {

	public Command createCommand(String commandId) throws CommandException {
		Command command = null;

		if (commandId.equals(RefreshCommand.REFRESH)) {
			return new RefreshCommand();
		}
		// If it doesn't find a command, it returns null
		return command;

	}

}
