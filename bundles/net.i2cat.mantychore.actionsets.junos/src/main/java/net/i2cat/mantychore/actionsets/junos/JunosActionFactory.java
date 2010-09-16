package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.ActionJunosConstants;
import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.IAction;
import com.iaasframework.capabilities.actionset.IActionFactory;

public class JunosActionFactory implements IActionFactory {
	Logger	logger	= LoggerFactory.getLogger(JunosActionFactory.class);

	public IAction createAction(String actionId) throws ActionException {
		logger.info("ACTIONSET - Action id to send: " + actionId);

		if (actionId.equals(ActionJunosConstants.GET_CONFIGURATION)) {
			return new GetConfigurationAction();
		} else {
			throw new ActionException("Action " + actionId + " not found");
		}
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionJunosConstants.GET_CONFIGURATION);
		return actionNames;
	}

	public IAction getCommitAction() {
		return null;
	}

	public IAction getRollbackAction() {
		return null;
	}

}