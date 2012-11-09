package org.opennaas.extensions.router.junos.actionssets;

import org.opennaas.extensions.router.capability.bgp.BGPActionSet;
import org.opennaas.extensions.router.capability.chassis.ChassisActionSet;
import org.opennaas.extensions.router.capability.gretunnel.GRETunnelActionSet;
import org.opennaas.extensions.router.capability.ip.IPActionSet;
import org.opennaas.extensions.router.capability.ospf.OSPFActionSet;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteActionSet;
import org.opennaas.extensions.router.capability.vrrp.VRRPActionSet;

public class ActionConstants {

	public static final String	GETCONFIG							= "getConfiguration";
	// public static final String CONFIGLT = "configureLogicalTunnel";

	public static final String	ISALIVE								= "isAlive";
	public static final String	VALIDATE							= "validate";

	// Chassis ActionSET
	public static final String	DELETESUBINTERFACE					= ChassisActionSet.DELETESUBINTERFACE;
	public static final String	CONFIGURESUBINTERFACE				= ChassisActionSet.CONFIGURESUBINTERFACE;
	public static final String	SET_TAGGEDETHERNET_ENCAPSULATION	= ChassisActionSet.SET_TAGGEDETHERNET_ENCAPSULATION;
	public static final String	REMOVE_TAGGEDETHERNET_ENCAPSULATION	= ChassisActionSet.REMOVE_TAGGEDETHERNET_ENCAPSULATION;
	public static final String	SET_VLANID							= ChassisActionSet.SET_VLANID;
	public static final String	SETENCAPSULATION					= "setEncapsulation";
	public static final String	SETENCAPSULATIONLABEL				= "setEncapsulationLabel";

	public static final String	CONFIGURESTATUS						= ChassisActionSet.CONFIGURESTATUS;

	// LogicalRouters
	public static final String	DELETELOGICALROUTER					= ChassisActionSet.DELETELOGICALROUTER;
	public static final String	CREATELOGICALROUTER					= ChassisActionSet.CREATELOGICALROUTER;
	public static final String	ADDINTERFACETOLOGICALROUTER			= ChassisActionSet.ADDINTERFACETOLOGICALROUTER;
	public static final String	REMOVEINTERFACEFROMLOGICALROUTER	= ChassisActionSet.REMOVEINTERFACEFROMLOGICALROUTER;

	// IP
	public static final String	SETIPv4								= IPActionSet.SET_IPv4;
	public static final String	SETINTERFACEDESCRIPTION				= IPActionSet.SET_INTERFACE_DESCRIPTION;

	// TODO THE SAME NAME FOR THE OTHER LIST LOGICAL ROUTERS
	public static final String	GETLOGICALROUTERS					= "getLogicalRouters";
	// TODO LISTLOGICALROUTER!!!
	public static final String	LISTLOGICALROUTER					= "listLogicalRouter";

	// GRETunnel ActionSET
	public static final String	CREATETUNNEL						= GRETunnelActionSet.CREATETUNNEL;
	public static final String	DELETETUNNEL						= GRETunnelActionSet.DELETETUNNEL;
	public static final String	GETTUNNELCONFIG						= GRETunnelActionSet.GETTUNNELCONFIG;
	public static final String	SHOWTUNNELS							= GRETunnelActionSet.SHOWTUNNELS;

	// OSPF actionset
	public static final String	OSPF_CONFIGURE						= OSPFActionSet.OSPF_CONFIGURE;
	public static final String	OSPF_CLEAR							= OSPFActionSet.OSPF_CLEAR;
	public static final String	OSPF_GET_CONFIGURATION				= OSPFActionSet.OSPF_GET_CONFIGURATION;
	public static final String	OSPF_ACTIVATE						= OSPFActionSet.OSPF_ACTIVATE;
	public static final String	OSPF_DEACTIVATE						= OSPFActionSet.OSPF_DEACTIVATE;
	public static final String	OSPF_ENABLE_INTERFACE				= OSPFActionSet.OSPF_ENABLE_INTERFACE;
	public static final String	OSPF_DISABLE_INTERFACE				= OSPFActionSet.OSPF_DISABLE_INTERFACE;
	public static final String	OSPF_CONFIGURE_AREA					= OSPFActionSet.OSPF_CONFIGURE_AREA;
	public static final String	OSPF_REMOVE_AREA					= OSPFActionSet.OSPF_REMOVE_AREA;
	public static final String	OSPF_ADD_INTERFACE_IN_AREA			= OSPFActionSet.OSPF_ADD_INTERFACE_IN_AREA;
	public static final String	OSPF_REMOVE_INTERFACE_IN_AREA		= OSPFActionSet.OSPF_REMOVE_INTERFACE_IN_AREA;

	// Static Route actionset
	public static final String	STATIC_ROUTE_CREATE					= StaticRouteActionSet.STATIC_ROUTE_CREATE;
	public static final String	STATIC_ROUTE_DELETE					= StaticRouteActionSet.STATIC_ROUTE_DELETE;

	// BGP actionset
	public static final String	CONFIGURE_BGP						= BGPActionSet.CONFIGURE_BGP;
	public static final String	UNCONFIGURE_BGP						= BGPActionSet.UNCONFIGURE_BGP;

	// VRRP actionset
	public static final String	VRRP_CONFIGURE						= VRRPActionSet.VRRP_CONFIGURE;
	public static final String	VRRP_UNCONFIGURE					= VRRPActionSet.VRRP_UNCONFIGURE;
	public static final String	VRRP_UPDATE_IP_ADDRESS				= VRRPActionSet.VRRP_UPDATE_VIRTUAL_IP_ADDRESS;
	public static final String	VRRP_UPDATE_PRIORITY				= VRRPActionSet.VRRP_UPDATE_PRIORITY;
}
