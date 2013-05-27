package org.opennaas.extensions.transports.virtual;

/**
 * This interface must be implemented by VirtualTransportProviders that have to provide a response to the requests 
 * that the virtual transport receives. VirtualTransportProviders have to be published at the OSGi registry, by the 
 * corresponding engine bundles (for example, the ome6500 bundle will publish an OME6500VirtualTransportProvider).
 * @author eduardgrasa
 *
 */
public interface IVirtualTransportProvider {
	public static final String TRANSPORT_VIRTUALTRANSPORTPROVIDER = "transport.virtualTransportProvider";
	
	public Object getMessageTransportResponse(Object request);
	public byte[] getStreamTransportReponse(byte[] request);
}
