package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.CommitAction;
import net.i2cat.mantychore.actionsets.junos.actions.KeepAliveAction;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.IActionSetFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicActionSetFactory implements IActionSetFactory {

	// public static final String COMMIT = "commit";
	// public static final String ROLLBACK = "rollback";
	// public static final String KEEPALIVE = "keepAlive";

	Logger	logger	= LoggerFactory.getLogger(BasicActionSetFactory.class);

	public Action createAction(String actionId) /* throws ActionException */{
		logger.info("ACTIONSET - Action id to send: " + actionId);
		// TODO add exceptions, not return null
		if (actionId.equals(ActionConstants.COMMIT)) {
			return new CommitAction();
		} else if (actionId.equals(ActionConstants.KEEPALIVE)) {
			return new KeepAliveAction();
		}
		// TODO implement RollbackAction class
		else if (actionId.equals(ActionConstants.ROLLBACK)) {
			return null;
		}
		// else {
		// throw new ActionException("Action " + actionId + " not found");
		// }
		return null;
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.COMMIT);
		actionNames.add(ActionConstants.KEEPALIVE);
		// actionNames.add(ActionConstants.ROLLBACK);
		return actionNames;
	}

}