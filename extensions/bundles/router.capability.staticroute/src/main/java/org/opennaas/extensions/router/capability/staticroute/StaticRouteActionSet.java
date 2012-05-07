package org.opennaas.extensions.router.capability.staticroute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticRouteActionSet {

	// Interfaces
    public static final String STATIC_ROUTE_CREATE = "createStaticRoute";
	private static final List<String>	actionIds  = new ArrayList(Arrays.asList(
																STATIC_ROUTE_CREATE));

	public static List<String> getActionIds() {
		return actionIds;
	}

}
