package org.opennaas.router.tests.capability;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.GRETunnelService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.router.capability.gretunnel.IGRETunnelService;

public class GRETunnelCapabilityIntegrationTest extends GRETunnelIntegrationTest {

	/**
	 * 
	 */
	private static Log	log	= LogFactory
									.getLog(GRETunnelCapabilityIntegrationTest.class);

	@Test
	public void createGRETunnelTest() {
		log.info("Test createGRETunnel method");
		try {
			iGRETunnelService = (IGRETunnelService) greTunnelCapability;
			Response resp = iGRETunnelService
					.createGRETunnel(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
			Assert.assertTrue(resp.getStatus() == Response.Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deleteGRETunnelTest() {
		log.info("Test createGRETunnel method");
		try {
			iGRETunnelService = (IGRETunnelService) greTunnelCapability;
			Response resp = iGRETunnelService.deleteGRETunnel(getGRETunnelService(TUNNEL_NAME, null, null, null, null));
			Assert.assertTrue(resp.getStatus() == Response.Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void showGRETunnelConfigurationTest() {
		log.info("Test showGRETunnelConfiguration method");
		try {
			iGRETunnelService = (IGRETunnelService) greTunnelCapability;
			List<GRETunnelService> resp = iGRETunnelService.showGRETunnelConfiguration();
			Assert.assertTrue(resp.size() > 0);
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testGRETunnelAction() {
		log.info("TEST GRE TUNNEL ACTION");
		try {
			iGRETunnelService = (IGRETunnelService) greTunnelCapability;
			Response resp = iGRETunnelService.createGRETunnel(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
			Assert.assertTrue(resp.getStatus() == Response.Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = iGRETunnelService.deleteGRETunnel(getGRETunnelService(TUNNEL_NAME, null, null, null, null));
			Assert.assertTrue(resp.getStatus() == Response.Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) greTunnelCapability.sendMessage(ActionConstants.GETTUNNELCONFIG,
					getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
			Assert.assertTrue(resp.getStatus() == Response.Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 3);

			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.getResponses().size() == 3);

			Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRefreshResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

			Assert.assertTrue(queueResponse.isOk());

			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

}