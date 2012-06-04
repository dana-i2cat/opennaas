/**
 *
 */
package org.opennaas.itests.router.ospf;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFArea.AreaType;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.utils.ModelHelper;
import org.opennaas.itests.router.helpers.TestsConstants;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * @author Jordi
 * @author Adrian Rosello
 */
@RunWith(JUnit4TestRunner.class)
public abstract class OSPFIntegrationTest
{
	protected static final String	RESOURCE_INFO_NAME	= "OSPF Test";

	protected ICapability			iOSPFCapability;
	protected IResource				routerResource;

	@Inject
	protected IResourceManager		resourceManager;

	@Inject
	protected IProtocolManager		protocolManager;

	@Inject
	protected BundleContext			bundleContext;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)")
	private BlueprintContainer		netconfService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerRepoService;

	private static final Log		log					= LogFactory
																.getLog(OSPFIntegrationTest.class);

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	/**
	 * Test to check if capability is available from OSGi.
	 */
	public void isCapabilityAccessibleFromResource()
			throws ResourceException, ProtocolException
	{
		startResource();
		Assert.assertTrue(routerResource.getCapabilities().size() > 0);

		stopResource();
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * Test to check activateOSPF method
	 */
	@Test
	public void activateOSPFTest()
			throws ProtocolException, ResourceException
	{
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.activateOSPF();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check OSPFClear Action
	 */
	@Test
	public void addInterfacesInOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException, Exception
	{
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.addInterfacesInOSPFArea(getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check clearOSPFConfiguration method
	 */
	@Test
	public void clearOSPFConfigurationTest()
			throws ProtocolException, ResourceException
	{
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.clearOSPFconfiguration(getOSPFService("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check configureOSPFArea method
	 */
	@Test
	public void configureOSPFAreaTest()
			throws IOException, ProtocolException, ResourceException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.configureOSPFArea(getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check configureOSPF method
	 */
	@Test
	public void configureOSPFTest() throws ResourceException, ProtocolException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.configureOSPF(getOSPFService("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check deactivateOSPF method
	 */
	@Test
	public void deactivateOSPFTest()
			throws ResourceException, ProtocolException
	{
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.deactivateOSPF();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check disableOSPFInterfaces method
	 */
	@Test
	public void disableOSPFInterfaceStatusTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.disableOSPFInterfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check enableOSPFInterfaceStatus method
	 */
	@Test
	public void enableOSPFInterfaceStatusTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.enableOSPFInterfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check getOSPFConfiguration method
	 */
	@Test
	public void getOSPFConfigurationTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.getOSPFConfiguration();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check removeInterfacesInOSPFArea method
	 */
	@Test
	public void removeInterfacesInOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException, Exception {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.removeInterfacesInOSPFArea(getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check removeOSPFArea method
	 */
	@Test
	public void removeOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException {
		startResource();

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(TestsConstants.OSPF_CAPABILIY_TYPE));
		ospfCapability.removeOSPFArea(getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(getOSPFInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * @param interfaceNames
	 * @return List<LogicalPort>
	 * @throws Exception
	 */
	private List<LogicalPort> getLogicalPorts(String[] interfaceNames) throws Exception {
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>(interfaceNames.length);
		for (String interfaceName : interfaceNames) {
			interfaces.add(createInterface(interfaceName));
		}
		return interfaces;
	}

	/**
	 * @param areaId
	 * @return OSPFArea
	 * @throws IOException
	 */
	private OSPFArea getOSPFArea(String areaId) throws IOException {
		OSPFArea area = new OSPFArea();
		area.setAreaID(ModelHelper.ipv4StringToLong(areaId));
		return area;
	}

	/**
	 * Create an interface
	 * 
	 * @param interfaceName
	 * @return NetworkPort
	 * @throws Exception
	 */
	private NetworkPort createInterface(String interfaceName) throws Exception {
		String argsInterface[] = new String[2];

		argsInterface = splitInterfaces(interfaceName);

		String name = argsInterface[0];
		int port = Integer.parseInt(argsInterface[1]);

		NetworkPort networkPort = new NetworkPort();
		networkPort.setName(name);
		networkPort.setPortNumber(port);
		networkPort.setLinkTechnology(LinkTechnology.OTHER);

		return networkPort;
	}

	/**
	 * Split interfaces
	 * 
	 * @param complexInterface
	 * @return String[]
	 * @throws Exception
	 */
	private String[] splitInterfaces(String complexInterface) throws Exception {
		String[] argsInterface = new String[2];

		argsInterface = complexInterface.split("\\.");
		if (argsInterface.length != 2) {
			Exception excep = new Exception("Invalid format in interface name.");
			throw excep;
		}

		return argsInterface;
	}

	/**
	 * @param routerId
	 * @return OSPFService
	 */
	private OSPFService getOSPFService(String routerId) {
		OSPFService ospfService = new OSPFService();
		if (routerId != null) {
			ospfService.setRouterID(routerId);
		}
		return ospfService;
	}

	/**
	 * @param areaId
	 * @param selectedAreaType
	 * @return OSPFAreaConfiguration
	 * @throws IOException
	 */
	private OSPFAreaConfiguration getOSPFAreaConfiguration(String areaId, AreaType selectedAreaType) throws IOException {
		OSPFArea area = new OSPFArea();
		area.setAreaID(ModelHelper.ipv4StringToLong(areaId));
		area.setAreaType(selectedAreaType);

		OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
		areaConfig.setOSPFArea(area);
		return areaConfig;
	}

	/**
	 * @return List<OSPFProtocolEndpoint>
	 */
	private List<OSPFProtocolEndpoint> getInterfaces(String[] interfaceNames) {
		List<OSPFProtocolEndpoint> ospfPeps = new ArrayList<OSPFProtocolEndpoint>();
		OSPFProtocolEndpoint pep;

		for (String ifaceName : interfaceNames) {
			pep = new OSPFProtocolEndpoint();
			pep.setName(ifaceName);
			ospfPeps.add(pep);
		}

		return ospfPeps;
	}

	@Before
	public void initBundles() throws ResourceException {
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

		CapabilityDescriptor ospfCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.OSPF_CAPABILIY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
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
				ProtocolSessionContext.PROTOCOL_URI, TestsConstants.RESOURCE_URI);
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
