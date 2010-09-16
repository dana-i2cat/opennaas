package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.ActionJunosConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.actionset.AbstractActionWithCommandSet;
import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.ActionState.State;
import com.iaasframework.resources.core.capability.CapabilityException;

public class GetConfigurationAction extends AbstractActionWithCommandSet {

	Logger					logger			= LoggerFactory.getLogger(GetConfigurationAction.class);
	private List<String>	commandsList	= null;
	int						index			= 0;

	public GetConfigurationAction() {
		super(ActionJunosConstants.GET_CONFIGURATION);
		initializeCommandsList();
	}

	private void initializeCommandsList() {
		commandsList = new ArrayList<String>();
		commandsList.add("getConfiguration");
		state.setSteps(commandsList);

	}

	@Override
	public void executeAction() throws ActionException {

		String nextCommand = state.getNextStep();
		if (state.getState() == State.INPROGRESS) {
			try {
				sendMessageToCommand(nextCommand);
			} catch (CapabilityException e) {
				logger.error("ACTIONSET - It ocurred a problem to sending a command", e);
				throw new ActionException("ACTIONSET - It ocurred a problem to sending a command", e);
			}
		}
	}

}