package net.i2cat.mantychore.actionsets.junos.chassis;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetInterfaceAction extends Action {

	public static final String	SETINTERFACE	= "CreateSubinterface";
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
}
