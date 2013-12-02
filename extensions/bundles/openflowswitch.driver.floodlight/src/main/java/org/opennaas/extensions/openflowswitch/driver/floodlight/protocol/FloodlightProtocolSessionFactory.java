package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class FloodlightProtocolSessionFactory implements IProtocolSessionFactory {

	Log	log	= LogFactory.getLog(FloodlightProtocolSessionFactory.class);

	public FloodlightProtocolSessionFactory() {
		super();
		log.info("FLOODLIGHT Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSession(String sessionID,
			ProtocolSessionContext context) throws ProtocolException {
		FloodlightProtocolSession session = new FloodlightProtocolSession(sessionID, context);
		return session;
	}

}
