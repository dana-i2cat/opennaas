package net.i2cat.luminis.actionsets.wonesys;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.luminis.actionsets.wonesys.actions.MakeConnectionAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RefreshModelConnectionsAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RemoveConnectionAction;
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
