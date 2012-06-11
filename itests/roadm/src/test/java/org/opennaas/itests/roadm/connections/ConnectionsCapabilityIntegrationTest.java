package org.opennaas.itests.roadm.connections;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.roadm.capability.connections.ConnectionsActionSet;
import org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability;
import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.MakeConnectionAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RefreshModelConnectionsAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RemoveConnectionAction;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.LockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.UnlockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;
import org.opennaas.itests.roadm.helpers.CapabilityHelper;
import org.opennaas.itests.roadm.mock.MockProtocolSessionManager;
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
	private final static Log		log				= LogFactory.getLog(ConnectionsCapabilityIntegrationTest.class);

	private final String			deviceID		= "roadm";
	private final String			queueID			= "queue";

	private String					resourceId		= "pedrosa";
	private String					hostIpAddress	= "10.10.80.11";
	private String					hostPort		= "27773";
	private int						sessionCounter	= 0;

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
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	// uses real connection
	@Ignore
	public void testRefreshMakeAndRemoveConnectionsReal() {

		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		log.info("Testing actions with real connection");
		IProtocolSessionManager sessionManager = null;
		try {
			sessionManager = getProtocolSessionManager();

			execActionsAndChecks(opticalSwitch1, sessionManager);

		} catch (ProtocolException e1) {
			Assert.fail(e1.getLocalizedMessage());
		} catch (ActionException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRefreshMakeAndRemoveConnectionsWithMock() {

		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		log.info("Testing actions with mock connection");
		IProtocolSessionManager sessionManager = getMockProtocolSessionManager();

		try {
			execActionsAndChecks(opticalSwitch1, sessionManager);
		} catch (ActionException e) {
			Assert.fail();
		}

	}

	@Test
	public void removeConnectionTest() {
		// WonesysProtocolSession = new WonesysProtocolSession();
		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			int initialConnectionsNum = proteus.getFiberConnections().size();
			log.info("Number of connections: " + initialConnectionsNum);

			IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

			FiberConnection connection = newRemoveConnectionParams(proteus);

			log.info("Making connection to remove...");

			// make connection
			MakeConnectionAction makeConnectionAction = new MakeConnectionAction();
			makeConnectionAction.setParams(connection);
			makeConnectionAction.setModelToUpdate(proteus);

			ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

			Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

			int connectionsNum = proteus.getFiberConnections().size();
			log.info("Number of connections: " + connectionsNum);

			log.info("Removing previous connection ...");
			// remove previously made connection
			RemoveConnectionAction removeConnectionAction = new RemoveConnectionAction();
			removeConnectionAction.setParams(connection);
			removeConnectionAction.setModelToUpdate(proteus);

			actionResponse = removeConnectionAction.execute(protocolSessionManager);

			Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

			/* it is executed at least one action */
			Assert.assertTrue(actionResponse.getResponses().size() > 0);

			/* all responses are correct */
			for (Response response : actionResponse.getResponses()) {
				Assert.assertTrue(response.getErrors().size() == 0);
			}

			log.info("Number of connections: " + proteus.getFiberConnections().size());
			Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum - 1);

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	@Test
	// uses real connection
	@Ignore
	public void makeConnectionTest() {
		// WonesysProtocolSession = new WonesysProtocolSession();
		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			IProtocolSessionManager protocolSessionManager = getProtocolSessionManager();

			makeConnectionAndCheck(proteus, protocolSessionManager);

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void makeConnectionWithMockTest() {
		// WonesysProtocolSession = new WonesysProtocolSession();
		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

			makeConnectionAndCheck(proteus, protocolSessionManager);

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

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
	public void testLockUnlockFunctionality() {
		// initBundles();
		testLockUnlock();
		testLockMultipleTimes();
		testUnlockWithoutLock();
		testLockUnlockInDifferentSessions();
	}

	private void testLockUnlock() {

		try {
			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());

			log.info("Response: " + response);

			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Unlocking...");
			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail(e.getLocalizedMessage());
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail(e.getLocalizedMessage());
		}

	}

	private void testLockMultipleTimes() {
		try {

			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Adquiring lock AGAIN...");

			// lockNode
			c = new LockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Unlocking...");
			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private void testUnlockWithoutLock() {
		try {

			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Unlocking...");

			// unlockNode
			WonesysCommand c = new UnlockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());
			Response resp = c.checkResponse(response);

			Assert.assertTrue(resp.getStatus().equals(Status.ERROR));
			Assert.assertTrue(resp.getErrors().contains("Node is not locked"));

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private void testLockUnlockInDifferentSessions() {
		try {

			log.info("Testing LockUnlockInDifferentSessions ...");

			WonesysProtocolSession session1 = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);
			WonesysProtocolSession session2 = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			if ((session1.getSessionContext().getSessionParameters().containsKey("protocol.mock") &&
					session1.getSessionContext().getSessionParameters().get("protocol.mock").equals("true")) ||
					(session2.getSessionContext().getSessionParameters().containsKey("protocol.mock") &&
					session2.getSessionContext().getSessionParameters().get("protocol.mock").equals("true"))) {
				// This test fails using mock device, as Proteus mock transport is not session aware
				log.info("Skipping test: not supported using mock protocol.");
				return;
			}

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");
			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session1.sendReceive(c.message());
			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Lock adquired!");

			log.info("Unlocking...");

			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session2.sendReceive(c.message());
			resp = c.checkResponse(response);

			log.info("Checking unlock failed with reason Node is locked...");
			Assert.assertTrue(resp.getStatus().equals(Status.ERROR));
			log.info("Error reason: " + resp.getErrors().get(0));
			Assert.assertTrue(resp.getErrors().contains("Node is locked"));

			session1.disconnect();
			session2.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private IProtocolSession getSession(String resourceId, String hostIpAddress, String hostPort) throws ProtocolException {

		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContext(hostIpAddress, hostPort);

		// get WonesysProtocolSession using ProtocolSessionManager
		IProtocolSession protocolSession = getProtocolSession(resourceId, sessionContext);
		if (protocolSession == null)
			throw new ProtocolException("Could not get a valid ProtocolSession");

		return protocolSession;
	}

	private ProtocolSessionContext createWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		protocolSessionContext.addParameter("protocol.mock", "true");
		return protocolSessionContext;
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

	public void makeConnectionAndCheck(ProteusOpticalSwitch proteus, IProtocolSessionManager protocolSessionManager) throws Exception {

		int connectionsNum = proteus.getFiberConnections().size();

		log.info("Number of connections: " + connectionsNum);

		log.info("Making connection...");
		MakeConnectionAction makeConnectionAction = new MakeConnectionAction();

		FiberConnection connection = newMakeConnectionParams(proteus);
		makeConnectionAction.setParams(connection);
		makeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

		/* it is executed at least one action */
		Assert.assertTrue(actionResponse.getResponses().size() > 0);

		/* all responses are correct */
		for (Response response : actionResponse.getResponses()) {
			Assert.assertTrue(response.getErrors().size() == 0);
		}

		checkModelIsUpdated(connection, proteus);

		log.info("Number of connections: " + proteus.getFiberConnections().size());
		Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum + 1);

		// justo to keep the state
		removeConnection(proteus, protocolSessionManager, connection);

	}

	private void checkModelIsUpdated(FiberConnection request, ProteusOpticalSwitch proteus) {

		ProteusOpticalSwitchCard srcCard = proteus.getCard(request.getSrcCard().getChasis(), request.getSrcCard().getSlot());
		FCPort srcPort = (FCPort) srcCard.getPort(request.getSrcPort().getPortNumber());
		DWDMChannel srcChannel = (DWDMChannel) ((WDMChannelPlan) srcCard.getChannelPlan()).getChannel(((WDMChannelPlan) srcCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getSrcFiberChannel().getLambda()));

		// check there is a subport of srcPort using desired channel
		WDMFCPort srcSubPort = (WDMFCPort) srcCard.getSubPort(srcPort, srcChannel);
		Assert.assertNotNull(srcSubPort);
		Assert.assertTrue(srcSubPort.getOutgoingDeviceConnections().size() > 0);

		ProteusOpticalSwitchCard dstCard = proteus.getCard(request.getDstCard().getChasis(), request.getDstCard().getSlot());
		FCPort dstPort = (FCPort) dstCard.getPort(request.getDstPort().getPortNumber());
		DWDMChannel dstChannel = (DWDMChannel) ((WDMChannelPlan) dstCard.getChannelPlan()).getChannel(((WDMChannelPlan) dstCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getDstFiberChannel().getLambda()));

		// check there is a subport of dstPort using desired channel
		WDMFCPort dstSubPort = (WDMFCPort) dstCard.getSubPort(dstPort, dstChannel);
		Assert.assertNotNull(dstSubPort);
		Assert.assertTrue(dstSubPort.getIncomingDeviceConnections().size() > 0);

		// check there is a deviceConnection chain between srcSupPort and dstSubPort
		WDMFCPort subPort1 = srcSubPort;
		int maxPortsInvolved = 4;
		int portHops = 0;
		// TODO CHECK WHILE CONDITION, its not checking ports are the same!!!
		while (((FCPort) subPort1.getDevices().get(0)).getPortNumber() != dstPort.getPortNumber()
				&& portHops < maxPortsInvolved) {

			// there is a port connected to subPort1 using same lambda
			WDMFCPort tmpSubPort = null;
			for (LogicalDevice port : subPort1.getOutgoingDeviceConnections()) {
				if (port instanceof WDMFCPort) {
					if (((WDMFCPort) port).getDWDMChannel().getLambda() == subPort1.getDWDMChannel().getLambda()) {
						tmpSubPort = (WDMFCPort) port;
						break;
					}
				}
			}
			Assert.assertNotNull(tmpSubPort);
			subPort1 = tmpSubPort;
			portHops++;
		}
		Assert.assertTrue(((FCPort) subPort1.getDevices().get(0)).getPortNumber() == dstPort.getPortNumber());

		// check there is a FiberConnection matching request
		boolean found = false;
		for (FiberConnection existentConnection : proteus.getFiberConnections()) {

			// match src
			if (existentConnection.getSrcCard().getChasis() == request.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getSlot() == request.getSrcCard().getSlot()) {
				if (existentConnection.getSrcFiberChannel().getLambda() == request.getSrcFiberChannel().getLambda()) {

					// match dst
					if (existentConnection.getDstCard().getChasis() == request.getDstCard().getChasis() &&
							existentConnection.getDstCard().getSlot() == request.getDstCard().getSlot()) {
						if (existentConnection.getDstFiberChannel().getLambda() == request.getDstFiberChannel().getLambda()) {
							found = true;
							break;
						}
					}

				}
			}
		}
		Assert.assertTrue(found);

	}

	private void removeConnection(ProteusOpticalSwitch proteus, IProtocolSessionManager protocolSessionManager, FiberConnection connection)
			throws ActionException {

		log.info("Removing previous connection ...");
		// remove previously made connection
		RemoveConnectionAction removeConnectionAction = new RemoveConnectionAction();
		removeConnectionAction.setParams(connection);
		removeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = removeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));
	}

	private ProtocolSessionManager getProtocolSessionManager() throws ProtocolException {

		ProtocolManager protocolManager = new ProtocolManager();
		ProtocolSessionManager protocolSessionManager = null;

		protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager("pedrosa");
		ProtocolSessionContext wonesysContext = newSessionContextWonesys();
		protocolManager.sessionFactoryAdded(new WonesysProtocolSessionFactory(), new HashMap<String, String>() {
			{
				put(ProtocolSessionContext.PROTOCOL, "wonesys");
			}
		});
		protocolSessionManager.registerContext(wonesysContext);

		return protocolSessionManager;
	}

	@Test
	public void connectionExistsTest() throws Exception {

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
		}

		OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
		ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

		int connectionsNum = proteus.getFiberConnections().size();

		IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

		FiberConnection connection = newRemoveConnectionParams(proteus);

		// make connection
		MakeConnectionAction makeConnectionAction = new MakeConnectionAction();
		makeConnectionAction.setParams(connection);
		makeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

		Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum + 1);

		// check connectionExisits works
		Assert.assertTrue(RemoveConnectionAction.connectionExists(connection, proteus));
	}

	private FiberConnection newRemoveConnectionParams(ProteusOpticalSwitch proteus) throws Exception {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		// DWDMChannel srcFiberChannel = new DWDMChannel();
		// srcFiberChannel.setLambda(1528.58);

		fiberConnection.setSrcCard(dropCard);
		fiberConnection.setSrcPort(srcPort);
		fiberConnection.setSrcFiberChannel(srcFiberChannel);

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		ProteusOpticalSwitchCard addCard = proteus.getCard(addChasis, addSlot);
		FCPort dstPort = ((WonesysPassiveAddCard) addCard).getCommonPort();

		DWDMChannel dstFiberChannel = (DWDMChannel) ((WDMChannelPlan) addCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		// DWDMChannel dstFiberChannel = new DWDMChannel();
		// dstFiberChannel.setLambda(1528.58);

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
		IProtocolSession protocolSession = factory.createProtocolSession(resourceId + sessionCounter, sessionContext);
		sessionCounter++;
		protocolSession.connect();
		return protocolSession;
	}

	public IProtocolSessionManager getMockProtocolSessionManager() {
		IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();
		return protocolSessionManager;
	}

	private void execActionsAndChecks(ProteusOpticalSwitch opticalSwitch1, IProtocolSessionManager sessionManager) throws ActionException {

		log.info("Refresh...");

		// refresh
		IAction action = new RefreshModelConnectionsAction();
		action.setModelToUpdate(opticalSwitch1);

		ActionResponse response = action.execute(sessionManager);
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkModelIsRefreshed((ProteusOpticalSwitch) action.getModelToUpdate());

		int initialConnectionsNum = opticalSwitch1.getFiberConnections().size();

		FiberConnection connection = newConnectionRequest(opticalSwitch1);

		log.info("MakeConnection...");

		// makeConnection
		action = new MakeConnectionAction();
		action.setModelToUpdate(opticalSwitch1);

		action.setParams(connection);

		response = action.execute(sessionManager);
		if (!response.getStatus().equals(STATUS.OK)) {
			Assert.fail(response.getInformation());
		}
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkConnectionAddedToModel(connection, opticalSwitch1);

		Assert.assertTrue(opticalSwitch1.getFiberConnections().size() == initialConnectionsNum + 1);

		log.info("RemoveConnection...");

		// removeConnection
		action = new RemoveConnectionAction();
		action.setModelToUpdate(opticalSwitch1);
		action.setParams(connection);

		response = action.execute(sessionManager);
		if (!response.getStatus().equals(STATUS.OK)) {
			Assert.fail(response.getInformation());
		}
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkConnectionRemovedFromModel(connection, opticalSwitch1);

		Assert.assertTrue(opticalSwitch1.getFiberConnections().size() == initialConnectionsNum);

	}

	private void checkConnectionRemovedFromModel(FiberConnection connection, ProteusOpticalSwitch opticalSwitch1) {

		Assert.assertFalse(RemoveConnectionAction.connectionExists(connection, opticalSwitch1));

		// TODO check subports have been removed
	}

	private void checkModelIsRefreshed(ProteusOpticalSwitch opticalSwitch) {
		Assert.assertTrue(opticalSwitch.getLogicalDevices().size() > 0);

		ProteusOpticalSwitchCard dropCard = null;
		for (LogicalDevice dev : opticalSwitch.getLogicalDevices()) {
			if (dev instanceof ProteusOpticalSwitchCard) {
				ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) dev;

				Assert.assertNotNull(card.getChannelPlan());
				Assert.assertTrue(card.getChannelPlan() instanceof WDMChannelPlan);

				if (card.getCardType().equals(CardType.ROADM_DROP)) {
					dropCard = card;

					int chassis = dropCard.getChasis();
					int slot = dropCard.getModuleNumber();

					Assert.assertTrue(WonesysCommand.toByteHexString(chassis, 1).length() == 2);
					Assert.assertTrue(WonesysCommand.toByteHexString(slot, 1).length() == 2);

					Assert.assertTrue(dropCard.getChannelPlan().getFirstChannel() >= 0);
					Assert.assertTrue(dropCard.getChannelPlan().getLastChannel() > dropCard.getChannelPlan().getFirstChannel());
					Assert.assertTrue(dropCard.getChannelPlan().getChannelGap() > 0);
					Assert.assertFalse(dropCard.getChannelPlan().getAllChannels().isEmpty());

					boolean internalConnectionsDetected = false;
					for (NetworkPort port : dropCard.getModulePorts()) {

						for (LogicalPort fcport : port.getPortsOnDevice()) {
							if (fcport instanceof FCPort) {
								Assert.assertTrue(((FCPort) fcport).getPortNumber() >= dropCard.getChannelPlan().getFirstChannel());
								Assert.assertTrue(((FCPort) fcport).getPortNumber() <= dropCard.getChannelPlan().getLastChannel());
							}
						}
						// when topology is complete:
						// module ports should be connected between each others (same card ports)
						internalConnectionsDetected = internalConnectionsDetected || !dropCard.getInternalConnections((FCPort) port)
								.isEmpty();
					}
					Assert.assertTrue(internalConnectionsDetected);

					// select SetChannel srcPort
					FCPort setSrcPort = ((WonesysDropCard) dropCard).getCommonPort();

					// select SetChannel dwdmChannel
					List<FiberChannel> freeChannels = dropCard.getFreeChannels(setSrcPort);
					int freeChannelsSize = freeChannels.size();
					DWDMChannel selectedChannel = (DWDMChannel) freeChannels.get(0);
					int dwdmChannel = selectedChannel.getChannelNumber();

					// select setChannel dstPort
					NetworkPort setDstPort = null;
					List<NetworkPort> reachablePorts = dropCard.getInternalConnections(setSrcPort);
					// selecting express port will create a passthrough connection
					if (reachablePorts.contains(((WonesysDropCard) dropCard).getExpressPort()))
						setDstPort = ((WonesysDropCard) dropCard).getExpressPort();
					Assert.assertNotNull(setDstPort);

				}
			}
		}

		for (FiberConnection connection : opticalSwitch.getFiberConnections()) {
			ProteusOpticalSwitchCard modelSrcCard = opticalSwitch.getCard(connection.getSrcCard().getChasis(), connection.getSrcCard().getSlot());
			ProteusOpticalSwitchCard modelDstCard = opticalSwitch.getCard(connection.getDstCard().getChasis(), connection.getDstCard().getSlot());
			Assert.assertTrue(connection.getSrcCard().equals(modelSrcCard));
			Assert.assertTrue(connection.getDstCard().equals(modelDstCard));

			Assert.assertTrue(connection.getSrcPort().equals(modelSrcCard.getPort(connection.getSrcPort().getPortNumber())));
			Assert.assertTrue(connection.getDstPort().equals(modelDstCard.getPort(connection.getDstPort().getPortNumber())));

			Assert.assertTrue(connection.getSrcFiberChannel().equals(
					((WDMChannelPlan) modelSrcCard.getChannelPlan()).getChannel(connection.getSrcFiberChannel().getNumChannel())));
			Assert.assertTrue(connection.getDstFiberChannel().equals(
					((WDMChannelPlan) modelDstCard.getChannelPlan()).getChannel(connection.getDstFiberChannel().getNumChannel())));

			// subPorts have been created
			WDMFCPort srcSubPort = null;
			for (LogicalPort subPort : connection.getSrcPort().getPortsOnDevice()) {
				if (subPort instanceof WDMFCPort) {
					if (((WDMFCPort) subPort).getDWDMChannel().getLambda() == connection.getSrcFiberChannel().getLambda()) {
						srcSubPort = (WDMFCPort) subPort;
						break;
					}
				}
			}
			Assert.assertNotNull(srcSubPort);
			Assert.assertFalse(srcSubPort.getOutgoingDeviceConnections().isEmpty());

			WDMFCPort dstSubPort = null;
			for (LogicalPort subPort : connection.getDstPort().getPortsOnDevice()) {
				if (subPort instanceof WDMFCPort) {
					if (((WDMFCPort) subPort).getDWDMChannel().getLambda() == connection.getDstFiberChannel().getLambda()) {
						dstSubPort = (WDMFCPort) subPort;
						break;
					}
				}
			}
			Assert.assertNotNull(dstSubPort);
			Assert.assertFalse(dstSubPort.getIncomingDeviceConnections().isEmpty());

			// there is a route from srcSubPort to dstSubPort
			WDMFCPort subPort = srcSubPort;
			int hopCounter = 0;
			while (!subPort.equals(dstSubPort) && hopCounter < 10) {
				subPort = (WDMFCPort) subPort.getOutgoingDeviceConnections().get(0);
				hopCounter++;
			}
			Assert.assertTrue(subPort.equals(dstSubPort));

		}

	}

	private void checkConnectionAddedToModel(FiberConnection request, ProteusOpticalSwitch proteus) {

		ProteusOpticalSwitchCard srcCard = proteus.getCard(request.getSrcCard().getChasis(), request.getSrcCard().getSlot());
		FCPort srcPort = (FCPort) srcCard.getPort(request.getSrcPort().getPortNumber());
		DWDMChannel srcChannel = (DWDMChannel) ((WDMChannelPlan) srcCard.getChannelPlan()).getChannel(((WDMChannelPlan) srcCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getSrcFiberChannel().getLambda()));

		// check there is a subport of srcPort using desired channel
		WDMFCPort srcSubPort = (WDMFCPort) srcCard.getSubPort(srcPort, srcChannel);
		Assert.assertNotNull(srcSubPort);
		Assert.assertTrue(srcSubPort.getOutgoingDeviceConnections().size() > 0);

		ProteusOpticalSwitchCard dstCard = proteus.getCard(request.getDstCard().getChasis(), request.getDstCard().getSlot());
		FCPort dstPort = (FCPort) dstCard.getPort(request.getDstPort().getPortNumber());
		DWDMChannel dstChannel = (DWDMChannel) ((WDMChannelPlan) dstCard.getChannelPlan()).getChannel(((WDMChannelPlan) dstCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getDstFiberChannel().getLambda()));

		// check there is a subport of dstPort using desired channel
		WDMFCPort dstSubPort = (WDMFCPort) dstCard.getSubPort(dstPort, dstChannel);
		Assert.assertNotNull(dstSubPort);
		Assert.assertTrue(dstSubPort.getIncomingDeviceConnections().size() > 0);

		// check there is a deviceConnection chain between srcSupPort and dstSubPort
		WDMFCPort subPort1 = srcSubPort;
		int maxPortsInvolved = 4;
		int portHops = 0;
		// TODO CHECK WHILE CONDITION, its not checking ports are the same!!!
		while (((FCPort) subPort1.getDevices().get(0)).getPortNumber() != dstPort.getPortNumber()
				&& portHops < maxPortsInvolved) {

			// there is a port connected to subPort1 using same lambda
			WDMFCPort tmpSubPort = null;
			for (LogicalDevice port : subPort1.getOutgoingDeviceConnections()) {
				if (port instanceof WDMFCPort) {
					if (((WDMFCPort) port).getDWDMChannel().getLambda() == subPort1.getDWDMChannel().getLambda()) {
						tmpSubPort = (WDMFCPort) port;
						break;
					}
				}
			}
			Assert.assertNotNull(tmpSubPort);
			subPort1 = tmpSubPort;
			portHops++;
		}
		Assert.assertTrue(((FCPort) subPort1.getDevices().get(0)).getPortNumber() == dstPort.getPortNumber());

		// check there is a FiberConnection matching request
		boolean found = false;
		for (FiberConnection existentConnection : proteus.getFiberConnections()) {

			// match src
			if (existentConnection.getSrcCard().getChasis() == request.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getSlot() == request.getSrcCard().getSlot()) {
				if (existentConnection.getSrcFiberChannel().getLambda() == request.getSrcFiberChannel().getLambda()) {

					// match dst
					if (existentConnection.getDstCard().getChasis() == request.getDstCard().getChasis() &&
							existentConnection.getDstCard().getSlot() == request.getDstCard().getSlot()) {
						if (existentConnection.getDstFiberChannel().getLambda() == request.getDstFiberChannel().getLambda()) {
							found = true;
							break;
						}
					}

				}
			}
		}
		Assert.assertTrue(found);

	}

	private FiberConnection newConnectionRequest(ProteusOpticalSwitch proteus) {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		double srcLambda = srcFiberChannel.getLambda();

		// DWDMChannel srcFiberChannel = new DWDMChannel();
		// srcFiberChannel.setLambda(1528.58);

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

		// DWDMChannel dstFiberChannel = new DWDMChannel();
		// dstFiberChannel.setLambda(1528.58);

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

}
