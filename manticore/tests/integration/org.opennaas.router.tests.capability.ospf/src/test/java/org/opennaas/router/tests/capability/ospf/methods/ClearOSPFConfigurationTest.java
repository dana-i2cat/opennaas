/**
 *
 */
package org.opennaas.router.tests.capability.ospf.methods;

import net.i2cat.mantychore.model.OSPFService;

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
public class ClearOSPFConfigurationTest extends OSPFIntegrationTest {

	/**
	 * Test to check clearOSPFConfiguration method
	 */
	@Test
	public void clearOSPFConfigurationTest()
		throws ProtocolException, ResourceException
	{
		startResource();

		OSPFCapability ospfCapability = (OSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.clearOSPFconfiguration(getOSPFService("12345678"));

		ICapability queueCapability = routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
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
}
