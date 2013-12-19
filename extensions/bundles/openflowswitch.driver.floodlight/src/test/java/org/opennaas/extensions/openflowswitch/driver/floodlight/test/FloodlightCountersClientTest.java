package org.opennaas.extensions.openflowswitch.driver.floodlight.test;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSessionFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.FloodlightCountersClientFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.IFloodlightCountersClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;

/**
 * Tests for {@link IFloodlightCountersClient} and {@link FloodlightCountersClientFactory}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class FloodlightCountersClientTest {

	private Log							log				= LogFactory.getLog(FloodlightCountersClientTest.class);

	private static final String			FLOODLIGHT_URI	= "http://dev.ofertie.i2cat.net:8080";
	private static final String			SWITCH_ID		= "00:00:00:00:00:00:00:03";
	private static final String			SESSION_ID		= "0001";

	private ProtocolSessionContext		context;
	private FloodlightProtocolSession	session;
	private IFloodlightCountersClient	client;

	@Before
	public void initSessionAndClient() throws ProtocolException {
		context = generateContext();
		session = (FloodlightProtocolSession) new FloodlightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
		session.connect();
		client = session.getFloodlightCountersClientForUse();
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
		// get counters
		CountersMap countersMap = client.getAllCountersForAllSwitches();

		log.info("Counters:");
		Iterator<Entry<String, Long>> it = countersMap.getCountersMap().entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Long> entry = it.next();
			log.info(entry.getKey() + ":" + entry.getValue());
		}
	}
}
