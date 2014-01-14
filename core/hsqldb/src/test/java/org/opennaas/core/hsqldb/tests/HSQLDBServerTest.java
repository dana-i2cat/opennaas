package org.opennaas.core.hsqldb.tests;

/*
 * #%L
 * OpenNaaS :: Core :: HSQLDB Database
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.hsqldb.internal.HSQLDBServer;

public class HSQLDBServerTest {
	protected static Log		log	= LogFactory.getLog(HSQLDBServerTest.class);
	private static HSQLDBServer	server;

	@Test
	public void startDatabaseTest() throws Exception {
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
