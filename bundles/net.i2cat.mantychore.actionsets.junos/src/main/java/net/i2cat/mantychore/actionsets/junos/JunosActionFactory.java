package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.KeepAliveAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.actionset.ActionException;
import com.iaasframework.capabilities.actionset.IAction;
import com.iaasframework.capabilities.actionset.IActionFactory;

public class JunosActionFactory implements IActionFactory {
	
	public static final String	GETCONFIG	= "getConfiguration";
	public static final String	KEEPALIVE	= "keepAlive";
	
	Logger	logger	= LoggerFactory.getLogger(JunosActionFactory.class);

	public IAction createAction(String actionId) throws ActionException {
		logger.info("ACTIONSET - Action id to send: " + actionId);

		if (actionId.equals(GetConfigurationAction.GETCONFIG)) {
			return new GetConfigurationAction();
		} else

		if (actionId.equals(KeepAliveAction.KEEPALIVE)) {
			return new KeepAliveAction();
		}

		else {
			throw new ActionException("Action " + actionId + " not found");
		}
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(GetConfigurationAction.GETCONFIG);
		actionNames.add(KeepAliveAction.KEEPALIVE);
		return actionNames;
	}

	public IAction getCommitAction() {
		return null;
	}

	public IAction getRollbackAction() {
		return null;
	}

}