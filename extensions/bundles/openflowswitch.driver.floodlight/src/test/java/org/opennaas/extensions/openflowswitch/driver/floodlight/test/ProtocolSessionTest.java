package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSessionFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class ProtocolSessionTest {
	
	private static final String FLOODLIGHT_URI = "http://dev.ofertie.i2cat.net:8080";
	private static final String SWITCH_ID = "00:00:00:00:00:00:00:03";
	private static final String SESSION_ID = "0001";
	
	ProtocolSessionContext context;
	FloodlightProtocolSession session;
	IFloodlightStaticFlowPusherClient client;
	
	FloodlightOFFlow flow;
	
	
	@Before
	public void initSessionAndClient() throws ProtocolException {
		context = generateContext();
		session = (FloodlightProtocolSession) new FloodlightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
		session.connect();
		client = session.getFloodlightClientForUse();
	}
	
	@Before
	public void initFlow() throws ProtocolException {
		
		flow = new FloodlightOFFlow();
		flow.setSwitchId(SWITCH_ID);
		flow.setName("flow-mod-1");
		flow.setPriority("32768");
		flow.setActive(true);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort("2");
		flow.setMatch(match);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType("output");
		action.setValue("3");

		flow.setActions(Arrays.asList(action));
	}
	
	
	@After
	public void disconnect() throws ProtocolException {
		session.disconnect();
	}

	private ProtocolSessionContext generateContext() {
		ProtocolSessionContext context = new ProtocolSessionContext();
		context.addParameter(ProtocolSessionContext.PROTOCOL, FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE);
		context.addParameter(ProtocolSessionContext.PROTOCOL_URI, FLOODLIGHT_URI);
		context.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
		context.addParameter(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, SWITCH_ID);
		return context;
	}
	
	// This test is ignored because it requires having a floodlight controller available at FLOODLIGHT_URI.
	// TODO Remove @Ignore to perform the test.
	@Ignore
	@Test
	public void clientTest() {
		client.addFlow(flow);
		client.deleteFlow(flow.getName());
		
		// Cannot check with asserts because getFlows() method does not work yet
		// Map<String,List<FloodlightOFFlow>> flows = client.getFlows(SWITCH_ID);
		// Assert.assertTrue("getFlows() contains created flow", flows.get(SWITCH_ID).contains(flow));
		
	}

}
