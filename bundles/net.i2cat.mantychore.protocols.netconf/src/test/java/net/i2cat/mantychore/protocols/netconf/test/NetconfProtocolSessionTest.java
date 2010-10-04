package net.i2cat.mantychore.protocols.netconf.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSession;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.mantychore.protocols.serializer.SerializerHelper;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.IProtocolSession;
import com.iaasframework.capabilities.protocol.ProtocolCapability;
import com.iaasframework.capabilities.protocol.ProtocolCapabilityClient;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.capabilities.protocol.api.ProtocolRequestMessage;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;

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
		capabilityDescriptor = newProtocolDescriptor(1,
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

	/* Descriptors to testing */

	private CapabilityDescriptor newProtocolDescriptor(long id, String protocolURI) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();
		/* protocol info */
		Information information = new Information();
		information.setDescription("junos protocol");
		information.setName("junos protocol");
		information.setType(IProtocolConstants.PROTOCOL);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(getCapabilityProperty(IProtocolConstants.PROTOCOL, "Netconf"));
		properties.add(getCapabilityProperty(IProtocolConstants.PROTOCOL_VERSION, "1.0.0"));
		properties.add(getCapabilityProperty(NetconfProtocolSession.PROTOCOL_URI, protocolURI));

		/* netconf parameters */

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityProperty getCapabilityProperty(String propertyName,
			String propertyValue) {
		CapabilityProperty capabilityProperty = new
				CapabilityProperty();
		capabilityProperty.setName(propertyName);
		capabilityProperty.setValue(propertyValue);
		return capabilityProperty;
	}

}
