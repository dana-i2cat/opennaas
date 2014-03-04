package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model;

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

import org.apache.commons.lang.StringUtils;
import org.opennaas.core.resources.SerializationException;

public class RouteSelectionLogic {

	private RouteSelectionMap	routeMap;
	private String				mappingUrl;

	public String getMappingUrl() {
		return mappingUrl;
	}

	public void setMappingUrl(String mappingUrl) {
		this.mappingUrl = mappingUrl;
	}

	public RouteSelectionLogic() {

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
	 * @throws IOException
	 * @throws SerializationException
	 */
	public List<String> getPotentialRoutes(RouteSelectionInput input) throws SerializationException, IOException {

		if (StringUtils.isEmpty(mappingUrl))
			throw new IOException("Didin't specify a file containing the mapping.");

		readMappingFile();

		if (StringUtils.isEmpty(input.getSrcPort()) || StringUtils.isEmpty(input.getDstPort()))
			return compareWithoutPorts(input);
		else
			return compareWithPorts(input);

	}

	/**
	 * If ports are specified, there's a direct match, since the object contains all attributes from the keys.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> compareWithPorts(RouteSelectionInput input) {
		if (!routeMap.getRouteMapping().containsKey(input))
			return new ArrayList<String>(0);

		return routeMap.getRouteMapping().get(input).getRouteIds();
	}

	/**
	 * If ports are not specified, the containsKey would not work, since the object does not contain all attributes from the key.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> compareWithoutPorts(RouteSelectionInput input) {

		for (RouteSelectionInput candidateInput : routeMap.getRouteMapping().keySet())
			if (candidateInput.getSrcIP().equals(input.getSrcIP()) && candidateInput.getDstIP().equals(input.getDstIP()) && candidateInput.getTos()
					.equals(input.getTos()))
				return routeMap.getRouteMapping().get(candidateInput).getRouteIds();

		return new ArrayList<String>(0);
	}

	private void readMappingFile() throws SerializationException, IOException {
		routeMap = RouteSelectionMapLoader.getRouteSelectionMapFromXmlFile(mappingUrl);
	}

}
