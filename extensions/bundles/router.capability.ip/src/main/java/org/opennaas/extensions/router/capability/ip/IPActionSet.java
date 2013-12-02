package org.opennaas.extensions.router.capability.ip;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class IPActionSet implements IActionSetDefinition {

	public static final String	SET_INTERFACE_DESCRIPTION	= "setInterfaceDescription";
	public static final String	SET_IPv4					= "setIPv4";
	public static final String	SET_IPv6					= "setIPv6";
	public static final String	ADD_IPv4					= "addIPv4";
	public static final String	ADD_IPv6					= "addIPv6";
	public static final String	REMOVE_IPv4					= "removeIPv4";
	public static final String	REMOVE_IPv6					= "removeIPv6";
}
