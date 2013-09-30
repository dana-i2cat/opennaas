package org.opennaas.extensions.ofertie.ncl.controller.api;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
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

}
