package org.opennaas.extensions.ofertie.ncl.controller.api;

import java.util.Collection;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.sdnnetwork.model.Route;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface INCLController {

	/**
	 * 
	 * @param flowRequest
	 * @param route
	 * @param networkId
	 * @return flowId of allocated flow
	 * @throws FlowAllocationException
	 */
	public String allocateFlow(FlowRequest flowRequest, Route route, String networkId) throws FlowAllocationException;

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
	public Collection<Flow> getFlows();

}
