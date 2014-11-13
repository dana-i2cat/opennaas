package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSessionFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.FloodlightCountersClientFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.IFloodlightCountersClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.IFloodlightPortsStatisticsClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * Tests for {@link IFloodlightCountersClient} and {@link FloodlightCountersClientFactory}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class FloodlightPortsStatisticsClientTest {

	private Log									log				= LogFactory.getLog(FloodlightPortsStatisticsClientTest.class);

	private static final String					FLOODLIGHT_URI	= "http://dev.ofertie.i2cat.net:8080";
	private static final String					SWITCH_ID		= "00:00:00:00:00:00:00:03";
	private static final String					SESSION_ID		= "0001";

	private ProtocolSessionContext				context;
	private FloodlightProtocolSession			session;
	private IFloodlightPortsStatisticsClient	client;

	@Before
	public void initSessionAndClient() throws ProtocolException {
		context = generateContext();
		session = (FloodlightProtocolSession) new FloodlightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
		session.connect();
		client = session.getFloodlightPortsStatisticsClientForUse();
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
	public void clientTest() throws ProtocolException, Exception {
		// get all switches statistics
		SwitchStatisticsMap switchStatisticsMap = client.getPortsStatisticsForAllSwitches();

		log.info("All switches statistics:");
		Iterator<Entry<String, Map<Integer, PortStatistics>>> it = switchStatisticsMap.getSwitchStatisticsMap().entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<Integer, PortStatistics>> entry = it.next();
			log.info("Swtich: " + entry.getKey());
			Map<Integer, PortStatistics> portsStatisticsMap = entry.getValue();
			printSwitchStatisticsMap(portsStatisticsMap);
		}

		// get switch 00:00:00:00:00:00:00:03 statistics
		switchStatisticsMap = client.getPortsStatistics("00:00:00:00:00:00:00:03");

		log.info("Switch statistics:");
		it = switchStatisticsMap.getSwitchStatisticsMap().entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<Integer, PortStatistics>> entry = it.next();
			log.info("Swtich: " + entry.getKey());
			Map<Integer, PortStatistics> portsStatisticsMap = entry.getValue();
			printSwitchStatisticsMap(portsStatisticsMap);
		}
	}

	private void printSwitchStatisticsMap(Map<Integer, PortStatistics> portsStatisticsMap) {
		Iterator<Entry<Integer, PortStatistics>> it = portsStatisticsMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, PortStatistics> e = it.next();
			printPortStatistics(e.getValue());
		}
	}

	private void printPortStatistics(PortStatistics pe) {
		log.info("\tStatistics for port " + pe.getPort());
		log.info("\t\tcollisions: " + pe.getCollisions());
		log.info("\t\treceiveBytes: " + pe.getReceiveBytes());
		log.info("\t\treceiveCRCErrors: " + pe.getReceiveCRCErrors());
		log.info("\t\treceiveDropped: " + pe.getReceiveDropped());
		log.info("\t\treceiveErrors: " + pe.getReceiveErrors());
		log.info("\t\treceiveFrameErrors: " + pe.getReceiveFrameErrors());
		log.info("\t\treceiveOverrunErrors: " + pe.getReceiveOverrunErrors());
		log.info("\t\treceivePackets: " + pe.getReceivePackets());
		log.info("\t\ttransmitBytes: " + pe.getTransmitBytes());
		log.info("\t\ttransmitDropped: " + pe.getTransmitDropped());
		log.info("\t\ttransmitErrors: " + pe.getTransmitErrors());
		log.info("\t\ttransmitPackets: " + pe.getTransmitPackets());
	}
}
