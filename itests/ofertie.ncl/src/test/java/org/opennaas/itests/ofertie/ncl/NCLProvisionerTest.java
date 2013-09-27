package org.opennaas.itests.ofertie.ncl;

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NCLProvisionerTest {

	private IResource			switch3;
	private IResource			switch4;
	private IResource			switch5;
	private IResource			switch6;
	private IResource			switch7;
	private IResource			switch8;

	private IResource			sdnNetResource;

	private static final String	SWITCH_3_ID						= "00:00:00:00:00:00:00:03";
	private static final String	SWITCH_4_ID						= "00:00:00:00:00:00:00:04";
	private static final String	SWITCH_5_ID						= "00:00:00:00:00:00:00:05";
	private static final String	SWITCH_6_ID						= "00:00:00:00:00:00:00:06";
	private static final String	SWITCH_7_ID						= "00:00:00:00:00:00:00:07";
	private static final String	SWITCH_8_ID						= "00:00:00:00:00:00:00:08";

	private static final String	FLOODLIGHT_ACTIONSET_NAME		= "floodlight";
	private static final String	FLOODLIGHT_ACTIONSET_VERSION	= "0.90";
	private static final String	FLOODLIGHT_PROTOCOL				= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;

	private static final String	OFSWITCH_RESOURCE_TYPE			= "openflowswitch";
	private static final String	SWITCH_ID_NAME					= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private static final String	CAPABILITY_URI					= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String	RESOURCE_URI					= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	SDN_ACTIONSET_NAME				= "internal";
	private static final String	SDN_ACTIONSET_VERSION			= "1.0";

	private static final String	SDN_RESOURCE_NAME				= "sdnNetwork";
	private static final String	SDNNETWORK_RESOURCE_TYPE		= "sdnnetwork";

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private INCLProvisioner		provisioner;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-ofertie-ncl", "opennaas-openflow-switch", "opennaas-openflow-switch-driver-floodlight"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void test() throws ResourceException, ProtocolException {
		createSwitches();
		createSDNNetwork();

	}

	private void createSwitches() throws ResourceException, ProtocolException {
		createSwitch(switch3, SWITCH_3_ID);
		createSwitch(switch4, SWITCH_4_ID);
		createSwitch(switch5, SWITCH_5_ID);
		createSwitch(switch6, SWITCH_6_ID);
		createSwitch(switch7, SWITCH_7_ID);
		createSwitch(switch8, SWITCH_8_ID);
	}

	private void createSDNNetwork() throws ResourceException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor provisionCapab = ResourceHelper.newCapabilityDescriptor(SDN_ACTIONSET_NAME,
				SDN_ACTIONSET_VERSION, OFProvisioningNetworkCapability.CAPABILITY_TYPE, CAPABILITY_URI);

		lCapabilityDescriptors.add(provisionCapab);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, SDNNETWORK_RESOURCE_TYPE,
				RESOURCE_URI, SDN_RESOURCE_NAME);

		sdnNetResource = resourceManager.createResource(resourceDescriptor);

		resourceManager.startResource(sdnNetResource.getResourceIdentifier());

	}

	private void createSwitch(IResource switchResource, String switchId) throws ResourceException, ProtocolException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(FLOODLIGHT_ACTIONSET_NAME,
				FLOODLIGHT_ACTIONSET_VERSION, OpenflowForwardingCapability.CAPABILITY_TYPE, CAPABILITY_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFSWITCH_RESOURCE_TYPE,
				RESOURCE_URI, switchId);

		switchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(), RESOURCE_URI,
				FLOODLIGHT_PROTOCOL, sessionParameters);

		// Start resource
		resourceManager.startResource(switchResource.getResourceIdentifier());

		prepareClientForSwitch(switchResource);

	}

	private void prepareClientForSwitch(IResource switchResource) throws ProtocolException {

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(switchResource.getResourceIdentifier().getId());

		FloodlightProtocolSession session = (FloodlightProtocolSession) sessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);

		session.setClientFactory(new FloodlightMockClientFactory());
		IFloodlightStaticFlowPusherClient client = session.getClientFactory().createClient(session.getSessionContext());
		session.setFloodlightClient(client);

	}
}
