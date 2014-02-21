package org.opennaas.extensions.genericnetwork.model.helpers;

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

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;

/**
 * {@link Topology} helper
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class TopologyHelper {

	/**
	 * Returns {@link NetworkElement} from a given {@link Topology} given his ID
	 * 
	 * @param topology
	 * @param id
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static NetworkElement getNetworkElementFromId(Topology topology, String id) throws IllegalArgumentException {
		for (NetworkElement networkElement : topology.getNetworkElements()) {
			if (networkElement.getId().equals(id))
				return networkElement;
		}
		throw new IllegalArgumentException("NetworkElement with ID = " + id + " not found.");
	}

	/**
	 * Returns {@link IResource} associated with a {@link NetworkElement} ID given a {@link IResourceManager}
	 * 
	 * @param id
	 * @param resourceManager
	 * @return
	 * @throws ResourceException
	 */
	public static IResource getResourceFromNetworkElementId(String id, IResourceManager resourceManager) throws ResourceException {
		return resourceManager.getResourceById(getResourceIdFromNetworkElementId(id, resourceManager));
	}

	/**
	 * Returns {@link IResource} ID associated with a {@link NetworkElement} ID given a {@link IResourceManager}
	 * 
	 * @param id
	 * @param resourceManager
	 * @return
	 * @throws ResourceException
	 */
	public static String getResourceIdFromNetworkElementId(String id, IResourceManager resourceManager) throws ResourceException {
		String[] typeAndName = getTypeAndNameFromNetworkElementId(id);
		return resourceManager.getIdentifierFromResourceTypeName(typeAndName[0], typeAndName[1]);
	}

	/**
	 * Return an String array with two element: resource type and resource name given an String with format [resourceType:resourceName]
	 * 
	 * @param id
	 * @return
	 */
	public static String[] getTypeAndNameFromNetworkElementId(String id) {
		String[] typeAndName = new String[2];
		typeAndName = id.split(":");
		if (typeAndName.length != 2 || typeAndName[0].equalsIgnoreCase("") || typeAndName[1].equalsIgnoreCase("")) {
			throw new IllegalArgumentException("Invalid id, it must have the format [resourceType:resourceName]");
		}
		return typeAndName;
	}
}
