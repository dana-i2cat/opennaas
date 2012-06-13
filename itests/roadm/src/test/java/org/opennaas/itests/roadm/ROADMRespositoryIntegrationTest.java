package org.opennaas.itests.roadm;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
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
public class ROADMRespositoryIntegrationTest
{
	private final static Log	log				= LogFactory.getLog(ROADMRespositoryIntegrationTest.class);

	@Inject
	@Filter("(type=roadm)")
	private IResourceRepository	repository;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IResourceManager	resourceManger;

	private IResource			mockResource	= new MockResource();
	private List<String>		startupActionNames;
	private AbstractCapability	connectionsCapability;

	@Inject
	@Filter("(capability=connections)")
	private ICapabilityFactory	connectionFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.repository)")
	private BlueprintContainer	repositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	private BlueprintContainer	wonesysProtocolService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextWonesysMock() {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				"protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		return protocolSessionContext;
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextWonesysMock());

	}

	@Test
	public void RemoveAndCreateResource() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptorProteus("roadm");

		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertFalse(repository.listResources().isEmpty());

		createProtocolForResource(resource.getResourceIdentifier().getId());

		repository.removeResource(resource.getResourceIdentifier().getId());
		Assert.assertTrue(repository.listResources().isEmpty());
	}

	@Test
	public void StartAndStopResource() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptorProteus("roadm");

		/* create resource */
		IResource resource = repository.createResource(resourceDescriptor);

		Assert.assertNotNull(resource.getResourceIdentifier());
		Assert.assertNotNull(resource.getResourceDescriptor());
		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		Assert.assertNull(resource.getProfile());

		Assert.assertFalse(repository.listResources().isEmpty());

		createProtocolForResource(resource.getResourceIdentifier().getId());

		/* start resource */
		repository.startResource(resource.getResourceIdentifier().getId());
		Assert.assertFalse(resource.getCapabilities().isEmpty());
		Assert.assertNotNull(resource.getModel());
		// Assert.assertNotNull(resource.getProfile());

		/* stop resource */
		repository.stopResource(resource.getResourceIdentifier().getId());

		Assert.assertNotNull(resource.getResourceIdentifier());
		Assert.assertNotNull(resource.getResourceDescriptor());
		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		// Assert.assertNull(resource.getProfile());
		Assert.assertFalse(repository.listResources().isEmpty());

		/* remove resource */

		repository.removeResource(resource.getResourceIdentifier().getId());

		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		Assert.assertNull(resource.getProfile());
		// Assert.assertTrue(repository.listResources().isEmpty());
	}

	@Test
	public void repoIsPublishedInResourceManagerTest() throws ResourceException, ProtocolException {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptorProteus("roadm");

		IResource resource = resourceManger.createResource(resourceDescriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManger.startResource(resource.getResourceIdentifier());
		resourceManger.stopResource(resource.getResourceIdentifier());
		resourceManger.removeResource(resource.getResourceIdentifier());
	}

	@Test
	public void MakeRemoveConnectionsResourceTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptorProteus("roadm");

		/* create resource */
		IResource resource = repository.createResource(resourceDescriptor);

		Assert.assertNotNull(resource.getResourceIdentifier());
		Assert.assertNotNull(resource.getResourceDescriptor());
		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		Assert.assertNull(resource.getProfile());

		Assert.assertFalse(repository.listResources().isEmpty());

		// add protocol for resource
		createProtocolForResource(resource.getResourceIdentifier().getId());

		/* start resource */
		repository.startResource(resource.getResourceIdentifier().getId());
		Assert.assertFalse(resource.getCapabilities().isEmpty());
		Assert.assertNotNull(resource.getModel());
		// Assert.assertNotNull(resource.getProfile());

		IConnectionsCapability connections = (IConnectionsCapability) resource.getCapabilityByInterface(IConnectionsCapability.class);
		if (connections == null)
			Assert.fail("Capability not found");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		if (queueCapability == null)
			Assert.fail("Capability not found");

		/* checking model */
		// TODO CHECK NEW MODEL CONFIG

		/* refresh connection */
		// Response resp = (Response) connections.sendMessage(ActionConstants.REFRESHCONNECTIONS, null);
		// List<ActionResponse> responses = (List<ActionResponse>) queueCapability.sendMessage(QueueManagerConstants.EXECUTE, null);
		//
		// Assert.assertTrue(responses.size() == 2);
		// ActionResponse actionResponse = responses.get(0);
		// Assert.assertEquals(ActionConstants.REFRESHCONNECTIONS, actionResponse.getActionID());
		// for (Response response : actionResponse.getResponses()) {
		// Assert.assertTrue(response.getStatus() == Response.Status.OK);
		// }
		//
		// List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueManagerConstants.GETQUEUE, null);
		// Assert.assertTrue(queue.size() == 0);

		OpticalSwitchFactory opticalSwitchFactory = new OpticalSwitchFactory();
		resource.setModel(opticalSwitchFactory.newPedrosaProteusOpticalSwitch());

		/* make connection */
		FiberConnection connectionRequest = newMakeConnectionParams((ProteusOpticalSwitch) resource.getModel());
		connections.makeConnection(connectionRequest);
		QueueResponse queueResponse = queueCapability.execute();

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

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 0);

		/* checking model */
		Assert.assertNotNull(getFiberConnection(connectionRequest, (ProteusOpticalSwitch) resource.getModel()));
		// checking for model intern details is in action tests

		/* remove connection */
		connections.removeConnection(connectionRequest);
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

		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 0);

		/* checking model */
		Assert.assertNull(getFiberConnection(connectionRequest, (ProteusOpticalSwitch) resource.getModel()));

		/* stop resource */
		repository.stopResource(resource.getResourceIdentifier().getId());

		Assert.assertNotNull(resource.getResourceIdentifier());
		Assert.assertNotNull(resource.getResourceDescriptor());
		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		// Assert.assertNull(resource.getProfile());
		Assert.assertFalse(repository.listResources().isEmpty());

		/* remove resource */

		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.removeResource(resource.getResourceIdentifier().getId());

		Assert.assertTrue(resource.getCapabilities().isEmpty());
		Assert.assertNull(resource.getModel());
		Assert.assertNull(resource.getProfile());
		// Assert.assertTrue(repository.listResources().isEmpty());
	}

	/**
	 * tests ConnectionsCapability.getActionSet().getStartupRefreshAction() != null
	 */
	@Test
	public void getStartUpRefreshActionTest() throws Exception {

		mockResource.setResourceDescriptor(ResourceHelper.newResourceDescriptorProteus("roadm"));

		// Test elements not null
		log.info("Checking connections factory");
		Assert.assertNotNull(connectionFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("connections"));
		log.info("Creating connection capability");
		connectionsCapability = (AbstractCapability) connectionFactory.create(mockResource);
		Assert.assertNotNull(connectionsCapability);
		connectionsCapability.initialize();

		Assert.assertFalse(connectionsCapability.getActionSet().getRefreshActionName().isEmpty());
		startupActionNames = connectionsCapability.getActionSet().getRefreshActionName();
	}

	@After
	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[repository.listResources().size()];
		toRemove = repository.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				try {
					repository.stopResource(resource.getResourceIdentifier().getId());
				} catch (ResourceException e) {
					log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				}
			}
			try {
				repository.removeResource(resource.getResourceIdentifier().getId());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}

		}

		log.info("Resource repo cleared!");
	}

	public ICapability getCapability(List<ICapability> capabilities, String type) {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		return null;
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

	public static FiberConnection getFiberConnection(FiberConnection connection, ProteusOpticalSwitch switchModel) {

		FiberConnection matchingConnection = null;

		for (FiberConnection existentConnection : switchModel.getFiberConnections()) {
			if ( // same cards
			existentConnection.getSrcCard().getChasis() == connection.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getModuleNumber() == connection.getSrcCard().getModuleNumber() &&
					existentConnection.getDstCard().getChasis() == connection.getDstCard().getChasis() &&
					existentConnection.getDstCard().getModuleNumber() == connection.getDstCard().getModuleNumber() &&
					// same ports
					existentConnection.getSrcPort().getPortNumber() == connection.getSrcPort().getPortNumber() &&
					existentConnection.getDstPort().getPortNumber() == connection.getDstPort().getPortNumber() &&
					// same channels
					existentConnection.getSrcFiberChannel().getLambda() == connection.getSrcFiberChannel().getLambda() &&
					existentConnection.getDstFiberChannel().getLambda() == connection.getDstFiberChannel().getLambda()) {
				matchingConnection = existentConnection;
				break;
			}
		}

		return matchingConnection;
	}
}
