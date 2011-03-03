package net.i2cat.mantychore.simpleclient.tests;


import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.capability.chassis.ChassisCapabilityFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.QueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

public class SimpleClientTest extends AbstractIntegrationTest {

	static Logger			log				= LoggerFactory
													.getLogger(SimpleClientTest.class);
	IQueueManagerService	queueManager;

	//TODO TO FIX
	ChassisCapability chassisCapability;
	String					deviceID		= "junos";
	String					queueID			= "queue";
	ProtocolSessionContext	protocolSessionContext;
	
	
	
	
	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return combine(
					   IntegrationTestsHelper.getMantychoreTestOptions(),
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
			
//			ChassisCapabilityFactory chassisFactory = getOsgiService(ChassisCapabilityFactory.class, 5000);
//			log.info("Getting queue manager...");
//			//TO FIX
//			queueManager = chassisFactory.create(null, deviceID);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}
	
	

	@Test
	public void testListAction() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");
		
		prepareQueueManagerTest();
		
		prepareChassisCapability();
		
		String actionID = "actionID";
		chassisCapability.handleMessage(actionID);
		
		
		
		
	}
	
	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@host.net:2212/mocksubsystem");

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		// ADDED
		return protocolSessionContext;

	}



}
