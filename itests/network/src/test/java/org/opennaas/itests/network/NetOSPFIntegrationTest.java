package org.opennaas.itests.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
public class NetOSPFIntegrationTest {

	protected static final String	ACTION_NAME				= null;
	protected static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	protected static final String	QUEUE_CAPABILIY_TYPE	= "netqueue";
	protected static final String	OSPF_CAPABILIY_TYPE		= "netospf";
	protected static final String	BASIC_CAPABILIY_TYPE	= "basicNetwork";
	protected static final String	CAPABILIY_VERSION		= null;
	protected static final String	RESOURCE_TYPE			= "network";
	protected static final String	RESOURCE_INFO_NAME		= "OSPF Test";
	protected static final String	RESOURCE_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final Log		log						= LogFactory
																	.getLog(NetOSPFIntegrationTest.class);

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.network.repository)")
	private BlueprintContainer		networkRepoService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerRepoService;

	private IResource				networkResource;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-network, opennaas-router"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	private void clearRepository() throws ResourceException {
		log.info("Clearing resource repo");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		log.info("Resource repo cleared!");
	}

	/**
	 * Start network resource with queue and ospf capability
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void startResource() throws ResourceException, ProtocolException {
		// Add OSPF Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor basicCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION, BASIC_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(basicCapabilityDescriptor);

		CapabilityDescriptor ospfCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION, OSPF_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfCapabilityDescriptor);

		// // Add Queue Capability Descriptor
		// CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION,
		// QUEUE_CAPABILIY_TYPE,
		// CAPABILITY_URI);
		// lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Network Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		networkResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(networkResource.getResourceIdentifier());

	}

	/**
	 * Test to check if capability is available from OSGi.
	 */
	@Test
	public void isCapabilityAccessibleFromResourceTest()
			throws ResourceException, ProtocolException
	{
		clearRepository();
		startResource();
		assertEquals(2, networkResource.getCapabilities().size());

		stopResource();
	}

	@Test
	public void testActivateOSPF() throws ResourceException, ProtocolException {
		clearRepository();
		startResource();

		List<IResource> routers = addRoutersToResourceManager();
		addRoutersToNetwork(routers);

		callActivateOSPF();

		checkRoutersQueueContainsOSPFConfigActions(routers);
	}

	private void checkRoutersQueueContainsOSPFConfigActions(List<IResource> routers) {

		Information inf = new Information();
		inf.setType("queue");

		for (IResource router : routers) {

			IQueueManagerCapability queueCapab = (IQueueManagerCapability) router.getCapability(inf);
			assertEquals(4, queueCapab.getActions().size());

			assertEquals(ActionConstants.OSPF_CONFIGURE, queueCapab.getActions().get(0).getActionID());
			assertEquals(ActionConstants.OSPF_CONFIGURE_AREA, queueCapab.getActions().get(1).getActionID());
			assertEquals(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, queueCapab.getActions().get(2).getActionID());
			assertEquals(ActionConstants.OSPF_ACTIVATE + "/" + ActionConstants.OSPF_DEACTIVATE, queueCapab.getActions().get(3).getActionID());
		}
	}

	private void callActivateOSPF() throws ResourceException {
		Information inf = new Information();
		inf.setType(OSPF_CAPABILIY_TYPE);

		INetOSPFCapability netOSPFCapability = (INetOSPFCapability) networkResource.getCapabilityByInterface(INetOSPFCapability.class);

		assertNotNull(netOSPFCapability);
		netOSPFCapability.activateOSPF();
	}

	private void addRoutersToNetwork(List<IResource> routers) throws CapabilityException {
		Information inf = new Information();
		inf.setType(BASIC_CAPABILIY_TYPE);

		INetworkBasicCapability topologyCapability = (INetworkBasicCapability) networkResource.getCapability(inf);
		assertNotNull(topologyCapability);

		for (IResource router : routers) {
			topologyCapability.addResource(router);
		}
	}

	private List<IResource> addRoutersToResourceManager() throws ResourceException, ProtocolException {

		List<IResource> resources = new ArrayList<IResource>(2);
		resources.add(startRouterResource("router1"));
		resources.add(startRouterResource("router2"));

		return resources;

	}

	private IResource startRouterResource(String routerName) throws ResourceException, ProtocolException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		CapabilityDescriptor ospfCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor("junos", "10.10",
				"ospf", CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, "router", RESOURCE_URI,
				routerName);

		IResource routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		addSessionContext(routerResource.getResourceIdentifier().getId());

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());

		return routerResource;
	}

	/**
	 * Stop and remove the network resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void stopResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(networkResource.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(networkResource.getResourceIdentifier());
	}

	/**
	 * If not exists the protocol session manager, it's created and add the session context
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private IProtocolSessionManager addSessionContext(String resourceId) throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, RESOURCE_URI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

}
