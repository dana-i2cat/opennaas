package net.i2cat.mantychore.actionsets.junos.test;

import net.i2cat.mantychore.actionsets.junos.JunosActionFactory;
import net.i2cat.mantychore.actionsets.junos.JunosActionHelper;

import org.junit.Before;
import org.junit.Test;

import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class JunosActionFactoryTest {
	private JunosActionFactory		junosActionFactory		= null;
	private CapabilityDescriptor	capabilityDescriptor	= null;
	int								counter					= 0;

	@Before
	public void setup() {
		junosActionFactory = new JunosActionFactory();
	}

	@Test
	public void testCorrectModuleDescriptor() {

		capabilityDescriptor = JunosActionHelper.newActionSetDescriptor(1);

		// capabilityDescriptor = NetconfProtocolHelper.
		// IProtocolSession protocolSession = null;
		// protocolSession =
		// netconfProtocolSessionFactory.createProtocolSessionInstance(capabilityDescriptor);

	}

}
