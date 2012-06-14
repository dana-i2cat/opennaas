package org.opennaas.extensions.protocols.tl1;

import java.net.URI;
import java.net.URISyntaxException;
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
		parseProtocolURI(protocolSessionContext);
		
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
	
	private void parseProtocolURI(ProtocolSessionContext protocolSessionContext) throws ProtocolException{
		Map<String, Object> params = protocolSessionContext.getSessionParameters();
		String uriStr = (String) params.get(ProtocolSessionContext.PROTOCOL_URI);

		if (uriStr == null) {
			throw new ProtocolException("Invalid uri");
		}

		try {
			URI uri = new URI(uriStr);
			String host = uri.getHost();
			
			//Get transport
			String transport = uri.getScheme();
			logger.debug("Adding "+ITransportConstants.TRANSPORT+"="+transport+" to protocol session context");
			protocolSessionContext.addParameter(ITransportConstants.TRANSPORT, transport);
			
			//Get transport.host and transport.port
			protocolSessionContext.addParameter(ITransportConstants.TRANSPORT_HOST, host);
			int port = uri.getPort();
			if (port != -1){
				protocolSessionContext.addParameter(ITransportConstants.TRANSPORT_PORT, ""+port);
			}
			
			//Get protocol.username and protocol.password
			String userInfo = uri.getUserInfo();
			String[] aux = null;
			if (userInfo != null){
				aux = userInfo.split(":");
				if (aux.length == 1 && aux[0].indexOf(":") == -1){
					protocolSessionContext.addParameter(ProtocolSessionContext.USERNAME, aux[0]);
				}else if (aux.length == 1 && aux[0].indexOf(":") != -1){
					protocolSessionContext.addParameter(ProtocolSessionContext.PASSWORD, aux[0]);
				}else if (aux.length == 2){
					protocolSessionContext.addParameter(ProtocolSessionContext.USERNAME, aux[0]);
					protocolSessionContext.addParameter(ProtocolSessionContext.PASSWORD, aux[1]);
				}
			}
			
			//Get optional parameters if present
			if (uri.getQuery() != null){
				aux = uri.getQuery().split("&");
				String[] aux2 = null;
				for(int i=0; i<aux.length; i++){
					aux2 = aux[0].split("=");
					protocolSessionContext.addParameter(aux2[0], aux2[1]);
				}
			}
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}
	
}
