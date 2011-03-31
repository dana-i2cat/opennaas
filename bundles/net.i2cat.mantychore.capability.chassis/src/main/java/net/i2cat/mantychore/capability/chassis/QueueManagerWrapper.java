package net.i2cat.mantychore.capability.chassis;

import java.util.Properties;

import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.resources.RegistryUtil;
import net.i2cat.nexus.resources.capability.CapabilityException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A queue can only accessed via reference, between 
 */
@Deprecated
public class QueueManagerWrapper {

	private final Logger	logger	= LoggerFactory
											.getLogger(QueueManagerWrapper.class);

	IProtocolSessionManager	protocolSessionManager;

	public IQueueManagerService getQueueManager(String resourceId)
			throws CapabilityException {

		BundleContext bundleContext = Activator.getContext();

		logger.info("getting service: " + IQueueManagerFactory.class.getName());
		IQueueManagerService queueManager = null;
		try {

			Filter filterQueue = RegistryUtil.createServiceFilter(
					IQueueManagerFactory.class.getName(), newPropertiesQueue());
			IQueueManagerFactory queueFactory = (IQueueManagerFactory) RegistryUtil
					.getServiceFromRegistry(bundleContext, filterQueue);

			// queueManager = queueFactory.createQueueManager();

			// TODO TO FIX PROBLEM WITH EXCEPTION
		} catch (Exception e) {
			throw new CapabilityException(e);
		}
		return queueManager;

	}

	public static final String	CAPABILITY_NAME	= "capability";

	private Properties newPropertiesQueue() {
		Properties props = new Properties();
		props.setProperty(CAPABILITY_NAME, "queue");

		return props;
	}

}
