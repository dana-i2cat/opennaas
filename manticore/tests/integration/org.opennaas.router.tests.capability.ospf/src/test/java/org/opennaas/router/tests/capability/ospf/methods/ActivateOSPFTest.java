/**
 * 
 */
package org.opennaas.router.tests.capability.ospf.methods;

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
public class ActivateOSPFTest extends OSPFIntegrationTest {

	/**
	 * Test to check activateOSPF method
	 */
	@Test
	public void activateOSPFTest() {
		try {
			startResource();

			OSPFCapability ospfCapability = (OSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
			ospfCapability.activateOSPF();

			ICapability queueCapability = routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());

			stopResource();
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
