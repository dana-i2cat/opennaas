package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public interface INetworkSelector {

	/**
	 * Finds a network able to allocate given qosPolicyRequest
	 * 
	 * @param flowRequest
	 * @return id of a network able to allocate given qosPolicyRequest
	 * @throws Exception
	 */
	public String findNetworkForRequest(QosPolicyRequest qosPolicyRequest) throws Exception;

	/**
	 * Finds the network containing a flow with given flowId
	 * 
	 * @param flowId
	 * @return id of the network containing a flow with given flowId
	 * @throws Exception
	 */
	public String findNetworkForFlowId(String flowId) throws Exception;

}
