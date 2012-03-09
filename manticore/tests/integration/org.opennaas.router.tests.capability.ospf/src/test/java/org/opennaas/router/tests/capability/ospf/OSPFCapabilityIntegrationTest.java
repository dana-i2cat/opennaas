package org.opennaas.router.tests.capability.ospf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;

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
}
