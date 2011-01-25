import static org.junit.Assert.fail;

import java.util.Vector;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfProtocolSessionFactoryTest {
	/** The logger **/
	Logger											log								= LoggerFactory.getLogger(NetconfProtocolSessionTest.class);

	private static NetconfProtocolSessionFactory	netconfProtocolSessionFactory	= null;
	private static ProtocolSessionContext			protocolSessionContext			= null;
	int												counter							= 0;
	IProtocolSession								protocolSession					= null;

	@BeforeClass
	public static void setup() {
		netconfProtocolSessionFactory = new NetconfProtocolSessionFactory();
		protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:boo@testing.default.net:22");
		// "mock://foo:boo@testing.default.net:22"
	}

	@Test
	public void testCorrectprotocolSession() {

		try {
			protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(protocolSession);
	}

	@Test
	public void testConnection() {
		try {
			protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
			// String exit = (String)
			// protocolSession.getSessionContext().getSessionParameters().get("protocol.uri");
			String s = protocolSession.getStatus().toString();
			log.debug(s);
			protocolSession.connect();
			s = protocolSession.getStatus().toString();
			log.debug(s);
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testKeepAlive() {

		try {
			protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
			protocolSession.connect();
			Query queryKeepAlive = QueryFactory.newKeepAlive();
			queryKeepAlive.setMessageId("1");
			Reply reply = (Reply) protocolSession.sendReceive(queryKeepAlive);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}
			protocolSession.disconnect();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

	}

	private void printErrors(Vector<Error> errors) {
		for (Error error : errors) {
			log.error("Error: " + error.getMessage());
		}

	}
}