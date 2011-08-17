package org.opennaas.core.platformmanager.tests;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.platformmanager.IPlatformManagerWS;
import org.opennaas.core.platformmanager.Java;
import org.opennaas.core.platformmanager.OperatingSystem;
import org.opennaas.core.platformmanager.Platform;
import org.opennaas.core.platformmanager.PlatformManagerWS;


public class PlatformManagerWSTest {

	private IPlatformManagerWS client = null;
	private static JaxWsServerFactoryBean server; 
	
	@BeforeClass
	public static void initJaxFactory() {
		//Loading a server for testing the web services
		server = new JaxWsServerFactoryBean();
		server.setServiceClass(PlatformManagerWS.class);
		PlatformManagerWS platformManageService = new PlatformManagerWS();
		server.setServiceBean(platformManageService);
		server.setAddress("http://localhost:9080/platformManagerWS");
		server.create();
	}
	
	@Before
	public void setup() {
		//Create a web service client
		createWebServiceClient();
	}
	
	private void createWebServiceClient() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IPlatformManagerWS.class);
		factory.setAddress("http://localhost:9080/platformManagerWS");
	    client = (IPlatformManagerWS) factory.create();
	}
	
	@Test
	public void testGetPlatformData(){
		try{
			Platform platform = client.getPlatformData();
			System.out.println(platform.toString());
			OperatingSystem operatingSystem = platform.getOperatingSystem();
			Assert.assertNotNull(operatingSystem.getName());
			Assert.assertNotNull(operatingSystem.getVersion());
			Assert.assertNotNull(operatingSystem.getArchitecture());
			Java java = platform.getJava();
			Assert.assertNotNull(java.getJreVendor());
			Assert.assertNotNull(java.getJreVersion());
			Assert.assertNotNull(java.getJvmVersion());
			Assert.assertNotNull(java.getJvmName());
			Assert.assertNotNull(java.getJvmVendor());
			Assert.assertNotNull(platform.getHardware().getCpus());
		}catch(Exception ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
