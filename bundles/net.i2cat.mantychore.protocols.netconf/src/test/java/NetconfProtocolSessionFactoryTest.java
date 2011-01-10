import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class NetconfProtocolSessionFactoryTest {

	private static NetconfProtocolSessionFactory	netconfProtocolSessionFactory	= null;
	private CapabilityDescriptor					capabilityDescriptor			= null;
	private static ProtocolSessionContext			protocolSessionContext			= null;
	int												counter							= 0;
	IProtocolSession								protocolSession					= null;

	@BeforeClass
	public static void setup() {
		netconfProtocolSessionFactory = new NetconfProtocolSessionFactory();
		protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:boo@testing.default.net:22");
	}

	@Test
	public void testCorrectprotocolSession() throws ProtocolException {

		protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
		Assert.assertNotNull(protocolSession);
	}

	@Test
	public void testConnection() throws ProtocolException {
		protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
		// String exit = (String) protocolSession.getSessionContext().getSessionParameters().get("protocol.uri");
		String s = protocolSession.getStatus().toString();
		protocolSession.connect();
	}

}