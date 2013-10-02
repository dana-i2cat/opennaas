package org.opennaas.itests.ofertie.ncl;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemTimeout;

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
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
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
public class NCLProvisionerTest {

	private IResource			switch3;
	private IResource			switch4;
	private IResource			switch5;
	private IResource			switch6;
	private IResource			switch7;
	private IResource			switch8;

	private IResource			sdnNetResource;

	private static final String	SWITCH_3_NAME					= "s3";
	private static final String	SWITCH_4_NAME					= "s4";
	private static final String	SWITCH_5_NAME					= "s5";
	private static final String	SWITCH_6_NAME					= "s6";
	private static final String	SWITCH_7_NAME					= "s7";
	private static final String	SWITCH_8_NAME					= "s8";

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
	private static final String	SDN_ACTIONSET_VERSION			= "1.0.0";

	private static final String	SDN_RESOURCE_NAME				= "sdnNetwork";
	private static final String	SDNNETWORK_RESOURCE_TYPE		= "sdnnetwork";

	/* FLOW REQUEST PARAMS */
	private static final String	SRC_IP_ADDRESS					= "192.168.2.10";
	private static final String	DST_IP_ADDRESS					= "192.168.2.11";
	private static final int	SRC_PORT						= 0;
	private static final int	DST_PORT						= 1;
	private static final int	TOS								= 0;
	private static final int	SRC_VLAN_ID						= 22;
	private static final int	DST_VLAN_ID						= 22;
	private static final int	QOS_MIN_DELAY					= 5;
	private static final int	QOS_MAX_DELAY					= 10;
	private static final int	QOS_MIN_JITTER					= 2;
	private static final int	QOS_MAX_JITTER					= 4;
	private static final int	QOS_MIN_BANDWIDTH				= 100;
	private static final int	QOS_MAX_BANDWIDTH				= 1000;
	private static final int	QOS_MIN_PACKET_LOSS				= 0;
	private static final int	QOS_MAX_PACKET_LOSS				= 1;

	/**
	 * Make sure blueprint for specified bundle has finished its initialization
	 */
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)", timeout = 20000)
	private BlueprintContainer	switchBlueprintContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)", timeout = 20000)
	private BlueprintContainer	floodlightDriverBundleContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.sdnnetwork)", timeout = 20000)
	private BlueprintContainer	sdnNetworkBlueprintContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.ofertie.ncl)", timeout = 20000)
	private BlueprintContainer	nclBlueprintContainer;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private INCLProvisioner		provisioner;

	@Configuration
	public static Option[] configuration() {
		return options(
				opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflow-switch", "opennaas-openflow-switch-driver-floodlight", "opennaas-sdn-network",
						"opennaas-ofertie-ncl"),
				includeTestHelper(),
				systemTimeout(1000 * 60 * 10),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void test() throws Exception {
		createSwitches();
		createSDNNetwork();

		FlowRequest flowRequest = generateSampleFlowRequest();
		String flowId = provisioner.allocateFlow(flowRequest);

	}

	private FlowRequest generateSampleFlowRequest() {

		FlowRequest myRequest = new FlowRequest();
		QoSRequirements myQoSRequirements = new QoSRequirements();

		myRequest.setSourceIPAddress(SRC_IP_ADDRESS);
		myRequest.setDestinationIPAddress(DST_IP_ADDRESS);
		myRequest.setSourcePort(SRC_PORT);
		myRequest.setDestinationPort(DST_PORT);
		myRequest.setTos(TOS);
		myRequest.setSourceVlanId(SRC_VLAN_ID);
		myRequest.setDestinationVlanId(DST_VLAN_ID);

		myQoSRequirements.setMinDelay(QOS_MIN_DELAY);
		myQoSRequirements.setMaxDelay(QOS_MAX_DELAY);
		myQoSRequirements.setMinJitter(QOS_MIN_JITTER);
		myQoSRequirements.setMaxJitter(QOS_MAX_JITTER);
		myQoSRequirements.setMinBandwidth(QOS_MIN_BANDWIDTH);
		myQoSRequirements.setMaxBandwidth(QOS_MAX_BANDWIDTH);
		myQoSRequirements.setMinPacketLoss(QOS_MIN_PACKET_LOSS);
		myQoSRequirements.setMaxPacketLoss(QOS_MAX_PACKET_LOSS);

		myRequest.setQoSRequirements(myQoSRequirements);

		return myRequest;
	}

	private void createSwitches() throws ResourceException, ProtocolException {
		createSwitch(switch3, SWITCH_3_ID, SWITCH_3_NAME);
		createSwitch(switch4, SWITCH_4_ID, SWITCH_4_NAME);
		createSwitch(switch5, SWITCH_5_ID, SWITCH_5_NAME);
		createSwitch(switch6, SWITCH_6_ID, SWITCH_6_NAME);
		createSwitch(switch7, SWITCH_7_ID, SWITCH_7_NAME);
		createSwitch(switch8, SWITCH_8_ID, SWITCH_8_NAME);
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

	private void createSwitch(IResource switchResource, String switchId, String switchName) throws ResourceException, ProtocolException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(FLOODLIGHT_ACTIONSET_NAME,
				FLOODLIGHT_ACTIONSET_VERSION, OpenflowForwardingCapability.CAPABILITY_TYPE, CAPABILITY_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFSWITCH_RESOURCE_TYPE,
				RESOURCE_URI, switchName);

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
