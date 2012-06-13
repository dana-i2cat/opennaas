package org.opennaas.extensions.router.capability.staticroute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class StaticRouteActionSet implements IActionSetDefinition {

	// Interfaces
	public static final String			STATIC_ROUTE_CREATE	= "createStaticRoute";
	private static final List<String>	actionIds			= new ArrayList<String>(Arrays.asList(
																	STATIC_ROUTE_CREATE));

	public static List<String> getActionIds() {
		return actionIds;
	}
}
