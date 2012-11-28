package org.opennaas.extensions.router.capability.vrrp;

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * @author Julio Carlos Barrera
 */
public class VRRPActionSet implements IActionSetDefinition {
	public static final String	VRRP_CONFIGURE		= "configureVRRP";
	public static final String	VRRP_UNCONFIGURE	= "unconfigureVRRP";
}