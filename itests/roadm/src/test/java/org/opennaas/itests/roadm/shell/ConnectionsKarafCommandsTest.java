package org.opennaas.itests.roadm.shell;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.opennaas.extensions.itests.helpers.AbstractKarafCommandTest;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.itests.roadm.helpers.RepositoryHelper;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandSession;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Spring week 26 <br/>
 * http://jira.i2cat.net:8080/browse/MANTYCHORE-156
 *
 * @author isart
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ConnectionsKarafCommandsTest extends AbstractKarafCommandTest
{
	static Log log =
		LogFactory.getLog(ConnectionsKarafCommandsTest.class);

	@Inject
	private BundleContext		bundleContext;

	@Inject @Filter("(type=roadm)")
	private IResourceRepository	repository;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	private IProtocolManager    protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.capability.connections)")
	private BlueprintContainer connectionService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	private BlueprintContainer	wonesysProtocolService;

	String				resourceName	= "pedrosa";
	String				chassisNum		= "0";
	String				srcCardNum		= "1";
	String				dstCardNum		= "17";
	String				srcPortNum		= "0";
	String				dstPortNum		= "129";
	int					channelNum		= 32;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	@Test
	public void getInventoryCommandBasicTest() throws Exception
	{
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertNotNull(resource);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		try {
			String responseStr;

			// // FIXME refresh should skip the queue
			// log.debug("executeCommandWithResponse(connections:getInventory -r " + resourceFriendlyID + ")");
			// responseStr = (String) executeCommandWithResponse("connections:getInventory -r " + resourceFriendlyID);
			// log.debug(responseStr);
			// // Assert.assertNotNull(response);
			// if (responseStr != null)
			// Assert.fail("Error in the getInventory command");

			// check model is updated (start Resource should get Inventory)
			ProteusOpticalSwitch proteus = (ProteusOpticalSwitch) resource.getModel();
			Assert.assertFalse(proteus.getLogicalDevices().isEmpty());
			Assert.assertFalse(proteus.getFiberConnections().isEmpty());
			int connectionsInitialSize = proteus.getFiberConnections().size();

			ProteusOpticalSwitchCard srcCard = proteus.getCard(0, Integer.parseInt(srcCardNum));
			Assert.assertNotNull(srcCard);
			Assert.assertNotNull(srcCard.getChannelPlan());

			ProteusOpticalSwitchCard dstCard = proteus.getCard(0, Integer.parseInt(dstCardNum));
			Assert.assertNotNull(dstCard);
			Assert.assertNotNull(dstCard.getChannelPlan());

			log.debug("executeCommandWithResponse(connections:getInventory " + resourceFriendlyID + ")");
			responseStr = (String) executeCommandWithResponse("connections:getInventory " + resourceFriendlyID);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the getInventory command");

			// check model is updated
			proteus = (ProteusOpticalSwitch) resource.getModel();
			Assert.assertFalse(proteus.getLogicalDevices().isEmpty());
			Assert.assertFalse(proteus.getFiberConnections().isEmpty());

			srcCard = proteus.getCard(0, Integer.parseInt(srcCardNum));
			Assert.assertNotNull(srcCard);
			Assert.assertNotNull(srcCard.getChannelPlan());

			dstCard = proteus.getCard(0, Integer.parseInt(dstCardNum));
			Assert.assertNotNull(dstCard);
			Assert.assertNotNull(dstCard.getChannelPlan());

		} finally {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		}
	}

	@Test
	public void makeConnectionAndListCommandsTest() throws Exception {

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		log.info("Creating resource...");
		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertNotNull(resource);

		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		try {

			String responseStr;

			// // TODO should refresh manually????
			// // FIXME refresh should skip the queue
			// log.info("executeCommandWithResponse(connections:getInventory " + resourceFriendlyID + ")");
			// responseStr = (String) executeCommandWithResponse("connections:getInventory " + resourceFriendlyID);
			// log.debug(responseStr);
			// // Assert.assertNotNull(response);
			// if (responseStr != null)
			// Assert.fail("Error in the getInventory command");

			String srcPortId = chassisNum + "-" + srcCardNum + "-" + srcPortNum;
			String dstPortId = chassisNum + "-" + dstCardNum + "-" + dstPortNum;

			WDMChannelPlan channelPlan = (WDMChannelPlan) ((ProteusOpticalSwitch) resource.getModel()).getCard(Integer.parseInt(chassisNum),
																											   Integer.parseInt(srcCardNum)).getChannelPlan();
			DWDMChannel channel = (DWDMChannel) channelPlan.getChannel(32);
			double lambda = channel.getLambda();

			log.info("executeCommandWithResponse(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
			responseStr = (String) executeCommandWithResponse("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the makeConnection command");

			log.info("executeCommandWithResponse(queue:execute " + resourceFriendlyID + ")");
			Integer response = (Integer) executeCommandWithResponse("queue:execute " + resourceFriendlyID);
			log.debug(response);
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the execute queue command");

			// check model is updated
			ProteusOpticalSwitch proteus = (ProteusOpticalSwitch) resource.getModel();
			boolean found = false;
			for (FiberConnection connection : proteus.getFiberConnections()) {
				if (connection.getSrcCard().getModuleNumber() == Integer.parseInt(srcCardNum) &&
					connection.getDstCard().getModuleNumber() == Integer.parseInt(dstCardNum) &&
					connection.getSrcPort().getPortNumber() == Integer.parseInt(srcPortNum) &&
					connection.getDstPort().getPortNumber() == Integer.parseInt(dstPortNum) &&
					connection.getSrcFiberChannel().getLambda() == lambda &&
					connection.getDstFiberChannel().getLambda() == lambda) {
					found = true;
					break;
				}
			}
			Assert.assertTrue(found);

			log.info("executeCommandWithResponse(connections:list " + resourceFriendlyID);
			responseStr = (String) executeCommandWithResponse("connections:list " + resourceFriendlyID);
			if (responseStr != null)
				Assert.fail("Error in the listConnections command");

		} finally {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		}
	}

	@Test
	public void removeConnectionCommandsTest() throws Exception {

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		log.info("Creating resource...");
		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertNotNull(resource);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		try {

			String responseStr;
			// // TODO should refresh manually????
			// // FIXME refresh should skip the queue
			// log.info("executeCommandWithResponse(connections:getInventory -r " + resourceFriendlyID + ")");
			// responseStr = (String) executeCommandWithResponse("connections:getInventory -r " + resourceFriendlyID);
			// log.debug(responseStr);
			// // Assert.assertNotNull(response);
			// if (responseStr != null)
			// Assert.fail("Error in getInventory command");

			String srcPortId = chassisNum + "-" + srcCardNum + "-" + srcPortNum;
			String dstPortId = chassisNum + "-" + dstCardNum + "-" + dstPortNum;

			WDMChannelPlan channelPlan = (WDMChannelPlan) ((ProteusOpticalSwitch) resource.getModel()).getCard(Integer.parseInt(chassisNum),
																											   Integer.parseInt(srcCardNum)).getChannelPlan();
			DWDMChannel channel = (DWDMChannel) channelPlan.getChannel(32);
			double lambda = channel.getLambda();

			log.info("executeCommandWithResponse(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
			responseStr = (String) executeCommandWithResponse("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the makeConnection command");

			// should print out of date information
			log.info("executeCommandWithResponse(connections:list " + resourceFriendlyID);
			responseStr = (String) executeCommandWithResponse("connections:list " + resourceFriendlyID);
			if (responseStr != null)
				Assert.fail("Error in the listConnections command");

			log.info("executeCommandWithResponse(connections:removeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
			responseStr = (String) executeCommandWithResponse("connections:removeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the removeConnection command");

			log.info("executeCommandWithResponse(queue:execute " + resourceFriendlyID + ")");
			Integer response = (Integer) executeCommandWithResponse("queue:execute " + resourceFriendlyID);
			log.debug(response);
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the execute queue command");

			// check model is updated
			ProteusOpticalSwitch proteus = (ProteusOpticalSwitch) resource.getModel();
			boolean found = false;
			for (FiberConnection connection : proteus.getFiberConnections()) {
				if (connection.getSrcCard().getModuleNumber() == Integer.parseInt(srcCardNum) &&
					connection.getDstCard().getModuleNumber() == Integer.parseInt(dstCardNum) &&
					connection.getSrcPort().getPortNumber() == Integer.parseInt(srcPortNum) &&
					connection.getDstPort().getPortNumber() == Integer.parseInt(dstPortNum) &&
					connection.getSrcFiberChannel().getLambda() == lambda &&
					connection.getDstFiberChannel().getLambda() == lambda) {
					found = true;
					break;
				}
			}
			Assert.assertFalse(found);

		} finally {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		}
	}

	@Test
	public void getInventoryCommandCompleteTest() throws Exception {
		// connections:getInventory
		// cards, number of connections
		// -r (refresh model before)

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertNotNull(resource);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		try {

			String responseStr;

			// // FIXME refresh should skip the queue
			// log.debug("executeCommandWithResponse(connections:getInventory " + resourceFriendlyID + ")");
			// responseStr = (String) executeCommandWithResponse("connections:getInventory " + resourceFriendlyID);
			// log.debug(responseStr);
			// // Assert.assertNotNull(response);
			// if (responseStr != null)
			// Assert.fail("Error in the getInventory command");

			// check model is updated (startResource should have loaded inventory)
			ProteusOpticalSwitch proteus = (ProteusOpticalSwitch) resource.getModel();
			Assert.assertFalse(proteus.getLogicalDevices().isEmpty());
			Assert.assertFalse(proteus.getFiberConnections().isEmpty());
			int connectionsInitialSize = proteus.getFiberConnections().size();

			ProteusOpticalSwitchCard srcCard = proteus.getCard(0, Integer.parseInt(srcCardNum));
			Assert.assertNotNull(srcCard);
			Assert.assertNotNull(srcCard.getChannelPlan());

			ProteusOpticalSwitchCard dstCard = proteus.getCard(0, Integer.parseInt(dstCardNum));
			Assert.assertNotNull(dstCard);
			Assert.assertNotNull(dstCard.getChannelPlan());

			log.debug("executeCommandWithResponse(connections:getInventory " + resourceFriendlyID + ")");
			responseStr = (String) executeCommandWithResponse("connections:getInventory " + resourceFriendlyID);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the getInventory command");

			// check model is updated
			proteus = (ProteusOpticalSwitch) resource.getModel();
			Assert.assertFalse(proteus.getLogicalDevices().isEmpty());
			Assert.assertFalse(proteus.getFiberConnections().isEmpty());

			srcCard = proteus.getCard(0, Integer.parseInt(srcCardNum));
			Assert.assertNotNull(srcCard);
			Assert.assertNotNull(srcCard.getChannelPlan());

			dstCard = proteus.getCard(0, Integer.parseInt(dstCardNum));
			Assert.assertNotNull(dstCard);
			Assert.assertNotNull(dstCard.getChannelPlan());

			String srcPortId = chassisNum + "-" + srcCardNum + "-" + srcPortNum;
			String dstPortId = chassisNum + "-" + dstCardNum + "-" + dstPortNum;

			WDMChannelPlan channelPlan = (WDMChannelPlan) ((ProteusOpticalSwitch) resource.getModel()).getCard(Integer.parseInt(chassisNum),
																											   Integer.parseInt(srcCardNum)).getChannelPlan();
			DWDMChannel channel = (DWDMChannel) channelPlan.getChannel(32);
			double lambda = channel.getLambda();

			log.info("executeCommandWithResponse(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
			responseStr = (String) executeCommandWithResponse("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the makeConnection command");

			log.debug("executeCommandWithResponse(queue:execute " + resourceFriendlyID + ")");
			Integer response = (Integer) executeCommandWithResponse("queue:execute " + resourceFriendlyID);
			log.debug(response);
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the execute queue command");

			log.debug("executeCommandWithResponse(connections:getInventory " + resourceFriendlyID + ")");
			responseStr = (String) executeCommandWithResponse("connections:getInventory " + resourceFriendlyID);
			log.debug(responseStr);
			// Assert.assertNotNull(response);
			if (responseStr != null)
				Assert.fail("Error in the getInventory command");

			// check model is updated
			proteus = (ProteusOpticalSwitch) resource.getModel();
			Assert.assertFalse(proteus.getLogicalDevices().isEmpty());
			Assert.assertFalse(proteus.getFiberConnections().isEmpty());
			Assert.assertTrue(proteus.getFiberConnections().size() > connectionsInitialSize);

			srcCard = proteus.getCard(0, Integer.parseInt(srcCardNum));
			Assert.assertNotNull(srcCard);
			Assert.assertNotNull(srcCard.getChannelPlan());

			dstCard = proteus.getCard(0, Integer.parseInt(dstCardNum));
			Assert.assertNotNull(dstCard);
			Assert.assertNotNull(dstCard.getChannelPlan());
		} finally {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		}
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newWonesysSessionContext());

	}

	private ProtocolSessionContext newWonesysSessionContext() {
		// String uri = System.getProperty("protocol.uri");
		// if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
		// uri = "mock://user:pass@host.net:2212/mocksubsystem";
		// }

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter("protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");

		// ADDED
		return protocolSessionContext;
	}

	public Object executeCommandWithResponse(String command) throws Exception {
		// Run some commands to make sure they are installed properly
		ByteArrayOutputStream outputError = new ByteArrayOutputStream();
		PrintStream psE = new PrintStream(outputError);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);
		CommandSession cs = commandProcessor.createSession(System.in, ps, psE);
		Object commandOutput = null;
		try {
			commandOutput = cs.execute(command);
			return commandOutput;
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		} finally {
			cs.close();
		}
		return commandOutput;
	}
}
