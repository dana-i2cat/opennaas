package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import java.util.List;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public interface IRequestToFlowsLogic {

	public List<NetOFFlow> getRequiredFlowsToSatisfyRequest(QosPolicyRequest qosPolicyRequest) throws Exception;

}
