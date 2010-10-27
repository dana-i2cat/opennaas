package cat.i2cat.manticore.test.wrappers.router;

import javax.xml.namespace.QName;

/**
 * This class contains the qualified names for the parameter resource properties.
 * 
 * @author Laia Ferrao
 */
public interface RouterQNames {
	public static final String NS = "http://manticore.i2cat.cat/2009/03/router";
	/** Parameter Resource Properties */ 
	public static final QName RESOURCE_PROPERTIES = new QName(NS,"RouterResourceRP");
	public static final QName RESOURCE_REFERENCE = new QName(NS,"RouterResourceRP");

	/** RPs of the Router Resource */
	public static final QName RP_ROUTERNAME = new QName(NS, "RouterName");
	public static final QName RP_ROUTERMODEL = new QName(NS, "Model");
	public static final QName RP_HOSTNAME = new QName(NS, "HostName");
	public static final QName RP_VERSIONOS = new QName(NS, "VersionOS");
	public static final QName RP_ISOPERATIONAL = new QName(NS, "isOperational");
	public static final QName RP_ISPHYSICAL = new QName(NS, "isPhysical");
	public static final QName RP_INTERFACES = new QName(NS, "Interfaces");
	public static final QName RP_CHILDREN = new QName(NS, "Children");
	public static final QName RP_LOCATION = new QName(NS, "Location");
	public static final QName RP_ACCESSCONFIGURATION = new QName(NS, "AccessConfiguration");
	public static final QName RP_PARENT = new QName(NS, "Parent");
	public static final QName RP_USERACCOUNTS = new QName(NS, "UserAccounts");
	public static final QName RP_POLLINGPERIOD = new QName(NS, "PollingPeriod");
	public static final QName RP_ALLOWSROUTERINSTANCECREATION = new QName(NS, "AllosRouterInstanceCreation");
	public static final QName RP_STATIC_ROUTES = new QName(NS, "StaticRoutes");
	public static final QName RP_RIP_CONFIGURATION = new QName(NS, "RIPConfiguration");	
	public static final QName RP_OSPF_CONFIGURATION = new QName(NS, "OSPFConfiguration");
	public static final QName RP_RIPNG_CONFIGURATION = new QName(NS, "RIPNGConfiguration");	
	public static final QName RP_OSPFV3_CONFIGURATION = new QName(NS, "OSPFV3Configuration");
	public static final QName RP_IBGP_CONFIGURATION = new QName(NS, "IBGPConfiguration");
	public static final QName RP_EBGP_CONFIGURATION = new QName(NS, "EBGPConfiguration");
	public static final QName RP_BGP_POLICIES = new QName(NS, "BGPPolicies");
	//public static final QName RP_ROUTETABLE_CONFIGURATION = new QName(NS, "RouteTableConfiguration");
	
	/** The Resource Key **/
	public static final QName RP_KEY = new QName(NS, "RouterResourceKey");

}