package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

public interface IRequestToFlowsLogic {

	public SDNNetworkOFFlow getRequiredFlowsToSatisfyRequest(FlowRequest flowRequest, Route route);

}
