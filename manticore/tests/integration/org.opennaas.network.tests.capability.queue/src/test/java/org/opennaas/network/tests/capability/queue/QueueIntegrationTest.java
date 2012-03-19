package org.opennaas.network.tests.capability.queue;

/**
 * 
 */
import static net.i2cat.nexus.tests.OpennaasExamOptions.includeFeatures;
import static net.i2cat.nexus.tests.OpennaasExamOptions.includeTestHelper;
import static net.i2cat.nexus.tests.OpennaasExamOptions.noConsole;
import static net.i2cat.nexus.tests.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.nexus.tests.ResourceHelper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.network.capability.queue.QueueCapability;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

/**
 * @author Jordi
 */
@RunWith(JUnit4TestRunner.class)
public class QueueIntegrationTest {
	private static final String	ACTION_NAME			= null;
	private static final String	CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String	CAPABILIY_TYPE		= "netqueue";
	private static final String	CAPABILIY_VERSION	= null;
	private static final String	RESOURCE_TYPE		= "network";
	private static final String	RESOURCE_INFO_NAME	= "Network Queue Test";
	private static final String	RESOURCE_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	@Inject
	private IProtocolManager	protocolManager;
	@Inject
	private IResourceManager	resourceManager;
	private IResource			networkResource;

	/**
	 * Initialize the configuration
	 * 
	 * @return
	 */
	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-bod, opennaas-network, opennaas-netconf"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	/**
	 * Test to check activateOSPF method
	 */
	@Test
	public void executeQueueTest() {
		try {
			startResource();
			QueueCapability queueCapability = (QueueCapability) networkResource.getCapability(getOSPFInformation(CAPABILIY_TYPE));
			queueCapability.execute();
			stopResource();
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Start network resource with 1 capability -> queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void startResource() throws ResourceException, ProtocolException {
		// Add OSPF Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		CapabilityDescriptor queueDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION,
				CAPABILIY_TYPE, CAPABILITY_URI);
		lCapabilityDescriptors.add(queueDescriptor);

		// Create resource
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);
		networkResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		addSessionContext(networkResource.getResourceIdentifier().getId());

		// Start resource
		resourceManager.startResource(networkResource.getResourceIdentifier());
	}

	/**
	 * Stop and remove the router resource
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

	private Information getOSPFInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}
}
