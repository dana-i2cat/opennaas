package net.i2cat.mantychore.commandsets.junos;

import net.i2cat.mantychore.commandsets.junos.commands.GetConfigurationCommand;
import net.i2cat.mantychore.commandsets.junos.commands.KeepAliveCommand;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.commandset.ICommand;
import com.iaasframework.capabilities.commandset.ICommandFactory;

public class JunosCommandFactory implements ICommandFactory {

	public ICommand createCommand(String commandId) throws CommandException {

		/* keep alive */
		if (commandId.equals(KeepAliveCommand.KEEPALIVE)) {
			return new KeepAliveCommand();
		} else if (commandId.equals(GetConfigurationCommand.GETCONFIG)) {
			return new GetConfigurationCommand();
		} else {
			throw new CommandException("Command " + commandId + " not found");
		}

	}

}
