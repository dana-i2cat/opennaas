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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.itests.router.helpers.CheckParametersHelper;
import org.opennaas.itests.router.helpers.ParamCreationHelper;
import org.opennaas.itests.router.helpers.TestsConstants;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
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
public class ChassisLRIntegrationTest
{

	private final static Log	log					= LogFactory.getLog(ChassisLRIntegrationTest.class);

	private final static String	RESOURCE_INFO_NAME	= "LogicalRouter Test";
	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IResourceManager	resourceManager;

	protected IResource			routerResource;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	private IProtocolManager	protocolManager;

	private boolean				isMock;
	private ResourceDescriptor	resourceDescriptor;
	private IResource			resource;
	private String				deviceID;
	private String				type;
	private EthernetPort		iface;
	private IResource			LRresource;
	private String				LRName				= "cpe2";
	private String				interfaceName		= "fe-0/0/3.1";

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerRepoService;

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
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	public ChassisLRIntegrationTest() {
		this.type = "router";
		this.deviceID = "junos";
		this.isMock = true;

		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName(interfaceName);
		ethernetPort.setPortNumber(1);
		iface = ethernetPort;

	}

	@Test
	public void GRESubInterfaceConfigurationTest() throws CapabilityException, ProtocolException {

		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));

		Assert.assertNotNull(chassisCapability);

		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceGRE();
		chassisCapability.createSubInterface(ethernetPort);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		Assert.assertNotNull(queueCapability);

		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

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
	 * @throws CapabilityException
	 * */
	@Test
	public void subInterfaceConfigurationTest() throws ProtocolException, CapabilityException {

		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));

		Assert.assertNotNull(chassisCapability);

		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEtherVLAN();

		chassisCapability.createSubInterface(ethernetPort);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		Assert.assertNotNull(queueCapability);

		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

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
	 * @throws CapabilityException
	 * */
	@Test
	public void simpleSubInterfaceConfigurationTest() throws ProtocolException, CapabilityException {

		/* send action */

		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));

		Assert.assertNotNull(chassisCapability);

		EthernetPort ethernetPort = (EthernetPort) ParamCreationHelper.newParamsInterfaceEthernet();

		chassisCapability.createSubInterface(ethernetPort);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		Assert.assertNotNull(queueCapability);

		/* execute action */
		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		if (isMock)
			return;

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) resource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

	}

	@Test
	public void setSubInterfaceDescriptionTest()
			throws CapabilityException, ResourceException, ProtocolException
	{
		/* send action */
		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));
		Assert.assertNotNull(chassisCapability);

		IIPCapability ipCapability = (IIPCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.IP_CAPABILITY_TYPE));
		Assert.assertNotNull(ipCapability);

		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName(iface.getName());
		ethernetPort.setPortNumber(iface.getPortNumber());
		ethernetPort.setDescription("Description for the setSubInterfaceDescription test");
		ethernetPort.setElementName(LRName);

		ipCapability.setInterfaceDescription(ethernetPort);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		Assert.assertNotNull(queueCapability);

		/* execute action */
		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		if (isMock)
			return;

		resourceManager.startResource(LRresource.getResourceIdentifier());

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsSubInterface((ComputerSystem) LRresource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

		String desc = ((EthernetPort) ((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(ethernetPort.getDescription()));
	}

	/**
	 * Test the possibility to configure subinterfaces with an encapsulation
	 * 
	 * @throws ProtocolException
	 * */
	@Test
	public void setInterfaceDescriptionTest()
			throws CapabilityException, ResourceException, ProtocolException
	{
		/* send action */
		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));
		Assert.assertNotNull(chassisCapability);

		IIPCapability ipCapability = (IIPCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.IP_CAPABILITY_TYPE));
		Assert.assertNotNull(ipCapability);

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		logicalPort.setElementName(LRName);

		ipCapability.setInterfaceDescription(logicalPort);

		/* execute action */
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		Assert.assertNotNull(queueCapability);

		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		if (isMock)
			return;

		resourceManager.startResource(LRresource.getResourceIdentifier());

		/* check the update model, it is only possible to check it with a real router */
		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) LRresource.getModel(), logicalPort);
		Assert.assertTrue(pos != -1);

		String desc = ((LogicalPort) ((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(logicalPort.getDescription()));
	}

	/**
	 * Put related task
	 * 
	 * @throws ProtocolException
	 * */
	@Test
	public void setSubInterfaceDescriptioninLRTest()
			throws CapabilityException, ResourceException, ProtocolException
	{
		/* send action */
		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));
		Assert.assertNotNull(chassisCapability);

		IIPCapability ipCapability = (IIPCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.IP_CAPABILITY_TYPE));
		Assert.assertNotNull(ipCapability);

		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName(iface.getName());
		ethernetPort.setPortNumber(iface.getPortNumber());
		ethernetPort.setDescription("Description for the setSubInterfaceDescription test");
		ethernetPort.setElementName(LRName);

		ipCapability.setInterfaceDescription(ethernetPort);

		/* execute action */
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		if (isMock)
			return;

		resourceManager.startResource(LRresource.getResourceIdentifier());

		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsSubInterface((ComputerSystem) LRresource.getModel(), ethernetPort);
		Assert.assertTrue(pos != -1);

		String desc = ((EthernetPort) ((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(ethernetPort.getDescription()));
	}

	/**
	 * Test the possibility to configure subinterfaces with an encapsulation
	 * 
	 * @throws ProtocolException
	 * */
	@Test
	public void setInterfaceDescriptionInLRTest()
			throws CapabilityException, ResourceException, ProtocolException
	{
		/* send action */
		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));
		Assert.assertNotNull(chassisCapability);

		IIPCapability ipCapability = (IIPCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.IP_CAPABILITY_TYPE));
		Assert.assertNotNull(ipCapability);

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		logicalPort.setElementName(LRName);

		ipCapability.setInterfaceDescription(logicalPort);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getChassisInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		if (isMock)
			return;

		resourceManager.startResource(LRresource.getResourceIdentifier());

		/* check the update model, it is only possible to check it with a real router */
		/* check the update model, it is only possible to check it with a real router */
		int pos = CheckParametersHelper.containsInterface((ComputerSystem) LRresource.getModel(), logicalPort);
		Assert.assertTrue(pos != -1);

		String desc = ((LogicalPort) ((ComputerSystem) LRresource.getModel()).getLogicalDevices().get(pos)).getDescription();
		Assert.assertTrue(desc.equals(logicalPort.getDescription()));
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	protected void clearRepository() throws ResourceException {
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

	protected IProtocolSessionManager addSessionContext(String resourceId) throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, TestsConstants.RESOURCE_URI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

	private Information getChassisInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}

	public void startResource() throws ResourceException, ProtocolException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.CHASSIS_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(chassisCapabilityDescriptor);

		// Add IP capability Descriptor

		CapabilityDescriptor ipCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.IP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ipCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		addSessionContext(routerResource.getResourceIdentifier().getId());

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	@Before
	public void initBundle() throws ResourceException, ProtocolException {
		clearRepository();
		log.info("Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws ResourceException {
		clearRepository();
		log.info("Stopped!");

	}

}