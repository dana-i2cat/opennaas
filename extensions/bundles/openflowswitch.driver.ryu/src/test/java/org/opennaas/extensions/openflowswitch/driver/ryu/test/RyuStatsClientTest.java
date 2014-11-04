package org.opennaas.extensions.openflowswitch.driver.ryu.test;

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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuConstants;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSessionFactory;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.RyuClientFactory;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * Tests for {@link IRyuStatsClient} and {@link RyuClientFactory}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class RyuStatsClientTest {

	private static final String		RYU_URI		= "http://192.168.122.127:8080/";
	private static final String		SWITCH_ID	= "1";
	private static final String		SESSION_ID	= "0001";

	private ProtocolSessionContext	context;
	private RyuProtocolSession		session;
	private IRyuStatsClient			client;

	@Before
	public void initSessionAndClient() throws ProtocolException {
		context = generateContext();
		session = (RyuProtocolSession) new RyuProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
		session.connect();
		client = session.getRyuClientForUse();
	}

	private ProtocolSessionContext generateContext() {
		ProtocolSessionContext context = new ProtocolSessionContext();
		context.addParameter(ProtocolSessionContext.PROTOCOL, RyuProtocolSession.RYU_PROTOCOL_TYPE);
		context.addParameter(ProtocolSessionContext.PROTOCOL_URI, RYU_URI);
		context.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
		context.addParameter(RyuProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, SWITCH_ID);
		return context;
	}

	// This test is ignored because it requires having a Ryu controller available at RYU_URI.
	// Remove @Ignore to perform the test.
	@Ignore
	@Test
	public void clientTest() throws ProtocolException, Exception {
		// get initial flows
		RyuOFFlowListWrapper listWrapper = client.getFlows(SWITCH_ID);

		int initialSize = listWrapper.size();

		RyuOFFlow flow = generateSampleRyuOFFlow();

		client.addFlowEntry(flow);

		// get flows, expect one more flow
		listWrapper = client.getFlows(SWITCH_ID);
		Assert.assertEquals(initialSize + 1, listWrapper.size());

		// check existence of valid rule

		// remove flow
		client.deleteFlowEntryStrictly(flow);

		// get flows, expect initial size again
		listWrapper = client.getFlows(SWITCH_ID);
		Assert.assertEquals(initialSize, listWrapper.size());
	}

	private static RyuOFFlow generateSampleRyuOFFlow() {
		RyuOFFlow forwardingRule = new RyuOFFlow();

		forwardingRule.setDpid(SWITCH_ID);

		forwardingRule.setPriority(RyuConstants.DEFAULT_PRIORITY);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("1");
		forwardingRule.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue("2");

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		return forwardingRule;
	}
}
