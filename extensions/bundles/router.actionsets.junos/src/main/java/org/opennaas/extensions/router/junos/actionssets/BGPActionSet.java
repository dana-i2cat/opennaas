package org.opennaas.extensions.router.junos.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.bgp.ConfigureBGPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.bgp.UnconfigureBGPAction;

public class BGPActionSet extends ActionSet {
	public BGPActionSet() {
		super.setActionSetId("bgpActionSet");
		this.putAction(ActionConstants.CONFIGURE_BGP, ConfigureBGPAction.class);
		this.putAction(ActionConstants.UNCONFIGURE_BGP, UnconfigureBGPAction.class);
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.CONFIGURE_BGP);
		actionNames.add(ActionConstants.UNCONFIGURE_BGP);
		return actionNames;
	}

}
