package net.i2cat.mantychore.protocols.netconf;

import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionFactory;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetconfProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Log	log	= LogFactory.getLog(NetconfProtocolSessionFactory.class);

	public NetconfProtocolSessionFactory() {
		super();
		log.info("Netconf Protocol Session Factory created");
	}

	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		// TODO Auto-generated method stub

		NetconfProtocolSession session = new NetconfProtocolSession(protocolSessionContext, sessionID);

		return session;
	}
}
