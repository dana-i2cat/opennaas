package org.opennaas.extensions.bod.autobahn.protocol;

import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class AutobahnProtocolSessionFactory implements IProtocolSessionFactory
{
    @Override
    public IProtocolSession
        createProtocolSession(String sessionID,
                              ProtocolSessionContext protocolSessionContext)
        throws ProtocolException
    {
        return new AutobahnProtocolSession(protocolSessionContext, sessionID);
    }
}