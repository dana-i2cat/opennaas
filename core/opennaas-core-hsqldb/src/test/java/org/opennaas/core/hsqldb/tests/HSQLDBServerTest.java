package org.opennaas.core.hsqldb.tests;

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
//import org.junit.Before;
import org.junit.Test;


import org.opennaas.core.hsqldb.internal.HSQLDBServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HSQLDBServerTest {
	protected static Log log = LogFactory.getLog( HSQLDBServerTest.class );
	private static HSQLDBServer server;

@Test
public void startDatabaseTest() throws Exception{
	server = new HSQLDBServer();
	Properties serverProperties = new Properties();
	serverProperties.put("server.port", "9101");
	serverProperties.put("server.database.0", "mem:test");
	serverProperties.put("server.dbname.0", "testdb");
	server.setServerProperties(serverProperties);
	server.afterPropertiesSet();
}
@Test
public void stopDatabaseTest() throws Exception {
	log.info("Stop Database Startup");
	BasicDataSource ds = new BasicDataSource();
	ds.setDriverClassName("org.hsqldb.jdbcDriver");
	ds.setUrl("jdbc:hsqldb:mem:test");
	ds.setUsername("sa");
	ds.setPassword("");
	server.setDataSource(ds);
	server.destroy();
}

}
