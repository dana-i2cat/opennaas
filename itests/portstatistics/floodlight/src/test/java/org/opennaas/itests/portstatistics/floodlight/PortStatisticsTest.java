package org.opennaas.itests.portstatistics.floodlight;

/*
 * #%L
 * OpenNaaS :: iTests :: Monitoring :: Floodlight
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

import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.IPortStatisticsCapability;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatisticsCapability;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.opennaas.itests.helpers.server.HTTPRequest;
import org.opennaas.itests.helpers.server.HTTPResponse;
import org.opennaas.itests.helpers.server.HTTPServerBehaviour;
import org.opennaas.itests.helpers.server.MockHTTPServerTest;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PortStatisticsTest extends MockHTTPServerTest {

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	protected BundleContext				context;

	private final static String			SERVER_URL			= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL	= "/wm/core";

	private final static String			SWITCH_ID			= "00:00:00:00:00:00:00:03";

	private final static String			GET_ALL_PORTS_URL	= SERVLET_CONTEXT_URL + "/switch/" + SWITCH_ID + "/port/json";

	private static final String			PROTOCOL			= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private final static String			RESOURCE_INFO_NAME	= "OpenflowSwitch";
	private static final String			ACTIONSET_NAME		= "floodlight";
	private static final String			ACTIONSET_VERSION	= "0.90";
	private static final String			MOCK_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String			RESOURCE_TYPE		= "openflowswitch";

	private static final String			SWITCH_ID_NAME		= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private final static Log			log					= LogFactory.getLog(PortStatisticsTest.class);

	private IResource					ofSwitchResource;
	private WSEndpointListenerHandler	listenerHandler;

	@Override
	protected void prepareBehaviours() throws Exception {

		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		List<String> omittedFields = new ArrayList<String>();

		HTTPRequest reqPortStats = new HTTPRequest(GET_ALL_PORTS_URL, HttpMethod.GET, MediaType.APPLICATION_JSON, null, omittedFields);
		HTTPResponse respPortStats = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON, IOUtils.toString(this.getClass()
				.getResourceAsStream("/portStatistics.json")), "");
		HTTPServerBehaviour behaviourPortStats = new HTTPServerBehaviour(reqPortStats, respPortStats, false);

		desiredBehaviours.add(behaviourPortStats);

	}

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-openflowswitch", "opennaas-openflowswitch-driver-floodlight", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void initTestScenario() throws Exception {

		log.info("Creating initial scenario.");

		prepareBehaviours();

		startServer(SERVLET_CONTEXT_URL);
		startResource(SERVER_URL, SWITCH_ID);

		log.info("Test initialized.");
	}

	@After
	public void shutDownTestScenario() throws Exception {

		log.info("Shutting down test scenario.");

		stopResource();
		stopServer();

		log.info("Test finished.");

	}

	@Test
	public void getPortStatsTest() throws ResourceException {

		IPortStatisticsCapability monitoringCapab = (IPortStatisticsCapability) getCapability(IPortStatisticsCapability.class);
		SwitchPortStatistics portStats = monitoringCapab.getPortStatistics();

		Assert.assertNotNull("Capability should return statistics.", portStats);

		Map<String, PortStatistics> statsMap = portStats.getStatistics();
		Assert.assertNotNull("SwitchPortStatistics should contain statistics", statsMap);
		Assert.assertEquals("Switch returns statistics of 3 ports.", 3, statsMap.size());

		Assert.assertTrue("Switch contains statistics for port 1.", statsMap.keySet().contains("1"));
		Assert.assertTrue("Switch contains statistics for port 2.", statsMap.keySet().contains("2"));
		Assert.assertTrue("Switch contains statistics for port 3.", statsMap.keySet().contains("3"));

		PortStatistics stats1 = statsMap.get("1");
		Assert.assertNotNull("Statistics of port 1 should contain information.", stats1);
		Assert.assertTrue("Statistics of port 1 should contain port 1.", stats1.getPort() == 1);
		Assert.assertTrue("Port 1 should have received 8329 bytes.", stats1.getReceiveBytes() == 8329);
		Assert.assertTrue("Port 1 should have transmited 4095 bytes.", stats1.getTransmitBytes() == 4095);
		Assert.assertTrue("Port 1 should have received 86 packets.", stats1.getReceivePackets() == 86);
		Assert.assertTrue("Port 1 should have transmited 67 packets.", stats1.getTransmitPackets() == 67);
		Assert.assertTrue("Port 1 should have dropped 0 received packets.", stats1.getReceiveDropped() == 0);
		Assert.assertTrue("Port 1 should have dropped 0 transmited packets.", stats1.getTransmitDropped() == 0);
		Assert.assertTrue("Port 1 should have received 0 errors packets.", stats1.getReceiveErrors() == 0);
		Assert.assertTrue("Port 1 should have transmited 0 errors packets.", stats1.getTransmitErrors() == 0);
		Assert.assertTrue("Port 1 should have received 0 frame errors packets.", stats1.getReceiveFrameErrors() == 0);
		Assert.assertTrue("Port 1 should have received 0 overrun errors packets.", stats1.getReceiveOverrunErrors() == 0);
		Assert.assertTrue("Port 1 should have received 0 CRC errors packets.", stats1.getReceiveCRCErrors() == 0);
		Assert.assertTrue("There should be no collisions in port 1.", stats1.getCollisions() == 0);

		PortStatistics stats2 = statsMap.get("2");
		Assert.assertNotNull("Statistics of port 2 should contain information.", stats2);
		Assert.assertTrue("Statistics of port 2 should contain port 2.", stats2.getPort() == 2);
		Assert.assertTrue("Port 2 should have received 8315 bytes.", stats2.getReceiveBytes() == 8315);
		Assert.assertTrue("Port 2 should have transmited 4233 bytes.", stats2.getTransmitBytes() == 4233);
		Assert.assertTrue("Port 2 should have received 86 packets.", stats2.getReceivePackets() == 86);
		Assert.assertTrue("Port 2 should have transmited 69 packets.", stats2.getTransmitPackets() == 69);
		Assert.assertTrue("Port 2 should have dropped 0 received packets.", stats2.getReceiveDropped() == 0);
		Assert.assertTrue("Port 2 should have dropped 0 transmited packets.", stats2.getTransmitDropped() == 0);
		Assert.assertTrue("Port 2 should have received 0 errors packets.", stats2.getReceiveErrors() == 0);
		Assert.assertTrue("Port 2 should have transmited 0 errors packets.", stats2.getTransmitErrors() == 0);
		Assert.assertTrue("Port 2 should have received 0 frame errors packets.", stats2.getReceiveFrameErrors() == 0);
		Assert.assertTrue("Port 2 should have received 0 overrun errors packets.", stats2.getReceiveOverrunErrors() == 0);
		Assert.assertTrue("Port 2 should have received 0 CRC errors packets.", stats2.getReceiveCRCErrors() == 0);
		Assert.assertTrue("There should be no collisions in port 1.", stats2.getCollisions() == 0);

		PortStatistics stats3 = statsMap.get("3");
		Assert.assertNotNull("Statistics of port 3 should contain information.", stats3);
		Assert.assertTrue("Statistics of port 3 should contain port 3.", stats3.getPort() == 3);
		Assert.assertTrue("Port 3 should have received 8701 bytes.", stats3.getReceiveBytes() == 8701);
		Assert.assertTrue("Port 3 should have transmited 3965 bytes.", stats3.getTransmitBytes() == 3965);
		Assert.assertTrue("Port 3 should have received 92 packets.", stats3.getReceivePackets() == 92);
		Assert.assertTrue("Port 3 should have transmited 65 packets.", stats3.getTransmitPackets() == 65);
		Assert.assertTrue("Port 3 should have dropped 0 received packets.", stats3.getReceiveDropped() == 0);
		Assert.assertTrue("Port 3 should have dropped 0 transmited packets.", stats3.getTransmitDropped() == 0);
		Assert.assertTrue("Port 3 should have received 0 errors packets.", stats3.getReceiveErrors() == 0);
		Assert.assertTrue("Port 3 should have transmited 0 errors packets.", stats3.getTransmitErrors() == 0);
		Assert.assertTrue("Port 3 should have received 0 frame errors packets.", stats3.getReceiveFrameErrors() == 0);
		Assert.assertTrue("Port 3 should have received 0 overrun errors packets.", stats3.getReceiveOverrunErrors() == 0);
		Assert.assertTrue("Port 3 should have received 0 CRC errors packets.", stats3.getReceiveCRCErrors() == 0);
		Assert.assertTrue("There should be no collisions in port 1.", stats3.getCollisions() == 0);

	}

	private void startResource(String serverURL, String switchId) throws ResourceException, ProtocolException, InterruptedException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor monitoringDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				PortStatisticsCapability.CAPABILITY_TYPE,
				MOCK_URI);
		lCapabilityDescriptors.add(monitoringDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				MOCK_URI, RESOURCE_INFO_NAME);

		ofSwitchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, ofSwitchResource.getResourceIdentifier().getId(), serverURL,
				PROTOCOL, sessionParameters);

		// Start resource
		listenerHandler = new WSEndpointListenerHandler();
		listenerHandler.registerWSEndpointListener(context, IPortStatisticsCapability.class);
		resourceManager.startResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBePublished();

	}

	private void stopResource() throws ResourceException, InterruptedException {
		resourceManager.stopResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBeUnpublished();
		resourceManager.removeResource(ofSwitchResource.getResourceIdentifier());
	}

	private ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		ICapability capab = ofSwitchResource.getCapabilityByInterface(clazz);
		Assert.assertNotNull(capab);
		return capab;
	}

}
