package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Route;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;

public class PathFinderMockup implements IPathFinder {
	
	private static final String DEFAULT_ROUTE = "0";
	/**
	 * Key: ToS in request, Value: Route to apply
	 */
	private static Map<String, Route> routes = initRoutes();

	@Override
	public Route findPathForRequest(FlowRequest flowRequest, String networkId)
			throws Exception {
		return selectRouteFromToS(flowRequest.getTos());
	}
	
	private Route selectRouteFromToS(int tos) {
		Route route;
		if (tos == -1)
			route = routes.get(DEFAULT_ROUTE);
		else
			route = routes.get(String.valueOf(tos));
		
		return route;
	}

	/**
	 * TODO Add routes according to testbed topology
	 * @return
	 */
	private static Map<String, Route> initRoutes() {
		Map<String, Route> routes = new HashMap<String, Route>();
		routes.put("0", new Route());
		routes.put("1", new Route());
		routes.put("2", new Route());
		return routes;
	}

}
