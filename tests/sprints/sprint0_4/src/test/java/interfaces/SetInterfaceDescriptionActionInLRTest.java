package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.CheckParametersHelper;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.nexus.tests.InitializerTestHelper;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
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
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;


@RunWith(JUnit4TestRunner.class)
public class SetInterfaceDescriptionActionInLRTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	@Inject
	BundleContext		bundleContext	= null;
	
	boolean				isMock;
	ResourceDescriptor	resourceDescriptor;
	IResource			resource		= null;
	String				deviceID;
	String				type;
	IResourceManager	resourceManager;
	IProfileManager		profileManager;
	
	String LRName = "TestLR1";
	IResource LRresource = null;
	EthernetPort iface;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
//					 , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	public SetInterfaceDescriptionActionInLRTest() {
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
	public void setUp() throws ResourceException, ProtocolException {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		resourceManager = getOsgiService(IResourceManager.class, 50000);
		profileManager = getOsgiService(IProfileManager.class, 30000);
		
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
		capabilities.add("queue");
		resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, type, capabilities);
		resource = resourceManager.createResource(resourceDescriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
		
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		
		if (! resource.getModel().getChildren().isEmpty()) {
			LRName = resource.getModel().getChildren().get(0);
		} else {
			//create LR
			chassisCapability.sendMessage(ActionConstants.CREATELOGICALROUTER, LRName);
			executeQueue(resource);
		}
		
		//createlogicalIface in LR
		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName("fe-0/3/2");
		ethernetPort.setPortNumber(2);
		ethernetPort.setElementName(LRName);
		chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, ethernetPort);
		executeQueue(resource);
		
		LRresource = resourceManager.getResource(resourceManager.getIdentifierFromResourceName("router", LRName));
		iface = ethernetPort;
	}

	/**
	 * Reset info for next tests
	 * 
	 * @throws ResourceException
	 * 
	 */
	public void tearDown() throws ResourceException {

		//delete created sub interface
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		try {
			chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, iface);
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}
		
		//delete LR
		chassisCapability.sendMessage(ActionConstants.DELETELOGICALROUTER, LRName);
		executeQueue(resource);
		
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
	public void setInterfaceDescriptionActionInLRTest() {
		try {
			setUp();

			setSubInterfaceDescriptionTest();
			setInterfaceDescriptionTest();
		
		} catch (ResourceException e) {
			Assert.fail("Impossible set up test: " + e.getMessage());
		} catch (ProtocolException e) {
			Assert.fail("Impossible set up test: " + e.getMessage());
		} catch (Exception e){
			Assert.fail("Error during test: " + e.getMessage());
		} finally {
			try {
				tearDown();
			} catch (ResourceException e) {
				Assert.fail("Impossible tear down test: " + e.getMessage());
			}
		}
	}

	/**
	 * Put related task
	 * */
	public void setSubInterfaceDescriptionTest() {

		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		
		int posIpv4 = InitializerTestHelper.containsCapability(resource, "ipv4");
		if (posIpv4 == -1)
			Assert.fail("Could not get ipv4 capability for given resource");
		ICapability ipCapability = resource.getCapabilities().get(posIpv4);
		
		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName(iface.getName());
		ethernetPort.setPortNumber(iface.getPortNumber());
		ethernetPort.setDescription("Description for the setSubInterfaceDescription test");
		ethernetPort.setElementName(LRName);
		
		try {
			ipCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}

		/* execute action */
		executeQueue(resource);

		/* refresh model */
		try {
			chassisCapability.sendMessage(ActionConstants.GETCONFIG, ethernetPort);
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}

		if (isMock)
			return;

		try {
			resourceManager.startResource(LRresource.getResourceIdentifier());
			
			/* check the update model, it is only possible to check it with a real router */
			int pos = CheckParametersHelper.containsSubInterface((ComputerSystem) LRresource.getModel(), ethernetPort);
			Assert.assertTrue(pos != -1);
			
			String desc = ((EthernetPort)((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
			Assert.assertTrue(desc.equals(ethernetPort.getDescription()));
		} catch (ResourceException e) {
			Assert.fail("Failed to start LR: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test the possibility to configure subinterfaces with an encapsulation
	 * */
	public void setInterfaceDescriptionTest() {
		/* send action */
		int posChassis = InitializerTestHelper.containsCapability(resource, "chassis");
		if (posChassis == -1)
			Assert.fail("Could not get Chassis capability for given resource");
		ICapability chassisCapability = resource.getCapabilities().get(posChassis);
		
		int posIpv4 = InitializerTestHelper.containsCapability(resource, "ipv4");
		if (posIpv4 == -1)
			Assert.fail("Could not get ipv4 capability for given resource");
		ICapability ipCapability = resource.getCapabilities().get(posIpv4);
		
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		logicalPort.setElementName(LRName);
		
		try {
			ipCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, logicalPort);
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}

		/* execute action */
		executeQueue(resource);

		/* refresh model */
		try {
			chassisCapability.sendMessage(ActionConstants.GETCONFIG, logicalPort);
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}

		if (isMock)
			return;

		try {
			resourceManager.startResource(LRresource.getResourceIdentifier());
			
			/* check the update model, it is only possible to check it with a real router */
			/* check the update model, it is only possible to check it with a real router */
			int pos = CheckParametersHelper.containsInterface((ComputerSystem) LRresource.getModel(), logicalPort);
			Assert.assertTrue(pos != -1);
			
			String desc = ((LogicalPort)((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
			Assert.assertTrue(desc.equals(logicalPort.getDescription()));
		} catch (ResourceException e) {
			Assert.fail("Failed to start LR: " + e.getLocalizedMessage());
		}
	}

	/**
	 * TODO This class has to be moved to the share helper
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 15000);
		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();
		protocolManager.getProtocolSessionManagerWithContext(resourceId, context);
		
		isMock = false;
		if (context.getSessionParameters().containsKey(ProtocolSessionContext.PROTOCOL_URI)){
			if (((String)context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI)).startsWith("mock")){
				isMock = true;
			}
		}
	}
	
	private QueueResponse executeQueue(IResource resource){
		/* execute action */
		int posQueue = InitializerTestHelper.containsCapability(resource, "queue");
		if (posQueue == -1)
			Assert.fail("Could not get Queue capability for given resource");
		ICapability queueCapability = resource.getCapabilities().get(posQueue);
		QueueResponse response = null;
		try {
			response = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(response.isOk());
		} catch (CapabilityException e) {
			Assert.fail("It was impossible to send the following message: " + e.getMessage());
		}
		return response;
	}
}
