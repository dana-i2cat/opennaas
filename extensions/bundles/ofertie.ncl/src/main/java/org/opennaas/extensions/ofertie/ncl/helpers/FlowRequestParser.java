package org.opennaas.extensions.ofertie.ncl.helpers;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public abstract class FlowRequestParser {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	public static List<NetOFFlow> parseFlowRequestIntoSDNFlow(FlowRequest flowRequest, Route route) {

		List<NetOFFlow> flows = new ArrayList<NetOFFlow>();

		FloodlightOFMatch commonMatch = buildOFMatchFromRequest(flowRequest);

		for (NetworkConnection connection : route.getNetworkConnections()) {
			if (connection.getSource().getDeviceId().equals(connection.getDestination().getDeviceId())) {

				NetOFFlow flow = new NetOFFlow();
				flow.setName(generateRandomFlowId());
				flow.setResourceId(connection.getSource().getDeviceId());
				flow.setActive(true);
				flow.setPriority(DEFAULT_FLOW_PRIORITY);
				flow.setMatch(new FloodlightOFMatch(commonMatch));

				String ingressPort = connection.getSource().getPortNumber();
				flow.getMatch().setIngressPort(ingressPort);

				String dstPort = connection.getDestination().getPortNumber();
				FloodlightOFAction action = new FloodlightOFAction();
				action.setType("output");
				action.setValue(dstPort);
				flow.setActions(Arrays.asList(action));

				flows.add(flow);
			} else {
				// ASSUMING LINKS BETWEEN DIFERENT DEVICES ARE ALREADY PROVISIONED
			}
		}

		return flows;
	}

	public static FloodlightOFMatch buildOFMatchFromRequest(FlowRequest flowRequest) {
		FloodlightOFMatch match = new FloodlightOFMatch();

		match.setSrcPort(String.valueOf(flowRequest.getSourcePort()));
		match.setDstPort(String.valueOf(flowRequest.getDestinationPort()));

		// The last two bits of the ToS are discarded, therefore we devide the hexadecimal value per 4
		// More information at http://www.tucny.com/Home/dscp-tos
		String tosBits = Integer.toHexString(flowRequest.getTos() / 4);
		match.setTosBits(tosBits);

		if (flowRequest.getSourceIPAddress() == null || flowRequest.getSourceIPAddress().isEmpty()) {
			// remove empty strings, use null instead
			match.setSrcIp(null);
		} else {
			match.setSrcIp(flowRequest.getSourceIPAddress());
		}
		if (flowRequest.getDestinationIPAddress() == null || flowRequest.getDestinationIPAddress().isEmpty()) {
			// remove empty strings, use null instead
			match.setDstIp(null);
		} else {
			match.setDstIp(flowRequest.getDestinationIPAddress());
		}

		match.setEtherType(calculateRequiredEtherType(match));

		return match;
	}

	private static String calculateRequiredEtherType(FloodlightOFMatch match) {

		String etherType = null;

		if (match == null)
			return null;

		if (match.getSrcIp() != null || match.getDstIp() != null)
			etherType = "2048";

		if (match.getTosBits() != null)
			etherType = "2048";

		return etherType;
	}

	private static String generateRandomFlowId() {
		return UUID.randomUUID().toString();
	}
}
