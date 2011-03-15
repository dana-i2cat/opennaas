package net.i2cat.mantychore.simpleclient.tests;


import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.i2cat.mantychore.actionsets.junos.BasicActionSetFactory;
import net.i2cat.mantychore.actionsets.junos.ChassisActionSetFactory;
import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.capability.chassis.ChassisCapabilityFactory;
import net.i2cat.mantychore.capability.chassis.IChassisCapabilityFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.System;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;

@RunWith(JUnit4TestRunner.class)
public class SimpleClientTest extends AbstractIntegrationTest {

	static Logger			log				= LoggerFactory
													.getLogger(SimpleClientTest.class);
	IQueueManagerService	queueManager;


	String					deviceID		= "junos";
	String					queueID			= "queue";
	ProtocolSessionContext	protocolSessionContext;
	ChassisCapability chassisCapability;
	
	IActionSetFactory basicActionFactory = new BasicActionSetFactory();
	
	IActionSetFactory actionFactory = new ChassisActionSetFactory();
	
	
	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return combine(
					   IntegrationTestsHelper.getMantychoreTestOptions(),
					   mavenBundle().groupId("net.i2cat.mantychore.capability").artifactId("net.i2cat.mantychore.capability.chassis"),					   
					   mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper")
//					   ,vmOption( "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" )
					   
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
			log.info("Getting chassis capability factory...");
			
			IChassisCapabilityFactory chassisFactory = getOsgiService(IChassisCapabilityFactory.class, 5000);
			
			log.info("Getting chassis capability...");

			List<String> list=actionFactory.getActionNames();
		
			list.addAll(basicActionFactory.getActionNames());
			//FIXME ADD ALL ACTIONS AVALIABLE FOR ACTIONSET	
			
			chassisCapability = chassisFactory.createChassisCapability(list,newSessionContextNetconf() , deviceID);
			//FIXME IT IS A PATCH TO GET THE QUEUE MANAGER SERVICE
			chassisCapability.setQueueManager(queueManager);
			chassisCapability.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}
	
	
@Test
	public void testActions() {	
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);		
		
		prepareQueueManagerTest();
		
		prepareChassisCapability();
		
		
		//chassisCapability.initialize();
		String actionId =ChassisActionSetFactory.SETINTERFACE;
		ComputerSystem model = new ComputerSystem();
		chassisCapability.setResource(model);
		
		Object params = newParamsInterface();
		chassisCapability.sendMessage(actionId,params);
		
		//check if it is added
		Assert.assertFalse(queueManager.getActions().size()!=1);
		
		try {
			List<ActionResponse> responses = queueManager.execute(newActionCommit(),newActionRollback(),newSessionContextNetconf());
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
		Assert.assertFalse(queueManager.getActions().size()!=0);

		
		chassisCapability.sendMessage(ChassisActionSetFactory.GETCONFIG,null);
		//check if it is added
		Assert.assertFalse(queueManager.getActions().size()!=1);
		
		try {
			List<ActionResponse> responses = queueManager.execute(newActionCommit(),newActionRollback(),newSessionContextNetconf());
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
		Assert.assertFalse(queueManager.getActions().size()!=0);
		//We only can test it if we are working with real routers
//		Assert.assertFalse(checkPort(model));
	
		
		Assert.assertNotNull(model.getLogicalDevices());
		for (LogicalDevice logicalDevice: model.getLogicalDevices()) {
			EthernetPort ethernetPort = (EthernetPort)logicalDevice;
			log.info("ethernetPort name: "+ethernetPort.getElementName());	
		}
		
		Assert.assertFalse(model.getLogicalDevices().size()==0);
		
		
	

	}

	private Action newActionRollback() {
	// TODO Auto-generated method stub
	return null;
}

	private Action newActionCommit() {
		return	basicActionFactory.createAction("commit");
	 
}

	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		//protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@host.net:2212/mocksubsystem");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "ssh://i2cat:mant6WWe@lola.hea.net:22/netconf");

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		// ADDED
		return protocolSessionContext;

	}

	private Object newParamsInterface() {
		 EthernetPort eth = new EthernetPort();
		 eth.setElementName("fe-0/3/1");
		 eth.setPortNumber(30);
		 IPProtocolEndpoint ip = new IPProtocolEndpoint();
		 ip.setIPv4Address("193.1.24.88");
		 ip.setSubnetMask("255.255.255.0");
		 eth.addProtocolEndpoint(ip);
		 ArrayList params = new ArrayList();
		 params.add(eth);
		 return params;
		 
	}
	
	private boolean checkPort (ComputerSystem model) {
		if (model.getLogicalDevices()==null || model.getLogicalDevices().size()==0) return false;		
		EthernetPort ethernetPort = (EthernetPort)model.getLogicalDevices();
		boolean resultEquals = ethernetPort.getElementName().equals("ge-0/1/0");
		resultEquals = resultEquals && ethernetPort.getPortNumber()==30;
		if (ethernetPort.getProtocolEndpoint()==null || ethernetPort.getProtocolEndpoint().size()==0) return false;
		IPProtocolEndpoint ip = (IPProtocolEndpoint)ethernetPort.getProtocolEndpoint().get(0);
		resultEquals = resultEquals && ip.getIPv4Address().equals("193.1.24.88");
		resultEquals = resultEquals && ip.getSubnetMask().equals("255.255.255.0");
		
		return resultEquals;
		
	}



}
