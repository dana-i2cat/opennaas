package org.opennaas.extensions.opendaylight.vtn.actionssets;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.opendaylight.vtn.protocol.OpenDaylightProtocolSession;

public abstract class OpenDaylightAction extends Action {

    protected OpenDaylightProtocolSession getOpenDaylightProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
        return (OpenDaylightProtocolSession) protocolSessionManager.obtainSessionByProtocol(OpenDaylightProtocolSession.OPENDAYLIGHT_PROTOCOL_TYPE, false);
    }

    protected String getSwitchIdFromSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
        ProtocolSessionContext sessionContext = getOpenDaylightProtocolSession(protocolSessionManager).getSessionContext();
        return (String) sessionContext.getSessionParameters().get(OpenDaylightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
    }

}
