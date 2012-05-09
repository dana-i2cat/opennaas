package org.opennaas.itests.router.staticroute;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

/**
 * Test to verify that Static Route Capability
 * 
 * @author Jordi Puig
 */
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class StaticRouteCapabilityIntegrationTest extends StaticRouteIntegrationTest {

	@Test
	/**
	 * Test to check if capability is available from OSGi.
	 */
	public void isCapabilityAccessibleFromResource()
			throws ResourceException, ProtocolException
	{
		startResource();
		Assert.assertTrue(!routerResource.getCapabilities().isEmpty());
		Assert.assertTrue(routerResource.getCapability(getInformation(QUEUE_CAPABILIY_TYPE)) != null);
		Assert.assertTrue(routerResource.getCapability(getInformation(STATIC_ROUTE_CAPABILITY_TYPE)) != null);
		stopResource();
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * Test to check create static route method
	 */
	@Test
	public void createStaticRouteTest()
			throws ProtocolException, ResourceException {
		startResource();

		StaticRouteCapability staticRouteCapability = (StaticRouteCapability) routerResource
				.getCapability(getInformation(STATIC_ROUTE_CAPABILITY_TYPE));
		staticRouteCapability.createStaticRoute("0.0.0.0", "0.0.0.0", "192.168.1.1");

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource.getCapability(getInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

}
