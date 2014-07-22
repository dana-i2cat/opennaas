package org.opennaas.itests.helpers.server;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Publishes and manages a http server.
 * 
 * Server is published in port 8080 and listen for all endpoints after the given url. For example, if the server is configured with the
 * "/service/acl/" url, it will listen for all endpoints beginning with url "http://localhost:8080/services/acl/*"
 * 
 * Server needs a list of {@link HTTPServerBehaviour} in order to answer requests to the url endpoints.
 * 
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class HTTPServer {

	private final static Log			log	= LogFactory.getLog(HTTPServer.class);

	private Server						server;
	private String						contextPath;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	public HTTPServer(int port) {

		server = new Server();

		// we use this connector since SelectChannelConnector contains a bug, producing the server to hang
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=357318
		SocketConnector connector = new SocketConnector();
		connector.setPort(port);

		server.setConnectors(new Connector[] { connector });
	}

	/**
	 * Sets the server base url, under which all endpoints will be listened by the server.
	 * 
	 * @param url
	 */
	public void setBaseURL(String url) {
		contextPath = url;
	}

	/**
	 * Returns the set of requests and responses the server contains.
	 * 
	 * @return
	 */
	public List<HTTPServerBehaviour> getDesiredBehaviours() {
		return desiredBehaviours;
	}

	/**
	 * 
	 * Sets the set of requests and responses the server will contain.
	 * 
	 * @param desiredBehaviours
	 */
	public void setDesiredBehaviours(List<HTTPServerBehaviour> desiredBehaviours) {
		this.desiredBehaviours = desiredBehaviours;
	}

	/**
	 * Starts the server instance under the configured base url.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {

		if (contextPath == null)
			throw new ServerConfigurationException("You must define a base URL to publish your services.");

		if (server.isRunning())
			throw new ServerConfigurationException("Server is already running.");

		log.info("Starting server in port " + String.valueOf(server.getConnectors()[0].getPort()) + " and context " + contextPath);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		server.setHandler(context);

		context.addServlet(new ServletHolder(new HTTPServerServlet(desiredBehaviours)), "/*");

		server.start();

		Thread.sleep(5000);

		log.info("Server successfully started");

	}

	/**
	 * Stops the server instance
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		if (server.isStopped())
			throw new ServerConfigurationException("Server is already stopped");

		server.stop();

		Thread.sleep(5000);

	}
}
