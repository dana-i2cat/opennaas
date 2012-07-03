package org.opennaas.extensions.macbridge.ios.resource.actionssets;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.AddStaticVLANRegistrationEntryToFilteringDatabaseAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.CreateVLANConfigurationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.DeleteStaticVLANRegistrationEntryFromFilteringDatabaseAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.DeleteVLANConfigurationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.RefreshAction;

public class VLANAwareBridgeActionSet extends ActionSet {

	public VLANAwareBridgeActionSet() {
		super.setActionSetId("VLANAwareBridgeActionSet");

		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.CREATE_VLAN_CONFIGURATION, CreateVLANConfigurationAction.class);
		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.DELETE_VLAN_CONFIGURATION, DeleteVLANConfigurationAction.class);
		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.ADD_STATIC_VLAN_REGISTRATION_ENTRY_TO_FILTERING_DATABASE, 
				AddStaticVLANRegistrationEntryToFilteringDatabaseAction.class);
		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.DELETE_STATIC_VLAN_REGISTRATION_ENTRY_FROM_FILTERING_DATABASE, 
				DeleteStaticVLANRegistrationEntryFromFilteringDatabaseAction.class);
		this.putAction(RefreshAction.REFRESH_ACTION, RefreshAction.class);

		/* TODO add refresh actions */
		this.refreshActions.add(RefreshAction.REFRESH_ACTION);
	}
}