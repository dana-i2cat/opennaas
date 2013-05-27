package org.opennaas.extensions.router.junos.actionssets;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.ConfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UnconfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPPriorityAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualIPAddressAction;

/**
 * @author Julio Carlos Barrera
 */
public class VRRPActionSet extends ActionSet {

	public VRRPActionSet() {
		super.setActionSetId("VRRPActionSet");

		this.putAction(ActionConstants.VRRP_CONFIGURE, ConfigureVRRPAction.class);
		this.putAction(ActionConstants.VRRP_UNCONFIGURE, UnconfigureVRRPAction.class);
		this.putAction(ActionConstants.VRRP_UPDATE_IP_ADDRESS, UpdateVRRPVirtualIPAddressAction.class);
		this.putAction(ActionConstants.VRRP_UPDATE_PRIORITY, UpdateVRRPPriorityAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
	}
}