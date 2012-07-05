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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapability;
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;
import org.opennaas.extensions.network.capability.ospf.NetOSPFCapability;
import org.opennaas.extensions.network.capability.queue.IQueueCapability;
import org.opennaas.extensions.network.capability.queue.QueueCapability;
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
	protected static final String	QUEUE_CAPABILIY_TYPE	= QueueCapability.CAPABILITY_TYPE;
	protected static final String	OSPF_CAPABILIY_TYPE		= NetOSPFCapability.CAPABILITY_TYPE;
	protected static final String	BASIC_CAPABILIY_TYPE	= NetworkBasicCapability.CAPABILITY_TYPE;
	protected static final String	CAPABILIY_VERSION		= null;
	protected static final String	RESOURCE_TYPE			= "network";
	protected static final String	RESOURCE_INFO_NAME		= "OSPF Test";
	protected static final String	RESOURCE_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final Log		log						= LogFactory.getLog(NetOSPFIntegrationTest.class);

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

	/*
	 * CONFIGURATION, BEFORE and AFTER
	 */

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-network, opennaas-router", "opennaas-junos"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Start network resource with queue and ospf capability
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	@Before
	public void startResource() throws ResourceException, ProtocolException {

		List<CapabilityDescriptor> capDescriptors = new ArrayList<CapabilityDescriptor>();

		capDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILIY_VERSION,
				BASIC_CAPABILIY_TYPE,
				CAPABILITY_URI));

		capDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILIY_VERSION,
				OSPF_CAPABILIY_TYPE,
				CAPABILITY_URI));

		capDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILIY_VERSION,
				QUEUE_CAPABILIY_TYPE,
				CAPABILITY_URI));

		// Network Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(capDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		networkResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(networkResource.getResourceIdentifier());

	}

	/**
	 * Stop and remove the network resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	@After
	public void stopResource() throws ResourceException, ProtocolException {
		resourceManager.stopResource(networkResource.getResourceIdentifier());
		resourceManager.removeResource(networkResource.getResourceIdentifier());

		// remove remaining if any
		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

	}

	/*
	 * TESTS
	 */

	/**
	 * Test to check if capability is available from OSGi.
	 */
	@Test
	public void isCapabilityAccessibleFromResourceTest()
			throws ResourceException, ProtocolException
	{
		assertEquals(3, networkResource.getCapabilities().size());
	}

	@Test
	public void testActivateOSPF() throws ResourceException, ProtocolException {

		List<IResource> routers = new ArrayList<IResource>(2);
		routers.add(startRouterResource("router1"));
		routers.add(startRouterResource("router2"));

		// Add routers to network
		INetworkBasicCapability basicCapability = (INetworkBasicCapability) networkResource.getCapabilityByInterface(INetworkBasicCapability.class);
		assertNotNull(basicCapability);

		for (IResource router : routers) {
			basicCapability.addResource(router);
		}

		// Activate OSPF
		INetOSPFCapability netOSPFCapability = (INetOSPFCapability) networkResource.getCapabilityByInterface(INetOSPFCapability.class);

		assertNotNull(netOSPFCapability);
		netOSPFCapability.activateOSPF();

		checkRoutersQueueContainsOSPFConfigActions(routers);
	}

	/**
	 * Test to check netqueue method
	 * 
	 * @throws ProtocolException
	 * @throws ResourceException
	 */
	@Test
	public void executeQueueTest() throws ResourceException, ProtocolException {
		Information information = new Information();
		information.setType(QUEUE_CAPABILIY_TYPE);

		QueueCapability queueCapability = (QueueCapability) networkResource.getCapabilityByInterface(IQueueCapability.class);
		queueCapability.execute();
	}

	/*
	 * HELPERS
	 */

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
	 * If not exists the protocol session manager, it's created and add the session context
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private IProtocolSessionManager addSessionContext(String resourceId) throws ProtocolException {
		ProtocolSessionContext psc = new ProtocolSessionContext();
		IProtocolSessionManager psm = protocolManager.getProtocolSessionManager(resourceId);

		psc.addParameter(ProtocolSessionContext.PROTOCOL_URI, RESOURCE_URI);
		psc.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");

		psm.registerContext(psc);

		return psm;
	}

	private void checkRoutersQueueContainsOSPFConfigActions(List<IResource> routers) throws ResourceException {

		for (IResource router : routers) {

			IQueueManagerCapability queueCapab = (IQueueManagerCapability) router.getCapabilityByInterface(IQueueManagerCapability.class);
			assertEquals(4, queueCapab.getActions().size());

			assertEquals(ActionConstants.OSPF_CONFIGURE, queueCapab.getActions().get(0).getActionID());
			assertEquals(ActionConstants.OSPF_CONFIGURE_AREA, queueCapab.getActions().get(1).getActionID());
			assertEquals(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, queueCapab.getActions().get(2).getActionID());
			assertEquals(ActionConstants.OSPF_ACTIVATE + "/" + ActionConstants.OSPF_DEACTIVATE, queueCapab.getActions().get(3).getActionID());
		}
	}

}
