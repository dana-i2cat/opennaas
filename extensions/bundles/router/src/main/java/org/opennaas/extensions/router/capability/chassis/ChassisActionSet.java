package org.opennaas.extensions.router.capability.chassis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class ChassisActionSet implements IActionSetDefinition {

	// Interfaces
	public static final String			CONFIGURESTATUS						= "configureInterfaceStatus";
	public static final String			DELETESUBINTERFACE					= "deleteSubInterface";
	public static final String			CONFIGURESUBINTERFACE				= "configureSubInterface";
	public static final String			SET_TAGGEDETHERNET_ENCAPSULATION	= "setTaggedEthEncapsulation";
	public static final String			REMOVE_TAGGEDETHERNET_ENCAPSULATION	= "removeTaggedEthEncapsulation";
	public static final String			SET_VLANID							= "setVlanId";

	// LogicalRouters
	public static final String			DELETELOGICALROUTER					= "deleteLogicalRouter";
	public static final String			CREATELOGICALROUTER					= "createLogicalRouter";
	public static final String			ADDINTERFACETOLOGICALROUTER			= "addInterfaceToLogicalRouter";
	public static final String			REMOVEINTERFACEFROMLOGICALROUTER	= "removeInterfaceFromLogicalRouter";

	private static final List<String>	actionIds							= new ArrayList<String>(Arrays.asList(
																					CONFIGURESTATUS,
																					DELETESUBINTERFACE,
																					CONFIGURESUBINTERFACE,
																					SET_TAGGEDETHERNET_ENCAPSULATION,
																					REMOVE_TAGGEDETHERNET_ENCAPSULATION,
																					SET_VLANID,
																					DELETELOGICALROUTER,
																					CREATELOGICALROUTER,
																					ADDINTERFACETOLOGICALROUTER,
																					REMOVEINTERFACEFROMLOGICALROUTER));

	public static List<String> getActionIds() {
		return actionIds;
	}

}
