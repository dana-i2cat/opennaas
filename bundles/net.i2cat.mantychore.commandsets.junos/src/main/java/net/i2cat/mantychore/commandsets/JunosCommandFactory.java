package net.i2cat.mantychore.commandsets;

import net.i2cat.mantychore.commandsets.commands.GetConfigurationCommand;
import net.i2cat.mantychore.commandsets.commands.KeepAliveCommand;
import net.i2cat.mantychore.constants.CommandJunosConstants;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.commandset.ICommand;
import com.iaasframework.capabilities.commandset.ICommandFactory;

public class JunosCommandFactory implements ICommandFactory {

	public ICommand createCommand(String commandId) throws CommandException {

		/* keep alive */
		if (commandId.equals(CommandJunosConstants.KEEPALIVE)) {
			return new KeepAliveCommand();
		} else if (commandId.equals(CommandJunosConstants.GETCONFIG)) {
			return new GetConfigurationCommand();
		} else {
			throw new CommandException("Command " + commandId + " not found");
		}

	}

}
