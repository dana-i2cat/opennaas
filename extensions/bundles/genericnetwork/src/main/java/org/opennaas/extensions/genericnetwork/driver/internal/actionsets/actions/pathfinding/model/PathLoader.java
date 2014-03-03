package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model;

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
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class PathLoader {

	public Map<String, Route> readRoutesFromFile(String url) throws FileNotFoundException, SerializationException {

		Map<String, Route> finalPaths = new HashMap<String, Route>();
		FileInputStream file = new FileInputStream((String) url);

		RouteList routes = ObjectSerializer.fromXml(file, RouteList.class);

		for (Route route : routes.getRoutes()) {
			finalPaths.put(route.getId(), route);

		}
		return finalPaths;
	}
}
