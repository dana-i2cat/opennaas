package org.opennaas.itests.openflowswitch;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenflowForwardingCapabilityIntegrationTest {

	private final static Log				log					= LogFactory.getLog(OpenflowForwardingCapabilityIntegrationTest.class);

	private static final String				SWITCH_ID			= "00:00:00:00:00:00:00:01";

	private static final String				ACTIONSET_NAME		= "floodlight";
	private static final String				ACTIONSET_VERSION	= "0.90";
	private static final String				CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String				RESOURCE_TYPE		= "openflowswitch";
	private static final String				RESOURCE_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String				RESOURCE_INFO_NAME	= "ofswitch";

	private static final String				PROTOCOL			= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private static final String				SWITCH_ID_NAME		= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private IOpenflowForwardingCapability	ofForwardingCapability;
	private IResource						ofSwitchResource;

	@Inject
	private IResourceManager				resourceManager;

	@Inject
	private IProtocolManager				protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)")
	private BlueprintContainer				floodlightDriverBundleContainer;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflow-switch", "opennaas-openflow-switch-driver-floodlight"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initBundle() throws ResourceException, ProtocolException {

		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	@Test
	public void myTest() {

	}

	public void startResource() throws ResourceException, ProtocolException {
		/* initialize model */
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				RESOURCE_URI,
				RESOURCE_INFO_NAME);

		ofSwitchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, SWITCH_ID);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, ofSwitchResource.getResourceIdentifier().getId(), RESOURCE_URI,
				PROTOCOL, sessionParameters);

		// Start resource
		resourceManager.startResource(ofSwitchResource.getResourceIdentifier());

	}

}