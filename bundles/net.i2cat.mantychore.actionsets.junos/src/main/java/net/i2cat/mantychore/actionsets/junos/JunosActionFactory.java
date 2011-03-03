package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.KeepAliveAction;
import net.i2cat.mantychore.actionsets.junos.chassis.SetInterfaceAction;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunosActionFactory /* implements IActionFactory */{

	public static final String	GETCONFIG		= "getConfiguration";
	public static final String	KEEPALIVE		= "keepAlive";
	public static final String	setinterface	= "setInterface";

	Logger						logger			= LoggerFactory.getLogger(JunosActionFactory.class);

	public Action createAction(String actionId) /* throws ActionException */{
		logger.info("ACTIONSET - Action id to send: " + actionId);
		// TODO add exceptions, not return null
		if (actionId.equals(GetConfigurationAction.GETCONFIG)) {
			return new GetConfigurationAction();
		} else

		if (actionId.equals(KeepAliveAction.KEEPALIVE)) {
			return new KeepAliveAction();
		}
		if (actionId.equals(SetInterfaceAction.SETINTERFACE)) {
			return new SetInterfaceAction();
		} else {
			return null;
		}
		// else {
		// throw new ActionException("Action " + actionId + " not found");
		// }
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(GetConfigurationAction.GETCONFIG);
		actionNames.add(KeepAliveAction.KEEPALIVE);
		actionNames.add(SetInterfaceAction.SETINTERFACE);
		return actionNames;
	}

	public Action getCommitAction() {
		return null;
	}

	public Action getRollbackAction() {
		return null;
	}

}