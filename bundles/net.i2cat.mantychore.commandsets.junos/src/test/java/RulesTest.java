import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesTest {
	private SessionContext	sessionContext;
	private NetconfSession	session;

	/** The logger **/
	Logger					log	= LoggerFactory
																.getLogger(RulesTest.class);

	@Test
	public void testRulesForLogicalRouters() {
		// try {
		// SessionContext sessionContext = new SessionContext();
		// sessionContext.setURI(new URI("mock://foo:bar@foo:22/foo"));
		// session = new NetconfSession(sessionContext);
		// session.connect();
		//
		// Reply reply = sendAndReceiveGetConfig();
		// String replyContain = reply.getContain();
		//
		// HashMap<String, Object> interfs = new HashMap<String, Object>();
		// /* Parse logical interface info */
		//
		// DigesterEngine logicalInterfParser = new
		// IPConfigurationInterfaceParser();
		// logicalInterfParser.init();
		// logicalInterfParser.setMapElements(interfs);
		// logicalInterfParser.configurableParse(new
		// ByteArrayInputStream(replyContain.getBytes("UTF-8")));
		// HashMap<String, Object> resultInterfs =
		// logicalInterfParser.getMapElements();
		//
		// reply = sendAndReceiveGetConfigInLogicalRouter();
		// replyContain = reply.getContain();
		//
		// logicalInterfParser = new
		// IPConfigurationInterfaceParser("logical-systems");
		// logicalInterfParser.init();
		// logicalInterfParser.setMapElements(interfs);
		// logicalInterfParser.configurableParse(new
		// ByteArrayInputStream(replyContain.getBytes("UTF-8")));
		//
		// HashMap<String, Object> resultInterfsInLogical =
		// logicalInterfParser.getMapElements();
		//
		// // FIXME CHECK IF ANY PARSER COULD GET SOME INTERFACE
		// Assert.assertTrue(!resultInterfsInLogical.isEmpty() &&
		// !resultInterfs.isEmpty());
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// Assert.fail(e.getMessage());
		// }

	}

	protected Reply sendAndReceiveGetConfig() throws ResourceNotFoundException,
			ParseErrorException, Exception {

		VelocityEngine velocityEngine = new VelocityEngine("/getconfiguration.vm", null);
		String command = velocityEngine.mergeTemplate();
		Query queryGetConfig = QueryFactory.newGetConfig("candidate", command, null);

		log.debug(queryGetConfig.toXML());
		Reply reply = session.sendSyncQuery(queryGetConfig);
		log.debug(reply.getContain());

		return reply;

	}

	protected Reply sendAndReceiveGetConfigInLogicalRouter() throws ResourceNotFoundException,
			ParseErrorException, Exception {

		VelocityEngine velocityEngine = new VelocityEngine("/getconfiguration.vm", null);
		String command = velocityEngine.mergeTemplate();

		Query querySetLogicalRouter = QueryFactory.newSetLogicalRouter("cpe1");
		querySetLogicalRouter.setMessageId("1");

		session.sendSyncQuery(querySetLogicalRouter);

		Query queryGetConfig = QueryFactory.newGetConfig("candidate", command, null);
		queryGetConfig.setMessageId("2");

		log.debug(queryGetConfig.toXML());
		Reply reply = session.sendSyncQuery(queryGetConfig);
		log.debug(reply.getContain());

		return reply;

	}

}
