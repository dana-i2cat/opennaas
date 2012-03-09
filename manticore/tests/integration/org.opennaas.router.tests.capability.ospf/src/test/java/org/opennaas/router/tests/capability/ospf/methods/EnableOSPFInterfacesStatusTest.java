package org.opennaas.router.tests.capability.ospf.methods;

/**
 *
 */
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.OSPFProtocolEndpoint;

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
public class EnableOSPFInterfacesStatusTest extends OSPFIntegrationTest {

	/**
	 * Test to check enableOSPFInterfaceStatus method
	 */
	@Test
	public void enableOSPFInterfaceStatusTest()
		throws ResourceException, ProtocolException
	{
		startResource();

		OSPFCapability ospfCapability = (OSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.enableOSPFInterfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		ICapability queueCapability = routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
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
