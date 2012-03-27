package org.opennaas.core.hsqldb.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

//import org.hsqldb.Server;
import org.hsqldb.ServerConfiguration;
import org.hsqldb.ServerConstants;
import org.hsqldb.persist.HsqlProperties;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class HSQLDBServer implements InitializingBean, DisposableBean {

	private static Log logger = LogFactory.getLog(HSQLDBServer.class);
	//private Server databaseServer;

	/**
	 * Properties used to customize instance.
	 */
	private Properties serverProperties;

	/**
	 * The actual server instance.
	 */
	private org.hsqldb.Server server;

	/**
	 * DataSource used for shutdown.
	 */
	private DataSource dataSource;

	public Properties getServerProperties() {
		return serverProperties;
	}

	public void setServerProperties(Properties serverProperties) {
		this.serverProperties = serverProperties;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void afterPropertiesSet() throws Exception {

		HsqlProperties configProps = new HsqlProperties(serverProperties);
		/*if (configProps == null) {
			logger.warn("we failed at getting an HSQL Server with serverProperties, trying to get one without");
			configProps = new HsqlProperties();
			if (configProps == null)
				logger.error("we failed at getting an HSQL Server, period. Crash and burn from here on.");
		}*/

		ServerConfiguration.translateDefaultDatabaseProperty(configProps);

		// finished setting up properties - set some important behaviors as
		// well;
		server = new org.hsqldb.Server();
		server.setRestartOnShutdown(false);
		server.setNoSystemExit(true);
		server.setProperties(configProps);

		logger.info("HSQL Server Startup sequence initiated");

		server.start();

		String portMsg = "port " + server.getPort();
		logger.info("HSQL Server listening on " + portMsg);
	}

	public void destroy() {

		logger.info("HSQL Server Shutdown sequence initiated");
		if (dataSource != null) {
			Connection con = null;
			try {
				con = dataSource.getConnection();
				con.createStatement().execute("SHUTDOWN");
			} catch (SQLException e) {
				logger.error("HSQL Server Shutdown failed: " + e.getMessage());
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (Exception ignore) {
				}
			}
		} else {
			logger
					.warn("HSQL ServerBean needs a dataSource property set to shutdown database safely.");
		}
		server.signalCloseAllServerConnections();
		int status = server.stop();
		long timeout = System.currentTimeMillis() + 5000;
		while (status != ServerConstants.SERVER_STATE_SHUTDOWN
				&& System.currentTimeMillis() < timeout) {
			try {
				Thread.sleep(100);
				status = server.getState();
			} catch (InterruptedException e) {
				logger.error("Error while shutting down HSQL Server: "
						+ e.getMessage());
				break;
			}
		}
		if (status != ServerConstants.SERVER_STATE_SHUTDOWN) {
			logger.warn("HSQL Server failed to shutdown properly.");
		} else {
			logger.info("HSQL Server Shutdown completed");
		}
		server = null;
	}

}
