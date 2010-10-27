package cat.i2cat.manticore.test.wrappers.ipnetwork;

import javax.xml.namespace.QName;

public interface IPNetworkQNames {
	public static final String NS = "http://manticore.i2cat.cat/2009/12/ipnetwork";
    /** Parameter Resource Properties */ 
	public static final QName RESOURCE_PROPERTIES = new QName(NS,"IPNetworkResourceRP");
	public static final QName RESOURCE_REFERENCE = new QName(NS,"IPNetworkResourceRP");
    
	/** RPs of the IPNetwork Resource */
	public static final QName RP_NAME = new QName(NS, "Name");
	public static final QName RP_ROUTERS = new QName(NS, "Routers");
	public static final QName RP_VPNS = new QName(NS, "VPNs");
	public static final QName RP_ROUTES = new QName(NS, "Routes");
	public static final QName RP_NETWORKGRAPH = new QName(NS, "Network Graph");
	public static final QName RP_SUBNETWORKS = new QName(NS, "Subnetworks");
	public static final QName RP_CONNECTIONTYPE = new QName(NS, "ConnectionType");

	/** The Resource Key **/
	public static final QName RP_KEY = new QName(NS, "IPNetworkResourceKey");
}
