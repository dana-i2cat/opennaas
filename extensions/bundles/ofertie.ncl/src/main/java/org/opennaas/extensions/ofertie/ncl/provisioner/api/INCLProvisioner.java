package org.opennaas.extensions.ofertie.ncl.provisioner.api;

import java.util.Collection;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat) 
 *
 */
public interface INCLProvisioner {
	
	/**
	 * Allocates a flow.
	 * 
	 * @param flowRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws ProvisionerException
	 */
	public String allocateFlow(FlowRequest flowRequest) throws FlowAllocationException, ProvisionerException;
	
	/**
	 * Updates already allocated flow having flowId. May cause re-allocating the flow.
	 * 
	 * @param flowId of flow to update
	 * @param updatedFlowRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	public String updateFlow(String flowId, FlowRequest updatedFlowRequest) throws FlowAllocationException, FlowNotFoundException, ProvisionerException;
	
	/**
	 * Deallocates an allocated flow.
	 * 
	 * @param flowId id of flow to deallocate
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	public void deallocateFlow(String flowId) throws FlowNotFoundException, ProvisionerException;
	
	/**
	 * Returns currently allocated flows.
	 * 
	 * @return Currently allocated flows
	 * @throws ProvisionerException
	 */
	public Collection<Flow> readAllocatedFlows() throws ProvisionerException;
	

}
