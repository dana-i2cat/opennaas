package net.i2cat.mantychore.engine.transports.ssh;

import com.iaasframework.engines.core.descriptor.ModuleDescriptor;
import com.iaasframework.engines.transports.ITransport;
import com.iaasframework.engines.transports.ITransportConstants;
import com.iaasframework.engines.transports.ITransportFactory;
import com.iaasframework.engines.transports.TransportException;

public class SSHTransportFactory implements ITransportFactory{
	public ITransport createTransportInstance(ModuleDescriptor descriptor) throws TransportException {
		String transportId = descriptor.getPropertyValue(ITransportConstants.TRANSPORT);
		if (transportId == null){
			//We ignore it or throw an exception?
			throw new TransportException("No transport module has been specified at the engine configuration");
		}else if (transportId.equals(SSHTransport.SSH)){
			return createSSHTransport(descriptor);
		}else{
			throw new TransportException("Transport "+transportId+" cannot be created by this factory");
		}
	}
	
	private ITransport createSSHTransport(ModuleDescriptor descriptor) throws TransportException{
		String host = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_HOST);
		String port = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_PORT);
		String user = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_USERNAME);
		String password = getAndValidateProperty(descriptor, ITransportConstants.TRANSPORT_PASSWORD);
		

		return new SSHTransport(user,password,host,Integer.parseInt(port));
	}
	
	private String getAndValidateProperty(ModuleDescriptor descriptor, String propertyName) throws TransportException{
		String property = descriptor.getPropertyValue(propertyName);
		if (property ==  null){
			throw new TransportException("Could not create an instance of transport. Property "+propertyName+" has not been specified");
		}
		
		return property;
	}
}
