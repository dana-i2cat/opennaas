package net.i2cat.mantychore.protocols.netconf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.protocol.IProtocolSession;
import com.iaasframework.capabilities.protocol.IProtocolSessionFactory;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class NetconfProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Logger	logger	= LoggerFactory.getLogger(NetconfProtocolSessionFactory.class);

	public NetconfProtocolSessionFactory() {
		super();
		logger.info("Netconf Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSessionInstance(CapabilityDescriptor capabilityDescriptor) throws ProtocolException {

		// TODO INCLUDE CHECK PROTOCOL CAPABILITIES
		// if
		// (capabilityDescriptor.getPropertyValue(IProtocolConstants.PROTOCOL_USERNAME)
		// == null) {
		// throw new ProtocolException("Netconf protocol session needs the " +
		// IProtocolConstants.PROTOCOL_USERNAME + " parameter");
		// }
		//
		// if
		// (capabilityDescriptor.getPropertyValue(IProtocolConstants.PROTOCOL_PASSWORD)
		// == null) {
		// throw new ProtocolException("Netconf protocol session needs the " +
		// IProtocolConstants.PROTOCOL_PASSWORD + " parameter");
		// }

		NetconfProtocolSession session = new NetconfProtocolSession(capabilityDescriptor);
		return session;
	}

}
