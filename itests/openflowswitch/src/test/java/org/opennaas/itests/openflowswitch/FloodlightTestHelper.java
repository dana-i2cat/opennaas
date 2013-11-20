package org.opennaas.itests.openflowswitch;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public abstract class FloodlightTestHelper {

	public static FloodlightOFFlow sampleFloodlightOFFlow(String name, String priority, String inputPort, String outputPort) {

		FloodlightOFFlow forwardingRule = new FloodlightOFFlow();
		forwardingRule.setName(name);
		forwardingRule.setPriority(priority);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);
		forwardingRule.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType(CreateOFForwardingAction.FORWARDING_ACTION);
		floodlightAction.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		return forwardingRule;
	}

}
