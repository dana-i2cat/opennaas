package org.opennaas.extensions.ofertie.ncl.controller.api;

import java.util.Collection;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowRetrievalException;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface INCLController {

	/**
	 * 
	 * @param flowWithRoute
	 * @param networkId
	 * @return flowId of allocated flow
	 * @throws FlowAllocationException
	 */
	public String allocateFlow(SDNNetworkOFFlow flowWithRoute, String networkId) throws FlowAllocationException;

	/**
	 * 
	 * @param flowId
	 * @param networkId
	 * @return flowId of deallocated flow
	 * @throws FlowAllocationException
	 */
	public String deallocateFlow(String flowId, String networkId) throws FlowAllocationException;

	/**
	 * 
	 * @return
	 */
	public Collection<SDNNetworkOFFlow> getFlows(String networkId) throws FlowRetrievalException;
}
