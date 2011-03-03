package net.i2cat.mantychore.capability.chassis;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.resources.RegistryUtil;

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

		logger.info("getting service: " + IProtocolManager.class.getName());
		IQueueManagerService queueManager = null;
		try {
			//TODO DON'T CALL QUEUEMANAGER
			queueManager = (IQueueManagerService) RegistryUtil.getServiceFromRegistry(bundleContext, QueueManager.class.getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return queueManager;
	}

}
