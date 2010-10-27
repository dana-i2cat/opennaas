package cat.i2cat.manticore.test.demo.step4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.globus.wsrf.container.ServiceHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis.wsrf.faults.BaseFaultType;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.BGPRuleType;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkTemplateType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkType;
import cat.i2cat.manticore.stubs.ipnetwork.SimpleBGPPolicyType;
import cat.i2cat.manticore.stubs.router.GetRoutersReq;
import cat.i2cat.manticore.stubs.router.GetRoutersResp;
import cat.i2cat.manticore.stubs.router.RouterTemplateType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.demo.jobs.BGPActivationJob;
import cat.i2cat.manticore.test.demo.jobs.BGPPoliciesCreationJob;
import cat.i2cat.manticore.test.demo.jobs.ExecuteQueueJob;
import cat.i2cat.manticore.test.demo.values.BGPValues;
import cat.i2cat.manticore.test.util.Utils;
import cat.i2cat.manticore.test.util.ssh.Host;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;
import cat.i2cat.manticore.test.wrappers.router.RouterWrapper;

public class ActivateIBGPinAS1Test {
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
			server.loadDatabase("db/initialdbConfigForGlobusDemo.sql");
			
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
			ipnetwork  = response_get.getNetworks()[0];
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createPoliciesandConfigureIBGPinR1()
	{
		RouterType routerSelected = null;
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R1")==0){
				routerSelected = router;
			}
		}
		
		SimpleBGPPolicyType[] policies = new SimpleBGPPolicyType[1];
		SimpleBGPPolicyType policy = new SimpleBGPPolicyType();
		policy.setName("routesIBGP");
		policy.setRawText("");
		BGPRuleType[] filterAction = new BGPRuleType[1];
		BGPRuleType rule = new BGPRuleType();
		rule.setAction(EngineConstants.BGP_POLICIES_ACTION_NONE);
		rule.setProtocols(new String[]{EngineConstants.BGP_PROTOCOL});
		rule.setNextHop(EngineConstants.NEXTHOP_SELF);
		rule.setLocalPref(-1);
		rule.setMED(-1);
		filterAction[0] = rule;
		policy.setFilterAction(filterAction);
		policies[0] = policy;
		
		try {
			BGPPoliciesCreationJob job = new BGPPoliciesCreationJob(routerSelected, ipnetwork.getEpr(), policies);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetwork.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*---------------CONFIGURE BGP----------------------------------*/
		
		BGPValues values = new BGPValues();
		values.setExportPolicies(new String[]{"routesIBGP"});
		values.setType(EngineConstants.INTERNAL_BGP_TYPE);
		values.setRouterASNumber("1");
		values.setLocalAddress("192.168.200.1");
		values.setRouterID("192.168.200.1");
		values.setNeighbors(new String[]{"192.168.215.1"});
		
		try {
			BGPActivationJob job = new BGPActivationJob(routerSelected, ipnetwork.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetwork.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createPoliciesandConfigureIBGPinR2R3R5R6R7R8()
	{
		RouterType routersSelected[] = new RouterType[6];
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R2")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R3")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R5")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R6")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R7")==0){
				routersSelected[0] = router;
			}else if(router.getRouterName().compareTo("R8")==0){
				routersSelected[0] = router;
			}
		}
		
		String[] localAddresses = new String[]{"192.168.205.1", "192.168.210.1", "192.168.220.1", "192.168.225.1", "192.168.230.1", "192.168.235.1"};
		String[] routerId = new String[]{"192.168.205.1", "192.168.210.1", "192.168.220.1", "192.168.225.1", "192.168.230.1", "192.168.235.1"};
		
		for(int i=0; i<6; i++){
			BGPValues values = new BGPValues();
			values.setType(EngineConstants.INTERNAL_BGP_TYPE);
			values.setRouterASNumber("1");
			values.setLocalAddress(localAddresses[i]);
			values.setRouterID(routerId[i]);
			values.setNeighbors(new String[]{"192.168.215.1"});
			
			try {
				BGPActivationJob job = new BGPActivationJob(routersSelected[0], ipnetwork.getEpr(), values);
				IPNetworkActionType[] queue = job.run();
				assertThat(queue.length, equalTo(i+1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			int returnValue = executeQueue(ipnetwork.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int executeQueue(EndpointReferenceType ipnetworkEPR){
		ExecuteQueueJob job = new ExecuteQueueJob(ipnetworkEPR);
		return job.run();
	}
	
	@Test
	public void createPoliciesandConfigureIBGPinR4()
	{
		RouterType routerSelected = null;
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R4")==0){
				routerSelected = router;
			}
		}
		
		BGPValues values = new BGPValues();
		values.setType(EngineConstants.INTERNAL_BGP_TYPE);
		values.setRouterASNumber("1");
		values.setLocalAddress("192.168.215.1");
		values.setRouterID("192.168.200.1");
		values.setNeighbors(new String[]{"192.168.200.1", "192.168.205.1", "192.168.210.1", 
				"192.168.220.1", "192.168.225.1", "192.168.230.1", "192.168.235.1"});
		
		try {
			BGPActivationJob job = new BGPActivationJob(routerSelected, ipnetwork.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetwork.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
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

