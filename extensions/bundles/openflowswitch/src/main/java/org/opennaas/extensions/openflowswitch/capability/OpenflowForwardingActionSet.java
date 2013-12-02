package org.opennaas.extensions.openflowswitch.capability;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class OpenflowForwardingActionSet implements IActionSetDefinition {

	public static final String	CREATEOFFORWARDINGRULE	= "createOFForwardingRule";
	public static final String	REMOVEOFFORWARDINGRULE	= "removeOFForwardingRule";

	public static final String	GETFLOWS				= "getFlows";
	public static final String	GETPORTS				= "getPorts";
}
