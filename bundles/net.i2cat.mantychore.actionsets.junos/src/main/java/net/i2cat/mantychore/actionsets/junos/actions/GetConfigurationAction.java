package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetConfigurationAction extends JunosAction {
	public static final String	GETCONFIG		= "getConfiguration";
	Logger						logger			= LoggerFactory.getLogger(GetConfigurationAction.class);
	private List<String>		commandsList	= null;
	int							index			= 0;

	public GetConfigurationAction() {
		super(GETCONFIG);
		initializeCommandsList();
	}

	protected void initializeCommandsList() {
		commandsList = new ArrayList<String>();
		/* commands */
		commandsList.add(GETCONFIG);
		// state.setSteps(commandsList);
	}

}