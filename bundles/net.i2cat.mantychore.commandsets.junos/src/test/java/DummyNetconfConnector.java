import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;

public class DummyNetconfConnector {

	private SessionContext	sessionContext;
	private NetconfSession	session;
	private URI				uri;

	public DummyNetconfConnector(String uri) throws URISyntaxException {
		this.uri = new URI(uri);
		try {
			sessionContext = new SessionContext();
			sessionContext.newConfiguration(new PropertiesConfiguration("test-default.properties"));

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
		}
	}

	public String sendAndReceive(Object command) throws Exception {
		// sessionContext.setURI(new URI("mock://foo:bar@foo:22/foo"));

		sessionContext.setURI(this.uri);
		session = new NetconfSession(sessionContext);
		session.connect();
		Reply reply = session.sendSyncQuery((Query) command);
		session.disconnect();

		if (reply.getErrors().size() != 0) {
			Enumeration<Error> e = reply.getErrors().elements();
			String s = "";
			while (e.hasMoreElements()) {
				s = s.concat("Error in message received: " + e.nextElement().getMessage() + "\n");
			}
			return s;
		}

		if (reply.isOk()) {
			return "true";
		}

		return reply.getContain();
	}
}
