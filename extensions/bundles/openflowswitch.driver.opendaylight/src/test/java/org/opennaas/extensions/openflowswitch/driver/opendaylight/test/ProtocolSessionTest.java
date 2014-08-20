package org.opennaas.extensions.openflowswitch.driver.opendaylight.test;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: OpenDaylight
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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.OpenDaylightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.OpenDaylightProtocolSessionFactory;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class ProtocolSessionTest {

    private static final String FLOODLIGHT_URI = "http://localhost:8080";
    private static final String SWITCH_ID = "00:00:00:00:00:00:00:03";
    private static final String SESSION_ID = "0001";
    private static final String name = "flowTest1";
    ProtocolSessionContext context;
    OpenDaylightProtocolSession session;
    IOpenDaylightStaticFlowPusherClient client;

    OpenDaylightOFFlow flow;

    @Before
    public void initSessionAndClient() throws ProtocolException {
        context = generateContext();
        session = (OpenDaylightProtocolSession) new OpenDaylightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
        session.connect();
        client = session.getOpenDaylightClientForUse();
    }

    @Before
    public void initFlow() throws ProtocolException {

        flow = new OpenDaylightOFFlow();
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
        context.addParameter(ProtocolSessionContext.PROTOCOL, OpenDaylightProtocolSession.OPENDAYLIGHT_PROTOCOL_TYPE);
        context.addParameter(ProtocolSessionContext.PROTOCOL_URI, FLOODLIGHT_URI);
        context.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
        context.addParameter(OpenDaylightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, SWITCH_ID);
        return context;
    }

	// This test is ignored because it requires having a floodlight controller available at FLOODLIGHT_URI.
    // TODO Remove @Ignore to perform the test.
    @Test
    @Ignore
    public void clientTest() throws ProtocolException, Exception {
        // add a flow
        client.addFlow(flow, SWITCH_ID, name);

		// get flows from switch
/*		List<FloodlightOFFlow> flows = client.getFlows(SWITCH_ID);
         Assert.assertEquals("Flows size must be 1", 1, flows.size());
         Assert.assertEquals("getFlows() must contain the created flow", flows.get(0).getName(), flow.getName());
         // Assert.assertEquals("getFlows() must contain the created flow action", flow.getActions().get(0).getType(),
         // flows.get(0).getActions().get(0).getType());
         Assert.assertEquals("getFlows() must contain the created flow action", flow.getActions().get(0).getValue(),
         flows.get(0).getActions().get(0).getValue());

         // delete flow
         client.deleteFlow(flow);

         // get flows from switch again
         flows = client.getFlows(SWITCH_ID);
         Assert.assertEquals("Flows size must be 0", 0, flows.size());
         */
    }
}
