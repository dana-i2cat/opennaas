package cat.i2cat.manticore.test.demo.step3;

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

public class ActivateEBGPinAS1AS2Test {
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	private static Logger log = Logger.getLogger(BGP.class);
	static Host server;
	private static RouterType[] routers;
	private static IPNetworkType[] ipnetworks;
	
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
		template.setName( new String[]{"R1", "R-AS2-1"} );
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
		template.setName( new String[]{"AS1", "AS2"} );
		template.setId(keys);
		request_get.setTemplate(template);
		
		try {
			response_get = IPNetworkWrapper.getIPNetworks(request_get, factoryURI);
			ipnetworks  = response_get.getNetworks();
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createPoliciesandConfigureEBGPinR1()
	{
		RouterType routerSelected = null;
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R1")==0){
				routerSelected = router;
			}
		}
		IPNetworkType ipnetworkSelected = null;
		for(IPNetworkType ipnetwork : ipnetworks){
			if(ipnetwork.getName().compareTo("AS1")==0){
				ipnetworkSelected = ipnetwork;
			}
		}
		
		SimpleBGPPolicyType[] policies = new SimpleBGPPolicyType[2];
		SimpleBGPPolicyType policy = new SimpleBGPPolicyType();
		policy.setName("routesBGP");
		policy.setRawText("");
		BGPRuleType[] filterAction = new BGPRuleType[1];
		BGPRuleType rule = new BGPRuleType();
		rule.setLocalPref(-1);
		rule.setMED(-1);
		rule.setAction(EngineConstants.BGP_POLICIES_ACTION_ACCEPT);
		rule.setProtocols(new String[]{EngineConstants.RIP_PROTOCOL, EngineConstants.OSPF_PROTOCOL, EngineConstants.OSPF_DIRECT, EngineConstants.RIP_STATIC});
		filterAction[0] = rule;
		policy.setFilterAction(filterAction);
		policies[0] = policy;
		
		SimpleBGPPolicyType policy2 = new SimpleBGPPolicyType();
		policy2.setName("intBGProutes");
		policy2.setRawText("");
		BGPRuleType[] filterAction2 = new BGPRuleType[1];
		BGPRuleType rule2 = new BGPRuleType();
		rule2.setAction(EngineConstants.BGP_POLICIES_ACTION_ACCEPT);
		rule2.setPrefixesList(new String[]{"10.1.1.0/30"});
		rule2.setLocalPref(160);
		rule2.setMED(-1);
		filterAction2[0] = rule2;
		policy2.setFilterAction(filterAction2);
		policies[1] = policy2;
		
		try {
			BGPPoliciesCreationJob job = new BGPPoliciesCreationJob(routerSelected, ipnetworkSelected.getEpr(), policies);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetworkSelected.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*---------------CONFIGURE BGP----------------------------------*/
		
		BGPValues values = new BGPValues();
		values.setExportPolicies(new String[]{"routesBGP"});
		values.setImportPolicies(new String[]{"intBGProutes"});
		values.setType(EngineConstants.EXTERNAL_BGP_TYPE);
		values.setRouterASNumber("1");
		values.setRemoteAS("2");
		values.setNeighborAddress("176.16.1.2");
		values.setRouterID("192.168.200.1");
		
		try {
			BGPActivationJob job = new BGPActivationJob(routerSelected, ipnetworkSelected.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetworkSelected.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createPoliciesandConfigureEBGPinRAS21()
	{
		RouterType routerSelected = null;
		for(RouterType router : routers){
			if(router.getRouterName().compareTo("R-AS2-1")==0){
				routerSelected = router;
			}
		}
		IPNetworkType ipnetworkSelected = null;
		for(IPNetworkType ipnetwork : ipnetworks){
			if(ipnetwork.getName().compareTo("AS2")==0){
				ipnetworkSelected = ipnetwork;
			}
		}
		
		SimpleBGPPolicyType[] policies = new SimpleBGPPolicyType[1];
		SimpleBGPPolicyType policy = new SimpleBGPPolicyType();
		policy.setName("routesBGP");
		policy.setRawText("");
		BGPRuleType[] filterAction = new BGPRuleType[1];
		BGPRuleType rule = new BGPRuleType();
		rule.setAction(EngineConstants.BGP_POLICIES_ACTION_ACCEPT);
		rule.setProtocols(new String[]{EngineConstants.OSPF_DIRECT, EngineConstants.OSPF_PROTOCOL});
		rule.setMED(-1);
		rule.setLocalPref(-1);
		filterAction[0] = rule;
		policy.setFilterAction(filterAction);
		policies[0] = policy;
		
		try {
			BGPPoliciesCreationJob job = new BGPPoliciesCreationJob(routerSelected, ipnetworkSelected.getEpr(), policies);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetworkSelected.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*---------------CONFIGURE BGP----------------------------------*/
		
		BGPValues values = new BGPValues();
		values.setExportPolicies(new String[]{"routesBGP"});
		values.setType(EngineConstants.EXTERNAL_BGP_TYPE);
		values.setRouterASNumber("2");
		values.setNeighborAddress("176.16.1.1");
		values.setRouterID("192.168.201.1");
		values.setRemoteAS("1");
		
		try {
			BGPActivationJob job = new BGPActivationJob(routerSelected, ipnetworkSelected.getEpr(), values);
			IPNetworkActionType[] queue = job.run();
			assertThat(queue.length, equalTo(1));
			int returnValue = executeQueue(ipnetworkSelected.getEpr());
			assertThat(returnValue, equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int executeQueue(EndpointReferenceType ipnetworkEPR){
		ExecuteQueueJob job = new ExecuteQueueJob(ipnetworkEPR);
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
