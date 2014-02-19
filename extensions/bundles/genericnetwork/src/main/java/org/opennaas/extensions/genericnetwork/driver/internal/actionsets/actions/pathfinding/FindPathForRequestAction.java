package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingActionSet;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingParamsMapping;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteList;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionInput;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionLogic;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class FindPathForRequestAction extends Action {

	private RouteSelectionLogic			routeSelectionLogic;

	/**
	 * Key: RouteId, Value: Route to apply
	 */
	private static Map<String, Route>	routes;

	public FindPathForRequestAction() throws IOException, SerializationException {

		this.actionID = PathFindingActionSet.FIND_PATH_FOR_REQUEST;
		routes = new HashMap<String, Route>();
		routeSelectionLogic = new RouteSelectionLogic();

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		Map<String, ?> mapParam;

		if (!(params instanceof Map<?, ?>))
			throw new ActionException(
					"Invalid params for action " + this.actionID + ". Excected param is a map with keys " + PathFindingParamsMapping.ROUTES_FILE_KEY + " and " + PathFindingParamsMapping.REQUEST_KEY);

		try {
			mapParam = (Map) params;
		} catch (ClassCastException e) {
			throw new ActionException(e);
		}

		// checks the hashmap contains all information
		if (!mapParam.keySet().contains(PathFindingParamsMapping.ROUTES_FILE_KEY) || !mapParam.keySet()
				.contains(PathFindingParamsMapping.REQUEST_KEY))
			throw new ActionException(
					"Invalid params for action " + this.actionID + ". Excected param is a map with keys " + PathFindingParamsMapping.ROUTES_FILE_KEY + " and " + PathFindingParamsMapping.REQUEST_KEY);

		// checks there's a valid CircuitRequest
		Object request = mapParam.get(PathFindingParamsMapping.REQUEST_KEY);

		if (!(request instanceof CircuitRequest))
			throw new ActionException(
					"Invalid params for action " + this.actionID + ". " + PathFindingParamsMapping.REQUEST_KEY + " should contain a " + CircuitRequest.class
							.getName() + " and not a " + request.getClass().getName());

		// checks there's a valid URL for the file containing the routes
		Object routeUrl = mapParam.get(PathFindingParamsMapping.ROUTES_FILE_KEY);

		if (!(routeUrl instanceof String))
			throw new ActionException(
					"Invalid params for action " + this.actionID + ". " + PathFindingParamsMapping.ROUTES_FILE_KEY + " should contain a String");

		try {
			FileInputStream file = new FileInputStream((String) routeUrl);
		} catch (FileNotFoundException e) {
			throw new ActionException(
					"Invalid params for action " + this.actionID + "." + PathFindingParamsMapping.ROUTES_FILE_KEY + " should contain a valid url to an existing file.",
					e);
		}

		// checks there's a valid URL for the file containing the mapping
		Object mappingURL = mapParam.get(PathFindingParamsMapping.ROUTES_MAPPING_KEY);

		if (!(mappingURL instanceof String))
			throw new ActionException(
					"Invalid params for action " + this.actionID + ". " + PathFindingParamsMapping.ROUTES_MAPPING_KEY + " should contain a String");

		try {
			FileInputStream file = new FileInputStream((String) mappingURL);
		} catch (FileNotFoundException e) {
			throw new ActionException(
					"Invalid params for action " + this.actionID + "." + PathFindingParamsMapping.ROUTES_MAPPING_KEY + " should contain a valid url to an existing file.",
					e);
		}

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		List<String> possibleRouteIds;

		Map<String, Object> mapParam = (Map<String, Object>) this.params;

		CircuitRequest request = (CircuitRequest) mapParam.get(PathFindingParamsMapping.REQUEST_KEY);
		String routesURL = (String) mapParam.get(PathFindingParamsMapping.ROUTES_FILE_KEY);
		String mappingURL = (String) mapParam.get(PathFindingParamsMapping.ROUTES_MAPPING_KEY);

		try {
			routes = readRoutesFromFile(routesURL);
		} catch (FileNotFoundException e) {
			throw new ActionException("Invalid url to load routes ", e);
		} catch (SerializationException e) {
			throw new ActionException("Invalid routes file " + routesURL, e);
		}

		RouteSelectionInput input = createRouteSelectionInputFromRequest(request);

		routeSelectionLogic.setMappingUrl(mappingURL);

		try {
			possibleRouteIds = routeSelectionLogic.getPotentialRoutes(input);
		} catch (SerializationException e) {
			throw new ActionException("Invalid file for mapping");
		} catch (IOException e) {
			throw new ActionException("Invalid file for mapping");
		}

		possibleRouteIds = filterNotCongestedRoutes(possibleRouteIds);

		if (possibleRouteIds.isEmpty())
			throw new ActionException("Unable to find uncongested route for given request");

		Route route = routes.get(possibleRouteIds.get(0));

		ActionResponse response = new ActionResponse();
		response.setActionID(this.actionID);
		response.setResult(route);
		response.setStatus(STATUS.OK);

		return response;

	}

	private Map<String, Route> readRoutesFromFile(String url) throws FileNotFoundException, SerializationException {

		Map<String, Route> finalPaths = new HashMap<String, Route>();
		FileInputStream file = new FileInputStream((String) url);

		RouteList routes = ObjectSerializer.fromXml(file, RouteList.class);

		for (Route route : routes.getRoutes()) {
			finalPaths.put(route.getId(), route);

		}
		return finalPaths;
	}

	private RouteSelectionInput createRouteSelectionInputFromRequest(CircuitRequest circuitRequest) {

		if (StringUtils.isEmpty(circuitRequest.getSource().getLinkPort()) || StringUtils.isEmpty(circuitRequest.getDestination().getLinkPort()))
			return new RouteSelectionInput(
					circuitRequest.getSource().getAddress(),
					circuitRequest.getDestination().getAddress(),
					String.valueOf(circuitRequest.getLabel()));

		else
			return new RouteSelectionInput(
					circuitRequest.getSource().getAddress(),
					circuitRequest.getDestination().getAddress(),
					String.valueOf(circuitRequest.getLabel()),
					circuitRequest.getSource().getLinkPort(),
					circuitRequest.getDestination().getLinkPort());
	}

	private List<String> filterNotCongestedRoutes(List<String> routes) {
		List<String> notCongested = new ArrayList<String>();
		for (String routeId : routes) {
			if (!isCongestedRoute(routeId)) {
				notCongested.add(routeId);
			}
		}
		return notCongested;
	}

	private boolean isCongestedRoute(String routeId) {
		Route route = routes.get(routeId);
		for (NetworkConnection connection : route.getNetworkConnections())
			if (connection.getSource().getState().isCongested() || connection.getDestination().getState().isCongested())
				return true;

		return false;
	}
}
