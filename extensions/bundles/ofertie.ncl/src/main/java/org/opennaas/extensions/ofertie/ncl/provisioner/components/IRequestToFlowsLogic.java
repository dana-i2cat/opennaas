package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import java.util.List;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public interface IRequestToFlowsLogic {

	public List<NetOFFlow> getRequiredFlowsToSatisfyRequest(FlowRequest flowRequest) throws Exception;

}
