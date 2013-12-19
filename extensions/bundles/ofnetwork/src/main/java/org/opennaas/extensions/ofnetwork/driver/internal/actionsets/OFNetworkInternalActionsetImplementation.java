package org.opennaas.extensions.ofnetwork.driver.internal.actionsets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.ofnetwork.capability.monitoring.MonitoringNetworkActionSet;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions.AllocateFlowAction;
import org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions.DeallocateFlowAction;
import org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions.GetAllocatedFlowsAction;
import org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions.GetNetworkStatistics;

/**
 * An ActionSet Implementation for SDNNetwork capabilities that delegates to OFSwitches in the network.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class OFNetworkInternalActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public OFNetworkInternalActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(OFProvisioningNetworkActionSet.ALLOCATEFLOW, AllocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.DEALLOCATEFLOW, DeallocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS, GetAllocatedFlowsAction.class);

		this.putAction(MonitoringNetworkActionSet.GET_NETWORK_STATISTICS, GetNetworkStatistics.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(OFProvisioningNetworkActionSet.ALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.DEALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);

		actionNames.add(MonitoringNetworkActionSet.GET_NETWORK_STATISTICS);

		return actionNames;
	}
}
