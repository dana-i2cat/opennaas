package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightConstants;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class FloodlightActionsTest {

	private CreateOFForwardingAction	createOFForwardingAction;

	@Before
	public void initActions() {
		createOFForwardingAction = new CreateOFForwardingAction();
		createOFForwardingAction.setActionID(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
	}

	@Test
	public void createOFForwardingActionValidCreationTest() throws ActionException {
		FloodlightOFFlow forwardingRule = generateValidFloodlightOFFlow();
		boolean isOk = createOFForwardingAction.checkParams(forwardingRule);
		Assert.assertTrue("Params are correct", isOk);
	}

	@Test(expected = ActionException.class)
	public void createOFForwardingActionValidInvalidGreaterPriorityCreationTest() throws ActionException {

		int priority = Integer.parseInt(FloodlightConstants.MAX_PRIORITY) + 1;

		FloodlightOFFlow forwardingRule = generateValidFloodlightOFFlow();
		forwardingRule.setPriority(String.valueOf(priority));
		boolean isOk = createOFForwardingAction.checkParams(forwardingRule);
		Assert.assertFalse("Check params failed", isOk);
	}

	@Test(expected = ActionException.class)
	public void createOFForwardingActionValidInvalidLowerPriorityCreationTest() throws ActionException {

		int priority = Integer.parseInt(FloodlightConstants.MIN_PRIORITY) - 1;

		FloodlightOFFlow forwardingRule = generateValidFloodlightOFFlow();
		forwardingRule.setPriority(String.valueOf(priority));
		boolean isOk = createOFForwardingAction.checkParams(forwardingRule);
		Assert.assertFalse("Check params failed", isOk);
	}

	private static FloodlightOFFlow generateValidFloodlightOFFlow() {
		return generateSampleFloodlightOFFlow("testingFlow", "1", "2");
	}

	private static FloodlightOFFlow generateSampleFloodlightOFFlow(String name, String inputPort, String outputPort) {

		FloodlightOFFlow forwardingRule = new FloodlightOFFlow();
		forwardingRule.setName(name);
		forwardingRule.setPriority(FloodlightConstants.DEFAULT_PRIORITY);
		forwardingRule.setActive(true);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);
		forwardingRule.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		return forwardingRule;
	}

}
