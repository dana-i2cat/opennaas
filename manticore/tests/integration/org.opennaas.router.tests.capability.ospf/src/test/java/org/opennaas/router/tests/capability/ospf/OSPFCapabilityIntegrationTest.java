package org.opennaas.router.tests.capability.ospf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test to verify that OSPF Capability is available from the container
 * 
 * @author Jordi Puig
 */
public class OSPFCapabilityIntegrationTest extends OSPFIntegrationTest {

	private static Log	log	= LogFactory
									.getLog(OSPFCapabilityIntegrationTest.class);

	@Test
	/**
	 * Test to check if capability is available from OSGi.
	 */
	public void isCapabilityAccessibleFromResource() {

		try {
			startResource();
			Assert.assertTrue(routerResource.getCapabilities().size() > 0);

			stopResource();
			Assert.assertTrue(resourceManager.listResources().isEmpty());
		} catch (IllegalArgumentException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		}
	}

}
