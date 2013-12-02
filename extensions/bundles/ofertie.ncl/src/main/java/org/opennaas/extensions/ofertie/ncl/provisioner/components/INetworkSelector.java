package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

public interface INetworkSelector {

	/**
	 * Finds a network able to allocate given flowRequest
	 * 
	 * @param flowRequest
	 * @return id of a network able to allocate given flowRequest
	 * @throws Exception
	 */
	public String findNetworkForRequest(FlowRequest flowRequest) throws Exception;

	/**
	 * Finds the network containing a flow with given flowId
	 * 
	 * @param flowId
	 * @return id of the network containing a flow with given flowId
	 * @throws Exception
	 */
	public String findNetworkForFlowId(String flowId) throws Exception;

}
