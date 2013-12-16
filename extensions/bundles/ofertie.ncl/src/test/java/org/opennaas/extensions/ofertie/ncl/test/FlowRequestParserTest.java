package org.opennaas.extensions.ofertie.ncl.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestHelper;
import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

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

		List<NetOFFlow> sdnNetOFFlows = FlowRequestParser.parseFlowRequestIntoSDNFlow(flowRequest, route);

		Assert.assertTrue(route.getNetworkConnections().size() >= sdnNetOFFlows.size());

		int j = 0;
		for (int i = 0; i < route.getNetworkConnections().size(); i++) {
			if (route.getNetworkConnections().get(i).getSource().getDeviceId()
					.equals(route.getNetworkConnections().get(i).getDestination().getDeviceId())) {

				NetOFFlow sdnNetOFFlow = sdnNetOFFlows.get(j);

				FloodlightOFMatch match = sdnNetOFFlow.getMatch();
				Assert.assertEquals("Source ip should match. ", flowRequest.getSourceIPAddress(), match.getSrcIp());
				Assert.assertEquals("Destination ip should match.", flowRequest.getDestinationIPAddress(), match.getDstIp());
				Assert.assertEquals("Source port should match", String.valueOf(flowRequest.getSourcePort()), match.getSrcPort());
				Assert.assertEquals("Destination port should match", String.valueOf(flowRequest.getDestinationPort()), match.getDstPort());
				Assert.assertEquals("ToS should match", String.valueOf(flowRequest.getTos() / 4), match.getTosBits());

				String ingressPort = route.getNetworkConnections().get(i).getSource().getPortNumber();
				Assert.assertEquals("Ingress port should be source port of correspondent network connection.", ingressPort, match.getIngressPort());

				List<FloodlightOFAction> actions = sdnNetOFFlow.getActions();
				Assert.assertEquals("SDNOFFlow should contain 1 action.", 1, actions.size());

				FloodlightOFAction action = actions.get(0);
				Assert.assertEquals("output", action.getType());
				Assert.assertEquals("Output value should be destination port of correspondent network connection.",
						route.getNetworkConnections().get(i).getDestination().getPortNumber(), action.getValue());

				j++;
			}
		}
	}
}
