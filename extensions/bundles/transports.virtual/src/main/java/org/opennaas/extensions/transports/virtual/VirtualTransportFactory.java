package org.opennaas.extensions.transports.virtual;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.ITransportConstants;
import org.opennaas.core.resources.transport.ITransportFactory;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a factory method to create TCP transport instances. The following properties need to 
 * be present in the capability descriptor:
 * transport = TCP
 * transport.host = <ip_address or hostname>
 * transport.port = <port number>
 */
public class VirtualTransportFactory implements ITransportFactory{
	
	/** The logger **/
	Logger logger = LoggerFactory.getLogger(VirtualTransportFactory.class);
	
	/** The virtual transport providers **/
	private Map<String, IVirtualTransportProvider> virtualTransportProviders = null;
	
	public VirtualTransportFactory(){
		this.virtualTransportProviders = new HashMap<String, IVirtualTransportProvider>();
	}

	public ITransport createTransportInstance(ProtocolSessionContext protocolSessionContext) throws TransportException {
		String transportId = null;
		try{
			transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		}catch(ClassCastException ex){
			throw new TransportException(ex.getMessage());
		}
		
		if (transportId == null){
			throw new TransportException("No transport id has been specified at the resource configuration");
		}else if (transportId.equals(VirtualTransport.VIRTUAL)){
			return createVirtualTransport(protocolSessionContext);
		}else{
			throw new TransportException("Transport "+transportId+" cannot be created by this factory");
		}
	}
	
	/**
	 * Called by blueprint every time a virtual transport provider is registered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void virtualTransportProviderAdded(IVirtualTransportProvider serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			virtualTransportProviders.put((String)serviceProperties.get("transport"), serviceInstance);
			logger.debug("New transport factory added for transports of type: "+serviceProperties.get("transport"));
		}
	}
	
	/**
	 * Called by blueprint every time a virtual transport provider is unregistered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void virtualTransportProviderRemoved(IVirtualTransportProvider serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			virtualTransportProviders.remove((String)serviceProperties.get("transport"));
			logger.debug("Existing transport factory removed for transports of type: "+serviceProperties.get("transport"));
		}
	}
	
	private ITransport createVirtualTransport(ProtocolSessionContext protocolSessionContext) throws TransportException{
		String provider = getAndValidateProperty(protocolSessionContext, IVirtualTransportProvider.TRANSPORT_VIRTUALTRANSPORTPROVIDER);
		if (provider == null){
			throw new TransportException("Virtual transport provider property not present in Protocol Session Context");
		}
		
		IVirtualTransportProvider vtProvider = this.virtualTransportProviders.get(provider);
		if (vtProvider == null){
			throw new TransportException("Could not find a virtual transport provider for resources of type "+provider);
		}
		
		return new VirtualTransport(vtProvider);
	}
	
	private String getAndValidateProperty(ProtocolSessionContext protocolSessionContext, String propertyName) throws TransportException{
		String property = null;
		try{
			property = (String) protocolSessionContext.getSessionParameters().get(propertyName);
		}catch(ClassCastException ex){
			throw new TransportException(ex.getMessage());
		}
		
		if (property ==  null){
			throw new TransportException("Could not create an instance of transport. Property "+propertyName+" has not been specified");
		}
		
		return property;
	}
}