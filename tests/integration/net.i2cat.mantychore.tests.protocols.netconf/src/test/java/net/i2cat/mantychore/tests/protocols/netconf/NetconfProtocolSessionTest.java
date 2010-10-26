package net.i2cat.mantychore.test.protocols.netconf;


import java.util.UUID;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolHelper;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.mantychore.protocols.serializer.SerializerHelper;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.protocol.IProtocolSession;
import com.iaasframework.capabilities.protocol.ProtocolCapability;
import com.iaasframework.capabilities.protocol.ProtocolCapabilityClient;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.capabilities.protocol.api.ProtocolRequestMessage;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class NetconfProtocolSessionTest {
	/** The logger **/
	Logger							log						= LoggerFactory.getLogger(NetconfProtocolSessionTest.class);

	private CapabilityDescriptor	capabilityDescriptor	= null;
	int								counter					= 0;
	IProtocolSession				protocolSession			= null;
	ProtocolCapability				protocolCapability		= null;
	String							resourceId				= "testNetconf";
	ProtocolCapabilityClient		protocolClient			= null;

	@Before
	public void setup() throws ProtocolException {
		capabilityDescriptor = NetconfProtocolHelper.newProtocolDescriptor(1,
				System.getProperty("net.i2cat.mantychore.protocols.netconf.test.transporturi"
						, "mock://foo:bar@foo:22/okServer")
						);

		try {
			protocolCapability = new ProtocolCapability(capabilityDescriptor, resourceId);
			protocolCapability.setProtocolSessionFactory(new NetconfProtocolSessionFactory());
			protocolCapability.initialize();
			protocolCapability.activate();

			protocolClient = new ProtocolCapabilityClient(resourceId);

		} catch (CapabilityException e) {
			log.error(e.getMessage(), e);
		}

	}

	@After
	public void close() {
		try {

			protocolClient.dispose();

			protocolCapability.deactivate();
			protocolCapability.shutdown();

		} catch (CapabilityException e) {
			log.error(e.getMessage(), e);
		}
	}

	/* operation tests */
	@Test
	public void KeepAliveTest() throws ProtocolException {

		ProtocolRequestMessage requestMessage = new ProtocolRequestMessage();
		Query queryKeepAlive = QueryFactory.newKeepAlive();
		String message = SerializerHelper.objectToString(queryKeepAlive);

		requestMessage.setMessage(message);
		requestMessage.setMessageID(UUID.randomUUID().toString());

		Object protocolResponse = protocolClient.sendReceiveMessage(message);

	}

}
