package net.i2cat.mantychore.queuemanager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.iaasframework.protocolsessionmanager.IProtocolManager;

public class ProtocolNetconfWrapper {
	IProtocolManager	protocolManager;
	BundleContext		bundleContext;

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
	// // TODO Auto-generated catch block
	// e.printStackTrace();
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
	//
	// private ProtocolSessionContext newSessionContextNetconf() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
