package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.constants.ActionJunosConstants;
import net.i2cat.mantychore.constants.CommandJunosConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveAction extends JunosAction {

	Logger					logger			= LoggerFactory.getLogger(KeepAliveAction.class);
	private List<String>	commandsList	= null;
	int						index			= 0;

	public KeepAliveAction() {
		super(ActionJunosConstants.KEEPALIVE);
		initializeCommandsList();
	}

	protected void initializeCommandsList() {
		commandsList = new ArrayList<String>();
		/* commands */
		commandsList.add(CommandJunosConstants.KEEPALIVE);
		state.setSteps(commandsList);

	}

}