package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.List;

import net.i2cat.mantychore.commandsets.junos.JunosCommandFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveAction extends Action {

	public static final String	KEEPALIVE		= "keepAlive";

	Logger						logger			= LoggerFactory.getLogger(KeepAliveAction.class);
	private List<String>		commandsList	= null;
	int							index			= 0;

	public KeepAliveAction() {
		// super(KEEPALIVE);
		initialize();
	}

	protected void initialize() {
		commands.add(getCommand(KEEPALIVE));
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