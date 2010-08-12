package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.actionset.AbstractActionWithCommandSet;
import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.ActionState.State;
import com.iaasframework.resources.core.capability.CapabilityException;

public class GetConfigurationAction extends AbstractActionWithCommandSet {
	public static final String	GET_CONFIGURATION	= "getConfiguration";
	Logger						logger				= LoggerFactory.getLogger(GetConfigurationAction.class);
	private List<String>		commandsList		= null;
	int							index				= 0;

	public GetConfigurationAction() {
		super(GET_CONFIGURATION);
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
				e.printStackTrace();
				throw new ActionException("", e);
			}
		}
	}

}