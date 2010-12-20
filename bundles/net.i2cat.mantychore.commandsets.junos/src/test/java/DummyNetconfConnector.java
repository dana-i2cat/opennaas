import java.net.URI;

public class DummyNetconfConnector {
	private URI	uri;

	public DummyNetconfConnector(URI uri) {
		this.uri = uri;

	}

	public String sendAndReceive() {
		// sessionContext.setURI(new URI("mock://foo:bar@foo:22/foo"));
		//
		// session = new NetconfSession(sessionContext);
		// session.connect();
		// Reply reply = session.sendSyncQuery((Query) command);
		// response = new CapabilityMessage();
		// response.setMessage(reply.getContain());
		//
		// session.disconnect();
		return null;

	}

}
