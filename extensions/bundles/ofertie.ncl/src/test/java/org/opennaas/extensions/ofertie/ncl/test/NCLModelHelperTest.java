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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.helpers.NCLModelHelper;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class NCLModelHelperTest {

	private final static String	PORT_ID_1	= "1";
	private final static String	PORT_ID_2	= "2";
	private final static String	PORT_ID_3	= "3";

	private final static String	SWITCH_ID_1	= "switch01";
	private final static String	SWITCH_ID_2	= "switch02";

	private final static String	FLOW_ID_1	= "flow1";
	private final static String	FLOW_ID_2	= "flow2";

	@Test
	public void circuitFlowsContainPortTest() {

		List<NetOFFlow> flows = new ArrayList<NetOFFlow>();
		NetOFFlow flow1 = generateSampleFlow(SWITCH_ID_1, FLOW_ID_1, PORT_ID_1, PORT_ID_2);
		NetOFFlow flow2 = generateSampleFlow(SWITCH_ID_2, FLOW_ID_2, PORT_ID_2, PORT_ID_3);

		flows.add(flow1);
		flows.add(flow2);

		Assert.assertTrue(NCLModelHelper.flowsContainPort(SWITCH_ID_1, PORT_ID_1, flows));
		Assert.assertTrue(NCLModelHelper.flowsContainPort(SWITCH_ID_1, PORT_ID_2, flows));

		Assert.assertTrue(NCLModelHelper.flowsContainPort(SWITCH_ID_2, PORT_ID_2, flows));
		Assert.assertTrue(NCLModelHelper.flowsContainPort(SWITCH_ID_2, PORT_ID_3, flows));

		Assert.assertFalse(NCLModelHelper.flowsContainPort(SWITCH_ID_1, PORT_ID_3, flows));
		Assert.assertFalse(NCLModelHelper.flowsContainPort(SWITCH_ID_2, PORT_ID_1, flows));

	}

	private NetOFFlow generateSampleFlow(String resourceId, String flowId, String ingressPort, String outputPort) {

		NetOFFlow flow = new NetOFFlow();

		flow.setResourceId(resourceId);
		flow.setName(flowId);
		flow.setActive(true);

		flow.setMatch(generateMatchWithIngressPort(ingressPort));

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(generateSampleOutputAction(outputPort));
		flow.setActions(actions);

		return flow;
	}

	private FloodlightOFMatch generateMatchWithIngressPort(String ingressPort) {

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(ingressPort);

		return match;
	}

	private FloodlightOFAction generateSampleOutputAction(String dstPort) {

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType("output");
		action.setValue(dstPort);

		return action;

	}

}
