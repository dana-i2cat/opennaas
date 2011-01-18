package net.i2cat.mantychore.tests.queuemanager;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import junit.framework.Assert;

import org.junit.Test;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.PaxHelper;

import com.iaasframework.protocolsessionmanager.IProtocolManager;
import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.IProtocolSessionManager;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class QueueManagerTest {
	static Logger	log				= LoggerFactory
											.getLogger(QueueManagerTest.class);

	@Inject
	BundleContext	bundleContext	= null;

	String			deviceID		= "junos";

	@Configuration
	public static Option[] configure() {
		Option[] optsSimpleService = PaxHelper.newSimpleTest();
		// mantychore
		// features

		Option[] opts_with_core = combine(optsSimpleService
				, mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.protocolsessionmanager")
				// for
				);

		Option[] opts_with_protocol = combine(opts_with_core
				, mavenBundle().groupId("net.i2cat.mantychore.protocols").artifactId("net.i2cat.mantychore.protocols.netconf")
				// for
				);

		Option[] allOpts = combine(opts_with_protocol
				, mavenBundle().groupId("net.i2cat.mantychore.queuemanager").artifactId("net.i2cat.mantychore.queuemanager")
				// for
				);

		return allOpts;
	}

	@Test
	public void ListBundles() {
		log.info("This is running inside Felix. With all configuration set up like you specified. ");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			Assert.fail();
		}
		PaxHelper.listBundles(bundleContext);

		/* initialize client */

	}

	@Test
	public void IQueueManagerTest() {
		try {
			log.info("Starting the test");

			// get the protocol Manager

			try {

				// create procotolSession
				initializeProtocol();

				/* get queueManager as a service */
				ServiceReference serviceReference = bundleContext.getServiceReference(IProtocolManager.class.getName());
				IProtocolManager protocolManager = (IProtocolManager) bundleContext.getService(serviceReference);

			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				Assert.fail();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}

	private IProtocolSession initializeProtocol() throws ProtocolException {

		/* get protocol manager */
		log.info("It get our protocolManager");
		ServiceReference serviceReference = bundleContext.getServiceReference(IProtocolManager.class.getName());
		IProtocolManager protocolManager = (IProtocolManager) bundleContext.getService(serviceReference);

		log.info("Create protocol session manager");
		protocolManager.createProtocolSessionManager(deviceID);
		IProtocolSessionManager protocolSessionManager =
				protocolManager.getProtocolSessionManager(deviceID);
		ProtocolSessionContext sessionContext = newSessionContextNetconf();

		log.info("Create protocol session");
		/* create and get a protocolsession for the device */
		protocolSessionManager.createProtocolSession(sessionContext);

		IProtocolSession protocolSession =
				protocolSessionManager.getProtocolSession(deviceID, false);

		return protocolSession;

	}

	// private void registerProtocolService(BundleContext bundleContext) throws
	// InterruptedException {
	//
	// // // IT IS HARDCODED, IT MUST REPLACED FOR THE PROTOCOL NETCONF PROJECT
	// // IProtocolSessionFactory protocolFactory = new MockProtocolFactory();
	// // Properties serviceProperties = new Properties();
	// //
	// // // FIXME TO BE EQUALS WHAT THE PROTOCOL NETCONF PROJECT
	// // serviceProperties.put(ProtocolSessionContext.PROTOCOL, "netconf");
	// // serviceProperties.put(ProtocolSessionContext.PROTOCOL_VERSION,
	// // "1.0.0");
	// // bundleContext.registerService(IProtocolSessionFactory.class.getName(),
	// // protocolFactory, serviceProperties);
	// }

	private static String	PROTOCOL_URI	= "protocol.uri";

	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(PROTOCOL_URI, ""); // TODO IT HAS TO
		// ADDED
		return protocolSessionContext;

	}

}
