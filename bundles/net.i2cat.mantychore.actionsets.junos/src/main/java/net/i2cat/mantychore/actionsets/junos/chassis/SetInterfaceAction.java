package net.i2cat.mantychore.actionsets.junos.chassis;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.commandsets.junos.JunosCommandFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.CommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetInterfaceAction extends Action {

	public static final String	SETINTERFACE	= "setInterface";
	Logger						logger			= LoggerFactory.getLogger(GetConfigurationAction.class);
	private List<String>		commandsList	= null;
	int							index			= 0;

	public SetInterfaceAction() {
		// super(SETINTERFACE);
		initializeCommandsList();
	}

	protected void initializeCommandsList() {
		commandsList = new ArrayList<String>();
		/* commands */
		commandsList.add(SETINTERFACE);
		// state.setSteps(commandsList);
	}

	public void initializeCommands() {

		JunosCommandFactory jcf = new JunosCommandFactory();
		for (String commandId : commandsList) {
			try {
				jcf.createCommand(commandId);
			} catch (CommandException e) {
				// TODO Auto-generated catch block
				logger.error("");
				e.printStackTrace();
			}
		}
	}

}
