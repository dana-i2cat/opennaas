package cat.i2cat.manticore.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.nullValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.encoding.DeserializationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis.wsrf.faults.BaseFaultType;

import cat.i2cat.manticore.stubs.ipnetwork.CreateReq;
import cat.i2cat.manticore.stubs.ipnetwork.CreateReqType;
import cat.i2cat.manticore.stubs.ipnetwork.CreateResp;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkTemplateType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkType;
import cat.i2cat.manticore.stubs.router.EBGPConfigurationType;
import cat.i2cat.manticore.stubs.router.FindReq;
import cat.i2cat.manticore.stubs.router.FindResp;
import cat.i2cat.manticore.stubs.router.GetRoutersReq;
import cat.i2cat.manticore.stubs.router.GetRoutersResp;
import cat.i2cat.manticore.stubs.router.IBGPConfigurationType;
import cat.i2cat.manticore.stubs.router.PhysicalInterfaceType;
import cat.i2cat.manticore.stubs.router.RouterTemplateType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.stubs.router.SubInterfaceType;
import cat.i2cat.manticore.test.util.Utils;
import cat.i2cat.manticore.test.util.ssh.Host;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;
import cat.i2cat.manticore.test.wrappers.router.RouterWrapper;

public class BGP {
	
	static {
//		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");
	}
	
	private static Logger log = Logger.getLogger(BGP.class);
	static Host server;
	private static EndpointReferenceType[] routersEPRs;
	private static EndpointReferenceType ipnetworkEPR;
	
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
		
		
		/*log.info("Database ok! Loading testbed on lola.hea.net:");
		try {			
			Utils.loadRemoteTestbed("193.1.190.254","router/nordunet/m20/","conf_M20_testbed1");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 193.1.190.254! reason: " + e.getMessage());
			e.printStackTrace();
		}*/
		
		/*
		log.info("Database ok! Loading testbed on 194.68.13.29:");
		try {			
			Utils.loadRemoteTestbed("194.68.13.29","router/nordunet/m10/","conf_M10_testbed1");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 194.68.13.29! reason: " + e.getMessage());
			e.printStackTrace();
		}*/
	}
	
	@Test
	public void willGoOk()
	{
		assertThat(1, lessThan(3));
	}
	
	@Test
	public void willFailForSure()
	{
		assertThat(1, lessThan(3));
	}
	
	@Test
	public void testWrappers_getRouters()
	{//get routers by name
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
		RouterType[] routers;

		String[] keys = new String[11];
		keys[0] = "";
		keys[1] = "";
		
		RouterTemplateType template = new RouterTemplateType();		
		template.setName( new String[]{"R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R-AS2-1", "R-AS2-2", "R-AS2-3"} );
		template.setResourceKey(keys);
		request_get.setTemplate(template);
		
		try {
			response_get = RouterWrapper.getRouters(request_get, factoryURI);
			
			routers = response_get.getRouters();
			
			assertThat(routers.length, equalTo(11));
			
			routersEPRs = new EndpointReferenceType[11];
			
			for (int i = 0; i < routers.length; i++ ) {
				routersEPRs[i] = routers[i].getRouterEPR();				
			}
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void testWrappers_find()
	{//find a router by name
		String factoryURI = "";
		URL baseURL;
		
		try {
			baseURL = ServiceHost.getDefaultBaseURL();
			factoryURI = baseURL.getProtocol() + "://" + server.getHost() + ":8443";
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		FindReq request_find = new FindReq();
		FindResp response_find;		
		RouterType router = null;
		EndpointReferenceType routerERP;
		EndpointReferenceType routerERP1;
		String[] keys = new String[1];
		keys[0] = "";

		
		RouterTemplateType template = new RouterTemplateType();
		template.setName( new String[]{"R1"} );
		template.setResourceKey(keys);
		request_find.setTemplate(template);
		
		try {
			response_find = RouterWrapper.find(request_find, factoryURI);		
			routerERP = response_find.getResources(0);//no puedo ver porque getResources(0)
			
			
			try {
				router = RouterWrapper.getRouterInstanceWithoutChildrenParent(routerERP);
				
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			routerERP1 = router.getRouterEPR();
			
			assertThat(routerERP, equalTo(routerERP1));
			
			log.info("Find response");
			log.info("------------ EPR list ------------");
			if (response_find.getResources() != null) {
				for (EndpointReferenceType epr : response_find.getResources()) {
					System.out.println(epr.toString());
				}
			}
			log.info("-------------------------------------");
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_find_RouterNames()
	{//find routers that are virtuals and names
		String factoryURI = "";
		URL baseURL;
		
		try {
			baseURL = ServiceHost.getDefaultBaseURL();
			factoryURI = baseURL.getProtocol() + "://" + server.getHost() + ":8443";
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		FindReq request_find = new FindReq();
		FindResp response_find;		
		EndpointReferenceType[] routerERPs;
		String[] keys = new String[13];
		for(int i = 0; i < keys.length; i++ )
		keys[i] = "";

		
		RouterTemplateType template = new RouterTemplateType();
		template.setIsParent(false);
		template.setResourceKey(keys);
		request_find.setTemplate(template);
		
		try {
			response_find = RouterWrapper.find(request_find, factoryURI);
			routerERPs = response_find.getResources();			
			
			assertThat(routerERPs.length, equalTo(13));
			
			RouterType[] routers = new RouterType[13];
			
			for(int i = 0; i < routerERPs.length; i++ ){
			try {
				routers[i] = RouterWrapper.getRouterInstanceWithoutChildrenParent(routerERPs[i]);
			
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			routers[i].setRouterEPR(routerERPs[i]);
			
			assertThat(routers[i].getRouterName(), notNullValue());		
			
			}
			
		} catch (BaseFaultType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	@Test
	public void test_getinterfaces()
	{//get the interfaces of a router
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
		PhysicalInterfaceType[] interfaces;
		SubInterfaceType[] sub_interfaces;
		RouterType router = null;
		
		String[] keys = new String[1];
		keys[0] = "";
		
		RouterTemplateType template = new RouterTemplateType();		
		template.setName( new String[]{"R6"} );
		template.setResourceKey(keys);
		request_get.setTemplate(template);
		
		try {
			response_get = RouterWrapper.getRouters(request_get, factoryURI);
			
			try {
				router = RouterWrapper.getRouterInstanceWithoutChildrenParent(response_get.getRouters(0).getRouterEPR());
			
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			interfaces = router.getPhysicalInterfaces();
			sub_interfaces = interfaces[0].getSubInterfaces();
						
			assertThat(interfaces.length, equalTo(2)); //R6 has 2 interface
			assertThat(sub_interfaces.length, equalTo(4));//R6 has 4 subinterfaces
			
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_getIbgpConfiguration_notNull()
	{//IBGP parameters can't be null and of a specific type

		RouterType router = new RouterType();
		IBGPConfigurationType ibgpConfiguration = new IBGPConfigurationType(); 
		IBGPConfigurationType IBGP_Conf;
		
		try {
			
			try {
				
				router = RouterWrapper.getRouterInstanceWithoutChildrenParent(routersEPRs[6]);
			
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			assertThat(router.getRouterName(), equalTo("R4"));
			
			//THIS VALUES SOHULD BE SET FROM THE TESTBED 
			/*ibgpConfiguration.setAsName("65535");
			ibgpConfiguration.setRouterID("192.168.110.33");
			ibgpConfiguration.setNeighbors(new String[]{"192.168.110.32", "192.168.110.34"});
			ibgpConfiguration.setRouterReflector(true);
			
			router.setIbgpConfiguration(ibgpConfiguration);			
			*/
			IBGP_Conf = router.getIbgpConfiguration();
			
			//Test Mandatory Parameters
		
			assertThat(IBGP_Conf.getAsName(), notNullValue());
			assertThat(IBGP_Conf.getAsName(), any(String.class)); //Especification says must be integer
			log.info(IBGP_Conf.getAsName());
			
			assertThat(IBGP_Conf.getRouterID(), notNullValue());
			assertThat(IBGP_Conf.getRouterID(), any(String.class));
			log.info(IBGP_Conf.getRouterID());
			
			assertThat(IBGP_Conf.getNeighbors(), notNullValue());
			assertThat(IBGP_Conf.getNeighbors(), any(String[].class));
			
			assertThat(IBGP_Conf.isRouterReflector(), notNullValue());
			assertThat(IBGP_Conf.isRouterReflector(), any(boolean.class));
			
			//Test Optional Parameters
			
			assertThat(IBGP_Conf.getLocalAddress(), any(String.class));
			
			assertThat(IBGP_Conf.getHoldTimer(), notNullValue()); //Default value must be 180seg
			assertThat(IBGP_Conf.getHoldTimer(), any(Integer.class)); 
			
			//assertThat(IBGP_Conf.getRemovePrivateAS(), any(boolean.class)); //RemovePrivateAS PARAMTER IS MISSING
			
		} catch (BaseFaultType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test_getEbgpConfiguration_notNull()
	{//EBGP parameters can't be null and of a specific type
		
		RouterType router = new RouterType();;

		EBGPConfigurationType[] EBGP_Conf;	
		
		try {
			
			try {
				
				router = RouterWrapper.getRouterInstanceWithoutChildrenParent(routersEPRs[3]);
				
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			assertThat(router.getRouterName(), equalTo("R1"));
						
			EBGP_Conf = router.getEbgpConfiguration();

			//Mandatory Parameters
			
			assertThat(EBGP_Conf[0].getAsName(), notNullValue());
			assertThat(EBGP_Conf[0].getAsName(), any(String.class)); //Especification says must be integer
			
			assertThat(EBGP_Conf[0].getRouterID(), notNullValue());
			assertThat(EBGP_Conf[0].getRouterID(), any(String.class));
			
			assertThat(EBGP_Conf[0].getNeighborAddress(), notNullValue());
			assertThat(EBGP_Conf[0].getNeighborAddress(), any(String.class));
			
			assertThat(EBGP_Conf[0].getRemoteAS(), notNullValue());
			assertThat(EBGP_Conf[0].getRemoteAS(), any(String.class));
						
			//Optional Parameters
			
			//assertThat(EBGP_Conf[0].getLocalAddress(), any(String.class)); //LocalAddress() PARAMTER IS MISSING
			
			assertThat(EBGP_Conf[0].getHoldTimer(), notNullValue()); //Default value must be 180seg
			assertThat(EBGP_Conf[0].getHoldTimer(), any(Integer.class)); 
			
			//assertThat(EBGP_Conf[0].getRemovePrivateAS(), any(boolean.class)); //RemovePrivateAS PARAMTER IS MISSING
			
			
			
		} catch (BaseFaultType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Test
	public void test_CreateNetwork()
	{
		String factoryURI = "";
		URL baseURL;
		
		try {
			baseURL = ServiceHost.getDefaultBaseURL();
			factoryURI = baseURL.getProtocol() + "://" + server.getHost() + ":8443";
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		CreateReq request_create = new CreateReq();
		CreateReqType request_create_type = new CreateReqType();		
		CreateResp response_create = new CreateResp();
		
		request_create_type.setName(new String ("net1"));
		request_create_type.setIpVersion(new String ("ipv4"));
		request_create_type.setRouters(routersEPRs);
		request_create_type.setConnectionType(new String ("Manhattan"));
		
		request_create.setCreateReq(request_create_type);
		
		GetIPNetworksReq request_get = new GetIPNetworksReq();
		GetIPNetworksResp response_get;

		IPNetworkType ipnetwork = new IPNetworkType();
		
		/*String[] keys = new String[1];
		keys[0] = "";
		
		IPNetworkTemplateType template = new IPNetworkTemplateType();		
		template.setName( new String[]{"net1"} );
		template.setId(keys);
		request_get.setTemplate(template);
		*/
		
		try {
			response_create = IPNetworkWrapper.create(request_create, factoryURI);
			ipnetworkEPR = response_create.getEndPointReference();
			ipnetwork.setEpr(ipnetworkEPR);
	
			assertThat(ipnetworkEPR, notNullValue());
			
		} catch (BaseFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test_setBGPConfiguration()
	{		
		RouterType [] routers = new RouterType[11];
		IPNetworkType ipnetwork = new IPNetworkType();
		EBGPConfigurationType[] ebgpConfiguration = new EBGPConfigurationType[1]; 
		ebgpConfiguration[0] = new EBGPConfigurationType();
		
		try {
			
			for(int i = 0; i < routersEPRs.length; i++ ){
				
				routers[i] = RouterWrapper.getRouterInstanceWithoutChildrenParent(routersEPRs[i]);
				
				if(routers[i].getRouterName().compareTo("R-AS2-1")==0){
					
					//THIS VALUES SOHULD BE SET FROM THE TESTBED 
					ebgpConfiguration[0].setAsName("2");
					ebgpConfiguration[0].setRouterID("192.168.201.1");
					ebgpConfiguration[0].setNeighborAddress("172.16.1.1");
					ebgpConfiguration[0].setRemoteAS("1");

					routers[i].setEbgpConfiguration(ebgpConfiguration);										
				}
				
				if(routers[i].getRouterName().compareTo("R1")==0){
					
					//THIS VALUES SOHULD BE SET FROM THE TESTBED 
					ebgpConfiguration[0].setAsName("1");
					ebgpConfiguration[0].setRouterID("192.168.200.1");
					ebgpConfiguration[0].setNeighborAddress("172.16.1.2");
					ebgpConfiguration[0].setRemoteAS("2");

					routers[i].setEbgpConfiguration(ebgpConfiguration);										
				}
				
			}			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	@AfterClass
	public static void teardown()
	{
		log.info("Logout...");
		
		//FALTA
		
		
		log.info("Logout ok! Cleanning database on server:");
		try {
			server.loadDatabase("db/empty_manticore.sql");
			
		} catch( Exception e )
		{
			log.error("Unable to load SQL script on remote DB! reason: " + e.getMessage());
			e.printStackTrace();
		}

		/*
		log.info("Cleanning ok! Restore configuration on lola.hea.net:");
		try {
			
			Utils.loadRemoteTestbed("193.1.190.254","router/nordunet/m20/","conf_init_M20");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 193.1.190.254! reason: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		log.info("Cleanning ok! Restore configuration on 194.68.13.29:");
		try {
			
			Utils.loadRemoteTestbed("194.68.13.29","router/nordunet/m10/", "conf_init_M10");
			
		} catch( Exception e )
		{
			log.error("Unable to testbed on 194.68.13.29! reason: " + e.getMessage());
			e.printStackTrace();
		}*/
		
	}
	
	
	
}
