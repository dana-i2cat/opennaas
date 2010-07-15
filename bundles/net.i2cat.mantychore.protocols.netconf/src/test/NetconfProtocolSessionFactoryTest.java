import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.IProtocolSession;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

public class NetconfProtocolSessionFactoryTest {

	private NetconfProtocolSessionFactory	netconfProtocolSessionFactory	= null;
	private CapabilityDescriptor			capabilityDescriptor			= null;
	int										counter							= 0;

	@Before
	public void setup() {
		netconfProtocolSessionFactory = new NetconfProtocolSessionFactory();
	}

	@Test
	public void testMissingUsernameModuleDescriptor() {
		capabilityDescriptor = getMissingUsernameCapabilityDescriptor();
		try {
			netconfProtocolSessionFactory
					.createProtocolSessionInstance(capabilityDescriptor);
		} catch (ProtocolException ex) {
			ex.printStackTrace();
			Assert.assertTrue(true);
			return;
		}

		Assert.assertTrue(false);
	}

	@Test
	public void testMissingPasswordModuleDescriptor() {
		capabilityDescriptor = getMissingPasswordCapabilityDescriptor();
		try {
			netconfProtocolSessionFactory
					.createProtocolSessionInstance(capabilityDescriptor);
		} catch (ProtocolException ex) {
			ex.printStackTrace();
			Assert.assertTrue(true);
			return;
		}

		Assert.assertTrue(false);
	}

	@Test
	public void testCorrectModuleDescriptor() {
		capabilityDescriptor = getCorrectCapabilityDescriptor();
		IProtocolSession protocolSession = null;
		try {
			protocolSession = netconfProtocolSessionFactory
					.createProtocolSessionInstance(capabilityDescriptor);
		} catch (ProtocolException ex) {
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}

		Assert.assertNotNull(protocolSession);
	}

	private CapabilityDescriptor getMissingUsernameCapabilityDescriptor() {
		List<CapabilityProperty> moduleProperties = new ArrayList<CapabilityProperty>();
		moduleProperties.add(getCapabilityProperty(
				IProtocolConstants.PROTOCOL_PASSWORD, "test"));
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityProperties(moduleProperties);
		return capabilityDescriptor;
	}

	private CapabilityDescriptor getMissingPasswordCapabilityDescriptor() {
		List<CapabilityProperty> moduleProperties = new ArrayList<CapabilityProperty>();
		moduleProperties.add(getCapabilityProperty(
				IProtocolConstants.PROTOCOL_USERNAME, "admin"));
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityProperties(moduleProperties);
		return capabilityDescriptor;
	}

	private CapabilityDescriptor getCorrectCapabilityDescriptor() {
		List<CapabilityProperty> moduleProperties = new ArrayList<CapabilityProperty>();
		moduleProperties.add(getCapabilityProperty(
				IProtocolConstants.PROTOCOL_USERNAME, "admin"));
		moduleProperties.add(getCapabilityProperty(
				IProtocolConstants.PROTOCOL_PASSWORD, "test"));
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityProperties(moduleProperties);
		return capabilityDescriptor;
	}

	private CapabilityProperty getCapabilityProperty(String propertyName,
			String propertyValue) {
		CapabilityProperty capabilityProperty = new CapabilityProperty();
		capabilityProperty.setName(propertyName);
		capabilityProperty.setValue(propertyValue);
		return capabilityProperty;
	}
}
