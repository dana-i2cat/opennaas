import static org.junit.Assert.fail;

import java.util.Vector;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfProtocolSessionTest {
	/** The logger **/
	static Logger							log						= LoggerFactory.getLogger(NetconfProtocolSessionTest.class);

	int										counter					= 0;
	IProtocolSession						protocolSession			= null;

	String									resourceId				= "testNetconf";

	private static NetconfProtocolSession	netconfProtocolSession	= null;

	@BeforeClass
	public static void setup() throws ProtocolException {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:boo@testing.default.net:22");
		try {
			netconfProtocolSession = new NetconfProtocolSession(protocolSessionContext, "1");
			netconfProtocolSession.connect();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());

		}

	}

	@AfterClass
	public static void close() {

		try {
			netconfProtocolSession.disconnect();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());

		}
	}

	/* operation tests */
	@Test
	public void KeepAliveTest() throws ProtocolException {

		// Object protocolResponse =
		// netconfProtocolSession.sendReceive(queryKeepAlive);
		// netconfProtocolSession.asyncSend(queryKeepAlive);

		Query queryKeepAlive = QueryFactory.newKeepAlive();
		queryKeepAlive.setMessageId("1");
		Reply reply = (Reply) netconfProtocolSession.sendReceive(queryKeepAlive);
		if (reply.containsErrors()) {
			printErrors(reply.getErrors());
			fail("The response received errors");
		}
		Assert.assertNotNull(reply.getContain());
	}

	@Test
	public void getConfigTest() throws ProtocolException {

		Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);

		log.debug(queryGetConfig.getOperation().getName());

		Reply reply = (Reply) netconfProtocolSession.sendReceive(queryGetConfig);

		log.debug(reply.getContain());
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
