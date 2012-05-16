package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;

@WebService
public class ProtocolSessionManagerServiceImpl implements IProtocolSessionManagerService {

	Log							log	= LogFactory.getLog(ProtocolSessionManagerService.class);
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
		log.info("Start of registerContext call");
		protocolManager.getProtocolSessionManager(resourceId).registerContext(context);
		log.info("End of registerContext call");
	}

	@Override
	public void unregisterContext(String resourceId, ProtocolSessionContext context) throws ProtocolException {
		log.info("Start of unregisterContext call");
		protocolManager.getProtocolSessionManager(resourceId).unregisterContext(context);
		log.info("End of unregisterContext call");

	}

	@Override
	public void unregisterContext(String resourceId, String protocol) throws ProtocolException {
		log.info("Start of unregisterContext call");
		protocolManager.getProtocolSessionManager(resourceId).unregisterContext(protocol);
		log.info("End of unregisterContext call");
	}

	@Override
	public List<ProtocolSessionContext> getRegisteredContexts(String resourceId) throws ProtocolException {
		log.info("Start of getRegisteredContexts call");
		List<ProtocolSessionContext> contexts = protocolManager.getProtocolSessionManager(resourceId).getRegisteredContexts();
		log.info("End of getRegisteredContexts call");
		return contexts;

	}

}
