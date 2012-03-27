package org.opennaas.extensions.router.junos.actionssets;

import org.opennaas.extensions.router.junos.actionssets.actions.ospf.AddOSPFInterfaceInAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ClearOSPFAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFInterfaceStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.GetOSPFConfigAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.RemoveOSPFAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.RemoveOSPFInterfaceInAreaAction;

import org.opennaas.core.resources.action.ActionSet;

public class OSPFActionSet extends ActionSet {

	public OSPFActionSet() {
		super.setActionSetId("OSPFActionSet");

		this.putAction(ActionConstants.OSPF_GET_CONFIGURATION, GetOSPFConfigAction.class);
		this.putAction(ActionConstants.OSPF_CONFIGURE, ConfigureOSPFAction.class);
		this.putAction(ActionConstants.OSPF_CLEAR, ClearOSPFAction.class);
		this.putAction(ActionConstants.OSPF_ACTIVATE, ConfigureOSPFStatusAction.class);
		this.putAction(ActionConstants.OSPF_DEACTIVATE, ConfigureOSPFStatusAction.class);
		this.putAction(ActionConstants.OSPF_ENABLE_INTERFACE, ConfigureOSPFInterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPF_DISABLE_INTERFACE, ConfigureOSPFInterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPF_CONFIGURE_AREA, ConfigureOSPFAreaAction.class);
		this.putAction(ActionConstants.OSPF_REMOVE_AREA, RemoveOSPFAreaAction.class);
		this.putAction(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, AddOSPFInterfaceInAreaAction.class);
		this.putAction(ActionConstants.OSPF_REMOVE_INTERFACE_IN_AREA, RemoveOSPFInterfaceInAreaAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.OSPF_GET_CONFIGURATION);
	}
}