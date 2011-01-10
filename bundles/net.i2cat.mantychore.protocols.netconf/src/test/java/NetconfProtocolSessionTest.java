import static org.junit.Assert.fail;

import java.util.Vector;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSession;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class NetconfProtocolSessionTest {
	/** The logger **/
	Logger									log						= LoggerFactory.getLogger(NetconfProtocolSessionTest.class);

	private CapabilityDescriptor			capabilityDescriptor	= null;
	int										counter					= 0;
	IProtocolSession						protocolSession			= null;
	// ProtocolCapability protocolCapability = null;
	String									resourceId				= "testNetconf";

	// ProtocolCapabilityClient protocolClient = null;
	private static NetconfProtocolSession	netconfProtocolSession	= null;

	@Before
	public void setup() throws ProtocolException {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:boo@testing.default.net:22");
		try {
			netconfProtocolSession = new NetconfProtocolSession(protocolSessionContext, "1");
			netconfProtocolSession.connect();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	@After
	public void close() {

		try {
			netconfProtocolSession.disconnect();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* operation tests */
	@Test
	public void KeepAliveTest() throws ProtocolException {

		// Object protocolResponse = netconfProtocolSession.sendReceive(queryKeepAlive);
		// netconfProtocolSession.asyncSend(queryKeepAlive);

		Query queryKeepAlive = QueryFactory.newKeepAlive();

		queryKeepAlive.setMessageId("1");

		log.debug(queryKeepAlive.toXML());

		Reply reply = (Reply) netconfProtocolSession.sendReceive(queryKeepAlive);
		if (reply.containsErrors()) {
			printErrors(reply.getErrors());
			fail("The response received errors");
		}

		// Assert.assertNotNull(protocolResponse);
	}

	@Test
	public void getConfigTest() throws ProtocolException {

		Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);

		log.debug(queryGetConfig.toXML());

		Reply reply = (Reply) netconfProtocolSession.sendReceive(queryGetConfig);

		// System.out.println(reply.getContain());

		if (reply.getContain() == null) {
			fail("The response received is null");
		}
		if (reply.containsErrors()) {
			printErrors(reply.getErrors());
			fail("The response received errors");
		}

	}

	private void printErrors(Vector<Error> errors) {
		for (Error error : errors) {
			log.error("Error: " + error.getMessage());
		}

	}

}
