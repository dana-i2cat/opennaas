package org.opennaas.extensions.transports.telnet;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.ITransportConstants;
import org.opennaas.core.resources.transport.ITransportFactory;
import org.opennaas.core.resources.transport.TransportException;

/**
 * Provides a factory method to create TCP transport instances. The following properties need to 
 * be present in the capability descriptor:
 * transport = TCP
 * transport.host = <ip_address or hostname>
 * transport.port = <port number>
 */
public class TelnetTransportFactory implements ITransportFactory{

	public ITransport createTransportInstance(ProtocolSessionContext protocolSessionContext) throws TransportException {
		String transportId = null;
		try{
			transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		}catch(ClassCastException ex){
			throw new TransportException(ex.getMessage());
		}
		
		if (transportId == null){
			throw new TransportException("No transport id has been specified at the resource configuration");
		}else if (transportId.equals(TelnetTransport.TELNET)){
			return createTelnetTransport(protocolSessionContext);
		}else{
			throw new TransportException("Transport "+transportId+" cannot be created by this factory");
		}
	}
	
	private ITransport createTelnetTransport(ProtocolSessionContext protocolSessionContext) throws TransportException{
		String host = getAndValidateProperty(protocolSessionContext, ITransportConstants.TRANSPORT_HOST);
		String port = null;
		try{
			port= getAndValidateProperty(protocolSessionContext, ITransportConstants.TRANSPORT_PORT);
		}catch(TransportException ex){
			port="23";
		}

		return new TelnetTransport(host, port);
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