package net.i2cat.luminis.commandsKaraf.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.WDMChannelPlan;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.luminis.commandsKaraf.tests.RepositoryHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;

/**
 * Spring week 26 <br/>
 * http://jira.i2cat.net:8080/browse/MANTYCHORE-156
 * 
 * @author isart
 */
@RunWith(JUnit4TestRunner.class)
public class ConnectionsKarafCommandsTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory
												.getLog(ConnectionsKarafCommandsTest.class);

	IResourceRepository	repository;
	IProfileManager		profileManager;

	String				resourceName	= "pedrosa";
	String				chassisNum		= "0";
	String				srcCardNum		= "1";
	String				dstCardNum		= "17";
	String				srcPortNum		= "0";
	String				dstPortNum		= "129";
	int					channelNum		= 32;

	@Test
	public void getInventoryCommandBasicTest() {

		initBundles();
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertNotNull(resource);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			try {

				// FIXME refresh should skip the queue
				log.debug("executeCommand(connections:getInventory -r " + resourceFriendlyID + ")");
				String responseStr = (String) executeCommand("connections:getInventory -r " + resourceFriendlyID);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the getInventory command");

				// check model is updated
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

				log.debug("executeCommand(connections:getInventory " + resourceFriendlyID + ")");
				responseStr = (String) executeCommand("connections:getInventory " + resourceFriendlyID);
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

			} catch (Exception e) {

				e.printStackTrace();
				Assert.fail(e.getLocalizedMessage());
			} finally {
				repository.stopResource(resource.getResourceIdentifier().getId());
				repository.removeResource(resource.getResourceIdentifier().getId());

				Thread.sleep(10000);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Assert.fail(e.getLocalizedMessage());
		}
	}

	//@Test
	public void makeConnectionAndListCommands/*Test*/() throws Exception {

		initBundles();
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			log.info("Creating resource...");
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertNotNull(resource);

			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			try {

				// TODO should refresh manually????
				// FIXME refresh should skip the queue
				log.info("executeCommand(connections:getInventory -r " + resourceFriendlyID + ")");
				String responseStr = (String) executeCommand("connections:getInventory -r " + resourceFriendlyID);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the getInventory command");

				String srcPortId = chassisNum + "-" + srcCardNum + "-" + srcPortNum;
				String dstPortId = chassisNum + "-" + dstCardNum + "-" + dstPortNum;

				WDMChannelPlan channelPlan = (WDMChannelPlan) ((ProteusOpticalSwitch) resource.getModel()).getCard(Integer.parseInt(chassisNum),
						Integer.parseInt(srcCardNum)).getChannelPlan();
				DWDMChannel channel = (DWDMChannel) channelPlan.getChannel(32);
				double lambda = channel.getLambda();

				log.info("executeCommand(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
				responseStr = (String) executeCommand("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the makeConnection command");

				log.info("executeCommand(queue:execute " + resourceFriendlyID + ")");
				Integer response = (Integer) executeCommand("queue:execute " + resourceFriendlyID);
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

				log.info("executeCommand(connections:list " + resourceFriendlyID);
				responseStr = (String) executeCommand("connections:list " + resourceFriendlyID);
				if (responseStr != null)
					Assert.fail("Error in the listConnections command");

			} catch (Exception e) {

				e.printStackTrace();
				Assert.fail(e.getLocalizedMessage());

			} finally {
				repository.stopResource(resource.getResourceIdentifier().getId());
				repository.removeResource(resource.getResourceIdentifier().getId());
				Thread.sleep(10000);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Thread.sleep(5000);
			Assert.fail(e.getLocalizedMessage());
		}

	}

	//@Test
	public void removeConnectionCommands/*Test*/() {

		initBundles();
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			log.info("Creating resource...");
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertNotNull(resource);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			try {

				// TODO should refresh manually????
				// FIXME refresh should skip the queue
				log.info("executeCommand(connections:getInventory -r " + resourceFriendlyID + ")");
				String responseStr = (String) executeCommand("connections:getInventory -r " + resourceFriendlyID);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in getInventory command");

				String srcPortId = chassisNum + "-" + srcCardNum + "-" + srcPortNum;
				String dstPortId = chassisNum + "-" + dstCardNum + "-" + dstPortNum;

				WDMChannelPlan channelPlan = (WDMChannelPlan) ((ProteusOpticalSwitch) resource.getModel()).getCard(Integer.parseInt(chassisNum),
						Integer.parseInt(srcCardNum)).getChannelPlan();
				DWDMChannel channel = (DWDMChannel) channelPlan.getChannel(32);
				double lambda = channel.getLambda();

				log.info("executeCommand(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
				responseStr = (String) executeCommand("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the makeConnection command");

				// should print out of date information
				log.info("executeCommand(connections:list " + resourceFriendlyID);
				responseStr = (String) executeCommand("connections:list " + resourceFriendlyID);
				if (responseStr != null)
					Assert.fail("Error in the listConnections command");

				log.info("executeCommand(connections:removeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
				responseStr = (String) executeCommand("connections:removeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the removeConnection command");

				log.info("executeCommand(queue:execute " + resourceFriendlyID + ")");
				Integer response = (Integer) executeCommand("queue:execute " + resourceFriendlyID);
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

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getLocalizedMessage());
			} finally {
				repository.stopResource(resource.getResourceIdentifier().getId());
				repository.removeResource(resource.getResourceIdentifier().getId());
				Thread.sleep(10000);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Assert.fail(e.getLocalizedMessage());
		}

	}

	//@Test
	public void getInventoryCommandComplete/*Test*/() {
		// connections:getInventory
		// cards, number of connections
		// -r (refresh model before)

		initBundles();
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("roadm", resourceName);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newConnectionsCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertNotNull(resource);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			try {

				// FIXME refresh should skip the queue
				log.debug("executeCommand(connections:getInventory -r " + resourceFriendlyID + ")");
				String responseStr = (String) executeCommand("connections:getInventory -r " + resourceFriendlyID);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the getInventory command");

				// check model is updated
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

				log.debug("executeCommand(connections:getInventory " + resourceFriendlyID + ")");
				responseStr = (String) executeCommand("connections:getInventory " + resourceFriendlyID);
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

				log.info("executeCommand(connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda + ")");
				responseStr = (String) executeCommand("connections:makeConnection " + resourceFriendlyID + " " + srcPortId + " " + lambda + " " + dstPortId + " " + lambda);
				log.debug(responseStr);
				// Assert.assertNotNull(response);
				if (responseStr != null)
					Assert.fail("Error in the makeConnection command");

				log.debug("executeCommand(queue:execute " + resourceFriendlyID + ")");
				Integer response = (Integer) executeCommand("queue:execute " + resourceFriendlyID);
				log.debug(response);
				// Assert.assertNotNull(response);
				if (response != null)
					Assert.fail("Error in the execute queue command");

				log.debug("executeCommand(connections:getInventory " + resourceFriendlyID + ")");
				responseStr = (String) executeCommand("connections:getInventory " + resourceFriendlyID);
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

			} catch (Exception e) {

				e.printStackTrace();
				Assert.fail(e.getLocalizedMessage());
			} finally {
				repository.stopResource(resource.getResourceIdentifier().getId());
				repository.removeResource(resource.getResourceIdentifier().getId());
				Thread.sleep(10000);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Assert.fail(e.getLocalizedMessage());
		}

	}

	public void initBundles() {
		
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		
		log.info("Getting services...");

		repository = getOsgiService(IResourceRepository.class, "type=roadm", 50000);
		profileManager = getOsgiService(IProfileManager.class, 25000);

		Assert.assertNotNull(repository);
		Assert.assertNotNull(profileManager);

		log.info("INFO: Initialized!");

	}

	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	@Configuration
	public Option[] additionalConfiguration() throws Exception {
		return combine(configuration(), new Customizer() {
			@Override
			public InputStream customizeTestProbe(InputStream testProbe) throws Exception {
				return TinyBundles.modifyBundle(testProbe).set(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional").build();
			}
		});
	}
	
	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
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

	public Object executeCommand(String command) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = getOsgiService(CommandProcessor.class);
		ByteArrayOutputStream outputError = new ByteArrayOutputStream();
		PrintStream psE = new PrintStream(outputError);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);
		CommandSession cs = cp.createSession(System.in, ps, psE);
		Object commandOutput = null;
		try {
			commandOutput = cs.execute(command);
			return commandOutput;
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		}

		cs.close();
		return commandOutput;
	}

}
