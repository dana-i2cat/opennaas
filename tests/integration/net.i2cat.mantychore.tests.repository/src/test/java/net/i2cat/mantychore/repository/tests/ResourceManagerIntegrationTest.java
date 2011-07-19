package net.i2cat.mantychore.repository.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
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

		// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
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
		resourceRepo = getOsgiService(IResourceRepository.class, 50000);
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
			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.INITIALIZED, resource.getState());

			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());

			Assert.assertNotNull(resourceRepo);
			List<IResource> resources1 = resourceRepo.listResources();
			Assert.assertFalse(resources1.isEmpty());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ResourceException e) {

			Assert.fail();
		} catch (ProtocolException e) {

			Assert.fail();
		}

	}

	@Test
	public void startResource() {
		try {
			Assert.assertNotNull(resourceRepo);
			List<IResource> resources = resourceRepo.listResources();
			Assert.assertFalse(resources.isEmpty());
			List<IResource> resources1 = rm.listResourcesByType("router");
			Assert.assertFalse(resources1.isEmpty());

			IResource resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);
			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.INITIALIZED, resource.getState());

			rm.startResource(rm.getIdentifierFromResourceName("router", "junosm20"));

			resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);

			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.ACTIVE, resource.getState());
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

	@Test
	public void stopResource() {
		try {
			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());
			rm.startResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			IResource resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);
			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.ACTIVE, resource.getState());

			rm.stopResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
			Assert.assertNotNull(resource);

			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.INITIALIZED, resource.getState());
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

	@Test
	public void removeResource() throws ResourceException {
		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());
		try {
			rm.removeResource(rm.getIdentifierFromResourceName("router", "junosm20"));

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

	@Test
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
			Assert.assertEquals(net.i2cat.nexus.resources.ILifecycle.State.INITIALIZED, resource.getState());

			List<IResource> resources = rm.listResourcesByType("router");
			Assert.assertFalse(resources.isEmpty());

		} catch (ResourceException e) {

			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			Assert.fail(e.getMessage());
		}

	}
}
