package org.opennaas.bod.actionsets.dummy;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.bod.actionsets.dummy.actions.RequestConnectionAction;
import org.opennaas.bod.actionsets.dummy.actions.ShutDownConnectionAction;
import org.opennaas.core.resources.action.ActionSet;

public class BoDActionSet extends ActionSet {

	public BoDActionSet() {
		super.setActionSetId("bodActionSet");
		this.putAction(ActionConstants.REQUESTCONNECTION, RequestConnectionAction.class);
		this.putAction(ActionConstants.SHUTDOWNCONNECTION, ShutDownConnectionAction.class);
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.REQUESTCONNECTION);
		actionNames.add(ActionConstants.SHUTDOWNCONNECTION);
		return actionNames;
	}

}