package net.i2cat.mantychore.network.model.topology;

import net.i2cat.mantychore.network.model.layer.AdaptationProperty;

/**
 * A network element(s) that can be represented as a connection point (ITU-T G.805 terminology) 
 * or as an incident of an edge on a vertex (in Graph theory). This is a logical network interface, 
 * thus an interface at a specific layer.
 * 
 * Connection points are always bidirectional. To represent a unidirectional interface, simply make the Link or Cross Connect unidirectional.
 * 
 * @author isart
 *
 */
public class ConnectionPoint extends TransportNetworkElement {
	
	AdaptationProperty toServerInterface;
	AdaptationProperty toClientInterface;
	
}
