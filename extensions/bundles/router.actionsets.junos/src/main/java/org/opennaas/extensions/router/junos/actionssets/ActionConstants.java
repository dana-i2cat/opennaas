package org.opennaas.extensions.router.junos.actionssets;

public class ActionConstants {

	public static final String	GETCONFIG							= "getConfiguration";
	public static final String	SETIPv4								= "setIPv4";
	// public static final String CONFIGLT = "configureLogicalTunnel";

	public static final String	ISALIVE								= "isAlive";
	public static final String	VALIDATE							= "validate";

	// Chassis ActionSET
	public static final String	DELETESUBINTERFACE					= "deletesubinterface";
	public static final String	CONFIGURESUBINTERFACE				= "configureSubInterface";
	public static final String	SETENCAPSULATION					= "setEncapsulation";
	public static final String	SETENCAPSULATIONLABEL				= "setEncapsulationLabel";
	public static final String	SET_TAGGEDETHERNET_ENCAPSULATION	= "setTaggedEthEncapsulation";
	public static final String	REMOVE_TAGGEDETHERNET_ENCAPSULATION	= "removeTaggedEthEncapsulation";
	public static final String	SET_VLANID							= "setVlanId";

	public static final String	CONFIGURESTATUS						= "configureInterfaceStatus";
	public static final String	SETINTERFACEDESCRIPTION				= "setInterfaceDescription";

	// LogicalRouters
	public static final String	DELETELOGICALROUTER					= "deleteLogicalRouter";
	public static final String	CREATELOGICALROUTER					= "createLogicalRouter";
	public static final String	ADDINTERFACETOLOGICALROUTER			= "addInterfaceToLogicalRouter";
	public static final String	REMOVEINTERFACEFROMLOGICALROUTER	= "removeInterfaceFromLogicalRouter";

	// TODO THE SAME NAME FOR THE OTHER LIST LOGICAL ROUTERS
	public static final String	GETLOGICALROUTERS					= "getLogicalRouters";
	// TODO LISTLOGICALROUTER!!!
	public static final String	LISTLOGICALROUTER					= "listLogicalRouter";

	// GRETunnel ActionSET
	public static final String	CREATETUNNEL						= "createTunnel";
	public static final String	DELETETUNNEL						= "deleteTunnel";
	public static final String	GETTUNNELCONFIG						= "getTunnelConfiguration";
	public static final String	SHOWTUNNELS							= "showTunnels";

	// OSPF actionset
	public static final String	OSPF_CONFIGURE						= "configureOSPF";
	public static final String	OSPF_CLEAR							= "clearOSPF";
	public static final String	OSPF_GET_CONFIGURATION				= "getOSPFConfiguration";
	public static final String	OSPF_ACTIVATE						= "activateOSPF";
	public static final String	OSPF_DEACTIVATE						= "deactivateOSPF";
	public static final String	OSPF_ENABLE_INTERFACE				= "enableOSPFInInterface";
	public static final String	OSPF_DISABLE_INTERFACE				= "disableOSPFInInterface";
	public static final String	OSPF_CONFIGURE_AREA					= "configureOSPFArea";
	public static final String	OSPF_REMOVE_AREA					= "removeOSPFArea";
	public static final String	OSPF_ADD_INTERFACE_IN_AREA			= "addOSPFInterfaceInArea";
	public static final String	OSPF_REMOVE_INTERFACE_IN_AREA		= "removeOSPFInterfaceInArea";

	// Static Route actionset
	public static final String	STATIC_ROUTE_CREATE					= "createStaticRoute";
}
