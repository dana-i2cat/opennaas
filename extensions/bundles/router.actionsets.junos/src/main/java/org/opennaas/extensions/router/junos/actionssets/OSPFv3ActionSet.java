package org.opennaas.extensions.router.junos.actionssets;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.AddOSPFv3InterfaceInAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ClearOSPFv3Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3AreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3InterfaceStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3StatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.GetOSPFv3ConfigAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.RemoveOSPFv3AreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.RemoveOSPFv3InterfaceInAreaAction;

public class OSPFv3ActionSet extends ActionSet {

	public OSPFv3ActionSet() {
		super.setActionSetId("OSPFv3ActionSet");

		this.putAction(ActionConstants.OSPFv3_GET_CONFIGURATION, GetOSPFv3ConfigAction.class);
		this.putAction(ActionConstants.OSPFv3_CONFIGURE, ConfigureOSPFv3Action.class);
		this.putAction(ActionConstants.OSPFv3_CLEAR, ClearOSPFv3Action.class);
		this.putAction(ActionConstants.OSPFv3_ACTIVATE, ConfigureOSPFv3StatusAction.class);
		this.putAction(ActionConstants.OSPFv3_DEACTIVATE, ConfigureOSPFv3StatusAction.class);
		this.putAction(ActionConstants.OSPFv3_ENABLE_INTERFACE, ConfigureOSPFv3InterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPFv3_DISABLE_INTERFACE, ConfigureOSPFv3InterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPFv3_CONFIGURE_AREA, ConfigureOSPFv3AreaAction.class);
		this.putAction(ActionConstants.OSPFv3_REMOVE_AREA, RemoveOSPFv3AreaAction.class);
		this.putAction(ActionConstants.OSPFv3_ADD_INTERFACE_IN_AREA, AddOSPFv3InterfaceInAreaAction.class);
		this.putAction(ActionConstants.OSPFv3_REMOVE_INTERFACE_IN_AREA, RemoveOSPFv3InterfaceInAreaAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.OSPFv3_GET_CONFIGURATION);

	}
}
