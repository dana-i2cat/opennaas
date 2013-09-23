package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import java.util.Collection;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public interface IOFProvisioningNetworkCapability extends ICapability {
	
	/**
	 * Allocates a flow in the network
	 * 
	 * @param flowWithRoute A Flow with a defined route
	 * @return flowId of created SDNNetworkOFFlow
	 * @throws CapabilityException
	 */
	public String allocateOFFlow(SDNNetworkOFFlow flowWithRoute) throws CapabilityException;
	
	/**
	 * Deallocates an existing flow in the network
	 * @param flowId
	 * @throws CapabilityException
	 */
	public void deallocateOFFlow(String flowId) throws CapabilityException;
	
	/**
	 * 
	 * @return allocated flows
	 * @throws CapabilityException
	 */
	public Collection<SDNNetworkOFFlow> getAllocatedFlows() throws CapabilityException;
	
	/**
	 * Updates an allocated flow in the network
	 * 
	 * @param flowId of the already allocated flow to update
	 * @param flowWithRoute A Flow with a defined route
	 * @return flowId of created SDNNetworkOFFlow
	 * @throws CapabilityException
	 */
	public String updateAllocatedOFFlow(String flowId, SDNNetworkOFFlow flowWithRoute) throws CapabilityException;

}
