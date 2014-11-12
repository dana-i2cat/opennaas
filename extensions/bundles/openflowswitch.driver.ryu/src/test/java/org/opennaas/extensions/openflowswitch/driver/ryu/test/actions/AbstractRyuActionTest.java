package org.opennaas.extensions.openflowswitch.driver.ryu.test.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public abstract class AbstractRyuActionTest {

	protected static final String		SWITCH_ID	= "1";
	protected static final String		FLOW_ID		= "flow1";

	protected RyuOFFlowListWrapper		serverResponse;
	protected RyuOFFlow					ryuOFFlow;
	protected IRyuStatsClient			client;
	protected IProtocolSessionManager	protocolSessionManager;

	protected void initializeRyuOFFlow() {

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
		match.setSrcIp("192.168.1.1");
		match.setDstIp("192.168.1.2");
		ryuOFFlow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue("2");

		ryuOFFlow.setName(FLOW_ID);

		ryuOFFlow.setActions(Arrays.asList(action));

	}

	protected void mockActionDependencies() throws ProtocolException {
		protocolSessionManager = PowerMockito.mock(IProtocolSessionManager.class);
		RyuProtocolSession ps = PowerMockito.mock(RyuProtocolSession.class);
		ProtocolSessionContext psc = PowerMockito.mock(ProtocolSessionContext.class);
		client = PowerMockito.mock(IRyuStatsClient.class);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(RyuProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, SWITCH_ID);

		Mockito.when(protocolSessionManager.obtainSessionByProtocol(RyuProtocolSession.RYU_PROTOCOL_TYPE, false)).thenReturn(ps);
		Mockito.when(ps.getSessionContext()).thenReturn(psc);
		Mockito.when(psc.getSessionParameters()).thenReturn(sessionParameters);
		Mockito.when(ps.getRyuClientForUse()).thenReturn(client);

	}

}
