package org.opennaas.itests.helpers.server;

import java.util.List;

import javax.xml.bind.JAXBException;

public abstract class MockHTTPServerTest {

	protected HTTPServer				server;
	protected List<HTTPServerBehaviour>	desiredBehaviours;

	protected void startServer(String servletUrl) throws Exception {

		server = new HTTPServer(8080);
		server.setBaseURL(servletUrl);
		server.setDesiredBehaviours(desiredBehaviours);
		server.start();

	}

	protected void stopServer() throws Exception {
		server.stop();
		desiredBehaviours = null;
	}

	protected abstract void prepareBehaviours() throws JAXBException;
}
