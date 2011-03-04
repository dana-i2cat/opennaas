package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.List;

import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunosAction extends Action {
	Logger					log				= LoggerFactory.getLogger(JunosAction.class);
	protected List<String>	commandsList	= null;

	public JunosAction(String idAction) {
		// super(idAction);
		log.info("New action: " + idAction);
		initializeCommandsList();
	}

	protected void initializeCommandsList() {
	}

	// public void executeAction() /*throws ActionException*/ {
	// log.info("Executing commands of action");
	// String nextCommand = state.getNextStep();
	// if (state.getState() == State.INPROGRESS) {
	// try {
	// sendMessageToCommand(nextCommand);
	// } catch (CapabilityException e) {
	// log.error("ACTIONSET - It ocurred a problem to sending a command", e);
	// throw new ActionException("ACTIONSET - It ocurred a problem to sending a command", e);
	// }
	// }
	// }
}
