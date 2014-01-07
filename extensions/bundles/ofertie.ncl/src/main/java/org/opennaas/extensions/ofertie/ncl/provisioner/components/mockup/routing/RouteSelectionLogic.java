package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.SerializationException;

public class RouteSelectionLogic {

	private final static String	PATHS_FILE_URL	= "etc/org.opennaas.extensions.ofertie.ncl.routemapping.xml";

	private RouteSelectionMap	routeMap;

	public RouteSelectionLogic() throws IOException, SerializationException {
		String xml = RouteSelectionMapLoader.readXMLFile(PATHS_FILE_URL);
		routeMap = RouteSelectionMapLoader.getRouteSelectionMapFromXml(xml);
	}

	/**
	 * @return the routeMap
	 */
	public RouteSelectionMap getRouteMap() {
		return routeMap;
	}

	/**
	 * @param routeMap
	 *            the routeMap to set
	 */
	public void setRouteMap(RouteSelectionMap routeMapping) {
		this.routeMap = routeMapping;
	}

	/**
	 * 
	 * @param input
	 * @return potential routes for given input
	 */
	public List<String> getPotentialRoutes(RouteSelectionInput input) {
		if (!routeMap.getRouteMapping().containsKey(input))
			return new ArrayList<String>(0);

		return routeMap.getRouteMapping().get(input).getRouteIds();
	}

}
