package net.i2cat.mantychore.actionsets.junos;

public class ActionConstants {

	public static final String	GETCONFIG				= "getConfiguration";
	public static final String	SETIPv4					= "setIPv4";
	// public static final String CONFIGLT = "configureLogicalTunnel";

	public static final String	ISALIVE					= "isAlive";
	public static final String	VALIDATE				= "validate";

	// Chassis ActionSET
	public static final String	DELETESUBINTERFACE		= "deletesubinterface";
	public static final String	CONFIGURESUBINTERFACE	= "configureSubInterface";
	public static final String	SETENCAPSULATION		= "setEncapsulation";
	public static final String	CONFIGURESTATUS			= "configureStatus";
	public static final String	DELETELOGICALROUTER		= "deleteLogicalRouter";
	public static final String	CREATELOGICALROUTER		= "createLogicalRouter";
	public static final String	SETINTERFACEDESCRIPTION	= "setInterfaceDescription";

	// TODO THE SAME NAME FOR THE OTHER LIST LOGICAL ROUTERS
	public static final String	GETLOGICALROUTERS		= "getLogicalRouters";

	// TODO LISTLOGICALROUTER!!!
	public static final String	LISTLOGICALROUTER		= "listLogicalRouter";

	// OSPF actionset
	public static final String	OSPF_CONFIGURE			= "configureOSPF";
	public static final String	OSPF_GET_CONFIGURATION	= "getOSPFConfiguration";
	public static final String	OSPF_ACTIVATE			= "activateOSPF";
	public static final String	OSPF_DEACTIVATE			= "deactivateOSPF";
	public static final String	OSPF_ENABLE_INTERFACE	= "enableOSPFInInterface";
	public static final String	OSPF_DISABLE_INTERFACE	= "disableOSPFInInterface";

}
