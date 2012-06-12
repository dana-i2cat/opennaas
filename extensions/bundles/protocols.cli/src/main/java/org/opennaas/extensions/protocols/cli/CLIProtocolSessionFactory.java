package org.opennaas.extensions.protocols.cli;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.ITransportConstants;
import org.opennaas.core.resources.transport.ITransportFactory;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages CLI Protocol sessions
 * @author edu
 *
 */
public class CLIProtocolSessionFactory implements IProtocolSessionFactory{
	
	/** The logger **/
	Logger logger = LoggerFactory.getLogger(CLIProtocolSessionFactory.class);
	
	/** The transport factory **/
	private Map<String, ITransportFactory> transportFactories = null;
	
	public CLIProtocolSessionFactory(){
		super();
		logger.info("CLI Protocol Session Manager created");
		this.transportFactories = new HashMap<String, ITransportFactory>();
	}
	
	/**
	 * Called by blueprint every time a transport factory is registered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void transportFactoryAdded(ITransportFactory serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			transportFactories.put((String)serviceProperties.get("transport"), serviceInstance);
			logger.debug("New transport factory added for transports of type: "+serviceProperties.get("transport"));
		}
	}
	
	/**
	 * Called by blueprint every time a transport factory is unregistered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void transportFactoryRemoved(ITransportFactory serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			transportFactories.remove((String)serviceProperties.get("transport"));
			logger.debug("Existing transport factory removed for transports of type: "+serviceProperties.get("transport"));
		}
	}
	
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException{
		String transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		if (transportId == null){
			throw new ProtocolException("The TL1 protocol session needs the "+ITransportConstants.TRANSPORT+" parameter");
		}
		
		ITransportFactory transportFactory = this.transportFactories.get(transportId);
		if (transportFactory == null){
			throw new ProtocolException("Could not find a Transport Factory for the "+transportId+" type of transport.");
		}
		
		CLIProtocolSession session = new CLIProtocolSession(protocolSessionContext, sessionID);
		ITransport transport = null;
		try{
			transport = transportFactory.createTransportInstance(protocolSessionContext);
		}catch(TransportException ex){
			throw new ProtocolException(ex);
		}
		
		session.wireTransport(transport);
		return session;
	}
}
