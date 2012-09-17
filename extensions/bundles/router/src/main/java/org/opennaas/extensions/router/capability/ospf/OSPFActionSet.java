package org.opennaas.extensions.router.capability.ospf;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class OSPFActionSet implements IActionSetDefinition {

	public static final String	OSPF_CONFIGURE					= "configureOSPF";
	public static final String	OSPF_CLEAR						= "clearOSPF";
	public static final String	OSPF_GET_CONFIGURATION			= "getOSPFConfiguration";
	public static final String	OSPF_ACTIVATE					= "activateOSPF";
	public static final String	OSPF_DEACTIVATE					= "deactivateOSPF";
	public static final String	OSPF_ENABLE_INTERFACE			= "enableOSPFInInterface";
	public static final String	OSPF_DISABLE_INTERFACE			= "disableOSPFInInterface";
	public static final String	OSPF_CONFIGURE_AREA				= "configureOSPFArea";
	public static final String	OSPF_REMOVE_AREA				= "removeOSPFArea";
	public static final String	OSPF_ADD_INTERFACE_IN_AREA		= "addOSPFInterfaceInArea";
	public static final String	OSPF_REMOVE_INTERFACE_IN_AREA	= "removeOSPFInterfaceInArea";

}
