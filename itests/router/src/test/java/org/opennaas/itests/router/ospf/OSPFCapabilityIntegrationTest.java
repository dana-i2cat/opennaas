package org.opennaas.itests.router.ospf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
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
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

/**
 * Test to verify that OSPF Capability is available from the container
 * 
 * @author Jordi Puig
 */
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OSPFCapabilityIntegrationTest extends OSPFIntegrationTest {

	private static Log	log	= LogFactory
									.getLog(OSPFCapabilityIntegrationTest.class);

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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.activateOSPF();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.addInterfacesInOSPFArea(getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.clearOSPFconfiguration(getOSPFService("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.configureOSPFArea(getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.configureOSPF(getOSPFService("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.deactivateOSPF();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.disableOSPFInterfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.enableOSPFInterfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.getOSPFConfiguration();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.removeInterfacesInOSPFArea(getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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

		IOSPFCapability ospfCapability = (IOSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.removeOSPFArea(getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
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
}
