package org.opennaas.extensions.router.junos.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ipv4.SetIPv4Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ipv4.SetIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ipv4.SetInterfaceDescriptionAction;

public class IPActionSet extends ActionSet {
	public IPActionSet() {
		super.setActionSetId("ipActionSet");
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(ActionConstants.SETIPv4, SetIPv4Action.class);
		this.putAction(ActionConstants.SETIPv6, SetIPv6Action.class);
		this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.SETIPv4);
		actionNames.add(ActionConstants.SETIPv6);
		actionNames.add(ActionConstants.SETINTERFACEDESCRIPTION);
		return actionNames;
	}

}
