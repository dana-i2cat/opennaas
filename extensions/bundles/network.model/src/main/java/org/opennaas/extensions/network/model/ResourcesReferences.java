package org.opennaas.extensions.network.model;

import java.util.HashMap;

/**
 * Class to store a mapping between resources in network topology and resources in ResourceManager.
 *
 * This mapping is made through networkModel names and resource ids. Hence, keys of this map should be resource names, while values should be resource IDs.
 *
 * @author isart
 */
public class ResourcesReferences extends HashMap<String, String> {

	private static final long serialVersionUID = -84597832755877390L;

}
