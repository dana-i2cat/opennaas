package cat.i2cat.manticore.test.demo.step2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.globus.wsrf.container.ServiceHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis.wsrf.faults.BaseFaultType;

import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkTemplateType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFAreaConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFRouterConfigurationType;
import cat.i2cat.manticore.stubs.router.GetRoutersReq;
import cat.i2cat.manticore.stubs.router.GetRoutersResp;
import cat.i2cat.manticore.stubs.router.RouterTemplateType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.demo.jobs.ExecuteQueueJob;
import cat.i2cat.manticore.test.demo.jobs.OSPFActivationJob;
import cat.i2cat.manticore.test.demo.values.OSPFValues;
import cat.i2cat.manticore.test.util.Utils;
import cat.i2cat.manticore.test.util.ssh.Host;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;
import cat.i2cat.manticore.test.wrappers.router.RouterWrapper;

public class ActivateIGPinAS2Test {
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	private static Logger log = Logger.getLogger(BGP.class);
	static Host server;
	private static RouterType[] routers;
	private static IPNetworkType ipnetwork;
	
	@BeforeClass
	public static void setup()
	{
		Properties props = new Properties();
		try {
		    props.load(new FileInputStream("build.properties"));
		    
		    if( props.getProperty("host")==null ||
		    	props.getProperty("host.ssh.port")==null ||
		    	props.getProperty("host.ssh.username")==null ||
		    	props.getProperty("host.ssh.password")==null ||
		    	props.getProperty("host.manticore.username")==null ||
		    	props.getProperty("host.manticore.password")==null )
		    {
		    	log.error("YOU NEED TO CREATE A build.properties FILE. SEE build-default.properties.");
		    }
		    
		} catch (IOException e) {
		}
		
		server = new Host();
		
		server.setHost( props.getProperty("host") );
		
		server.setPortSSH( Integer.parseInt( props.getProperty("host.ssh.port") ) );
		server.setUserSSH( props.getProperty("host.ssh.username") );
		server.setPasswordSSH( props.getProperty("host.ssh.password") );
		
		server.setUserManticore( props.getProperty("host.manticore.username") );
		server.setPasswordManticore( props.getProperty("host.manticore.password") );
		
		log.info("Login...");
		try {
			server.login();
			
		} catch( Exception e )
		{
			log.error("Unable to login! reason: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		log.info("Login ok! Loading database on server:");
		try {
			server.loadDatabase("db/initialdbConfigForGlobusDemo.sql.sql");
			
		} catch( Exception e )
		{
			log.error("Unable to load SQL script on remote DB! reason: " + e.getMessage());
			e.printStackTrace();
		}
		
		log.info("Database ok! Loading testbed on lola.hea.net:");
		try {			
			Utils.loadRemoteTestbed("193.1.190.254","router/heanet/m20/","conf_M20_demo_s24_without_routing_ethernet");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 193.1.190.254! reason: " + e.getMessage());
			e.printStackTrace();
		}
		getRouters();
		getIPNetwork();
	}

	private static void getRouters(){
		String factoryURI = "";
		URL baseURL;
		
		try {
			baseURL = ServiceHost.getDefaultBaseURL();
			factoryURI = baseURL.getProtocol() + "://" + server.getHost() + ":8443";
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		GetRoutersReq request_get = new GetRoutersReq();
		GetRoutersResp response_get;
		
		String[] keys = new String[1];
		keys[0] = "";
		
		RouterTemplateType template = new RouterTemplateType();		
		template.setName( new String[]{"R-AS2-1", "R-AS2-2", "R-AS2-3"} );
		template.setResourceKey(keys);
		request_get.setTemplate(template);
		
		try {
			response_get = RouterWrapper.getRouters(request_get, factoryURI);
			
			routers = response_get.getRouters();
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private static void getIPNetwork(){
		String factoryURI = "";
		URL baseURL;
		
		try {
			baseURL = ServiceHost.getDefaultBaseURL();
			factoryURI = baseURL.getProtocol() + "://" + server.getHost() + ":8443";
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		GetIPNetworksReq request_get = new GetIPNetworksReq();
		GetIPNetworksResp response_get;
		
		String[] keys = new String[1];
		keys[0] = "";
		
		IPNetworkTemplateType template = new IPNetworkTemplateType();		
		template.setName( new String[]{"AS2"} );
		template.setId(keys);
		request_get.setTemplate(template);
		
		try {
			response_get = IPNetworkWrapper.getIPNetworks(request_get, factoryURI);
			ipnetwork = response_get.getNetworks()[0];
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void configureOSPFinRAS21RAS22RAS23()
	{
		OSPFValues values = new OSPFValues(routers);
		
		
		for(int i=0; i<routers.length; i++){
			OSPFRouterConfigurationType ospfRouterConfig = values.getOSPFConfigurationByRouterName(routers[i].getRouterName());
			ospfRouterConfig.setRedistributeProtocol(null);
			OSPFAreaConfigurationType areaConfig = values.initializeOSPFAreaConfiguration();
			
			values.addArea(routers[i].getRouterName(), areaConfig);
			
			if(routers[i].getRouterName().compareTo("R-AS2-1")==0){
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lt-1/2/0", "100");
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lo0", "1");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.100", "lo0.1"});
			}else if(routers[i].getRouterName().compareTo("R-AS2-2")==0){
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lt-1/2/0", "101");
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lt-1/2/0", "102");
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lo0", "3");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.101", "lt-1/2/0.102", "lo0.3"});
			}else if(routers[i].getRouterName().compareTo("R-AS2-3")==0){
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lt-1/2/0", "103");
				values.getOSPFSubInterfaceConfiguration(routers[i].getRouterName(), "lo0", "4");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.103", "lo0.4"});
			}
		}
		
		try {
			OSPFActivationJob job = new OSPFActivationJob(routers, ipnetwork.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue();
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int executeQueue(){
		ExecuteQueueJob job = new ExecuteQueueJob(ipnetwork.getEpr());
		return job.run();
	}
	
	@AfterClass
	public static void teardown()
	{
		log.info("Logout...");
		
/*
		log.info("Logout ok! Cleanning database on server:");
		try {
			server.loadDatabase("db/initialdbConfigForGlobusDemo.sql");
			
		} catch( Exception e )
		{
			log.error("Unable to load SQL script on remote DB! reason: " + e.getMessage());
			e.printStackTrace();
		}
*/
/*
		log.info("Cleanning ok! Restore configuration on lola.hea.net:");
		try {
			
			Utils.loadRemoteTestbed("193.1.190.254","router/heanet/m20/","conf_M20_demo_s24_without_routing_ethernet");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 193.1.190.254! reason: " + e.getMessage());
			e.printStackTrace();
		}
*/
	}
}
