package org.opennaas.itests.router.gre;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class GRETunnelCapabilityIntegrationTest extends GRETunnelIntegrationTest {

	/**
	 *
	 */
	private static Log	log	= LogFactory
									.getLog(GRETunnelCapabilityIntegrationTest.class);

	@Test
	public void createGRETunnelTest() throws CapabilityException, ProtocolException
	{
		log.info("Test createGRETunnel method");
		IGRETunnelCapability iGRETunnelService = greTunnelCapability;
		iGRETunnelService
				.createGRETunnel(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void deleteGRETunnelTest() throws CapabilityException, ProtocolException {
		log.info("Test createGRETunnel method");
		IGRETunnelCapability iGRETunnelService = greTunnelCapability;
		iGRETunnelService.deleteGRETunnel(getGRETunnelService(TUNNEL_NAME, null, null, null, null));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void showGRETunnelConfigurationTest() throws CapabilityException, ProtocolException {
		log.info("Test showGRETunnelConfiguration method");
		IGRETunnelCapability iGRETunnelService = greTunnelCapability;
		List<GRETunnelService> resp = iGRETunnelService.showGRETunnelConfiguration();
		Assert.assertTrue(resp.size() > 0);
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testGRETunnelAction() throws CapabilityException, ProtocolException {
		log.info("TEST GRE TUNNEL ACTION");
		IGRETunnelCapability iGRETunnelService = greTunnelCapability;
		iGRETunnelService.createGRETunnel(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
		iGRETunnelService.deleteGRETunnel(getGRETunnelService(TUNNEL_NAME, null, null, null, null));

		// resp = (Response) greTunnelCapability.sendMessage(ActionConstants.GETTUNNELCONFIG,
		// getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
		// Assert.assertTrue(resp.getStatus() == Response.Status.QUEUED);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 2);

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertEquals(queueResponse.getResponses().size(), 2);

		Assert.assertEquals(queueResponse.getPrepareResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getConfirmResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRefreshResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRestoreResponse().getStatus(), ActionResponse.STATUS.PENDING);

		Assert.assertTrue(queueResponse.isOk());

		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 0);
	}
}