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

import org.junit.Test;
import org.opennaas.core.hsqldb.internal.HSQLDBServer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

@SuppressWarnings("deprecation")
public class HSQLDBServerIntegrationTest extends
		AbstractDependencyInjectionSpringContextTests {
	private HSQLDBServer	server;

	@Override
	protected String[] getConfigLocations() {
		return fakeDatabasePaths;
	}

	private static String[]	fakeDatabasePaths	= {
												"classpath*:/applicationContext-test.xml", "classpath*:/applicationContext.xml",
												};

	@Override
	protected void onSetUp() throws Exception {
		ApplicationContext context = applicationContext;
		server = (HSQLDBServer) context.getBean("dataBase");
	}

	@Test
	public void testServer() {
		System.out.println("Server Port:" + server.getServerProperties().getProperty("server.port"));
	}
}
