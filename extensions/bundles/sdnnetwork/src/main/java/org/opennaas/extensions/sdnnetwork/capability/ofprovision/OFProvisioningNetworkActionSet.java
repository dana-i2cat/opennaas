package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class OFProvisioningNetworkActionSet implements IActionSetDefinition {

	/**
	 * An Action that allocates the flow in the network. It receives an SDNNetworkOFFlow with a Route as a parameter. It returns the flowId in its
	 * ActionResponse.getResult()
	 */
	public static final String	ALLOCATEFLOW		= "allocateFlow";
	/**
	 * An Action that deallocates the flow in the network. It receives an flowId of an allocated SDNNetworkOFFlow as a parameter. It returns nothing
	 * (null) in its ActionResponse.getResult()
	 */
	public static final String	DEALLOCATEFLOW		= "deallocateFlow";
	/**
	 * An Action that retrieves allocated flows in the network. It receives no parameters It returns a collection of SDNNetworkOFFlow in its
	 * ActionResponse.getResult()
	 */
	public static final String	GETALLOCATEDFLOWS	= "getAllocatedFlows";

}
