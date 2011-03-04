package net.i2cat.mantychore.actionsets.junos.chassis;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.commandsets.junos.JunosCommandFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetInterfaceAction extends Action {

	public static final String	CREATESUBINTERFACE	= "CreateSubinterface";									// command to set an Interface
	Logger						logger				= LoggerFactory.getLogger(GetConfigurationAction.class);

	int							index				= 0;

	public SetInterfaceAction() {
		// super(SETINTERFACE);
		// log.info("New action: " + idAction);
		initialize();

	}

	protected void initialize() {
		commands.add(getCommand(CREATESUBINTERFACE));
	}

	protected Command getCommand(String commandID) {
		try {
			JunosCommandFactory commandFactory = new JunosCommandFactory();
			return commandFactory.createCommand(commandID);
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
