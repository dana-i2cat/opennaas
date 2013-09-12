package org.opennaas.extensions.openflowswitch.capability;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class OpenflowForwardingActionSet implements IActionSetDefinition {

	public static final String	createOFForwardingRule	= "createOFForwardingRule";
	public static final String	removeOFForwardingRule	= "removeOFForwardingRule";

	public static final String	getFlows				= "getFlows";
	public static final String	getPorts				= "getPorts";
}
