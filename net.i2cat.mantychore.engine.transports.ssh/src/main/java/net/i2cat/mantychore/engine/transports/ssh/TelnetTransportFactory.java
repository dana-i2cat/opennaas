package net.i2cat.mantychore.engine.transports.ssh;

import com.iaasframework.engines.core.descriptor.ModuleDescriptor;
import com.iaasframework.engines.transports.ITransport;
import com.iaasframework.engines.transports.ITransportConstants;
import com.iaasframework.engines.transports.ITransportFactory;
import com.iaasframework.engines.transports.TransportException;

/**
 * Provides a factory method to create Telnet transport instances. Assumes the engine
 * descriptor object is of type java.util.Properties
 * @author Carlos Baez
 */
public class TelnetTransportFactory implements ITransportFactory {

	public ITransport createTransportInstance(ModuleDescriptor descriptor) throws TransportException {
		String transportId = descriptor.getPropertyValue(ITransportConstants.TRANSPORT);
		if (transportId == null){
			//We ignore it or throw an exception?
			throw new TransportException("No transport module has been specified at the engine configuration");
		}else if (transportId.equals(TelnetTransport.TELNET)){
			return createTelnetTransport(descriptor);
		}else{
			throw new TransportException("Transport "+transportId+" cannot be created by this factory");
		}
	}
	
	private ITransport createTelnetTransport(ModuleDescriptor descriptor) throws TransportException{
		String host = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_HOST);
		String port = null;
		try{
			port = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_PORT);
		}catch(TransportException ex){
			//if there is no port, we'll use the default one: 23
			port = "23";
		}
		
		return new TelnetTransport(host, port);
	}
	
	private String getAndValidateProperty(ModuleDescriptor descriptor, String propertyName) throws TransportException{
		String property = descriptor.getPropertyValue(propertyName);
		if (property ==  null){
			throw new TransportException("Could not create an instance of transport. Property "+propertyName+" has not been specified");
		}
		
		return property;
	}
}
