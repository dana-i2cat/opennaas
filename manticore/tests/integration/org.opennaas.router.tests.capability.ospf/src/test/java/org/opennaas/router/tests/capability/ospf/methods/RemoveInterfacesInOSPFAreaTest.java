/**
 *
 */
package org.opennaas.router.tests.capability.ospf.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.utils.ModelHelper;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.router.capability.ospf.OSPFCapability;
import org.opennaas.router.tests.capability.ospf.OSPFIntegrationTest;

/**
 * @author Jordi
 */
public class RemoveInterfacesInOSPFAreaTest extends OSPFIntegrationTest {

	/**
	 * Test to check removeInterfacesInOSPFArea method
	 */
	@Test
	public void removeInterfacesInOSPFAreaTest()
		throws ResourceException, ProtocolException, IOException, Exception
	{
		startResource();

		OSPFCapability ospfCapability = (OSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.removeInterfacesInOSPFArea(getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
												  getOSPFArea("0.0.0.0"));

		ICapability queueCapability = routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
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
	public String[] splitInterfaces(String complexInterface) throws Exception {
		String[] argsInterface = new String[2];

		argsInterface = complexInterface.split("\\.");
		if (argsInterface.length != 2) {
			Exception excep = new Exception("Invalid format in interface name.");
			throw excep;
		}

		return argsInterface;
	}

}
