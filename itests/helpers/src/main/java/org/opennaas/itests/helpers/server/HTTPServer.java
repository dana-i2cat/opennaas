package org.opennaas.itests.helpers.server;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HTTPServer {

	private final static Log			log	= LogFactory.getLog(HTTPServer.class);

	Server								server;
	private String						contextPath;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	public HTTPServer(int port) {
		server = new Server(port);
	}

	public void setBaseURL(String url) {
		contextPath = url;
	}

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
		log.info("Server successfully started");

	}

	public void stop() throws Exception {
		if (server.isStopped())
			throw new ServerConfigurationException("Server is already stopped");

		server.stop();

	}

	public List<HTTPServerBehaviour> getDesiredBehaviours() {
		return desiredBehaviours;
	}

	public void setDesiredBehaviours(List<HTTPServerBehaviour> desiredBehaviours) {
		this.desiredBehaviours = desiredBehaviours;
	}
}
