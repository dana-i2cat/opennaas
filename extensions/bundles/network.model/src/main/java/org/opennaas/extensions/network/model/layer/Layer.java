package org.opennaas.extensions.network.model.layer;

import org.opennaas.extensions.network.model.topology.NetworkElement;

/**
 * A specific encoding of data in a network connection.
 * 
 * @author isart
 * 
 */
public abstract class Layer extends NetworkElement {

	/**
	 * @return the name of the layer
	 */
	@Override
	public abstract String getName();
}
