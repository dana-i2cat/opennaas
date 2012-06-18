package org.opennaas.extensions.capability.macbridge.vlanawarebridge;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class VLANAwareBridgeActionSet implements IActionSetDefinition {
	
	public static final String	CREATE_VLAN_CONFIGURATION											= "createVLANConfiguration";
	public static final String	DELETE_VLAN_CONFIGURATION											= "deleteVLANConfiguration";
	public static final String	ADD_STATIC_VLAN_REGISTRATION_ENTRY_TO_FILTERING_DATABASE			= "addStaticVLANRegistrationEntryToFilteringDatabase";
	public static final String	DELETE_STATIC_VLAN_REGISTRATION_ENTRY_FROM_FILTERING_DATABASE		= "deleteStaticVLANRegistrationEntryFromFilteringDatabase";

}
