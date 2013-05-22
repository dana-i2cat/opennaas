package org.opennaas.extensions.router.capability.ospfv3;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class OSPFv3ActionSet implements IActionSetDefinition {

	public static final String	OSPFv3_CONFIGURE				= "configureOSPFv3";
	public static final String	OSPFv3_CLEAR					= "clearOSPFv3";
	public static final String	OSPFv3_GET_CONFIGURATION		= "getOSPFv3Configuration";
	public static final String	OSPFv3_ACTIVATE					= "activateOSPFv3";
	public static final String	OSPFv3_DEACTIVATE				= "deactivateOSPFv3";
	public static final String	OSPFv3_ENABLE_INTERFACE			= "enableOSPFv3InInterface";
	public static final String	OSPFv3_DISABLE_INTERFACE		= "disableOSPFv3InInterface";
	public static final String	OSPFv3_CONFIGURE_AREA			= "configureOSPFv3Area";
	public static final String	OSPFv3_REMOVE_AREA				= "removeOSPFv3Area";
	public static final String	OSPFv3_ADD_INTERFACE_IN_AREA	= "addOSPv3FInterfaceInArea";
	public static final String	OSPFv3_REMOVE_INTERFACE_IN_AREA	= "removeOSPFv3InterfaceInArea";

}
