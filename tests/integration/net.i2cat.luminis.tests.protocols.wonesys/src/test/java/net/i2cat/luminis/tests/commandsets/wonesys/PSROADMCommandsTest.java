package net.i2cat.luminis.tests.commandsets.wonesys;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.util.List;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.GetInventoryCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.psroadm.GetChannelPlan;
import net.i2cat.luminis.commandsets.wonesys.commands.psroadm.GetChannels;
import net.i2cat.luminis.commandsets.wonesys.commands.psroadm.SetChannel;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import org.opennaas.core.resources.command.Command;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class PSROADMCommandsTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	Log						log				= LogFactory.getLog(PSROADMCommandsTest.class);

	private String			resourceId		= "Proteus-Pedrosa";
	private String			hostIpAddress	= "10.10.80.11";
	private String			hostPort		= "27773";

	@Inject
	private BundleContext	bundleContext;

	@Configuration
	public static Option[] configure() {
		return combine(
						IntegrationTestsHelper.getLuminisTestOptions(),
						mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper")
		// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006")
		);
	}

	public void loadBundlesAndServices() {

		assertNotNull(bundleContext);

		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 20000);
		assertNotNull(protocolManager);
	}

	@Test
	public void testROADCommands() throws CommandException, ProtocolException {

		// try {
		//
		// loadBundlesAndServices();
		//
		// WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);
		//
		// // TODO get session
		//
		// ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		//
		// // getInventory
		// WonesysCommand c = new GetInventoryCommand();
		// c.initialize();
		// String response = (String) session.sendReceive(c.message());
		// Response resp = c.checkResponse(response);
		// c.parseResponse(resp, opticalSwitch1);
		//
		// Assert.assertTrue(opticalSwitch1.getLogicalDevices().size() > 0);
		//
		// // obtain ROADM drop card
		// ProteusOpticalSwitchCard dropCard = null;
		// for (LogicalDevice dev : opticalSwitch1.getLogicalDevices()) {
		// if (dev instanceof ProteusOpticalSwitchCard) {
		// ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) dev;
		// if (card.getCardType().equals(CardType.ROADM_DROP)) {
		// dropCard = card;
		//
		// int chassisNum = dropCard.getChasis();
		// int slotNum = dropCard.getModuleNumber();
		//
		// // String chassis = Integer.toHexString(chassisNum);
		// // if (chassis.length() == 1)
		// // chassis = "0" + chassis;
		// //
		// // String slot = Integer.toHexString(slotNum);
		// // if (slot.length() == 1)
		// // slot = "0" + slot;
		//
		// // getChannelPlan
		// c = new GetChannelPlan(chassisNum, slotNum);
		// c.initialize();
		// response = (String) session.sendReceive(c.message());
		// resp = c.checkResponse(response);
		// c.parseResponse(resp, opticalSwitch1);
		//
		// Assert.assertTrue(dropCard.getChannelPlan().getFirstChannel() >= 0);
		// Assert.assertTrue(dropCard.getChannelPlan().getLastChannel() > dropCard.getChannelPlan().getFirstChannel());
		// Assert.assertTrue(dropCard.getChannelPlan().getChannelGap() > 0);
		// Assert.assertTrue(dropCard.getChannelPlan().getAllChannels().size() > 0);
		//
		// for (NetworkPort port : dropCard.getModulePorts()) {
		// Assert.assertTrue(port.getPortsOnDevice().size() == 0);
		// }
		//
		// // getChannels
		// c = new GetChannels(chassisNum, slotNum);
		// c.initialize();
		// response = (String) session.sendReceive(c.message());
		// resp = c.checkResponse(response);
		// c.parseResponse(resp, opticalSwitch1);
		//
		// for (NetworkPort port : dropCard.getModulePorts()) {
		// for (LogicalPort fcport : port.getPortsOnDevice()) {
		// if (fcport instanceof FCPort) {
		// Assert.assertTrue(((FCPort) fcport).getPortNumber() >= dropCard.getChannelPlan().getFirstChannel());
		// Assert.assertTrue(((FCPort) fcport).getPortNumber() <= dropCard.getChannelPlan().getLastChannel());
		// // module ports should be connected between each others (other card module ports)
		// Assert.assertTrue(((FCPort) fcport).getOutgoingDeviceConnections().size() > 0);
		// }
		// }
		// }

		// FIXME remove opticalSwitchCoontroller references

		// // select SetChannel dwdmChannel
		// List<Integer> freeChannels = OpticalSwitchController.getFreeChannels((FCPort) dropCard.getModulePorts().get(0));
		// int freeChannelsSize = freeChannels.size();
		// int dwdmChannel = freeChannels.get(0);
		//
		// // select setChannel dstPort
		// NetworkPort dstInternPort = null;
		// List<NetworkPort> reachablePorts = OpticalSwitchController.getReachablePorts(dropCard.getModulePorts().get(0));
		// for (NetworkPort port : reachablePorts) {
		// if (port.equals(((WonesysDropCard) dropCard).getExpressPort())) {
		// // selecting express port will create a passthrough connection
		// dstInternPort = port;
		// }
		// }
		// Assert.assertNotNull(dstInternPort);
		//
		// // setChannel
		// c = new SetChannel(chassis, slot, Integer.toHexString(dwdmChannel), Integer.toHexString(dstInternPort.getPortNumber()));
		// c.initialize();
		// response = (String) session.sendReceive(c.message());
		// resp = c.checkResponse(response);
		// c.parseResponse(resp, opticalSwitch1);
		//
		// // check model has changed ok
		// FCPort srcPort = null;
		// for (LogicalDevice port : dropCard.getModulePorts().get(0).getPortsOnDevice()) {
		// if (port instanceof FCPort) {
		// if (((FCPort) port).getPortNumber() == dwdmChannel) {
		// srcPort = (FCPort) port;
		// break;
		// }
		// }
		// }
		// Assert.assertNotNull(srcPort);
		//
		// FCPort dstParentPort = null;
		// FCPort dstPort = null;
		// for (LogicalDevice connectedPort : dstInternPort.getOutgoingDeviceConnections()) {
		// if (connectedPort instanceof FCPort) {
		// dstParentPort = (FCPort) connectedPort;
		// for (LogicalDevice port : connectedPort.getPortsOnDevice()) {
		// if (port instanceof FCPort) {
		// if (((FCPort) port).getPortNumber() == dwdmChannel) {
		// dstPort = (FCPort) port;
		// break;
		// }
		// }
		// }
		// if (dstPort != null)
		// break;
		// }
		// }
		// Assert.assertNotNull(dstParentPort);
		// Assert.assertNotNull(dstPort);
		//
		// Assert.assertTrue(srcPort.getOutgoingDeviceConnections().contains(dstPort));
		// Assert.assertTrue(OpticalSwitchController.getFreeChannels((FCPort) dropCard.getModulePorts().get(0)).size() ==
		// freeChannelsSize - 1);
		//
		// // setChannel as before
		// c = new SetChannel(chassisNum, slotNum, dwdmChannel, 0);
		// c.initialize();
		// response = (String) session.sendReceive(c.message());
		// resp = c.checkResponse(response);
		// c.parseResponse(resp, opticalSwitch1);
		//
		// // check model has changed
		//
		// Assert.assertTrue(OpticalSwitchController.getFreeChannels((FCPort) dropCard.getModulePorts().get(0)).size() ==
		// freeChannelsSize);
		// Assert.assertFalse(dropCard.getModulePorts().get(0).getPortsOnDevice().contains(srcPort));
		// Assert.assertFalse(dstParentPort.getPortsOnDevice().contains(dstPort));

		// }
		// }
		// }
		//
		// } catch (ProtocolException e) {
		// log.error("Error happened!!!!", e);
		// Assert.fail();
		// } catch (CommandException e) {
		// log.error("Error happened!!!!", e);
		// Assert.fail();
		// }
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
		// protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		protocolSessionContext.addParameter("protocol.mock", "true");
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		if (protocolManager == null)
			return null;

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		return protocolSessionManager.obtainSession(sessionContext, true);
	}

}
