package org.opennaas.extensions.sdnnetwork.driver.internal.actionsets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions.AllocateFlowAction;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions.DeallocateFlowAction;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions.GetAllocatedFlowsAction;

/**
 * An ActionSet Implementation for SDNNetwork capabilities that delegates to OFSwitches in the network.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SDNNetworkInternalActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public SDNNetworkInternalActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(OFProvisioningNetworkActionSet.ALLOCATEFLOW, AllocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.DEALLOCATEFLOW, DeallocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS, GetAllocatedFlowsAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(OFProvisioningNetworkActionSet.ALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.DEALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);

		return actionNames;
	}
}
