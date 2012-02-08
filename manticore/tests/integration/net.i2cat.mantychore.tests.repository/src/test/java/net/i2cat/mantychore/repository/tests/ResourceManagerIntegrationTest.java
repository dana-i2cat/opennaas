package net.i2cat.mantychore.repository.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class ResourceManagerIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log					log				= LogFactory
														.getLog(ResourceManagerIntegrationTest.class);

	IResourceManager			rm;

	@Inject
	BundleContext				bundleContext	= null;

	private IResourceRepository	resourceRepo;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")

//		 , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
		);
		return options;
	}

	@Before
	public void initBundles() throws ResourceException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		rm = getOsgiService(IResourceManager.class, 50000);
		resourceRepo = getOsgiService(IResourceRepository.class, "type=router", 50000);
		log.info("INFO: Initialized!");

	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	public static ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}

	@Test
	public void resourceWorkflow() {
		createResource();
		startResource();
		stopResource();
		removeResource();
		createResourcetwo();
	}

	public void createResource() {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor descriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		try {
			IResource resource = rm.createResource(descriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());

			Assert.assertEquals("junosm20", rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());

			Assert.assertNotNull(resourceRepo);
			List<IResource> resources1 = resourceRepo.listResources();
			Assert.assertFalse(resources1.isEmpty());

		} catch (ResourceException e) {

			Assert.fail();
		} catch (ProtocolException e) {

			Assert.fail();
		}

	}

	public void startResource() {
		try {
			Assert.assertNotNull(resourceRepo);
			List<IResource> resources = resourceRepo.listResources();
			Assert.assertFalse(resources.isEmpty());
			List<IResource> resources1 = rm.listResourcesByType("router");
			Assert.assertFalse(resources1.isEmpty());

			IResource resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);
			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

			rm.startResource(rm.getIdentifierFromResourceName("router", "junosm20"));

			resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);

			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.ACTIVE, resource.getState());

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void stopResource() {
		try {
			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());

			IResource resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);
			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.ACTIVE, resource.getState());

			rm.stopResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);

			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void removeResource() {
		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());
		try {
			rm.removeResource(rm.getIdentifierFromResourceName("router", "junosm20"));

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}

	}

	public void createResourcetwo() {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor descriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		try {
			IResource resource = rm.createResource(descriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());

			Assert.assertEquals("junosm20", rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
			Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());

		} catch (ResourceException e) {

			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			Assert.fail(e.getMessage());
		}

	}
}
