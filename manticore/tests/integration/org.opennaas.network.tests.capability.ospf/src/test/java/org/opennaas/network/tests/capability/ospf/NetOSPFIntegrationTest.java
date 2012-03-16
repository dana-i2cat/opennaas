package org.opennaas.network.tests.capability.ospf;

import static net.i2cat.nexus.tests.OpennaasExamOptions.includeFeatures;
import static net.i2cat.nexus.tests.OpennaasExamOptions.includeTestHelper;
import static net.i2cat.nexus.tests.OpennaasExamOptions.noConsole;
import static net.i2cat.nexus.tests.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class NetOSPFIntegrationTest {

	protected static final String	ACTION_NAME				= null;
	protected static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	protected static final String	QUEUE_CAPABILIY_TYPE	= "netqueue";
	protected static final String	OSPF_CAPABILIY_TYPE		= "netospf";
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

	private IResource				networkResource;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-network, "),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
		// , new VMOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"));
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

		CapabilityDescriptor ospfCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION, OSPF_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, CAPABILIY_VERSION, QUEUE_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

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
		Assert.assertTrue(networkResource.getCapabilities().size() == 2);

		stopResource();
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * Stop and remove the network resourc e
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

}
