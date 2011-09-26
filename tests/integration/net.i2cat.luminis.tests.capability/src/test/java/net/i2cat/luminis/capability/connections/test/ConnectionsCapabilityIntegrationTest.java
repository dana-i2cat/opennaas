package net.i2cat.luminis.capability.connections.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.WDMChannelPlan;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import net.i2cat.mantychore.model.utils.OpticalSwitchFactory;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class ConnectionsCapabilityIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log			log				= LogFactory
															.getLog(ConnectionsCapabilityIntegrationTest.class);
	static MockResource	mockResource;
	String				deviceID		= "roadm";
	String				queueID			= "queue";
	static ICapability	connectionsCapability;

	@Inject
	BundleContext		bundleContext	= null;
	private ICapability	queueCapability;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	public void initResource() throws Exception {

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel((IModel) switchFactory.newPedrosaProteusOpticalSwitch());

		mockResource.setResourceDescriptor(CapabilityHelper.newResourceDescriptor("roadm"));
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextWonesys() {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				"protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		// ADDED
		return protocolSessionContext;

	}

	public void initCapability() {

		try {
			log.info("INFO: Before test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);

			// IQueueManagerService queueManagerService = (IQueueManagerService) getOsgiService(IQueueManagerService.class,
			// "(capability=queue)(capability.name=" + deviceID + ")", 5000);

			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);

			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextWonesys());

			ICapabilityFactory connectionFactory = getOsgiService(ICapabilityFactory.class, "capability=connections", 10000);
			// Test elements not null
			log.info("Checking connections factory");
			Assert.assertNotNull(connectionFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("connections"));
			log.info("Creating connection capability");
			connectionsCapability = connectionFactory.create(mockResource);
			Assert.assertNotNull(connectionsCapability);
			connectionsCapability.initialize();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	@Before
	public void initBundles() throws Exception {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		initResource();
		initCapability();
	}

	@Test
	public void TestConnectionsAction() {
		log.info("Test connections actions");
		List<String> availableActions = new ArrayList<String>();
		availableActions.add(ActionConstants.MAKECONNECTION);
		availableActions.add(ActionConstants.REMOVECONNECTION);
		availableActions.add(ActionConstants.REFRESHCONNECTIONS);
		// availableActions.add(ActionConstants.GETINVENTORY);

		try {

			log.info("checking capability actionSet contains desired actions...");
			IActionSet actionSet = ((AbstractCapability) connectionsCapability).getActionSet();
			List<String> actionSetNames = actionSet.getActionNames();

			for (String actionName : availableActions) {
				Assert.assertTrue(actionSetNames.contains(actionName));
			}

			/* send message */
			log.info("send message makeConnection...");
			FiberConnection connectionRequest = newMakeConnectionParams((ProteusOpticalSwitch) mockResource.getModel());
			Response resp = (Response) connectionsCapability.sendMessage(ActionConstants.MAKECONNECTION, connectionRequest);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			/* check queue */
			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 1);
			/* check queue */
			log.info("exec queue...");
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

			Assert.assertTrue(queueResponse.isOk());

			boolean foundAndOk = false;
			for (ActionResponse response : queueResponse.getResponses()) {
				if (response.getActionID().equals(ActionConstants.MAKECONNECTION)) {
					if (response.getStatus() == STATUS.OK) {
						foundAndOk = true;
						for (Response subresponse : response.getResponses()) {
							Assert.assertTrue(subresponse.getStatus() == Response.Status.OK);
						}
					}
				}
			}
			Assert.assertTrue(foundAndOk);

			/* check queue */
			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);

			/* ---------------- work flow for remove connections -------------------- */

			/* send message */
			log.info("send message removeConnection...");
			resp = (Response) connectionsCapability.sendMessage(ActionConstants.REMOVECONNECTION, connectionRequest);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			/* check queue */
			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 1);

			/* check queue */
			log.info("exec queue...");
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());

			foundAndOk = false;
			for (ActionResponse response : queueResponse.getResponses()) {
				if (response.getActionID().equals(ActionConstants.REMOVECONNECTION)) {
					if (response.getStatus() == STATUS.OK) {
						foundAndOk = true;
						for (Response subresponse : response.getResponses()) {
							Assert.assertTrue(subresponse.getStatus() == Response.Status.OK);
						}
					}
				}
			}
			Assert.assertTrue(foundAndOk);

			/* check queue */
			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);

		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	private FiberConnection newMakeConnectionParams(ProteusOpticalSwitch proteus) throws Exception {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		double srcLambda = srcFiberChannel.getLambda();

		fiberConnection.setSrcCard(dropCard);
		fiberConnection.setSrcPort(srcPort);
		fiberConnection.setSrcFiberChannel(srcFiberChannel);

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		ProteusOpticalSwitchCard addCard = proteus.getCard(addChasis, addSlot);
		FCPort dstPort = ((WonesysPassiveAddCard) addCard).getCommonPort();

		DWDMChannel dstFiberChannel = (DWDMChannel) ((WDMChannelPlan) addCard.getChannelPlan()).getChannel(
				((WDMChannelPlan) addCard.getChannelPlan()).getChannelNumberFromLambda(srcLambda));

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

}
