package org.opennaas.extensions.router.opener.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class OpenerProtocolSessionFactory implements IProtocolSessionFactory {

	Log	log	= LogFactory.getLog(OpenerProtocolSessionFactory.class);

	public OpenerProtocolSessionFactory() {
		super();
		log.info("OPENER Protocol Session Factory created");
	}

	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		OpenerProtocolSession session = new OpenerProtocolSession(sessionID, protocolSessionContext);
		return session;
	}

}
