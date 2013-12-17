package org.opennaas.extensions.sdnnetwork.capability.monitoring;

import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IActionSetDefinition;
import org.opennaas.extensions.sdnnetwork.model.NetworkStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class MonitoringNetworkActionSet implements IActionSetDefinition {

	/**
	 * An action that retrieves the statistics of all port of the network switches. It receives no parameters, and it returns a
	 * {@link NetworkStatistics} object in the {@link ActionResponse#getResult()} method.
	 */
	public static final String	GET_NETWORK_STATISTICS	= "getNetworkStatistics";
}
