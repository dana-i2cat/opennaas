package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;

@WebService
public class ProtocolSessionManagerServiceImpl implements IProtocolSessionManagerService {

	private IProtocolManager	protocolManager;

	/**
	 * @return
	 */
	public IProtocolManager getProtocolManager() {
		return protocolManager;
	}

	/**
	 * @param protocolManager
	 */
	public void setProtocolManager(IProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}

	@Override
	public void registerContext(String resourceId, ProtocolSessionContext context) throws ProtocolException {
		protocolManager.getProtocolSessionManager(resourceId).registerContext(context);

	}

	@Override
	public void unregisterContext(String resourceId, ProtocolSessionContext context) throws ProtocolException {
		protocolManager.getProtocolSessionManager(resourceId).unregisterContext(context);
	}

	@Override
	public void unregisterContext(String resourceId, String protocol) throws ProtocolException {
		protocolManager.getProtocolSessionManager(resourceId).unregisterContext(protocol);

	}

	@Override
	public List<ProtocolSessionContext> getRegisteredContexts(String resourceId) throws ProtocolException {
		return protocolManager.getProtocolSessionManager(resourceId).getRegisteredContexts();
	}

}
