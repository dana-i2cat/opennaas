package org.opennaas.itests.router.ip;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.itests.helpers.InitializerTestHelper;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.itests.router.helpers.CheckParametersHelper;
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
public class SetInterfaceDescriptionActionTest
{
	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.ip)")
	private BlueprintContainer	ipService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.chassis)")
	private BlueprintContainer	chassisService;

	private boolean				isMock;
	private ResourceDescriptor	resourceDescriptor;
	private IResource			resource;
	private String				deviceID;
	private String				type;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	public SetInterfaceDescriptionActionTest() {
		this.type = "router";
		this.deviceID = "junos";
	}

	/**
	 * Prepare the resource to do the test
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 * 
	 */
	@Before
	public void setUp() throws ResourceException, ProtocolException {

		// Reset repository
		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());

			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");
		resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, type, capabilities);
		resource = resourceManager.createResource(resourceDescriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	/**
	 * Reset info for next tests
	 * 
	 * @throws ResourceException
	 * 
	 */
	@After
	public void tearDown() throws ResourceException {

		// Reset repository
		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());

			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	@Test
	public void configureSubInterfaceDescriptionTest() throws CapabilityException, ProtocolException {
		setSubInterfaceDescriptionTest();
		setInterfaceDescriptionTest();
	}

	/**
	 * Put related task
	 * 
	 * @throws ProtocolException
	 * */
	public void setSubInterfaceDescriptionTest() throws CapabilityException, ProtocolException {
		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilities().get(posChassis);

		int posIpv4 = InitializerTestHelper.containsCapability(resource, "ipv4");
		if (posIpv4 == -1)
			Assert.fail("Could not get ipv4 capability for given resource");
		IIPCapability ipCapability = (IIPCapability) resource.getCapabilities().get(posIpv4);

		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName("fe-0/3/2");
		ethernetPort.setPortNumber(2);
		ethernetPort.setDescription("Description for the setSubInterfaceDescription test");

		chassisCapability.createSubInterface(ethernetPort);
		ipCapability.setInterfaceDescription(ethernetPort);

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		QueueResponse response = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(response.isOk());

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsSubInterface((ComputerSystem) resource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

		String desc = ((EthernetPort) ((ComputerSystem) resource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(ethernetPort.getDescription()));

		// delete created sub interface
		chassisCapability.deleteSubInterface(ethernetPort);
		response = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(response.isOk());
	}

	/**
	 * Test the possibility to configure subinterfaces with an encapsulation
	 * 
	 * @throws ProtocolException
	 * */
	public void setInterfaceDescriptionTest() throws CapabilityException, ProtocolException {
		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilities().get(posChassis);

		int posIpv4 = InitializerTestHelper.containsCapability(resource, "ipv4");
		if (posIpv4 == -1)
			Assert.fail("Could not get ipv4 capability for given resource");
		IIPCapability ipCapability = (IIPCapability) resource.getCapabilities().get(posIpv4);

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");

		ipCapability.setInterfaceDescription(logicalPort);

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		QueueResponse response = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(response.isOk());

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) resource.getModel(), logicalPort);
		Assert.assertTrue(pos != -1);

		String desc = ((LogicalPort) ((ComputerSystem) resource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(logicalPort.getDescription()));
	}

	/**
	 * TODO This class has to be moved to the share helper
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {
		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();
		protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		isMock = false;
		if (context.getSessionParameters().containsKey(ProtocolSessionContext.PROTOCOL_URI)) {
			if (((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI)).startsWith("mock")) {
				isMock = true;
			}
		}
	}

	private QueueResponse executeQueue(IResource resource) throws CapabilityException, ProtocolException {
		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		QueueResponse response = null;
		response = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(response.isOk());
		return response;
	}
}
