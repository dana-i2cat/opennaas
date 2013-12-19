package org.opennaas.extensions.ofertie.ncl.test;

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

		Assert.assertTrue(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_1, PORT_ID_1, flows));
		Assert.assertTrue(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_1, PORT_ID_2, flows));

		Assert.assertTrue(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_2, PORT_ID_2, flows));
		Assert.assertTrue(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_2, PORT_ID_3, flows));

		Assert.assertFalse(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_1, PORT_ID_3, flows));
		Assert.assertFalse(NCLModelHelper.circuitFlowsContainPort(SWITCH_ID_2, PORT_ID_1, flows));

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
