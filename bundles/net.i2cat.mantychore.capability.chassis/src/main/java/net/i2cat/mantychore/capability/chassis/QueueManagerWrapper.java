package net.i2cat.mantychore.capability.chassis;

import java.util.Properties;

import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.resources.RegistryUtil;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueManagerWrapper {

	private Logger			logger	= LoggerFactory.getLogger(QueueManagerWrapper.class);

	IProtocolSessionManager	protocolSessionManager;

	public IQueueManagerService getQueueManager(String resourceId) {

		IQueueManagerService queueManager = getOSGiQueueManager();

		return queueManager;

	}


	private IQueueManagerService getOSGiQueueManager() {
		BundleContext bundleContext = Activator.getContext();

		logger.info("getting service: " + IQueueManagerFactory.class.getName());
		IQueueManagerService queueManager = null;
		try {

			Filter filterQueue  = RegistryUtil.createServiceFilter(IQueueManagerFactory.class.getName(),newPropertiesQueue());
			

			IQueueManagerFactory queueFactory = (IQueueManagerFactory) RegistryUtil.getServiceFromRegistry(bundleContext, filterQueue);
			
			
		} catch (InterruptedException e) {
			// FIXME TO THROW PROBLEM IT CAN'T RETURN A NULL VALUE
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			// FIXME TO THROW PROBLEM IT CAN'T RETURN A NULL VALUE
			e.printStackTrace();
		}
		
		return queueManager;
	}
	
	
	public static final String CAPABILITY_NAME="capability";
	
	private Properties newPropertiesQueue() {
		Properties props = new Properties();
		props.setProperty(CAPABILITY_NAME, "queue");
		
		return props;
	}

}
