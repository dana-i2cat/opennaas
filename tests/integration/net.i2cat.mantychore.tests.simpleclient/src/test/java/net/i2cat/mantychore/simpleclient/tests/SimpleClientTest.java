package net.i2cat.mantychore.simpleclient.tests;


import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import net.i2cat.mantychore.actionsets.junos.JunosActionFactory;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.capability.chassis.ChassisCapabilityFactory;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.QueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.System;

public class SimpleClientTest extends AbstractIntegrationTest {

	static Logger			log				= LoggerFactory
													.getLogger(SimpleClientTest.class);
	IQueueManagerService	queueManager;


	String					deviceID		= "junos";
	String					queueID			= "queue";
	ProtocolSessionContext	protocolSessionContext;
	ChassisCapability chassisCapability;
	
	JunosActionFactory actionFactory = new JunosActionFactory();
	
	
	
	
	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return combine(
					   IntegrationTestsHelper.getMantychoreTestOptions(),
					   mavenBundle().groupId("net.i2cat.mantychore.capability").artifactId("net.i2cat.mantychore.capability.chassis"),					   
					   mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper")
					   
		);
	}

	/* initialize client */

	private void prepareQueueManagerTest() {
		try {
			log.info("Starting the test");

			/* get queueManager as a service */
			log.info("Getting queue manager factory...");
			IQueueManagerFactory queueManagerFactory = getOsgiService(IQueueManagerFactory.class, 5000);
			log.info("Getting queue manager...");
			queueManager = queueManagerFactory.createQueueManager(deviceID);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}
	

	private void prepareChassisCapability() {
		try {
			log.info("Starting the test");

			/* get queueManager as a service */
			log.info("Getting queue manager factory...");
			
			ChassisCapabilityFactory chassisFactory = getOsgiService(ChassisCapabilityFactory.class, 5000);
//			log.info("Getting queue manager...");

//			//TODO ADD ALL ACTIONS AVALIABLE FOR ACTIONSET	
			chassisCapability = chassisFactory.createChassisCapability(actionFactory.getActionNames(),newSessionContextNetconf() , deviceID);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}
	
	
@Test
	public void testActions() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");
		
		prepareQueueManagerTest();
		
		prepareChassisCapability();
		
//		IActionSetFactory junosActionFactory = JunosActionFactory();
		
		
		//chassisCapability.initialize();
		String actionId =JunosActionFactory.SETINTERFACE;
		ComputerSystem model = new ComputerSystem();
		chassisCapability.setResource(model);
		
		Object params = newParamsInterface();
		chassisCapability.sendMessage(actionId,params);
		
		//check if it is added
		Assert.assertTrue(queueManager.getActions().size()!=1);
		
		try {
			List<ActionResponse> responses = queueManager.execute();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		} catch (CommandException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
		
		//check if it is added
		Assert.assertTrue(queueManager.getActions().size()!=0);

		
		//CHECK MODEL
		
		
		
		
	}
	
	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@host.net:2212/mocksubsystem");

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		// ADDED
		return protocolSessionContext;

	}

	private Object newParamsInterface() {
		 EthernetPort eth = new EthernetPort();
		 eth.setElementName("ge-0/1/0");
		 eth.setPortNumber(30);
		 IPProtocolEndpoint ip = new IPProtocolEndpoint();
		 ip.setIPv4Address("193.1.24.88");
		 ip.setSubnetMask("255.255.255.0");
		 eth.addProtocolEndpoint(ip);
		 ArrayList params = new ArrayList();
		 params.add(eth);
		 return params;
		 
	}



}
