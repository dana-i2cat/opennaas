import java.net.URI;
import java.net.URISyntaxException;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

public class DummyNetconfConnector {

	private SessionContext	sessionContext;
	private NetconfSession	session;
	private URI				uri;

	public DummyNetconfConnector(String uri) throws URISyntaxException {
		this.uri = new URI(uri);

	}

	public String sendAndReceive(Object command) throws Exception {
		sessionContext.setURI(new URI("mock://foo:bar@foo:22/foo"));
		session = new NetconfSession(sessionContext);
		session.connect();
		Reply reply = session.sendSyncQuery((Query) command);
		session.disconnect();
		return reply.getContain();

	}

}
