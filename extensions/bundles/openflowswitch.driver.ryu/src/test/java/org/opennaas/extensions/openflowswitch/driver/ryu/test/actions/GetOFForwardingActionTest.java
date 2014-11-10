package org.opennaas.extensions.openflowswitch.driver.ryu.test.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.GetOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetOFForwardingActionTest {

	private static final String	SWITCH_ID	= "00:00:00:00:00:00:00:01";

	GetOFForwardingAction		action;
	RyuOFFlowListWrapper		serverResponse;

	RyuOFFlow					ryuOFFlow;

	@Before
	public void prepareTest() {
		action = new GetOFForwardingAction();
		serverResponse = generateRyuOFFLowListWrapper();
	}

	@Test
	public void actionIdTest() {
		Assert.assertEquals("ActionId should match " + OpenflowForwardingActionSet.GETFLOWS, OpenflowForwardingActionSet.GETFLOWS,
				action.getActionID());
	}

	@Test
	public void checkPararamsTest() throws ActionException {

		Assert.assertTrue("Check Params should not fail for null parameters.", action.checkParams(null));
		Assert.assertTrue("Check Params should not fail for any parameter.", action.checkParams(new Object()));

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

		// test
		ActionResponse response = action.execute(psm);
		Assert.assertNotNull("Action response should not be null", response);
		Assert.assertEquals("Action status should be " + ActionResponse.STATUS.OK, ActionResponse.STATUS.OK, response.getStatus());

		Assert.assertTrue("Action should return a list.", response.getResult() instanceof List<?>);

		@SuppressWarnings("unchecked")
		List<OFFlow> flows = (List<OFFlow>) response.getResult();
		Assert.assertEquals("Response should contain 1 OFFlow", 1, flows.size());

		Assert.assertEquals(ryuOFFlow.getMatch(), flows.get(0).getMatch());
		Assert.assertEquals(ryuOFFlow.getName(), flows.get(0).getName());
		Assert.assertEquals(ryuOFFlow.getActions(), flows.get(0).getActions());
		Assert.assertEquals(ryuOFFlow.getPriority(), flows.get(0).getPriority());

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
