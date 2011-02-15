package net.i2cat.mantychore.queuemanager;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolNetconfWrapper {

	private Logger			logger	= LoggerFactory.getLogger(ProtocolNetconfWrapper.class);

	IProtocolSessionManager	protocolSessionManager;

	public String createProtocolSession(String resourceId, ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		IProtocolManager protocolManager = getOSGiServiceProtocolManager();
		try {
			protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		} catch (ProtocolException e) {
			logger.warn("the protocol session is not created...");
			protocolManager.createProtocolSessionManager(resourceId);
			protocolSessionManager = null;
		}
		if (protocolSessionManager == null) {
			protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		}

		String sessionId = protocolSessionManager.checkOut(protocolSessionContext);

		return sessionId;

	}

	public IProtocolSession getProtocolSession(String sessionId) throws ProtocolException {
		return protocolSessionManager.getProtocolSession(sessionId, true);
	}

	public void releaseProtocolSession(String sessionId) throws ProtocolException {
		protocolSessionManager.checkIn(sessionId);
	}

	private IProtocolManager getOSGiServiceProtocolManager() {
		BundleContext bundleContext = Activator.getContext();

		logger.info("getting service: " + IProtocolManager.class.getName());
		ServiceReference serviceReference = bundleContext.getServiceReference(IProtocolManager.class.getName());
		return (IProtocolManager) bundleContext.getService(serviceReference);
	}

}
