package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveAction extends JunosAction {

	public static final String	KEEPALIVE		= "keepAlive";

	Logger						logger			= LoggerFactory.getLogger(KeepAliveAction.class);
	private List<String>		commandsList	= null;
	int							index			= 0;

	public KeepAliveAction() {
		super(KEEPALIVE);
		initializeCommandsList();
	}

	protected void initializeCommandsList() {
		commandsList = new ArrayList<String>();
		/* commands */
		commandsList.add(KEEPALIVE);
		state.setSteps(commandsList);

	}

}