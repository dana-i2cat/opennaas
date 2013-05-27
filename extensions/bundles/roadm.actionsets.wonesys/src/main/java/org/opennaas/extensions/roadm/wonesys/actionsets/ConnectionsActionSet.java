package org.opennaas.extensions.roadm.wonesys.actionsets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.roadm.wonesys.actionsets.actions.MakeConnectionAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RefreshModelConnectionsAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RemoveConnectionAction;
import org.opennaas.core.resources.action.ActionSet;

public class ConnectionsActionSet extends ActionSet {
	public ConnectionsActionSet() {
		super.setActionSetId("connectionsActionSet");
		this.putAction(ActionConstants.MAKECONNECTION, MakeConnectionAction.class);
		this.putAction(ActionConstants.REMOVECONNECTION, RemoveConnectionAction.class);
		this.putAction(ActionConstants.REFRESHCONNECTIONS, RefreshModelConnectionsAction.class);

		this.refreshActions.add(ActionConstants.REFRESHCONNECTIONS);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.MAKECONNECTION);
		actionNames.add(ActionConstants.REMOVECONNECTION);
		actionNames.add(ActionConstants.REFRESHCONNECTIONS);
		return actionNames;
	}


}
