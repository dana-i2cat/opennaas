package org.opennaas.core.resources.transport;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * This interface must be implemented by the transport packages. Each one must provide at least one class 
 * that implements this interface and publish it to the OSGi registry. This is mainly to avoid the cyclic dependency
 * problem between modules and transports. Implementing classes do not need to maintain the created instances (i.e. 
 * they can be stateless)
 * The create method of the transport factory will validate the information in the module descriptor (if there's
 * missing or incorrect information it will throw an exception) and, if correct, it will create a transport instance.
 * @author Eduard Grasa
 *
 */
public interface ITransportFactory {
	public ITransport createTransportInstance(ProtocolSessionContext context) throws TransportException;
}
