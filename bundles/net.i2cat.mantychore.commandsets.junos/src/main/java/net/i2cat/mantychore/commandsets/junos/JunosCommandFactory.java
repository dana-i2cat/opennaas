package net.i2cat.mantychore.commandsets.junos;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.commandset.ICommand;
import com.iaasframework.capabilities.commandset.ICommandFactory;

public class JunosCommandFactory implements ICommandFactory {

	public ICommand createCommand(String commandId) throws CommandException {
		ICommand command = null;

		if (commandId.equals(RefreshCommand.REFRESH)) {
			return new RefreshCommand();
		}
		// FIXME IS IT NECESSARY THROW AN EXCEPTION IF IT CAN GET A COMMAND??
		return command;

	}

}
