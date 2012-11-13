package org.opennaas.extensions.network.capability.basic;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class NetworkBasicActionSet implements IActionSetDefinition {

	public static final String	ADD_RESOURCE	= "addResource";
	public static final String	REMOVE_RESOURCE	= "removeResource";
	public static final String	LIST_RESOURCES	= "listResources";
	public static final String	IMPORT_MODEL	= "importModel";
	public static final String	EXPORT_MODEL	= "exportModel";

	private static List<String>	actionNames;

	static {
		actionNames = new ArrayList<String>();

		actionNames.add(ADD_RESOURCE);
		actionNames.add(REMOVE_RESOURCE);
		actionNames.add(LIST_RESOURCES);

		actionNames.add(IMPORT_MODEL);
		actionNames.add(EXPORT_MODEL);
	}

	public static List<String> getActionNames() {
		return actionNames;
	}
}
