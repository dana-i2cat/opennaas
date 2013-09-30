package org.opennaas.extensions.ofertie.ncl.helpers;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public abstract class FlowRequestParser {

	public static SDNNetworkOFFlow parseFlowRequestIntoSDNFlow(FlowRequest flowRequest, Route route) {

		SDNNetworkOFFlow sdnNetworkOFFlow = new SDNNetworkOFFlow();
		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();

		FloodlightOFMatch match = buildOFMatchFromRequest(flowRequest, route);

		FloodlightOFAction forwardingAction = buildForwardingActionFromRequest(flowRequest, route);
		actions.add(forwardingAction);

		sdnNetworkOFFlow.setMatch(match);
		sdnNetworkOFFlow.setRoute(route);
		sdnNetworkOFFlow.setActions(actions);

		return sdnNetworkOFFlow;
	}

	public static FloodlightOFAction buildForwardingActionFromRequest(FlowRequest flowRequest, Route route) {
		FloodlightOFAction action = new FloodlightOFAction();

		int lastConnectionIndex = route.getNetworkConnections().size() - 1;

		String dstPort = String.valueOf(route.getNetworkConnections().get(lastConnectionIndex).getDestination().getId());

		action.setType("output");
		action.setValue(dstPort);

		return action;
	}

	public static FloodlightOFMatch buildOFMatchFromRequest(FlowRequest flowRequest, Route route) {
		FloodlightOFMatch match = new FloodlightOFMatch();

		match.setSrcPort(String.valueOf(flowRequest.getSourcePort()));
		match.setDstPort(String.valueOf(flowRequest.getDestinationPort()));
		match.setTosBits(String.valueOf(flowRequest.getTos()));
		match.setSrcIp(flowRequest.getSourceIPAddress());
		match.setDstIp(flowRequest.getDestinationIPAddress());

		String ingressPort = route.getNetworkConnections().get(0).getSource().getId();
		match.setIngressPort(ingressPort);

		return match;
	}

}
