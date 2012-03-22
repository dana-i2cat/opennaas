package interfaces.configure;

import helpers.CheckParametersHelper;
import helpers.ParamCreationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.nexus.tests.InitializerTestHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueConstants;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

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
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.ip)")
    private BlueprintContainer ipService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer queueService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
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
	public void configureSubInterfaceTest() {
		/* test to configure a simple subinterface */
		simpleSubInterfaceConfigurationTest();

		/* test to configure a subinterface */
		subInterfaceConfigurationTest();
	}

	/**
	 * Put related task
	 * */
	public void simpleSubInterfaceConfigurationTest() {

		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEthernet();
		try {
			chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		ICapability queueCapability = resource.getCapabilities().get(posQueue);
		try {
			queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		// TODO check queue response is OK

		/* refresh model */
		try {
			chassisCapability.sendMessage(ActionConstants.GETCONFIG, ethernetPort);
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
	 * */
	public void subInterfaceConfigurationTest() {
		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEtherVLAN();
		try {
			chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		try {
			chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		ICapability queueCapability = resource.getCapabilities().get(posQueue);
		try {
			queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		} catch (CapabilityException e) {
			Assert.fail("Impossible send message: " + e.getMessage());
		}

		// TODO check queue response is OK

		/* refresh model */
		try {
			chassisCapability.sendMessage(ActionConstants.GETCONFIG, ethernetPort);
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
