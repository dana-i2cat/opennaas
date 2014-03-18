package org.opennaas.extensions.router.capability.l3vlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class L3VlanActionset implements IActionSetDefinition {

	public static final String			L3VLAN_ADD_IP_TO_DOMAIN			= "addIpAddressToBridgedDomain";
	public static final String			L3VLAN_REMOVE_IP_FROM_DOMAIN	= "removeIpAddressfromBridgedDomain";

	private static final List<String>	actionIds						= new ArrayList<String>(Arrays.asList(
																				L3VLAN_ADD_IP_TO_DOMAIN, L3VLAN_REMOVE_IP_FROM_DOMAIN));

	public static List<String> getActionIds() {
		return actionIds;
	}

}
