package net.i2cat.mantychore.commandsets;

import net.i2cat.mantychore.commandsets.commands.KeepAliveCommand;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.commandset.ICommand;
import com.iaasframework.capabilities.commandset.ICommandFactory;

public class JunosCommandFactory implements ICommandFactory {

	public ICommand createCommand(String commandId) throws CommandException {

		if (commandId.equals(KeepAliveCommand.KEEP_ALIVE)) {
			return new KeepAliveCommand();
		} else {
			throw new CommandException("Command " + commandId + " not found");
		}

	}

}
