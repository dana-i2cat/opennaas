package org.opennaas.extensions.genericnetwork.model.helpers;

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
		String[] typeAndName = getTypeAndNameFromNetworkElementId(id);
		return resourceManager.getResourceById(resourceManager.getIdentifierFromResourceTypeName(typeAndName[0], typeAndName[1]));
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
