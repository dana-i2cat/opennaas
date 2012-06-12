package org.opennaas.extensions.protocols.tl1;

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
 * This class manages TL1 Protocol sessions
 * @author edu
 *
 */
public class TL1ProtocolSessionFactory implements IProtocolSessionFactory{
	
	/** The logger **/
	Logger logger = LoggerFactory.getLogger(TL1ProtocolSessionFactory.class);
	
	/** The transport factory **/
	private Map<String, ITransportFactory> transportFactories = null;

	public TL1ProtocolSessionFactory(){
		super();
		logger.info("TL1 Protocol Session Factory created");
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

	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		if ((String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.USERNAME) == null){
			throw new ProtocolException("The TL1 protocol session needs the "+ProtocolSessionContext.USERNAME+" parameter");
		}
		
		if ((String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PASSWORD) == null){
			throw new ProtocolException("The TL1 protocol session needs the "+ProtocolSessionContext.PASSWORD+" parameter");
		}
		
		String transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		if (transportId == null){
			throw new ProtocolException("The TL1 protocol session needs the "+ITransportConstants.TRANSPORT+" parameter");
		}
		
		ITransportFactory transportFactory = this.transportFactories.get(transportId);
		if (transportFactory == null){
			throw new ProtocolException("Could not find a Transport Factory for the "+transportId+" type of transport.");
		}
		
		TL1ProtocolSession session = new TL1ProtocolSession(protocolSessionContext, sessionID);
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
