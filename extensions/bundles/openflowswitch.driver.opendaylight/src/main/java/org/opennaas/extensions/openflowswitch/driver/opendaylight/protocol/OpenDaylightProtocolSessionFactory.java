package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class OpenDaylightProtocolSessionFactory implements IProtocolSessionFactory {

	Log	log	= LogFactory.getLog(OpenDaylightProtocolSessionFactory.class);

	public OpenDaylightProtocolSessionFactory() {
		super();
		log.info("OPENDAYLIGHT Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSession(String sessionID,
			ProtocolSessionContext context) throws ProtocolException {
		OpenDaylightProtocolSession session = new OpenDaylightProtocolSession(sessionID, context);
		return session;
	}

}
