package org.opennaas.itests.helpers.server;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Defines the main architecture of a test which contains a resource capability with a HTTP REST driver.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class MockHTTPServerTest {

	private final static Log			log	= LogFactory.getLog(MockHTTPServerTest.class);

	protected HTTPServer				server;
	protected List<HTTPServerBehaviour>	desiredBehaviours;

	/**
	 * Creates and starts a HTTP server in port 8080, listening for requests under the given servletURL url. It will contain the previous configured
	 * list of behaviors, which define the set of requests the server accepts and the responses associated to them.
	 * 
	 * @param servletUrl
	 * @throws Exception
	 */
	protected void startServer(String servletUrl) throws Exception {

		log.info("Creating HTTP server on http://localhost:8080" + servletUrl);

		server = new HTTPServer(8080);
		server.setBaseURL(servletUrl);

		log.debug("Adding desired behaviors to server.");
		server.setDesiredBehaviours(desiredBehaviours);
		server.start();

		log.info("HTTP Server successfully created on http://localhost:8080" + servletUrl);

	}

	/**
	 * Stops the server instance.
	 * 
	 * @throws Exception
	 */
	protected void stopServer() throws Exception {

		log.info("Stopping HTTP server");
		server.stop();
		desiredBehaviours = null;

		log.info("HTTP server stopped.");
	}

	/**
	 * Method should create the list of behaviors the server will contain.
	 * 
	 * @throws Exception
	 */
	protected abstract void prepareBehaviours() throws Exception;
}
