package org.opennaas.itests.router.ip;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.itests.router.TestsConstants;
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
public class IPCapabilityIntegrationTest
{
	private final static Log	log					= LogFactory.getLog(IPCapabilityIntegrationTest.class);

	private final static String	RESOURCE_INFO_NAME	= "IPv4 test";

	protected ICapability		iIPCapability;

	protected IResource			routerResource;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)")
	private BlueprintContainer	netconfService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerRepoService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void startResource() throws ResourceException, ProtocolException {
		/* initialize model */
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ipCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.IP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ipCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		addSessionContext(routerResource.getResourceIdentifier().getId());

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	@Before
	public void initBundles() throws ResourceException, ProtocolException {

		clearRepository();
		log.info("INFO: Initialized!");
		startResource();

	}

	@Test
	public void testSetIPv4() throws ProtocolException, ResourceException {

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(getIPInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIPv4(getLogicalPort(), getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getIPInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
		// queueCapability.execute();
	}

	@Test
	public void testSetInterfaceDescription() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(getIPInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setInterfaceDescription(getLogicalPort());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getIPInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	private LogicalPort getLogicalPort() {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		return logicalPort;
	}

	/**
	 * @return
	 */
	private IPProtocolEndpoint getIPProtocolEndPoint() {
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		ipProtocolEndpoint.setIPv4Address("192.168.0.1");
		ipProtocolEndpoint.setSubnetMask("255.255.255.0");
		return ipProtocolEndpoint;
	}

	protected IProtocolSessionManager addSessionContext(String resourceId) throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, TestsConstants.RESOURCE_URI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	protected void clearRepository() throws ResourceException {
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

	protected Information getIPInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}

}
