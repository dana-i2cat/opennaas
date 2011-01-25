package net.i2cat.mantychore.queuemanager;

import net.i2cat.mantychore.protocols.sessionmanager.IProtocolManager;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.iaasframework.core.internal.persistence.Activator;

public class ProtocolNetconfWrapper {
	IProtocolSessionManager	protocolSessionManager;

	public String createProtocolSession(String resourceId, ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		IProtocolManager protocolManager = getOSGiServiceProtocolManager();
		protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		if (protocolSessionManager == null) {
			protocolManager.createProtocolSessionManager(resourceId);
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
		BundleContext bundleContext = Activator.getBundleContext();
		ServiceReference serviceReference = bundleContext.getServiceReference(IProtocolManager.class.getName());
		return (IProtocolManager) bundleContext.getService(serviceReference);
	}

}
