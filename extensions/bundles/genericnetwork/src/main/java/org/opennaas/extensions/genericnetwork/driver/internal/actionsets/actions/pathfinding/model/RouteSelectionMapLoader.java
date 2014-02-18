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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

public abstract class RouteSelectionMapLoader {

	private static Log	LOG	= LogFactory.getLog(RouteSelectionMapLoader.class);

	public static RouteSelectionMap getRouteSelectionMapFromXmlFile(String url) throws SerializationException, FileNotFoundException {

		LOG.debug("Parsing RouteSelectionMap from xml.");

		FileInputStream file = new FileInputStream(url);

		RouteSelectionMap map = ObjectSerializer.fromXml(file, RouteSelectionMap.class);

		LOG.debug("RouteSelectionMap parsed from xml.");

		return map;
	}

}
