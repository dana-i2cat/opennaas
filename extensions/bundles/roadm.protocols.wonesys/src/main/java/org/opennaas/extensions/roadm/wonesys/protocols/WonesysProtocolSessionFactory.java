package org.opennaas.extensions.roadm.wonesys.protocols;

import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for WonesysProtocolSession creation. Registering this factory as an osgi service will allow other bundles use of it.
 * <p>
 * This registration may be done declaratively, through an xml file in OSGI-INF folder.
 *
 * @author isart
 *
 */
public class WonesysProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Log	logger	= LogFactory.getLog(WonesysProtocolSessionFactory.class);

	public WonesysProtocolSessionFactory() {
		super();
		logger.info("W-onesys Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		WonesysProtocolSession session = new WonesysProtocolSession(protocolSessionContext, sessionID);

		return session;
	}
}
