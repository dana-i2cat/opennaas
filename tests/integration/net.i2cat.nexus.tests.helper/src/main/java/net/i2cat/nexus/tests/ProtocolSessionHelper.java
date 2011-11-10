package net.i2cat.nexus.tests;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class ProtocolSessionHelper {

	String	resourceId	= "RandomDevice";

	// public IProtocolSessionManager getMockProtocolSessionManager() {
	//
	// IProtocolManager protocolManager = new ProtocolManager();
	// IProtocolSessionManager protocolSessionManager = null;
	// try {
	// protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
	// ProtocolSessionContext netconfContext = newSessionContextNetconf();
	// protocolManager.sessionFactoryAdded(new NetconfProtocolSessionFactory(), new HashMap<String, String>() {
	// {
	// put(ProtocolSessionContext.PROTOCOL, "netconf");
	// }
	// });
	// protocolSessionManager.registerContext(netconfContext);
	// } catch (ProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return protocolSessionManager;
	// }

	/**
	 * Configure the protocol to connect
	 */
	public static ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty() ) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";

		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}
}
