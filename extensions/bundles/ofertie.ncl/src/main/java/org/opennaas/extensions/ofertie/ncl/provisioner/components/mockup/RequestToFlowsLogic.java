package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

public class RequestToFlowsLogic implements IRequestToFlowsLogic {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	@Override
	public SDNNetworkOFFlow getRequiredFlowsToSatisfyRequest(FlowRequest flowRequest, Route route) {

		SDNNetworkOFFlow flowWithRoute = FlowRequestParser.parseFlowRequestIntoSDNFlow(flowRequest, route);
		flowWithRoute.setActive(true);
		flowWithRoute.setPriority(DEFAULT_FLOW_PRIORITY);

		return flowWithRoute;
	}

}
