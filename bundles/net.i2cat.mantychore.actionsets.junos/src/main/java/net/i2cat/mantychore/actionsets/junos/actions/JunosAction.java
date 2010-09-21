package net.i2cat.mantychore.actionsets.junos.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.actionset.AbstractActionWithCommandSet;
import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.ActionState.State;
import com.iaasframework.resources.core.capability.CapabilityException;

public abstract class JunosAction extends AbstractActionWithCommandSet {
	Logger	log	= LoggerFactory.getLogger(JunosAction.class);

	public JunosAction(String idAction) {
		super(idAction);
		log.info("New action: " + idAction);
		initializeCommandsList();
	}

	protected abstract void initializeCommandsList();

	@Override
	public void executeAction() throws ActionException {
		log.info("Executing commands of action");
		String nextCommand = state.getNextStep();
		if (state.getState() == State.INPROGRESS) {
			try {
				sendMessageToCommand(nextCommand);
			} catch (CapabilityException e) {
				log.error("ACTIONSET - It ocurred a problem to sending a command", e);
				throw new ActionException("ACTIONSET - It ocurred a problem to sending a command", e);
			}
		}
	}
}
