package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.List;

import net.i2cat.mantychore.commandsets.junos.JunosCommandFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetConfigurationAction extends Action {
	public static final String	GETCONFIG		= "getConfiguration";
	Logger						logger			= LoggerFactory.getLogger(GetConfigurationAction.class);
	int							index			= 0;

	public GetConfigurationAction() {
		// super(GETCONFIG);
		initialize();
	}

	protected void initialize() {
		//FIXME IT IS NECESSARY TO PUT THE REFRESH NAME IN ANOTHER CLASS..
		commands.add(getCommand("refresh"));
	}

	protected Command getCommand(String commandID) {
		try {
			JunosCommandFactory commandFactory = new JunosCommandFactory();
			return commandFactory.createCommand(commandID);
		} catch (CommandException e) {
			// FIXME IT CAN NOT TRHOW PRINTSTACKTRACE
			e.printStackTrace();
		}
		//FIXME IT CAN NOT RETURN NULL PARAMS
		return null;
	}
}