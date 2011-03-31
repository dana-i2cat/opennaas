package net.i2cat.mantychore.commons.wrappers;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.resources.RegistryUtil;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolNetconfWrapper {

	private final Logger	logger		= LoggerFactory
												.getLogger(ProtocolNetconfWrapper.class);

	IProtocolSessionManager	protocolSessionManager;
	String					sessionId	= "";
	BundleContext			bundleContext;

	public IProtocolSessionManager getProtocolSessionManager(String resourceId)
			throws ProtocolException {

		IProtocolManager protocolManager;
		try {
			protocolManager = getOSGiServiceProtocolManager();
		} catch (InterruptedException e1) {
			throw new ProtocolException(e1);
		}
		try {
			protocolSessionManager = protocolManager
					.getProtocolSessionManager(resourceId);
		} catch (ProtocolException e) {
			logger.warn("the protocol session is not created...");
			protocolManager.createProtocolSessionManager(resourceId);
			protocolSessionManager = null;
		}
		if (protocolSessionManager == null) {
			protocolSessionManager = protocolManager
					.getProtocolSessionManager(resourceId);
		}

		return protocolSessionManager;
	}

	public IProtocolSession getProtocolSession(
			ProtocolSessionContext protocolSessionContext)
			throws ProtocolException {
		sessionId = protocolSessionManager.checkOut(protocolSessionContext);
		return protocolSessionManager.getProtocolSession(sessionId, true);
	}

	public void releaseProtocolSession() throws ProtocolException {
		protocolSessionManager.checkIn(sessionId);
	}

	public static ProtocolNetconfWrapper newActionWrapper(
			BundleContext bundleContext,
			IProtocolSessionManager protocolSessionManager) {
		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();

		protocolWrapper.setBundleContext(bundleContext);
		protocolWrapper.setProtocolSessionManager(protocolSessionManager);
		return protocolWrapper;
	}

	// public String createProtocolSession(String resourceId,
	// ProtocolSessionContext protocolSessionContext)
	// throws ProtocolException {
	//
	// IProtocolManager protocolManager = getOSGiServiceProtocolManager();
	// try {
	// protocolSessionManager = protocolManager
	// .getProtocolSessionManager(resourceId);
	// } catch (ProtocolException e) {
	// logger.warn("the protocol session is not created...");
	// protocolManager.createProtocolSessionManager(resourceId);
	// protocolSessionManager = null;
	// }
	// if (protocolSessionManager == null) {
	// protocolSessionManager = protocolManager
	// .getProtocolSessionManager(resourceId);
	// }
	//
	// String sessionId = protocolSessionManager
	// .checkOut(protocolSessionContext);
	//
	// return sessionId;
	//
	// }
	//
	// public IProtocolSession getProtocolSession(String sessionId)
	// throws ProtocolException {
	// return protocolSessionManager.getProtocolSession(sessionId, true);
	// }
	//
	// public void releaseProtocolSession(String sessionId)
	// throws ProtocolException {
	// protocolSessionManager.checkIn(sessionId);
	// }
	//
	private IProtocolManager getOSGiServiceProtocolManager()
			throws InterruptedException {

		logger.info("getting service: " + IProtocolManager.class.getName());
		IProtocolManager protocolManager = (IProtocolManager) RegistryUtil
				.getServiceFromRegistry(bundleContext,
						IProtocolManager.class.getName());

		return protocolManager;
	}

	public IProtocolSessionManager getProtocolSessionManager() {
		return protocolSessionManager;
	}

	public void setProtocolSessionManager(
			IProtocolSessionManager protocolSessionManager) {
		this.protocolSessionManager = protocolSessionManager;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
