/*******************************************************************************
* Copyright (c) 2005,2007 i2CAT Foundation and Communications Research Centre
* Canada.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Laia Ferrao (i2CAT)
*******************************************************************************/
package cat.i2cat.manticore.test.wrappers.router;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.globus.axis.gsi.GSIConstants;
import org.globus.axis.util.Util;
import org.globus.gsi.jaas.JaasSubject;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.security.Constants;
import org.globus.wsrf.security.SecurityException;
import org.ietf.jgss.GSSCredential;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.ResourceNotDestroyedFaultType;
import org.oasis.wsrf.lifetime.ResourceUnknownFaultType;
import org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse;
import org.oasis.wsrf.properties.GetMultipleResourceProperties_Element;
import org.xml.sax.InputSource;

import cat.i2cat.manticore.stubs.router.AccessConfigurationType;
import cat.i2cat.manticore.stubs.router.AdvancedBGPPolicyType;
import cat.i2cat.manticore.stubs.router.BGPPolicyType;
import cat.i2cat.manticore.stubs.router.CreateNewRouterInstanceReq;
import cat.i2cat.manticore.stubs.router.CreateNewRouterInstanceResp;
import cat.i2cat.manticore.stubs.router.CreateReq;
import cat.i2cat.manticore.stubs.router.CreateResp;
import cat.i2cat.manticore.stubs.router.EBGPConfigurationType;
import cat.i2cat.manticore.stubs.router.FindReq;
import cat.i2cat.manticore.stubs.router.FindResp;
import cat.i2cat.manticore.stubs.router.GetRoutersReq;
import cat.i2cat.manticore.stubs.router.GetRoutersResp;
import cat.i2cat.manticore.stubs.router.IBGPConfigurationType;
import cat.i2cat.manticore.stubs.router.InvokeReq;
import cat.i2cat.manticore.stubs.router.InvokeResp;
import cat.i2cat.manticore.stubs.router.LocationType;
import cat.i2cat.manticore.stubs.router.ModifyReq;
import cat.i2cat.manticore.stubs.router.ModifyResp;
import cat.i2cat.manticore.stubs.router.OSPFRouterConfigurationType;
import cat.i2cat.manticore.stubs.router.OSPFv3RouterConfigurationType;
import cat.i2cat.manticore.stubs.router.PhysicalInterfaceType;
import cat.i2cat.manticore.stubs.router.RIPRouterConfigurationType;
import cat.i2cat.manticore.stubs.router.RIPngRouterConfigurationType;
import cat.i2cat.manticore.stubs.router.RouteTableType;
import cat.i2cat.manticore.stubs.router.RouterFactoryPortType;
import cat.i2cat.manticore.stubs.router.RouterPortType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.stubs.router.SetPollingPeriodReq;
import cat.i2cat.manticore.stubs.router.SimpleBGPPolicyType;
import cat.i2cat.manticore.stubs.router.StaticRouteType;
import cat.i2cat.manticore.stubs.router.UserAccountType;
import cat.i2cat.manticore.stubs.router.service.RouterFactoryServiceAddressingLocator;
import cat.i2cat.manticore.stubs.router.service.RouterServiceAddressingLocator;
//import cat.i2cat.manticore.ui.rcp.model.Session;
//import cat.i2cat.manticore.ui.rcp.model.router.PhysicalInterface;
//import cat.i2cat.manticore.ui.rcp.model.router.RouterInstance;
import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.util.login.Session;
import cat.i2cat.manticore.test.wrappers.router.RouterQNames;


/**
 * This class wraps the operations for the RouterResource
 * 
 * @author Laia Ferrao
 *
 */

public class RouterWrapper {
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//									FACTORY SERVICE												//		
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		  Util.registerTransport();
	}
	
	private static Logger log = Logger.getLogger( RouterWrapper.class );
	
	/**
	 * Tells the router factory located at factoryURI to create a router
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The data of the router instance created.
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static CreateResp create(CreateReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		CreateResp resp = null;
		
		try {
			RouterFactoryPortType routerFactory = getFactoryPortType(factoryURI 
					+ "/wsrf/services/manticore/RouterResourceFactoryService");   
			resp = routerFactory.create(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return resp;
	}
	
	/**
	 * Tells the router factory located at factoryURI to get the EPRs of router resources that
	 * match a given template
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The list of the router instances available in the DB
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static FindResp find(FindReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		FindResp result = null;
		
		try {
			RouterFactoryPortType routerFactory = getFactoryPortType(factoryURI
					+ "/wsrf/services/manticore/RouterResourceFactoryService");   
			result = routerFactory.find(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return result;
	}
	
	/**
	 * Tells the router factory located at factoryURI to get the EPRs of router resources that
	 * match a given template
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The list of the router instances available in the DB
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static GetRoutersResp getRouters(GetRoutersReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		GetRoutersResp result = null;
		
		try {
			RouterFactoryPortType routerFactory = getFactoryPortType(factoryURI
					+ "/wsrf/services/manticore/RouterResourceFactoryService");   
			result = routerFactory.getRouters(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return result;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//									INSTANCE SERVICE											//		
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the value of multiple resource properties
	 * 
	 * @param property
	 * @param routerResource
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws MalformedURIException 
	 */
	private static GetMultipleResourcePropertiesResponse getMultipleRPs(GetMultipleResourceProperties_Element properties, 
			EndpointReferenceType routerResource) throws ServiceException, RemoteException, MalformedURIException {
		RouterPortType router = getInstancePortType(routerResource);   
		return router.getMultipleResourceProperties(properties);
	}
	
	/**
	 * PAU: NUEVO ACTUALIZADO
	 * Gets a router instance without the children router instances and the parent.
	 * 
	 * @param routerResource The EPR of the router instance
	 * @return The router instance requested
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws DeserializationException
	 */
	public static RouterType getRouterInstanceWithoutChildrenParent(EndpointReferenceType routerResource) throws ServiceException, 
			RemoteException, DeserializationException {
		
		log.debug("Get Router Instance");		
		
		try
		{
			GetMultipleResourceProperties_Element multipleRPs = new GetMultipleResourceProperties_Element();
		    
			log.debug("multipleRPs: " + multipleRPs);
					
			QName[] rps = new QName[21];
		    
		    rps[0] = RouterQNames.RP_ROUTERMODEL;
		    rps[1] = RouterQNames.RP_ROUTERNAME;
		    rps[2] = RouterQNames.RP_HOSTNAME;
		    rps[3] = RouterQNames.RP_VERSIONOS;
		    rps[4] = RouterQNames.RP_ISOPERATIONAL;
		    rps[5] = RouterQNames.RP_ISPHYSICAL;
		    rps[6] = RouterQNames.RP_ALLOWSROUTERINSTANCECREATION;
		    rps[7] = RouterQNames.RP_RIP_CONFIGURATION;
		    rps[8] = RouterQNames.RP_OSPF_CONFIGURATION;
		    rps[9] = RouterQNames.RP_RIPNG_CONFIGURATION;
		    rps[10] = RouterQNames.RP_OSPFV3_CONFIGURATION;
		    rps[11] = RouterQNames.RP_IBGP_CONFIGURATION;
		    rps[12] = RouterQNames.RP_EBGP_CONFIGURATION;
		    rps[13] = RouterQNames.RP_BGP_POLICIES;
		    rps[14] = RouterQNames.RP_STATIC_ROUTES;
		    rps[15] = RouterQNames.RP_INTERFACES;
		    rps[16] = RouterQNames.RP_CHILDREN;
		    rps[17] = RouterQNames.RP_LOCATION;
		    rps[18] = RouterQNames.RP_ACCESSCONFIGURATION;
		    rps[19] = RouterQNames.RP_PARENT;
		    rps[20] = RouterQNames.RP_USERACCOUNTS;
		  
		    multipleRPs.setResourceProperty(rps);
		    
		    GetMultipleResourcePropertiesResponse response = getMultipleRPs(multipleRPs, routerResource);
		    
			for( QName names : multipleRPs.getResourceProperty() )
				log.debug("\tQName: " + names);
		    
			RouterType router = new RouterType();
			
			router.setRouterEPR(routerResource);
			
			//Router Model
			router.setRouterModel((String)ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[0].toString())), String.class));
		    
		    //Router Name
		    router.setRouterName((String) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[1].toString())), String.class));
		    
		    //Hostname
		    router.setHostName((String) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[2].toString())), String.class));
		    log.debug("Host name: " + router.getHostName());
		    
		    //Version OS
		    router.setVersionOS((String) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[3].toString())), String.class));
		    log.debug("Version OS: " + router.getVersionOS());
		    
		    //Is Operational
		    router.setIsOperation((Boolean) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[4].toString())), Boolean.class));
		    
		    //Is Physical
		    router.setIsPhysical((Boolean) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[5].toString())), Boolean.class));
		    
		    //Is isAllowsRouterInstancesCreation
		    router.setAllowsRouterInstanceCreation((Boolean) ObjectDeserializer.deserialize(
					new InputSource(new StringReader(response.get_any()[6].toString())), Boolean.class));

		    Vector<PhysicalInterfaceType> physicalInterfaceList = new Vector<PhysicalInterfaceType>();
		    Vector<StaticRouteType> staticRouteList = new Vector<StaticRouteType>();
		    Vector<UserAccountType> userAccountList = new Vector<UserAccountType>();
		    Vector<EBGPConfigurationType> ebgpConfigList = new Vector<EBGPConfigurationType>();
		    Vector<BGPPolicyType> bgpPolicyList = new Vector<BGPPolicyType>();
		    
		    for (int i = 7; i < response.get_any().length; i++)
		    {
		    	String item = response.get_any()[i].toString();
		    	
		    	log.debug("response: " + item);
		    	
		    	if (item.indexOf("Interfaces") != -1)
		    	{
		    		PhysicalInterfaceType pint = (PhysicalInterfaceType) ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), PhysicalInterfaceType.class );
		    		physicalInterfaceList.add( pint );
		    		log.debug("Physical Interface: " + pint);
		    		
		    	} else if (item.indexOf("StaticRoutes") != -1)
		    	{
		    		StaticRouteType sroute = (StaticRouteType) ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), StaticRouteType.class);
		    		staticRouteList.add( sroute );
		    		
		    	} else if (item.indexOf("Children") != -1)
		    	{
		    		//do nothing
			    
		    	} else if (item.indexOf("Location") != -1)
			    {
		    		router.setLocation( (LocationType) ObjectDeserializer.deserialize( new InputSource( new StringReader(item)), LocationType.class) );
				    
		    	} else if (item.indexOf("AccessConfiguration") != -1)
		    	{
		    		router.setAccessConfiguration( (AccessConfigurationType) ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), AccessConfigurationType.class) );
				    
		    	} else if (item.indexOf("Parent") != -1) 
		    	{
		    		//do nothing
		    	
		    	} else if (item.indexOf("UserAccounts") != -1)
		    	{
		    		UserAccountType user = (UserAccountType) ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), UserAccountType.class);
		    		userAccountList.add( user );
		    		
		    	} else if (item.indexOf("RIPConfiguration") != -1)
		    	{
		    		 //RIP Configuration
				    RIPRouterConfigurationType ripConfType = (RIPRouterConfigurationType)ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), RIPRouterConfigurationType.class);
				    router.setRipConfiguration( ripConfType );
				    
		    	} else if (item.indexOf("OSPFConfiguration") != -1)
		    	{
		    		 //OSPF Configuration
				    OSPFRouterConfigurationType ospfConfType = (OSPFRouterConfigurationType)ObjectDeserializer.deserialize(	new InputSource(new StringReader(item)), OSPFRouterConfigurationType.class);
				    router.setOspfConfiguration( ospfConfType );
		    	
		    	}else if (item.indexOf("RIPNGConfiguration") != -1)
		    	{
		    		 //RIPng Configuration
				    RIPngRouterConfigurationType ripngConfType = (RIPngRouterConfigurationType)ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), RIPngRouterConfigurationType.class);
				    router.setRipngConfiguration( ripngConfType );
				    
		    	} else if (item.indexOf("OSPFV3Configuration") != -1)
		    	{
		    		 //OSPFv3 Configuration
				    OSPFv3RouterConfigurationType ospfv3ConfType = (OSPFv3RouterConfigurationType)ObjectDeserializer.deserialize( new InputSource(new StringReader(item)), OSPFv3RouterConfigurationType.class);
				    router.setOspfv3Configuration( ospfv3ConfType );
				    
		    	} else if (item.indexOf("IBGPConfiguration") != -1) 
		    	{
		    		 //IBGP Configuration
				    IBGPConfigurationType ibgpConfigType = (IBGPConfigurationType)ObjectDeserializer.deserialize( new InputSource( new StringReader(item)), IBGPConfigurationType.class);
				    router.setIbgpConfiguration( ibgpConfigType );
				    
		    	} else if (item.indexOf("EBGPConfiguration") != -1)
		    	{
		    		 //EBGP Configuration
		    		EBGPConfigurationType ebgpConfigType = (EBGPConfigurationType)ObjectDeserializer.deserialize( new InputSource( new StringReader(item)), EBGPConfigurationType.class);
		    		ebgpConfigList.add( ebgpConfigType );
		    		
		    	} else if (item.indexOf("BGPPolicies") != -1)
		    	{
		    		 //BGP Policies
		    		BGPPolicyType policy = (BGPPolicyType)ObjectDeserializer.deserialize( new InputSource( new StringReader(item)), BGPPolicyType.class);
		    		bgpPolicyList.add( policy );
		    	}
		    }
		    
		    log.debug("End of response processing - response ");
		    
		    if(physicalInterfaceList.size()>0)
		    	router.setPhysicalInterfaces( physicalInterfaceList.toArray( new PhysicalInterfaceType[  physicalInterfaceList.size() ] ) );
		    
		    if(staticRouteList.size()>0)
		    	router.setStaticRoutes( staticRouteList.toArray( new StaticRouteType[ staticRouteList.size() ]) );
		    
		    log.debug("userAccountList: " + userAccountList.get(0) );
		    if(userAccountList.size()>0)
		    	router.setUserAccounts( userAccountList.toArray( new UserAccountType[ userAccountList.size() ] ) );
		    
		    if(ebgpConfigList.size()>0) //no entra nunca
		    	router.setEbgpConfiguration( ebgpConfigList.toArray( new EBGPConfigurationType[ ebgpConfigList.size() ] ) );
		    
		    if(bgpPolicyList.size()>0){ //no entra nunca
		    	AdvancedBGPPolicyType[] advancedBgpPolicies = ObjectMapper.getAdvancedBGPPolicies(bgpPolicyList.toArray( new BGPPolicyType[ bgpPolicyList.size() ] ));
		    	router.setAdvancedBgpPolicies(advancedBgpPolicies);
		    	SimpleBGPPolicyType[] simpleBgpPolicies = ObjectMapper.getSimpleBGPPolicies(bgpPolicyList.toArray( new BGPPolicyType[ bgpPolicyList.size() ] ));
		    	router.setSimpleBgpPolicies(simpleBgpPolicies);
		    }
		    
		    return router;
		    
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
	    }
	}
	
	/**
	 * Gets a router instance without the children router instances and the parent.
	 * 
	 * @param routerResource The EPR of the router instance
	 * @return The router instance requested
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws DeserializationException
	 */
//	public static RouterType getRouterInstanceWithoutChildrenParent(EndpointReferenceType routerResource) throws ServiceException, 
//			RemoteException, DeserializationException {
//		System.out.println("---> Get Router Instance <---");
//		
//		try {
//			RouterType router = new RouterType();
//			router.setRouterEPR(routerResource);
//			
//			GetMultipleResourceProperties_Element multipleRPs = new GetMultipleResourceProperties_Element();
//		    QName[] rps = new QName[23];
//		    
//		    //Parameters to get
//		    rps[0] = RouterQNames.RP_ROUTERNAME;
//		    rps[1] = RouterQNames.RP_ROUTERMODEL;
//		    rps[2] = RouterQNames.RP_HOSTNAME;
//		    rps[3] = RouterQNames.RP_VERSIONOS;
//		    rps[4] = RouterQNames.RP_ISOPERATIONAL;
//		    rps[5] = RouterQNames.RP_ISPHYSICAL;
//		    rps[6] = RouterQNames.RP_ALLOWSROUTERINSTANCECREATION;
//		    rps[7] = RouterQNames.RP_INTERFACES;
//		    rps[8] = RouterQNames.RP_CHILDREN;
//		    rps[9] = RouterQNames.RP_LOCATION;
//		    rps[10] = RouterQNames.RP_ACCESSCONFIGURATION;
//		    rps[11] = RouterQNames.RP_PARENT;
//		    rps[12] = RouterQNames.RP_USERACCOUNTS;
//		    rps[13] = RouterQNames.RP_LOGICALNAME;
//		    rps[14] = RouterQNames.RP_STATIC_ROUTES;
//		    rps[15] = RouterQNames.RP_RIP_CONFIGURATION;
//		    rps[16] = RouterQNames.RP_OSPF_CONFIGURATION;
//		    rps[17] = RouterQNames.RP_RIPNG_CONFIGURATION;
//		    rps[18] = RouterQNames.RP_OSPFV3_CONFIGURATION;
//		    rps[19] = RouterQNames.RP_EBGP_CONFIGURATION;
//		    rps[20] = RouterQNames.RP_IBGP_CONFIGURATION;
//		    rps[21] = RouterQNames.RP_BGP_POLICIES;
//		    rps[22] = RouterQNames.RP_ROUTER_TABLE_CONFIGURATION;
//		    multipleRPs.setResourceProperty(rps);
//		    
//		    //Store the parameters serially in response
//		    GetMultipleResourcePropertiesResponse response = getMultipleRPs(multipleRPs, routerResource);
//		    
//		    //Router Name
//		    router.setRouterName((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[0].toString())), String.class));
//		    //System.out.println("   - Router name: " + router.getRouterName());
//		    
//		    //Router Model
//		    router.setRouterModel((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[1].toString())), String.class));
//		    //System.out.println("   - Router model: " + router.getModel());
//		    
//		    //Hostname
//		    router.setHostName((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[2].toString())), String.class));
//		    //System.out.println("   - Host name: " + router.getHostName());
//		    
//		    //Version OS
//		    router.setVersionOS((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[3].toString())), String.class));
//		    //System.out.println("   - Version OS: " + router.getVersionOS());
//		    
//		    //Is Operational
//		    router.setIsOperation((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[4].toString())), Boolean.class));
//		    //System.out.println("   - Is Operational: " + router.isOperational());
//		    
//		    //Is Physical
//		    router.setIsPhysical((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[5].toString())), Boolean.class));
//		    //System.out.println("   - Is Physical: " + router.isPhysical());
//	    
//		    //Is isAllowsRouterInstancesCreation
//		    router.setAllowsRouterInstanceCreation((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[6].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		    //Interfaces
//		    router.setPhysicalInterfaces((PhysicalInterfaceType[]) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[7].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		    //Children
//		    router.setChildren((RouterType[]) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[8].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //Location
//		    router.setLocation((LocationType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[9].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //AccessConfiguration
//		    router.setAccessConfiguration((AccessConfigurationType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[10].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //Parent
//		    router.setParent((EndpointReferenceType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[11].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //UserAccounts
//		    router.setUserAccounts((UserAccountType[]) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[12].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //PollingPeriod
//		    router.setLogicalName((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[13].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//
//		    //StaticRoutes
//		    router.setStaticRoutes((StaticRouteType[]) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[14].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //RIPConfiguration
//		    router.setRipConfiguration((RIPRouterConfigurationType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[15].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    //OSPFConfiguration
//		    router.setOspfConfiguration((OSPFRouterConfigurationType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[16].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		    
//		    //RIPNGConfiguration
//		    router.setRipngConfiguration((RIPngRouterConfigurationType) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[17].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		    //OSPFV3Configuration
//		    router.setOspfv3Configuration((OSPFv3RouterConfigurationType)ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[18].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//
//		  //EBGPConfiguration
//		    router.setEbgpConfiguration((EBGPConfigurationType[])ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[19].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		  //IBGPConfiguration
//		    router.setIbgpConfiguration((IBGPConfigurationType)ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[20].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		  //BGPPolicies
//		    router.setBgpPolicies((BGPPolicyType[])ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[21].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		  //RouterTableConfiguration
//		    router.setRouteTableConfiguration((RouteTableType[])ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[22].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    		    
//		    
//		    for (int i = 7; i < response.get_any().length; i++) {
//		    	String item = response.get_any()[i].toString();
//		    	
//		    	if (item.indexOf("Interfaces") != -1) {
//		    		PhysicalInterfaceType pint = (PhysicalInterfaceType) ObjectDeserializer.deserialize(
//		    				new InputSource(new StringReader(item)), PhysicalInterfaceType.class);
//		    		PhysicalInterface pi = ObjectMapper.physicalInterfaceType2PhysicalInterface(pint); 
//		    		router.addInterface(pi);
//		    		pi.setRouterInstance(router);
//		    		
//		    		//System.out.println("   - PhysicalInterface: " + pint.getLocation() + "; type: " + pint.getType());
//			    } else if (item.indexOf("Children") != -1) {
//		    		//do nothing
//			    } else if (item.indexOf("Location") != -1) {
//			    	LocationType loc = (LocationType) ObjectDeserializer.deserialize(
//							new InputSource(new StringReader(item)), LocationType.class);
//		    		
//		    		router.setLocation(ObjectMapper.locationType2Location(loc));
//				    
//				    //System.out.println("   - Location: " + router.getLocation().getAddress());
//		    	} else if (item.indexOf("AccessConfiguration") != -1) {
//		    		AccessConfigurationType acc = (AccessConfigurationType) ObjectDeserializer.deserialize(
//							new InputSource(new StringReader(item)), AccessConfigurationType.class);
//		    		
//		    		router.setAccessConfiguration(ObjectMapper.accessConfigurationType2AccessConfiguration(acc));
//				    
//				    //System.out.println("   - Access Configuration: " + router.getAccessConfiguration().getIpAccessAddress());
//		    	} else if (item.indexOf("Parent") != -1) {
//		    		//do nothing
//		    	} else if (item.indexOf("UserAccounts") != -1) {
//		    		UserAccountType user = (UserAccountType) ObjectDeserializer.deserialize(
//		    				new InputSource(new StringReader(item)), UserAccountType.class);
//		    		
//		    		router.addUserAccount(ObjectMapper.userAccountType2UserAccount(user));
//		    		
//		    		//System.out.println("   - User Account: " + router.getUserAccounts());
//		    	}
//		    }
//		    
//		    return router;
//		} catch (Exception e) {
//		    e.printStackTrace();
//		    return null;
//	    }
//	}
	
	/**
	 * Gets a router instance without the parent.
	 * 
	 * @param routerResource The EPR of the router instance
	 * @return The router instance
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws DeserializationException
	 */
//	public static RouterInstance getRouterInstanceWithoutParent(EndpointReferenceType routerResource) throws ServiceException, RemoteException, DeserializationException {
//		//System.out.println("---> Get Router Instance <---");
//		
//		try {
//			RouterInstanceType router = new RouterInstanceType();
//			router.setEpr(routerResource);
//			
//			GetMultipleResourceProperties_Element multipleRPs = new GetMultipleResourceProperties_Element();
//		    QName[] rps = new QName[13];
//		    
//		    rps[0] = RouterQNames.RP_ROUTERNAME;
//		    rps[1] = RouterQNames.RP_ROUTERMODEL;
//		    rps[2] = RouterQNames.RP_HOSTNAME;
//		    rps[3] = RouterQNames.RP_VERSIONOS;
//		    rps[4] = RouterQNames.RP_ISOPERATIONAL;
//		    rps[5] = RouterQNames.RP_ISPHYSICAL;
//		    rps[6] = RouterQNames.RP_ALLOWSROUTERINSTANCECREATION;
//		    rps[7] = RouterQNames.RP_INTERFACES;
//		    rps[8] = RouterQNames.RP_CHILDREN;
//		    rps[9] = RouterQNames.RP_LOCATION;
//		    rps[10] = RouterQNames.RP_ACCESSCONFIGURATION;
//		    rps[11] = RouterQNames.RP_PARENT;
//		    rps[12] = RouterQNames.RP_USERACCOUNTS;
//		    multipleRPs.setResourceProperty(rps);
//		    
//		    GetMultipleResourcePropertiesResponse response = getMultipleRPs(multipleRPs, routerResource);
//		    
//		    //Router Name
//		    router.setRouterName((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[0].toString())), String.class));
//		    //System.out.println("   - Router name: " + router.getRouterName());
//		    
//		    //Router Model
//		    router.setModel((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[1].toString())), String.class));
//		    //System.out.println("   - Router model: " + router.getModel());
//		    
//		    //Hostname
//		    router.setHostName((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[2].toString())), String.class));
//		    //System.out.println("   - Host name: " + router.getHostName());
//		    
//		    //Version OS
//		    router.setVersionOS((String) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[3].toString())), String.class));
//		    //System.out.println("   - Version OS: " + router.getVersionOS());
//		    
//		    //Is Operational
//		    router.setOperational((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[4].toString())), Boolean.class));
//		    //System.out.println("   - Is Operational: " + router.isOperational());
//		    
//		    //Is Physical
//		    router.setIsPhysical((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[5].toString())), Boolean.class));
//		    //System.out.println("   - Is physical: " + router.isPhysical());
//		    
//		    //Is AllowsRouterInstancesCreation
//		    router.setAllowsRouterInstancesCreation((Boolean) ObjectDeserializer.deserialize(
//					new InputSource(new StringReader(response.get_any()[6].toString())), Boolean.class));
//		    //System.out.println("   - Routers children Allowed: " + router.isAllowsRouterInstancesCreation());
//		    
//		    for (int i = 7; i < response.get_any().length; i++) {
//		    	String item = response.get_any()[i].toString();
//		    	
//		    	if (item.indexOf("Interfaces") != -1) {
//		    		PhysicalInterfaceType pint = (PhysicalInterfaceType) ObjectDeserializer.deserialize(
//		    				new InputSource(new StringReader(item)), PhysicalInterfaceType.class);
//		    		PhysicalInterface pi = ObjectMapper.physicalInterfaceType2PhysicalInterface(pint); 
//		    		router.addInterface(pi);
//		    		pi.setRouterInstance(router);
//		    		
//		    		//System.out.println("   - PhysicalInterface: " + pint.getLocation() + "; type: " + pint.getType());
//			    } else if (item.indexOf("Children") != -1) {
//		    		EndpointReferenceType epr = (EndpointReferenceType) ObjectDeserializer.deserialize(
//		    				new InputSource(new StringReader(item)), EndpointReferenceType.class);
//		    		
//		    		router.addLogicalRouter(getRouterInstanceWithoutParent(epr));
//		    		
//		    		//System.out.println("   - Children: " + router.getChildren());
//			    } else if (item.indexOf("Location") != -1) {
//			    	LocationType loc = (LocationType) ObjectDeserializer.deserialize(
//							new InputSource(new StringReader(item)), LocationType.class);
//		    		
//		    		router.setLocation(ObjectMapper.locationType2Location(loc));
//				    
//				    //System.out.println("   - Location: " + router.getLocation().getAddress());
//		    	} else if (item.indexOf("AccessConfiguration") != -1) {
//		    		AccessConfigurationType acc = (AccessConfigurationType) ObjectDeserializer.deserialize(
//							new InputSource(new StringReader(item)), AccessConfigurationType.class);
//		    		
//		    		router.setAccessConfiguration(ObjectMapper.accessConfigurationType2AccessConfiguration(acc));
//				    
//				    //System.out.println("   - Access Configuration: " + router.getAccessConfiguration().getIpAccessAddress());
//		    	} else if (item.indexOf("Parent") != -1) {
//		    		//do nothing
//		    	} else if (item.indexOf("UserAccounts") != -1) {
//		    		UserAccountType user = (UserAccountType) ObjectDeserializer.deserialize(
//		    				new InputSource(new StringReader(item)), UserAccountType.class);
//		    		
//		    		router.addUserAccount(ObjectMapper.userAccountType2UserAccount(user));
//		    		
//		    		//System.out.println("   - User Account: " + router.getUserAccounts());
//		    	}
//		    }		    
//		    return router;
//		} catch (Exception e) {
//		    e.printStackTrace();
//		    return null;
//	    }
//	}
	
	/**
	 * Creates a new router instance
	 * 
	 * @param request
	 * @param routerResource
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws BaseFaultType
	 * @throws MalformedURIException 
	 */
	public static CreateNewRouterInstanceResp createNewRouterInstance(CreateNewRouterInstanceReq request, EndpointReferenceType routerResource) throws ServiceException, 
	RemoteException, BaseFaultType, MalformedURIException {
		RouterPortType router = getInstancePortType(routerResource);
		return router.createNewRouterInstance(request);
	}
	
	/**
	 * Invokes an operation (create subInterface, delete subInterface, modify subInterface, refresh router)
	 * 
	 * @param req The request
	 * @param routerResource The EPR of the router instance
	 */
	public static InvokeResp invoke(InvokeReq req, EndpointReferenceType routerResource) throws MalformedURIException, ServiceException, RemoteException {
		RouterPortType router = getInstancePortType(routerResource);
		InvokeResp resp = router.invoke(req);
		return resp;
	}
	
	/**
	 * Pooling: Automatic Router Refresh
	 * 
	 * @param req The request
	 * @param routerResource The EPR of the router instance
	 */
	public static void automaticRouterRefresh(SetPollingPeriodReq req, EndpointReferenceType routerResource) throws MalformedURIException, ServiceException, RemoteException {
		RouterPortType router = getInstancePortType(routerResource);
		router.setPollingPeriod(req);
	}
	
	/**
	 * Removes a router instance from the database
	 * 
	 * @param routerResource
	 * @throws MalformedURIException
	 * @throws ServiceException
	 * @throws ResourceUnknownFaultType
	 * @throws ResourceNotDestroyedFaultType
	 * @throws RemoteException
	 */
	public static void removeRouterInstance(EndpointReferenceType routerResource) throws MalformedURIException, ServiceException, ResourceUnknownFaultType, ResourceNotDestroyedFaultType, RemoteException
	{
		RouterPortType router = getInstancePortType(routerResource);
		//destroy the resource
		router.destroy(new Destroy());
	}
	
	public static ModifyResp modifyRouterInformation(ModifyReq req, EndpointReferenceType routerResource) throws MalformedURIException, ServiceException, RemoteException {
		RouterPortType router = getInstancePortType(routerResource);
		ModifyResp resp = router.modify(req);
		return resp;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//								INSTANCE, FACTORY, SECURITY										//		
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static RouterPortType getInstancePortType(EndpointReferenceType routerResource) throws ServiceException, MalformedURIException {
		RouterServiceAddressingLocator instance = new RouterServiceAddressingLocator();
		RouterPortType router = instance.getRouterPortTypePort(routerResource);
		setSecurity((Stub) router);
		
		return router;
	}
	
	private static RouterFactoryPortType getFactoryPortType(String factoryURI) throws MalformedURIException, ServiceException {
		RouterFactoryServiceAddressingLocator factory = new RouterFactoryServiceAddressingLocator();
		EndpointReferenceType endpoint = new EndpointReferenceType();
		endpoint.setAddress(new Address(factoryURI));
		RouterFactoryPortType routerFactory = factory.getRouterFactoryPortTypePort(endpoint);
		setSecurity((Stub)routerFactory);
		
		return routerFactory;
	}

	private static void setSecurity(Stub stub) {
		stub._setProperty(Constants.GSI_SEC_CONV, Constants.SIGNATURE);
		stub._setProperty(GSIConstants.GSI_CREDENTIALS, Session.getInstance().getCredentials());
		stub._setProperty(Constants.AUTHORIZATION, HostAuthorization.getInstance());
		stub._setProperty(GSIConstants.GSI_MODE,GSIConstants.GSI_MODE_FULL_DELEG);
	}
}