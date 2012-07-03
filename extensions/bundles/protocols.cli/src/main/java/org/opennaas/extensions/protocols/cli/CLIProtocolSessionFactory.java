package org.opennaas.extensions.protocols.cli;

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
		logger.info("Attempting to create a new CLI protocol session with sessionID: "+sessionID);
		
		parseProtocolURI(protocolSessionContext);
		
		String transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		logger.debug("TransportId is "+transportId);
		if (transportId == null){
			throw new ProtocolException("The CLI protocol session needs the "+ITransportConstants.TRANSPORT+" parameter");
		}
		
		ITransportFactory transportFactory = this.transportFactories.get(transportId);
		if (transportFactory == null){
			throw new ProtocolException("Could not find a Transport Factory for the "+transportId+" type of transport.");
		}
		logger.debug("Found transport factory for transport "+transportId);
		
		CLIProtocolSession session = new CLIProtocolSession(protocolSessionContext, sessionID);
		ITransport transport = null;
		try{
			transport = transportFactory.createTransportInstance(protocolSessionContext);
		}catch(TransportException ex){
			throw new ProtocolException(ex);
		}
		
		session.wireTransport(transport);
		logger.debug("Wired transport");
		return session;
	}
	
	private void parseProtocolURI(ProtocolSessionContext protocolSessionContext) throws ProtocolException{
		Map<String, Object> params = protocolSessionContext.getSessionParameters();
		String uriStr = (String) params.get(ProtocolSessionContext.PROTOCOL_URI);

		if (uriStr == null) {
			throw new ProtocolException("Invalid uri");
		}

		try {
			logger.debug("Found URI string: "+uriStr);
			URI uri = new URI(uriStr);
			String host = uri.getHost();
			
			//Get transport
			String transport = uri.getScheme();
			logger.debug("Adding "+ITransportConstants.TRANSPORT+"="+transport+" to protocol session context");
			protocolSessionContext.addParameter(ITransportConstants.TRANSPORT, transport);
			
			//Get transport.host and transport.port
			logger.debug("Adding "+ITransportConstants.TRANSPORT_HOST+"="+host+" to protocol session context");
			protocolSessionContext.addParameter(ITransportConstants.TRANSPORT_HOST, host);
			int port = uri.getPort();
			if (port != -1){
				logger.debug("Adding "+ITransportConstants.TRANSPORT_PORT+"="+port+" to protocol session context");
				protocolSessionContext.addParameter(ITransportConstants.TRANSPORT_PORT, ""+port);
			}
			
			//Get protocol.username and protocol.password
			String userInfo = uri.getUserInfo();
			String[] aux = null;
			if (userInfo != null){
				aux = userInfo.split(":");
				if (aux.length == 1 && aux[0].indexOf(":") == -1){
					logger.debug("Adding "+ProtocolSessionContext.USERNAME+"="+aux[0]+" to protocol session context");
					protocolSessionContext.addParameter(ProtocolSessionContext.USERNAME, aux[0]);
				}else if (aux.length == 1 && aux[0].indexOf(":") != -1){
					logger.debug("Adding "+ProtocolSessionContext.PASSWORD+"="+aux[0]+" to protocol session context");
					protocolSessionContext.addParameter(ProtocolSessionContext.PASSWORD, aux[0]);
				}else if (aux.length == 2){
					if (!aux[0].equals("")){
						logger.debug("Adding "+ProtocolSessionContext.USERNAME+"="+aux[0]+" to protocol session context");
						protocolSessionContext.addParameter(ProtocolSessionContext.USERNAME, aux[0]);
					}
					logger.debug("Adding "+ProtocolSessionContext.PASSWORD+"="+aux[1]+" to protocol session context");
					protocolSessionContext.addParameter(ProtocolSessionContext.PASSWORD, aux[1]);
				}
			}
			
			//Get optional parameters if present
			if (uri.getQuery() != null){
				aux = uri.getQuery().split("&");
				String[] aux2 = null;
				for(int i=0; i<aux.length; i++){
					aux2 = aux[i].split("=");
					logger.debug("Adding "+aux2[0]+"="+aux2[1]+" to protocol session context");
					protocolSessionContext.addParameter(aux2[0], aux2[1]);
				}
			}
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}
}
