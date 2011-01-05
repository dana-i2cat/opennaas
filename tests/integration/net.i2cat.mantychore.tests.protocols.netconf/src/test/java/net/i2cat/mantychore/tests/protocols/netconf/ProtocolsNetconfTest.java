package net.i2cat.mantychore.tests.protocols.netconf;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ConfigurerTestFactory;

import com.iaasframework.protocolsessionmanager.IProtocolManager;
import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.IProtocolSessionFactory;
import com.iaasframework.protocolsessionmanager.IProtocolSessionManager;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;
import com.iaasframework.resources.core.RegistryUtil;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

@RunWith(JUnit4TestRunner.class)
public class ProtocolsNetconfTest {

	static Logger	log				= LoggerFactory
											.getLogger(ProtocolsNetconfTest.class);

	@Inject
	BundleContext	bundleContext	= null;

	String			deviceID		= "junos";

	@Configuration
	public static Option[] configure() {
		return ConfigurerTestFactory.newProtocolSessionManagerTest();
	}

	@Test
	public void ListBundles() {
		log.debug("This is running inside Felix. With all configuration set up like you specified. ");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			log.debug(e.getMessage());
		}
		listBundles(bundleContext);

		/* initialize client */

	}

	@Test
	public void TestProtocolSessionManager() {

		try {
			registerProtocolService(bundleContext);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listBundles(bundleContext);
		CapabilityDescriptor descriptor = createProtocolDescriptor();
		try {
			// IProtocolSessionFactory protocolFactory =
			getProtocolFactoryFromOSGiRegistry(descriptor);

			// get the protocol Manager
			IProtocolManager protocolManager = (IProtocolManager)
					RegistryUtil.getServiceFromRegistry(bundleContext, "IProtocolManager");
			try {
				// new protocol session manager
				protocolManager.createProtocolSessionManager(deviceID);
				IProtocolSessionManager protocolSessionManager =
						protocolManager.getProtocolSessionManager(deviceID);

				// get the protocolSession for netconf
				protocolSessionManager.createProtocolSession(newSessionContextNetconf());

				// the value of PROTOCOL, an identifier. Also it includes a
				// boolean
				// to blocking or that protocolsession inside the SessionManager
				IProtocolSession protocolSession =
						protocolSessionManager.getProtocolSession("netconf", false);
				Boolean itWasReceived = (Boolean)
						protocolSession.sendReceive("I am sending a message");

				Assert.assertTrue(itWasReceived);

			} catch (ProtocolException e) {
				log.error(e.getMessage());
				Assert.fail();
			}

		} catch (CapabilityException e) {
			log.error(e.getMessage());
		} catch (InvalidSyntaxException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	private void listBundles(BundleContext bundleContext) {
		Bundle b = null;
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			System.out.println(b.toString() + " : " + getStateString(b.getState()));
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private void registerProtocolService(BundleContext bundleContext) throws
			InterruptedException {

		// IT IS HARDCODED, IT MUST REPLACED FOR THE PROTOCOL NETCONF PROJECT
		IProtocolSessionFactory protocolFactory = new MockProtocolFactory();
		Properties serviceProperties = new Properties();

		// FIXME TO BE EQUALS WHAT THE PROTOCOL NETCONF PROJECT
		serviceProperties.put(ProtocolSessionContext.PROTOCOL, "netconf");
		serviceProperties.put(ProtocolSessionContext.PROTOCOL_VERSION, "1.0.0");
		bundleContext.registerService(IProtocolSessionFactory.class.getName(),
				protocolFactory, serviceProperties);

	}

	private static String	PROTOCOL_URI	= "protocol.uri";

	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new
				ProtocolSessionContext();
		protocolSessionContext.addParameter(PROTOCOL_URI, "");
		return protocolSessionContext;

	}

	private IProtocolSessionFactory getProtocolFactoryFromOSGiRegistry(CapabilityDescriptor
			capabilityDescriptor)
			throws CapabilityException, InvalidSyntaxException, InterruptedException {
		IProtocolSessionFactory protocolFactory = null;
		Properties properties =
				createProtocolFilterToQueryOSGiRegistry(capabilityDescriptor);
		Filter filter =
				RegistryUtil.createServiceFilter(IProtocolSessionFactory.class.getName(),
						properties);
		protocolFactory = (IProtocolSessionFactory)
				RegistryUtil.getServiceFromRegistry(
						bundleContext, filter);

		return protocolFactory;
	}

	private Properties createProtocolFilterToQueryOSGiRegistry(CapabilityDescriptor
			capabilityDescriptor) {
		Properties properties = new Properties();
		properties.put(ProtocolSessionContext.PROTOCOL,
				capabilityDescriptor.getPropertyValue(ProtocolSessionContext.PROTOCOL));
		properties.put(ProtocolSessionContext.PROTOCOL_VERSION,
				capabilityDescriptor.getPropertyValue(ProtocolSessionContext.PROTOCOL_VERSION));

		return properties;
	}

	private CapabilityDescriptor createProtocolDescriptor() {
		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();
		/* protocol info */
		// Information information = new Information();
		// information.setDescription("junos protocol");
		// information.setName("junos protocol");
		// information.setType(IProtocolConstants.PROTOCOL);
		// information.setVersion("1.0.0");
		// capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new
				ArrayList<CapabilityProperty>();

		properties.add(newProperty(ProtocolSessionContext.PROTOCOL, "netconf"));
		properties.add(newProperty(ProtocolSessionContext.PROTOCOL_VERSION,
				"1.0.0"));

		/* netconf parameters */

		capabDescriptor.setCapabilityProperties(properties);

		// capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityProperty newProperty(String name, String value) {

		CapabilityProperty property = new CapabilityProperty();
		property.setName(name);
		property.setValue(value);
		return property;
	}

	private static String getStateString(int value) {
		if (value == Bundle.ACTIVE) {
			return "ACTIVE";
		} else if (value == Bundle.INSTALLED) {
			return "INSTALLED";
		} else if (value == Bundle.RESOLVED) {
			return "RESOLVED";
		} else if (value == Bundle.UNINSTALLED) {
			return "UNINSTALLED";
		}

		return "UNKNOWN";
	}

}
