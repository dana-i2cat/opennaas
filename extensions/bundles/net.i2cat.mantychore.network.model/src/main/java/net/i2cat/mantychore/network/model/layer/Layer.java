package net.i2cat.mantychore.network.model.layer;

import net.i2cat.mantychore.network.model.topology.NetworkElement;

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
