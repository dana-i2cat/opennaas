package net.i2cat.mantychore.protocols.netconf;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Logger	logger	= LoggerFactory.getLogger(NetconfProtocolSessionFactory.class);

	public NetconfProtocolSessionFactory() {
		super();
		logger.info("Netconf Protocol Session Factory created");
	}


	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		// TODO Auto-generated method stub

		NetconfProtocolSession session = new NetconfProtocolSession(protocolSessionContext, sessionID);

		return session;
	}
}
