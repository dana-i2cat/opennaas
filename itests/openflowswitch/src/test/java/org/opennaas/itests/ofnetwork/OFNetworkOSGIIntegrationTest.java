package org.opennaas.itests.ofnetwork;

/*
 * #%L
 * OpenNaaS :: iTests :: Openflow Switch
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

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.ofnetwork.driver.internal.actionsets.OFNetworkInternalActionsetImplementation;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OFNetworkOSGIIntegrationTest {

	private final static Log			log						= LogFactory.getLog(OFNetworkOSGIIntegrationTest.class);

	private static final String			NET_RESOURCE_TYPE		= "ofnetwork";
	private static final String			NET_RESOURCE_NAME		= "net1";

	private static final String			SWITCH_RESOURCE_TYPE	= "openflowswitch";
	private static final String			SWITCH_RESOURCE_NAME	= "s1";

	private static final String			WS_URI					= "http://localhost:8888/opennaas/" + NET_RESOURCE_TYPE + "/" + NET_RESOURCE_NAME + "/" + OFProvisioningNetworkCapability.CAPABILITY_TYPE;
	private static final String			WS_USERNAME				= "admin";
	private static final String			WS_PASSWORD				= "123456";

	/**
	 * Make sure blueprint for org.opennaas.extensions.ofnetwork bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.ofnetwork)", timeout = 20000)
	private BlueprintContainer			sdnNetworkBlueprintContainer;

	@Inject
	protected BundleContext				context;

	@Inject
	private IProtocolManager			protocolManager;

	@Inject
	private IResourceManager			resourceManager;

	@Inject
	@Filter("(type=ofnetwork)")
	private IResourceRepository			sdnNetworkRepository;

	@Inject
	@Filter("(capability=ofprovisionnet)")
	private ICapabilityFactory			capabilityFactory;

	@Inject
	@Filter("(&(actionset.name=internal)(actionset.capability=ofprovisionnet))")
	private IActionSet					actionset;

	private ResourceDescriptor			sdnResourceDescriptor;
	private ResourceDescriptor			ofswitchResourceDescriptor;

	// WS endpoint listeners
	private WSEndpointListenerHandler	ofNetListener;
	private WSEndpointListenerHandler	switchListener;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				// OpennaasExamOptions.openDebugSocket(),
				includeFeatures("opennaas-ofnetwork", "itests-helpers", "opennaas-openflowswitch-driver-floodlight"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initializeOFNetDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(null, NET_RESOURCE_TYPE, null, NET_RESOURCE_NAME);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(OFNetworkInternalActionsetImplementation.ACTIONSET_ID, "1.0.0",
				OFProvisioningNetworkCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.sdnResourceDescriptor = resourceDescriptor;
	}

	@Before
	public void initializeSwitchDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(null, SWITCH_RESOURCE_TYPE, null, SWITCH_RESOURCE_NAME);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor("floodlight", "0.90",
				OpenflowForwardingCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.ofswitchResourceDescriptor = resourceDescriptor;

	}

	@After
	public void clearRM() throws ResourceException, InterruptedException {
		resourceManager.destroyAllResources();

		if (switchListener != null) {
			switchListener.waitForEndpointToBeUnpublished();
		}
		if (ofNetListener != null) {
			ofNetListener.waitForEndpointToBeUnpublished();
		}

		switchListener = null;
		ofNetListener = null;
	}

	@Test
	public void resourceWorkflowTest() throws Exception {

		IResource resource = resourceManager.createResource(sdnResourceDescriptor);
		Assert.assertEquals(State.INITIALIZED, resource.getState());
		Assert.assertFalse(resourceManager.listResources().isEmpty());

		ofNetListener = new WSEndpointListenerHandler();
		ofNetListener.registerWSEndpointListener(context, IOFProvisioningNetworkCapability.class);
		resourceManager.startResource(resource.getResourceIdentifier());
		ofNetListener.waitForEndpointToBePublished();

		Assert.assertEquals(State.ACTIVE, resource.getState());

		// retrieve capability, will throw exception if unable
		resource.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		resourceManager.stopResource(resource.getResourceIdentifier());
		Assert.assertEquals(State.INITIALIZED, resource.getState());

		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	@Test
	public void ofProvisioningNetworkCapabilityTest() throws Exception {

		IResource sdnResource = resourceManager.createResource(sdnResourceDescriptor);

		ofNetListener = new WSEndpointListenerHandler();
		ofNetListener.registerWSEndpointListener(context, IOFProvisioningNetworkCapability.class);
		resourceManager.startResource(sdnResource.getResourceIdentifier());
		ofNetListener.waitForEndpointToBePublished();

		IResource switchResource = resourceManager.createResource(ofswitchResourceDescriptor);
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, "00:00:00:00:00:00:00:01");

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(),
				"mock://user:pass@host.net:2212/mocksubsystem", "floodlight", sessionParameters);

		switchListener = new WSEndpointListenerHandler();
		switchListener.registerWSEndpointListener(context, IOpenflowForwardingCapability.class);
		resourceManager.startResource(switchResource.getResourceIdentifier());
		switchListener.waitForEndpointToBePublished();

		prepareClient(switchResource);

		IOFProvisioningNetworkCapability capab = (IOFProvisioningNetworkCapability) sdnResource
				.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		ofProvisioningNetworkCapabilityCheck(capab);
	}

	@Test
	public void ofProvisioningNetworkCapabilityWSTest() throws Exception {

		IResource resource = resourceManager.createResource(sdnResourceDescriptor);

		ofNetListener = new WSEndpointListenerHandler();
		ofNetListener.registerWSEndpointListener(context, IOFProvisioningNetworkCapability.class);
		resourceManager.startResource(resource.getResourceIdentifier());
		ofNetListener.waitForEndpointToBePublished();

		IResource switchResource = resourceManager.createResource(ofswitchResourceDescriptor);
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, "00:00:00:00:00:00:00:01");

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(),
				"mock://user:pass@host.net:2212/mocksubsystem", "floodlight", sessionParameters);

		switchListener = new WSEndpointListenerHandler();
		switchListener.registerWSEndpointListener(context, IOpenflowForwardingCapability.class);
		resourceManager.startResource(switchResource.getResourceIdentifier());
		switchListener.waitForEndpointToBePublished();

		prepareClient(switchResource);

		IOFProvisioningNetworkCapability capabClient = InitializerTestHelper.createRestClient(WS_URI, IOFProvisioningNetworkCapability.class, null,
				WS_USERNAME, WS_PASSWORD);

		ofProvisioningNetworkCapabilityCheck(capabClient);
	}

	public void ofProvisioningNetworkCapabilityCheck(IOFProvisioningNetworkCapability capab) throws Exception {

		List<NetOFFlow> flow1 = generateSampleFlows("1", "2");
		List<NetOFFlow> flow2 = generateSampleFlows("2", "1");

		Assert.assertTrue("OF network shouldn't contain any flow.", capab.getAllocatedFlows().isEmpty());
		capab.allocateFlows(flow1);
		Assert.assertTrue("OF network should contain flows in first list", capab.getAllocatedFlows().containsAll(flow1));
		capab.allocateFlows(flow2);
		Assert.assertTrue("OF network should contain flows in second list.", capab.getAllocatedFlows().containsAll(flow2));
		Assert.assertTrue("OF network should contain flows in first list, too.", capab.getAllocatedFlows().containsAll(flow1));

		capab.deallocateFlows(flow1);
		Assert.assertTrue("OF network should already contain flows in second list.", capab.getAllocatedFlows().containsAll(flow2));
		Set<NetOFFlow> currentFlows = capab.getAllocatedFlows();
		for (NetOFFlow past : flow1) {
			Assert.assertFalse("OF network should not contain flows in first list.", currentFlows.contains(past));
		}

		capab.deallocateFlows(flow2);
		currentFlows = capab.getAllocatedFlows();
		for (NetOFFlow past : flow1) {
			Assert.assertFalse("OF network should not contain flows in second list.", currentFlows.contains(past));
		}
		Assert.assertTrue("OF network shouldn't contain any flow.", capab.getAllocatedFlows().isEmpty());

	}

	private List<NetOFFlow> generateSampleFlows(String inputPort, String outputPort) {

		List<NetOFFlow> flows = new ArrayList<NetOFFlow>();

		NetOFFlow flow = new NetOFFlow();
		flow.setPriority("1");
		flow.setActive(true);
		flow.setResourceId("s1");
		flow.setName(flow.getResourceId() + ":" + inputPort + "-" + outputPort);

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);
		flow.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);
		flow.setActions(actions);

		flows.add(flow);
		return flows;
	}

	private void prepareClient(IResource switchResource) throws ProtocolException {

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(switchResource.getResourceIdentifier().getId());

		FloodlightProtocolSession session = (FloodlightProtocolSession) sessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);

		session.setClientFactory(new FloodlightMockClientFactory());
		IFloodlightStaticFlowPusherClient client = session.getClientFactory().createClient(session.getSessionContext());
		session.setFloodlightClient(client);

	}

}
