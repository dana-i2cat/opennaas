package org.opennaas.itests.roadm.protocol.wonesys;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.GetInventoryCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannels;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;

import org.osgi.service.blueprint.container.BlueprintContainer;

import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class SendCommandTest {

	static Log log	= LogFactory.getLog(SendCommandTest.class);

	@Inject
	private BundleContext	bundleContext;

	@Inject
	private IProtocolManager protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	BlueprintContainer wonesysProtocolService;

	private String			resourceId		= "Proteus-Pedrosa";
	private String			hostIpAddress	= "10.10.80.11";
	private String			hostPort 		= "27773";

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
	}

	@Test
	public void testSendMultipleMessages() throws ProtocolException {

		//loadBundles();

		sendMultipleMessages(1);

		sendMultipleMessages(5);
	}

	@Test
	public void sendCommandTest() throws ProtocolException, CommandException {

		//loadBundles();

		/* Wait for the activation of all the bundles */
		//IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContext(hostIpAddress, hostPort);
		sessionContext.addParameter("protocol.mock", "true");
		try {
			// get WonesysProtocolSession using ProtocolSessionManager
			IProtocolSession protocolSession = getProtocolSession(resourceId, sessionContext);
			if (protocolSession == null)
				throw new ProtocolException("Could not get a valid ProtocolSession");

			// create command
			WonesysCommand command = new GetInventoryCommand();
			command.initialize();

			// send command & receive response
			Object rawResponse = protocolSession.sendReceive(command.message());
			WonesysResponse response = (WonesysResponse) command.checkResponse(rawResponse);

			assertNotNull(response);

			// assure response is correct
			log.info("Received response " + response.getWonesysResponseMessage().toString());
			if (response.getStatus() == Response.Status.OK) {
				log.info("Response is OK");
			} else if (response.getStatus() == Response.Status.ERROR) {
				log.info("Response is ERROR");
				assertTrue(response.getErrors().size() > 0);
				for (String error : response.getErrors())
					log.info(error);
			}

			List<int[]> model = new ArrayList<int[]>();

			// command.parseResponse(response, model);
			String responseData = response.getInformation();
			int resultLength = 4 * 2;
			int resultsCount = responseData.length() / resultLength;
			String[] results = new String[resultsCount];
			log.info("Node has " + resultsCount + " cards");

			for (int i = 0; i < resultsCount; i++) {
				results[i] = responseData.substring(i * resultLength, (i + 1) * resultLength);

				String schasis = results[i].substring(0, 2);
				String sslot = results[i].substring(2, 4);
				String stype = results[i].substring(4, 6);
				String ssubtype = results[i].substring(6, 8);

				int chasis = Integer.parseInt(schasis, 16);
				int slot = Integer.parseInt(sslot, 16);
				int type = Integer.parseInt(stype, 16);
				int subtype = Integer.parseInt(ssubtype, 16);

				log.info("Chasis:" + chasis + " Slot:" + slot + " Type:" + type + " SubType:" + subtype);

				model.add(new int[] { chasis, slot, type, subtype });
			}

			for (int[] card : model) {
				if (card[2] == 11 &&
						(card[3] == 1 || card[3] == 3)) {
					command = new GetChannels(card[0], card[1]);
					command.initialize();

					rawResponse = protocolSession.sendReceive(command.message());
					response = (WonesysResponse) command.checkResponse(rawResponse);

					// assure response is correct
					log.info("Received response " + response.getWonesysResponseMessage().toString());
					if (response.getStatus() == Response.Status.OK) {
						log.info("Response is OK");
					} else if (response.getStatus() == Response.Status.ERROR) {
						log.info("Response is ERROR");
						assertTrue(response.getErrors().size() > 0);
						for (String error : response.getErrors())
							log.info(error);
					}

					log.info(response.getInformation());
				}
			}

		} catch (ProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (CommandException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void sendMultipleMessages(int numMessagesToSend) throws ProtocolException {

		ProtocolSessionContext protocolSessionContext1 = createWonesysProtocolSessionContext(
				hostIpAddress, hostPort);
		// use mock transport
		protocolSessionContext1.addParameter("protocol.mock", "true");

		String sessionId1 = "1";

		Object command = createGetCommand();

		WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
		try {

			IProtocolSession protocolSession = factory.createProtocolSession(
					sessionId1, protocolSessionContext1);
			protocolSession.connect();
			for (int i = 0; i < numMessagesToSend; i++) {
				log.info("Sending message... " + command);
				Object response = protocolSession.sendReceive(command);
				log.info("Received response: " + (String) response);
				assertNotNull(response);
				assertFalse(response.equals(""));
			}
			protocolSession.disconnect();

		} catch (ProtocolException e) {
			log.info("Failed to send message!", e);
			throw e;
		}
	}

	private ProtocolSessionContext createWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		//IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		if (protocolManager == null)
			return null;

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		return protocolSessionManager.obtainSession(sessionContext, true);
	}

	private Object createGetCommand() {
		// getInventory command
		String cmd = "5910ffffffffff01ffffffff0000";
		String xor = getXOR(cmd);

		cmd += xor + "00";

		return cmd;
	}

	private String getXOR(String cmd) {

		int xor = Integer.parseInt(cmd.substring(0, 2), 16)
				^ Integer.parseInt(cmd.substring(2, 4), 16);
		for (int i = 4; i <= cmd.length() - 2; i++) {
			xor = xor ^ Integer.parseInt(cmd.substring(i, i + 2), 16);
			i++;
		}
		String hxor = Integer.toHexString(xor);
		if (hxor.length() < 2) {
			hxor = "0" + hxor;
		}
		return hxor;
	}

}
