package net.i2cat.mantychore.queuemanager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.IProtocolManager;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class ProtocolNetconfWrapper {
	IProtocolManager	protocolManager;
	BundleContext		bundleContext;

	static Logger		log	= LoggerFactory
									.getLogger(ProtocolNetconfWrapper.class);

	public ProtocolNetconfWrapper(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		ServiceReference serviceReference = bundleContext.getServiceReference(IProtocolManager.class.getName());
		protocolManager = (IProtocolManager) bundleContext.getService(serviceReference);

	}

	// public IProtocolSession getProtocol(String resourceID, String protocolID)
	// {
	//
	// try {
	// protocolManager.createProtocolSessionManager(resourceID);
	// } catch (ProtocolException e) {
	// log.error(e.getMessage());
	// }
	// IProtocolSessionManager protocolSessionManager =
	// protocolManager.getProtocolSessionManager(resourceID);
	//
	// // get the protocolSession for netconf
	// protocolSessionManager.createProtocolSession(newSessionContextNetconf());
	//
	// // the value of PROTOCOL, an identifier. Also it includes a
	// // boolean
	// // to blocking or that protocolsession inside the SessionManager
	// IProtocolSession protocolSession =
	// protocolSessionManager.getProtocolSession(protocolID, false);
	//
	// }

	private ProtocolSessionContext newSessionContextNetconf() {
		// TODO Auto-generated method stub
		return null;
	}

}
