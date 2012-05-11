package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

@WebService(portName = "ProtocolSessionManagerPort", serviceName = "ProtocolSessionManagerService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IProtocolSessionManagerService {

	/**
	 * Registers a new context on this session manager. The context will be used if a session with the context's protocol is requested.
	 * 
	 * Only one context per protocol is accepted. Older registered context for given context's protocol (if any) will be unregistered.
	 * 
	 * @param resourceId
	 * @param context
	 * @throws ProtocolException
	 *             If trying to register a context for a protocol that is not supported. Or unregistering older context fails.
	 */
	void registerContext(String resourceId, ProtocolSessionContext context) throws ProtocolException;

	/**
	 * Unregisters a previously registered context. This context will no longer used to create new sessions. All sessions using this context are
	 * terminated.
	 * 
	 * @param resourceId
	 * @param context
	 *            The context that will be unregistered.
	 * @throws ProtocolException
	 *             If there is an error terminating sessions
	 */
	void unregisterContext(String resourceId, ProtocolSessionContext context) throws ProtocolException;

	/**
	 * Unregisters a previously registered context. This context will no longer used to create new sessions. All sessions using this context are
	 * terminated.
	 * 
	 * @param protocol
	 *            The protocol of the context that will be unregistered.
	 * @throws ProtocolException
	 *             If there is an error terminating sessions
	 */
	@WebMethod(operationName = "unregisterContextByProtocol")
	void unregisterContext(String resourceId, String protocol) throws ProtocolException;

	/**
	 * Returns the list of registered contexts.
	 * 
	 * @return
	 * @throws ProtocolException
	 */
	List<ProtocolSessionContext> getRegisteredContexts(String resourceId) throws ProtocolException;

}
