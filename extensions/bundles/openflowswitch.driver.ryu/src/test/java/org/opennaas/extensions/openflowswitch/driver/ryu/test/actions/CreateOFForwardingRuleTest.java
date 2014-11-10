package org.opennaas.extensions.openflowswitch.driver.ryu.test.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CreateOFForwardingRuleTest {

	private static final String	SWITCH_ID	= "00:00:00:00:00:00:00:01";

	CreateOFForwardingAction	action;
	RyuOFFlow					ryuOFFlow;

	@Before
	public void prepareTesT() {
		action = new CreateOFForwardingAction();
		action.setModelToUpdate(new OpenflowSwitchModel());

		generateRyuOFFlow();
	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE,
				OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, action.getActionID());
	}

	@Test(expected = ActionException.class)
	public void checkNullParamsTest() throws ActionException {
		action.checkParams(null);
	}

	@Test(expected = ActionException.class)
	public void wronParamsTypeTest() throws ActionException {
		action.checkParams(new Object());

	}

	@Test
	public void actionExecutionTest() throws Exception {
		// mock protocolsession and client
		IProtocolSessionManager psm = PowerMockito.mock(IProtocolSessionManager.class);
		RyuProtocolSession ps = PowerMockito.mock(RyuProtocolSession.class);
		ProtocolSessionContext psc = PowerMockito.mock(ProtocolSessionContext.class);
		IRyuStatsClient client = PowerMockito.mock(IRyuStatsClient.class);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(RyuProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, SWITCH_ID);

		Mockito.when(psm.obtainSessionByProtocol(RyuProtocolSession.RYU_PROTOCOL_TYPE, false)).thenReturn(ps);
		Mockito.when(ps.getSessionContext()).thenReturn(psc);
		Mockito.when(psc.getSessionParameters()).thenReturn(sessionParameters);
		Mockito.when(ps.getRyuClientForUse()).thenReturn(client);

		action.setParams(ryuOFFlow);
		action.execute(psm);

		Mockito.verify(client, Mockito.times(1)).addFlowEntry(ryuOFFlow);

	}

	private void generateRyuOFFlow() {

		ryuOFFlow = new RyuOFFlow();
		ryuOFFlow.setDpid("1");
		ryuOFFlow.setCookie("10");
		ryuOFFlow.setTableId("10");
		ryuOFFlow.setPriority("10");
		ryuOFFlow.setIdleTimeout("10");
		ryuOFFlow.setHardTimeout("10");
		ryuOFFlow.setFlags("10");

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("1");
		ryuOFFlow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue("2");

		ryuOFFlow.setActions(Arrays.asList(action));

	}

}
