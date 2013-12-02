package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;

public abstract class FloodlightAction extends Action {

	protected FloodlightProtocolSession getFloodlightProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (FloodlightProtocolSession) protocolSessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);
	}

	protected String getSwitchIdFromSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		ProtocolSessionContext sessionContext = getFloodlightProtocolSession(protocolSessionManager).getSessionContext();
		return (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
	}

}
