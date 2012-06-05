package org.opennaas.itests.router.chassis;

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
import org.opennaas.extensions.itests.helpers.InitializerTestHelper;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.itests.router.helpers.CheckParametersHelper;
import org.opennaas.itests.router.helpers.ParamCreationHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * These tests check the subinterface configurations
 * 
 * http://jira.i2cat.net:8080/browse/MANTYCHORE-272
 * 
 */

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ConfigureSubInterfaceTest
{
	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	private IProtocolManager	protocolManager;

	private boolean				isMock;
	private ResourceDescriptor	resourceDescriptor;
	private IResource			resource;
	private String				deviceID;
	private String				type;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.ip)")
	private BlueprintContainer	ipService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	public ConfigureSubInterfaceTest() {
		this.type = "router";
		this.deviceID = "junos";
		this.isMock = true;
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

		InitializerTestHelper.removeResources(resourceManager);

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
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
		InitializerTestHelper.removeResources(resourceManager);

		// TODO remove protocol
	}

	@Test
	public void configureSubInterfaceTest() throws ProtocolException {
		/* test to configure a simple subinterface */
		// simpleSubInterfaceConfigurationTest();

		/* test to configure a subinterface */
		// subInterfaceConfigurationTest();

		/* test to configure a gre subinterface */
		GRESubInterfaceConfigurationTest();
	}

	private void GRESubInterfaceConfigurationTest() throws ProtocolException {

		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");

		IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilities().get(posChassis);
		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceGRE();
		try {
			chassisCapability.createSubInterface(ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		try {
			queueCapability.execute();
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) resource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);
	}

	/**
	 * Put related task
	 * 
	 * @throws ProtocolException
	 * */
	public void simpleSubInterfaceConfigurationTest() throws ProtocolException {

		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilities().get(posChassis);
		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEthernet();
		try {
			chassisCapability.createSubInterface(ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		try {
			queueCapability.execute();
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) resource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

	}

	/**
	 * Test the possibility to configure subinterfaces with an encapsulation
	 * 
	 * @throws ProtocolException
	 * */
	public void subInterfaceConfigurationTest() throws ProtocolException {
		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilities().get(posChassis);
		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEtherVLAN();
		try {
			chassisCapability.createSubInterface(ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilities().get(posQueue);
		try {
			queueCapability.execute();
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) resource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

	}

	/**
	 * TODO This class has to be moved to the share helper
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, ResourceHelper.newSessionContextNetconf());
	}
}
