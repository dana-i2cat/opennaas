import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.commands.JunosCommand;
import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.ListLogicalRouterParser;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class ConfigurationParserNetconfConectorTest {

	private static NetconfProtocolSessionFactory	netconfProtocolSessionFactory;
	private static ProtocolSessionContext			protocolSessionContext;
	IProtocolSession								protocolSession	= null;

	/** logger **/
	Logger											log				= LoggerFactory
																			.getLogger(JunosCommand.class);

	public void setupConnection() {
		netconfProtocolSessionFactory = new NetconfProtocolSessionFactory();
		protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:boo@testing.default.net:22");
		protocolSession = netconfProtocolSessionFactory.createProtocolSession("1", protocolSessionContext);
		protocolSession.connect();
	}

	@Test
	public void getConfigurationCommandTest() throws ProtocolException {

		Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);
		queryGetConfig.setMessageId("1");

		Reply reply = (Reply) protocolSession.sendReceive(queryGetConfig);

		HashMap<String, Object> interfs = new HashMap<String, Object>();
		/* Parse logical interface info */
		DigesterEngine logicalRouter = new ListLogicalRouterParser();
		logicalRouter.init();
		logicalRouter.setMapElements(interfs);
		/*
		 * parse a string to byte array, and it is sent to the logicalInterfaceParser
		 */
		try {
			logicalRouter.configurableParse(new ByteArrayInputStream(reply.getContain().getBytes("UTF-8")));
			Assert.assertNotNull(logicalRouter.toString());
			System.out.println(logicalRouter.toString());

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Errors encodign the response");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error in I/O ");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Sax error");
		}

	}
}
