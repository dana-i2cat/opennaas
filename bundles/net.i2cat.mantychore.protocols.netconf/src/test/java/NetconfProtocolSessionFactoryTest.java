

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSession;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.IProtocolSession;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;

public class NetconfProtocolSessionFactoryTest {

	private NetconfProtocolSessionFactory	netconfProtocolSessionFactory	= null;
	private CapabilityDescriptor			capabilityDescriptor			= null;
	int										counter							= 0;

	@Before
	public void setup() {
		netconfProtocolSessionFactory = new NetconfProtocolSessionFactory();
	}

	@Test
	public void testCorrectModuleDescriptor() throws ProtocolException {
		capabilityDescriptor = newProtocolDescriptor(1,
				System.getProperty("net.i2cat.mantychore.protocols.netconf.test.transporturi"
						, "mock://foo:bar@foo:22/okServer")
						);

		IProtocolSession protocolSession = null;
		protocolSession = netconfProtocolSessionFactory.createProtocolSessionInstance(capabilityDescriptor);

		Assert.assertNotNull(protocolSession);
	}

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