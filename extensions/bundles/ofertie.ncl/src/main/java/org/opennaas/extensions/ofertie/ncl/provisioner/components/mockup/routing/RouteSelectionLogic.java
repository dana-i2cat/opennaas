package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
