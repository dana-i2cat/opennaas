package org.opennaas.itests.router;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
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
public class ResourceManagerIntegrationTest
{
	private final static Log	log	= LogFactory.getLog(ResourceManagerIntegrationTest.class);

	@Inject
	private IResourceManager	rm;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("type=router")
	private IResourceRepository	resourceRepo;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	repositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.chassis)")
	private BlueprintContainer	chassisService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.ip)")
	private BlueprintContainer	ipService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
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
		return protocolSessionContext;

	}

	@Test
	public void resourceWorkflow()
			throws InterruptedException, ResourceException, ProtocolException
	{
		createResource();
		startResource();
		stopResource();
		removeResource();
		createResourcetwo();
	}

	public void createResource() throws ResourceException, ProtocolException {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor descriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		IResource resource = rm.createResource(descriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());

		Assert.assertEquals("junosm20", rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());

		Assert.assertNotNull(resourceRepo);
		List<IResource> resources1 = resourceRepo.listResources();
		Assert.assertFalse(resources1.isEmpty());
	}

	public void startResource() throws ResourceException {
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
	}

	public void stopResource() throws ResourceException, InterruptedException {
		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());

		IResource resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
		Assert.assertNotNull(resource);
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.ACTIVE, resource.getState());

		rm.stopResource(rm.getIdentifierFromResourceName("router", "junosm20"));
		resource = rm.getResource(rm.getIdentifierFromResourceName("router", "junosm20"));
		Assert.assertNotNull(resource);

		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());
	}

	public void removeResource() throws ResourceException {
		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());
		rm.removeResource(rm.getIdentifierFromResourceName("router", "junosm20"));
	}

	public void createResourcetwo() throws ResourceException, ProtocolException {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor descriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		IResource resource = rm.createResource(descriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());

		Assert.assertEquals("junosm20", rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

		List<IResource> resources = rm.listResourcesByType("router");
		Assert.assertFalse(resources.isEmpty());
	}
}
