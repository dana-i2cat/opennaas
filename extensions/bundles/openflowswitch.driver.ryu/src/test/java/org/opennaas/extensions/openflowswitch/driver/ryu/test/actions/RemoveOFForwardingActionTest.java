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
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.RemoveOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.powermock.api.mockito.PowerMockito;

public class RemoveOFForwardingActionTest {

	private static final String	SWITCH_ID	= "00:00:00:00:00:00:00:01";
	private static final String	FLOW_ID		= "flow1";

	RemoveOFForwardingAction	action;
	RyuOFFlowListWrapper		serverResponse;
	RyuOFFlow					ryuOFFlow;

	@Before
	public void prepareTest() {
		action = new RemoveOFForwardingAction();
		serverResponse = generateRyuOFFLowListWrapper();
	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE,
				OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE,
				action.getActionID());
	}

	@Test(expected = ActionException.class)
	public void checkNullPararamsTest() throws ActionException {
		action.checkParams(null);
	}

	@Test
	public void checkParamsTest() throws ActionException {
		Assert.assertTrue("Action should accept String parameters.", action.checkParams(FLOW_ID));
	}

	@Test
	public void executeActionTest() throws Exception {
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
		Mockito.when(client.getFlows(Mockito.eq(SWITCH_ID))).thenReturn(serverResponse);

		// execute action
		action.setParams(FLOW_ID);
		action.execute(psm);

		Mockito.verify(client, Mockito.times(1)).deleteFlowEntryStrictly(ryuOFFlow);

	}

	private RyuOFFlowListWrapper generateRyuOFFLowListWrapper() {

		RyuOFFlowListWrapper wrapper = new RyuOFFlowListWrapper();

		ryuOFFlow = new RyuOFFlow();
		ryuOFFlow.setDpid("1");
		ryuOFFlow.setCookie("10");
		ryuOFFlow.setTableId("10");
		ryuOFFlow.setPriority("10");
		ryuOFFlow.setIdleTimeout("10");
		ryuOFFlow.setHardTimeout("10");
		ryuOFFlow.setFlags("10");
		ryuOFFlow.setName(FLOW_ID);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("1");
		ryuOFFlow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue("2");

		ryuOFFlow.setActions(Arrays.asList(action));
		wrapper.add(ryuOFFlow);

		return wrapper;
	}

}
