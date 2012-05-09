package org.opennaas.itests.roadm.connections;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.roadm.capability.connections.ConnectionsActionSet;
import org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability;
import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;
import org.opennaas.itests.roadm.helpers.CapabilityHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ConnectionsCapabilityIntegrationTest
{
	private final static Log		log			= LogFactory.getLog(ConnectionsCapabilityIntegrationTest.class);

	private final String			deviceID	= "roadm";
	private final String			queueID		= "queue";

	private MockResource			mockResource;
	private IConnectionsCapability	connectionsCapability;
	private IQueueManagerCapability	queueCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=connections)")
	private ICapabilityFactory		connectionFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	private BlueprintContainer		wonesysProtocolService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void initResource() throws Exception {

		OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel((IModel) switchFactory.newPedrosaProteusOpticalSwitch());
		mockResource.setResourceDescriptor(CapabilityHelper.newResourceDescriptor("roadm"));
		mockResource.setResourceIdentifier(new ResourceIdentifier(mockResource.getResourceDescriptor().getInformation().getType(), mockResource
				.getResourceDescriptor().getId()));
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

	public void initCapability() throws Exception {

		log.info("INFO: Before test, getting queue...");

		queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextWonesys());

		// Test elements not null
		log.info("Checking connections factory");
		Assert.assertNotNull(connectionFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("connections"));
		log.info("Creating connection capability");
		connectionsCapability = (IConnectionsCapability) connectionFactory.create(mockResource);
		Assert.assertNotNull(connectionsCapability);
		((ICapabilityLifecycle) connectionsCapability).initialize();
	}

	@Before
	public void setup() throws Exception {
		initResource();
		initCapability();
	}

	@Test
	public void TestConnectionsAction() throws Exception {
		log.info("Test connections actions");
		List<String> availableActions = new ArrayList<String>();
		availableActions.add(ConnectionsActionSet.MAKE_CONNECTION);
		availableActions.add(ConnectionsActionSet.REMOVE_CONNECTION);
		availableActions.add(ActionConstants.REFRESHCONNECTIONS);
		// availableActions.add(ActionConstants.GETINVENTORY);

		log.info("checking capability actionSet contains desired actions...");
		IActionSet actionSet = ((AbstractCapability) connectionsCapability).getActionSet();
		List<String> actionSetNames = actionSet.getActionNames();

		for (String actionName : availableActions) {
			Assert.assertTrue(actionSetNames.contains(actionName));
		}

		/* send message */
		log.info("send message makeConnection...");
		FiberConnection connectionRequest = newMakeConnectionParams((ProteusOpticalSwitch) mockResource.getModel());
		connectionsCapability.makeConnection(connectionRequest);

		/* check queue */
		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 1);
		/* check queue */
		log.info("exec queue...");
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();

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
		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 0);

		/* ---------------- work flow for remove connections -------------------- */

		/* send message */
		log.info("removeConnection...");
		connectionsCapability.removeConnection(connectionRequest);

		/* check queue */
		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 1);

		/* check queue */
		log.info("exec queue...");
		queueResponse = (QueueResponse) queueCapability.execute();
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
		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 0);
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
