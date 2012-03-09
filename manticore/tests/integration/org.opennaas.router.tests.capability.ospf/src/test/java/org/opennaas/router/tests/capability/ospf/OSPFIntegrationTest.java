/**
 * 
 */
package org.opennaas.router.tests.capability.ospf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

/**
 * @author Jordi
 */
@RunWith(JUnit4TestRunner.class)
public class OSPFIntegrationTest extends AbstractIntegrationTest {

	protected static final String			ACTION_NAME				= "junos";
	protected static final String			CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	protected static final String			QUEUE_CAPABILIY_TYPE	= "queue";
	protected static final String			OSPF_CAPABILIY_TYPE		= "ospf";
	protected static final String			OSPF_CAPABILIY_VERSION	= "10.10";
	protected static final String			RESOURCE_TYPE			= "router";
	protected static final String			RESOURCE_INFO_NAME		= "OSPF Test";
	protected static final String			RESOURCE_URI			= "mock://user:pass@host.net:2212/mocksubsystem";

	protected static ProtocolSessionManager	protocolSessionManager;
	protected IResourceManager				resourceManager;
	protected IProtocolManager				protocolManager;
	@Inject
	protected BundleContext					bundleContext;
	protected ICapability					iOSPFCapability;
	protected IResource						routerResource;
	private static Log						log						= LogFactory
																			.getLog(OSPFIntegrationTest.class);

	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	/**
	 * Initialize the configuration
	 * 
	 * @return
	 */
	@Configuration
	public static Option[] configure() {
		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	@Before
	public void initBundles() throws ResourceException {
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		// Get Resource Manager
		resourceManager = getOsgiService(IResourceManager.class, 50000);

		// Get the protocol session manager
		protocolManager = getOsgiService(IProtocolManager.class, 50000);

		clearRepository();

		log.info("INFO: Initialized!");
	}

	/**
	 * Start router resource with 2 capabilities -> ospf & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add OSPF Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ospfCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, OSPF_CAPABILIY_VERSION,
				OSPF_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		addSessionContext(routerResource.getResourceIdentifier().getId());

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	/**
	 * Stop and remove the router resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(routerResource.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(routerResource.getResourceIdentifier());
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	protected void clearRepository() {
		log.info("Clearing resource repo");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			try {
				if (resource.getState().equals(State.ACTIVE)) {
					resourceManager.stopResource(resource.getResourceIdentifier());
				}
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				Assert.fail(e.getLocalizedMessage());
			}
		}

		log.info("Resource repo cleared!");
	}

	/**
	 * If not exists the protocol session manager, it's created and add the session context
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	protected IProtocolSessionManager addSessionContext(String resourceId) throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, RESOURCE_URI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

	protected Information getOSPFInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}
}
