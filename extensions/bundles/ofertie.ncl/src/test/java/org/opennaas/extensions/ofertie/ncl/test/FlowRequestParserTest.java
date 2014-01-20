package org.opennaas.extensions.ofertie.ncl.test;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequesttHelper;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
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

		QosPolicyRequest qosPolicyRequest = QoSPolicyRequesttHelper.generateSampleFlowRequest();
		Route route = QoSPolicyRequesttHelper.generateSampleRoute();

		List<NetOFFlow> sdnNetOFFlows = QoSPolicyRequestParser.parseFlowRequestIntoSDNFlow(qosPolicyRequest, route);

		Assert.assertTrue(route.getNetworkConnections().size() >= sdnNetOFFlows.size());

		int j = 0;
		for (int i = 0; i < route.getNetworkConnections().size(); i++) {
			if (route.getNetworkConnections().get(i).getSource().getDeviceId()
					.equals(route.getNetworkConnections().get(i).getDestination().getDeviceId())) {

				NetOFFlow sdnNetOFFlow = sdnNetOFFlows.get(j);

				FloodlightOFMatch match = sdnNetOFFlow.getMatch();
				Assert.assertEquals("Source ip should match. ", qosPolicyRequest.getSource().getAddress(), match.getSrcIp());
				Assert.assertEquals("Destination ip should match.", qosPolicyRequest.getDestination().getAddress(), match.getDstIp());
				Assert.assertEquals("Source port should match", qosPolicyRequest.getSource().getPort(), match.getSrcPort());
				Assert.assertEquals("Destination port should match", qosPolicyRequest.getDestination().getPort(), match.getDstPort());
				Assert.assertEquals("ToS should match", String.valueOf(Integer.parseInt(qosPolicyRequest.getLabel()) / 4), match.getTosBits());

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
