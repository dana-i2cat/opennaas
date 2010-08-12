package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;

import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.IAction;
import com.iaasframework.capabilities.actionset.IActionFactory;

public class JunosActionFactory implements IActionFactory {

	public IAction createAction(String actionId) throws ActionException {
		if (actionId.equals(GetConfigurationAction.GET_CONFIGURATION)) {
			return new GetConfigurationAction();
		} else {
			throw new ActionException("Action " + actionId + " not found");
		}
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(GetConfigurationAction.GET_CONFIGURATION);
		return actionNames;
	}

	public IAction getCommitAction() {
		return null;
	}

	public IAction getRollbackAction() {
		return null;
	}

}