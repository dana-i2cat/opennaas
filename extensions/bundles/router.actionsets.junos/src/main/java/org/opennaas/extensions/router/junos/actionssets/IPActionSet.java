package org.opennaas.extensions.router.junos.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.AddIPv4Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.AddIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.RemoveIPv4Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.RemoveIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.SetIPv4Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.SetIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.SetInterfaceDescriptionAction;

public class IPActionSet extends ActionSet {
	public IPActionSet() {
		super.setActionSetId("ipActionSet");
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(ActionConstants.SETIPv4, SetIPv4Action.class);
		this.putAction(ActionConstants.SETIPv6, SetIPv6Action.class);
		this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);
		this.putAction(ActionConstants.ADDIPv4, AddIPv4Action.class);
		this.putAction(ActionConstants.ADDIPv6, AddIPv6Action.class);
		this.putAction(ActionConstants.REMOVEIPv4, RemoveIPv4Action.class);
		this.putAction(ActionConstants.REMOVEIPv6, RemoveIPv6Action.class);

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
		actionNames.add(ActionConstants.ADDIPv4);
		actionNames.add(ActionConstants.ADDIPv6);
		actionNames.add(ActionConstants.REMOVEIPv4);
		actionNames.add(ActionConstants.REMOVEIPv6);

		return actionNames;
	}

}
