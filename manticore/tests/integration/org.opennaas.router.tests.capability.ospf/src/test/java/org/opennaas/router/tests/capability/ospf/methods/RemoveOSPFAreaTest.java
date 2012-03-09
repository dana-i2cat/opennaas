/**
 *
 */
package org.opennaas.router.tests.capability.ospf.methods;

import java.io.IOException;

import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFArea.AreaType;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
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
 *
 */
public class RemoveOSPFAreaTest extends OSPFIntegrationTest {

	/**
	 * Test to check removeOSPFArea method
	 */
	@Test
	public void removeOSPFAreaTest()
		throws ResourceException, ProtocolException, IOException
	{
		startResource();

		OSPFCapability ospfCapability = (OSPFCapability) routerResource.getCapability(getOSPFInformation(OSPF_CAPABILIY_TYPE));
		ospfCapability.removeOSPFArea(getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		ICapability queueCapability = routerResource.getCapability(getOSPFInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
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

}
