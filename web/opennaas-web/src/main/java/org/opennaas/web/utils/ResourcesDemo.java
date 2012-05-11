package org.opennaas.web.utils;

/**
 * @author Jordi
 */
public class ResourcesDemo {

	/** NETWORK COMPONENTS **/

	public static final String	NETWORK_NAME				= "networkdemo";

	public static final String	NETWORK_INTERFACE_LOLA_MYRE	= "router:logicallola1:fe-0/3/0.13";
	public static final String	NETWORK_INTERFACE_MYRE_LOLA	= "router:logicalmyre1:ge-2/0/0.13";

	public static final String	NETWORK_INTERFACE_LOLA_GSN	= "router:logicallola1:ge-0/2/0.80";
	public static final String	NETWORK_INTERFACE_GSN_LOLA	= "router:logicalgsn1:ge-1/0/7.59";

	public static final String	NETWORK_INTERFACE_MYRE_GSN	= "router:logicalmyre1:ge-2/0/1.81";
	public static final String	NETWORK_INTERFACE_GSN_MYRE	= "router:logicalgsn1:ge-1/0/7.60";

	/** LOLA COMPONENTS **/

	// routers
	public static final String	ROUTER_LOLA_NAME			= "lolaM20";
	public static final String	LOGICAL_LOLA_NAME			= "logicalheanet1";

	// interfaces
	public static final String	LOLA_IFACE1					= "fe-0/3/3.1";
	public static final String	LOLA_IFACE2					= "fe-0/3/0.13";
	public static final String	LOLA_IFACE3					= "ge-0/2/0.80";
	public static final String	LOLA_IFACE_GRE				= "gr-1/2/0.1";

	public static final Integer	LOLA_IFACE1_VLAN			= 1;
	public static final Integer	LOLA_IFACE2_VLAN			= 13;
	public static final Integer	LOLA_IFACE3_VLAN			= 80;
	// ips
	public static final String	LOLA_IFACE1_IP				= "193.1.190.250";
	public static final String	LOLA_IFACE2_IP				= "192.168.1.14";
	public static final String	LOLA_IFACE3_IP				= "192.168.1.5";

	// static route
	public static final String	LOLA_STATIC_ROUTE			= "193.1.190.249";

	// gre
	public static final String	LOLA_GRE_TUNNEL_IP			= "192.168.1.17";
	public static final String	LOLA_GRE_TUNNEL_DESTINY		= "80.88.40.26";

	/** MYRE COMPONENTS **/

	// routers
	public static final String	ROUTER_MYRE_NAME			= "myreM7i";
	public static final String	LOGICAL_MYRE_NAME			= "logicalmyre1";

	// interfaces
	public static final String	MYRE_IFACE1					= "ge-2/0/0.12";
	public static final String	MYRE_IFACE2					= "ge-2/0/0.13";
	public static final String	MYRE_IFACE3					= "ge-2/0/1.81";
	public static final String	MYRE_IFACE_GRE				= "gr-1/1/0.2";

	public static final Integer	MYRE_IFACE1_VLAN			= 12;
	public static final Integer	MYRE_IFACE2_VLAN			= 13;
	public static final Integer	MYRE_IFACE3_VLAN			= 81;

	// ips
	public static final String	MYRE_IFACE1_IP				= "193.1.190.1";
	public static final String	MYRE_IFACE2_IP				= "192.168.1.13";
	public static final String	MYRE_IFACE3_IP				= "192.168.1.10";

	// static route
	public static final String	MYRE_STATIC_ROUTE			= "193.1.190.2";

	// gre
	public static final String	MYRE_GRE_TUNNEL_IP			= "192.168.1.33";
	public static final String	MYRE_GRE_TUNNEL_DESTINY		= "134.226.53.108";

	/** GSN COMPONENTS **/

	// routers
	public static final String	ROUTER_GSN_NAME				= "gsnMX10";
	public static final String	LOGICAL_GSN_NAME			= "logicalGSN1";

	// interfaces
	public static final String	GSN_IFACE1					= "ge-1/0/7.59";
	public static final String	GSN_IFACE2					= "ge-1/0/7.60";

	public static final Integer	GSN_IFACE1_VLAN				= 59;
	public static final Integer	GSN_IFACE2_VLAN				= 60;

	// ips
	public static final String	GSN_IFACE1_IP				= "192.168.1.6";
	public static final String	GSN_IFACE2_IP				= "192.168.1.9";

	/** COMMON COMPONENTS **/

	public static final String	IP_NET_MASK					= "255.255.255.252";
	public static final String	DEFAULT_ROUTE				= "0.0.0.0";
	public static final String	DEFAULT_ROUTE_MASK			= "0.0.0.0";

}
