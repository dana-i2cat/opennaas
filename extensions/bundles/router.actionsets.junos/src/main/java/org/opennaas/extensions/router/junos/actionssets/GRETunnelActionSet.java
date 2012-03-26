package org.opennaas.extensions.router.junos.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.CreateTunnelAction;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.DeleteTunnelAction;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.GetTunnelConfigurationAction;

import org.opennaas.core.resources.action.ActionSet;

public class GRETunnelActionSet extends ActionSet {

	public GRETunnelActionSet() {
		super.setActionSetId("gretunnelActionSet");

		this.putAction(ActionConstants.CREATETUNNEL, CreateTunnelAction.class);
		this.putAction(ActionConstants.DELETETUNNEL, DeleteTunnelAction.class);
		this.putAction(ActionConstants.GETTUNNELCONFIG, GetTunnelConfigurationAction.class);
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(ActionConstants.CREATETUNNEL);
		actionNames.add(ActionConstants.GETTUNNELCONFIG);
		actionNames.add(ActionConstants.SHOWTUNNELS);
		actionNames.add(ActionConstants.DELETETUNNEL);

		return actionNames;
	}
}