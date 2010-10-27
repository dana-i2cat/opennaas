package cat.i2cat.manticore.test.demo.step1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.globus.wsrf.container.ServiceHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis.wsrf.faults.BaseFaultType;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkTemplateType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFAreaConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFRouterConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.RIPRouterConfigurationType;
import cat.i2cat.manticore.stubs.router.GetRoutersReq;
import cat.i2cat.manticore.stubs.router.GetRoutersResp;
import cat.i2cat.manticore.stubs.router.RouterTemplateType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.demo.jobs.AddStaticRoutesJob;
import cat.i2cat.manticore.test.demo.jobs.ExecuteQueueJob;
import cat.i2cat.manticore.test.demo.jobs.OSPFActivationJob;
import cat.i2cat.manticore.test.demo.jobs.RIPActivationJob;
import cat.i2cat.manticore.test.demo.jobs.RIPGetDefaultConfigurationJob;
import cat.i2cat.manticore.test.demo.jobs.RemoveStaticRoutesJob;
import cat.i2cat.manticore.test.demo.values.OSPFValues;
import cat.i2cat.manticore.test.demo.values.RIPValues;
import cat.i2cat.manticore.test.demo.values.StaticRoute;
import cat.i2cat.manticore.test.util.Utils;
import cat.i2cat.manticore.test.util.ssh.Host;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;
import cat.i2cat.manticore.test.wrappers.router.RouterWrapper;

public class ActivateIGPInAS1Test {
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
			server.loadDatabase("db/clean_manticore2.sql");
			
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
		template.setName( new String[]{"R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8"} );
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
		template.setName( new String[]{"AS1"} );
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
	public void configureStaticRouteR1andR3()
	{
		RouterType[] routersSelected = new RouterType[2];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R1")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R3")==0){
				routersSelected[1] = router;
			}
		}
		
		Vector<StaticRoute> staticRoutes = new Vector<StaticRoute>();
		StaticRoute sr = new StaticRoute("192.168.2.0/30", "192.168.1.2", false);
		staticRoutes.add(sr);
		
		try {
		
			AddStaticRoutesJob job1 = new AddStaticRoutesJob(ipnetwork.getEpr(), routersSelected[0].getRouterEPR(), staticRoutes);
			job1.run();

			staticRoutes.clear();
			sr = new StaticRoute("192.168.1.0/30", "192.168.2.1", false);
			staticRoutes.add(sr);

			AddStaticRoutesJob job2 = new AddStaticRoutesJob(ipnetwork.getEpr(), routersSelected[1].getRouterEPR(), staticRoutes);
			IPNetworkActionType[] queue = job2.run();
			assertThat(queue.length, equalTo(2));
			int returnValue = executeQueue();
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteStaticRouteR1andR3()
	{
		RouterType[] routersSelected = new RouterType[2];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R1")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R3")==0){
				routersSelected[1] = router;
			}
		}
		
		Vector<StaticRoute> staticRoutes = new Vector<StaticRoute>();
		StaticRoute sr = new StaticRoute("192.168.2.0/30", "192.168.1.2", false);
		staticRoutes.add(sr);
		
		try {
		
			RemoveStaticRoutesJob job1 = new RemoveStaticRoutesJob(ipnetwork.getEpr(), routersSelected[0].getRouterEPR(), staticRoutes);
			job1.run();

			staticRoutes.clear();
			sr = new StaticRoute("192.168.1.0/30", "192.168.2.1", false);
			staticRoutes.add(sr);

			RemoveStaticRoutesJob job2 = new RemoveStaticRoutesJob(ipnetwork.getEpr(), routersSelected[1].getRouterEPR(), staticRoutes);
			IPNetworkActionType[] queue = job2.run();
			assertThat(queue.length, equalTo(2));
			int returnValue = executeQueue();
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void configureRIPinR1R2R3R4()
	{
		RouterType[] routersSelected = new RouterType[4];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R1")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R2")==0){
				routersSelected[1] = router;
			}else if(router.getRouterName().compareTo("R3")==0){
				routersSelected[2] = router;
			}else if(router.getRouterName().compareTo("R4")==0){
				routersSelected[3] = router;
			}
		}
		
		RIPValues values = new RIPValues();
		RIPGetDefaultConfigurationJob getdefnetjob = new RIPGetDefaultConfigurationJob(routersSelected, ipnetwork.getEpr());
		HashMap<String, String[]> getDefaultNetworks = getdefnetjob.run();
		
		for(RouterType router : routersSelected){
			RIPRouterConfigurationType ripRouterConfig = values.getRIPConfigurationByRouterName(router.getRouterName());
			String[] announcedNets = getDefaultNetworks.get(router.getRouterName());
			ripRouterConfig.setAnnouncedNets(announcedNets);
			ripRouterConfig.setRedistributeProtocol(new String[]{EngineConstants.RIP_PROTOCOL, EngineConstants.OSPF_DIRECT, EngineConstants.OSPF_PROTOCOL});	
		}
		
		try {
			RIPActivationJob job = new RIPActivationJob(routersSelected, ipnetwork.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue();
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void configureOSPFinR4R5R6()
	{
		RouterType[] routersSelected = new RouterType[3];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R4")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R5")==0){
				routersSelected[1] = router;
			}else if(router.getRouterName().compareTo("R6")==0){
				routersSelected[2] = router;
			}
		}
		OSPFValues values = new OSPFValues(routersSelected);
		
		
		for(int i=0; i<routersSelected.length; i++){
			OSPFRouterConfigurationType ospfRouterConfig = values.getOSPFConfigurationByRouterName(routersSelected[i].getRouterName());
			ospfRouterConfig.setRedistributeProtocol(null);
			OSPFAreaConfigurationType areaConfig = values.initializeOSPFAreaConfiguration();
			values.addArea(routersSelected[i].getRouterName(), areaConfig);
			
			if(routersSelected[i].getRouterName().compareTo("R4")==0){
				ospfRouterConfig.setRedistributeProtocol(new String[]{EngineConstants.RIP_PROTOCOL});
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "7");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.7"});
			}else if(routersSelected[i].getRouterName().compareTo("R5")==0){
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "9");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.9"});
			}else if(routersSelected[i].getRouterName().compareTo("R6")==0){
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "8");
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "10");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.8", "lt-1/2/0.10"});
			}
		}
		
		try {
			OSPFActivationJob job = new OSPFActivationJob(routersSelected, ipnetwork.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue();
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void configureOSPFinR6R7R8()
	{
		RouterType[] routersSelected = new RouterType[3];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R6")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R7")==0){
				routersSelected[1] = router;
			}else if(router.getRouterName().compareTo("R8")==0){
				routersSelected[2] = router;
			}
		}
		OSPFValues values = new OSPFValues(routersSelected);
		
		
		for(int i=0; i<routersSelected.length; i++){
			OSPFRouterConfigurationType ospfRouterConfig = values.getOSPFConfigurationByRouterName(routersSelected[i].getRouterName());
			ospfRouterConfig.setRedistributeProtocol(null);
			OSPFAreaConfigurationType areaConfig = values.initializeOSPFAreaConfiguration();
			areaConfig.setAreaID("0.0.0.1");
			areaConfig.setAreaType(EngineConstants.OSPF_TYPE_NON_BACKBONE);
			values.addArea(routersSelected[i].getRouterName(), areaConfig);
			
			if(routersSelected[i].getRouterName().compareTo("R6")==0){
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "11");
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "13");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.11", "lt-1/2/0.13"});
			}else if(routersSelected[i].getRouterName().compareTo("R7")==0){
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "12");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.12"});
			}else if(routersSelected[i].getRouterName().compareTo("R8")==0){
				values.getOSPFSubInterfaceConfiguration(routersSelected[i].getRouterName(), "lt-1/2/0", "14");
				areaConfig.setListSubInterfaceIDs(new String[]{"lt-1/2/0.14"});
			}
		}
		
		try {
			OSPFActivationJob job = new OSPFActivationJob(routersSelected, ipnetwork.getEpr(), values);
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
