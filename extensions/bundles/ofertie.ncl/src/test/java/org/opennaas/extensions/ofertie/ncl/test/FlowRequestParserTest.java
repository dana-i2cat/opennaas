package org.opennaas.extensions.ofertie.ncl.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestHelper;
import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestParser;
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
public class FlowRequestParserTest {

	@Test
	public void parseFlowRequestTest() {

		FlowRequest flowRequest = FlowRequestHelper.generateSampleFlowRequest();
		Route route = FlowRequestHelper.generateSampleRoute();

		SDNNetworkOFFlow sdnNetOFFlow = FlowRequestParser.parseFlowRequestIntoSDNFlow(flowRequest, route);

		FloodlightOFMatch match = sdnNetOFFlow.getMatch();

		Assert.assertEquals("Source ip should match. ", flowRequest.getSourceIPAddress(), match.getSrcIp());
		Assert.assertEquals("Destination ip should match.", flowRequest.getDestinationIPAddress(), match.getDstIp());
		Assert.assertEquals("Source port should match", String.valueOf(flowRequest.getSourcePort()), match.getSrcPort());
		Assert.assertEquals("Destination port should match", String.valueOf(flowRequest.getDestinationPort()), match.getDstPort());
		Assert.assertEquals("ToS should match", String.valueOf(flowRequest.getTos()), match.getTosBits());

		String ingressPort = route.getNetworkConnections().get(0).getSource().getPortNumber();
		Assert.assertEquals("Ingress port should be source port of first network connection.", ingressPort, match.getIngressPort());

		Assert.assertEquals("Route should match by the provided one.", route, sdnNetOFFlow.getRoute());

		List<FloodlightOFAction> actions = sdnNetOFFlow.getActions();
		Assert.assertEquals("SDNOFFlow should contain 1 action.", 1, actions.size());

		FloodlightOFAction action = actions.get(0);
		Assert.assertEquals("output", action.getType());
		Assert.assertEquals("Output value should be \"1\".", "device021", action.getValue());
	}
}
