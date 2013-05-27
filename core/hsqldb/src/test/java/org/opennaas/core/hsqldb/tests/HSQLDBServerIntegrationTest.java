package org.opennaas.core.hsqldb.tests;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.opennaas.core.hsqldb.internal.HSQLDBServer;

@SuppressWarnings("deprecation")
public class HSQLDBServerIntegrationTest extends
AbstractDependencyInjectionSpringContextTests {
	    private HSQLDBServer server;
	    @Override
	    protected String[] getConfigLocations() {
	        return fakeDatabasePaths;
	    }

	    private static String[] fakeDatabasePaths = {
	        "classpath*:/applicationContext-test.xml", "classpath*:/applicationContext.xml",
	    };

	    @Override
	    protected void onSetUp() throws Exception {
	        ApplicationContext context = applicationContext;
	        server = (HSQLDBServer) context.getBean("dataBase");
	    }

	    @Test
	    public void  testServer(){
	    	System.out.println("Server Port:"+server.getServerProperties().getProperty("server.port"));
	    }
}
