package org.opennaas.itests.helpers.server;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class MockHTTPServerTest {

	private final static Log			log	= LogFactory.getLog(MockHTTPServerTest.class);

	protected HTTPServer				server;
	protected List<HTTPServerBehaviour>	desiredBehaviours;

	protected void startServer(String servletUrl) throws Exception {

		log.info("Creating HTTP server on http://localhost:8080" + servletUrl);

		server = new HTTPServer(8080);
		server.setBaseURL(servletUrl);

		log.debug("Adding desired behaviors to server.");
		server.setDesiredBehaviours(desiredBehaviours);
		server.start();

		log.info("HTTP Server successfully created on http://localhost:8080" + servletUrl);

	}

	protected void stopServer() throws Exception {

		log.info("Stopping HTTP server");
		server.stop();
		desiredBehaviours = null;

		log.info("HTTP server stopped.");
	}

	protected abstract void prepareBehaviours() throws Exception;
}
