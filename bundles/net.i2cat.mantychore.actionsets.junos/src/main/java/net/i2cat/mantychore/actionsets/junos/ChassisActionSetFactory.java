package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.KeepAliveAction;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionException;
import net.i2cat.mantychore.commons.IActionSetFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisActionSetFactory implements IActionSetFactory {

	Logger	logger	= LoggerFactory.getLogger(ChassisActionSetFactory.class);

	public Action createAction(String actionId) throws ActionException {
		logger.info("ACTIONSET - Action id to send: " + actionId);

		if (actionId.equals(ActionConstants.GETCONFIG)) {
			return new GetConfigurationAction();
		} else if (actionId.equals(ActionConstants.KEEPALIVE)) {
			return new KeepAliveAction();
		} else {
			throw new ActionException("Action " + actionId + " not found");
		}
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.KEEPALIVE);
		actionNames.add(ActionConstants.SETINTERFACE);
		return actionNames;
	}

}