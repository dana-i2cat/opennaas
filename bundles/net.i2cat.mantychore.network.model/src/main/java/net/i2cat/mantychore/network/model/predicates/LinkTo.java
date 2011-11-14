package net.i2cat.mantychore.network.model.predicates;

import net.i2cat.mantychore.network.model.topology.TransportNetworkElement;

/**
 * 
 * The linkTo ties Interfaces and Links together. This property defines uni-directional connections between Interfaces, or from Interfaces to Links and Links to Interfaces. All data send out the subject (the egress Interface or Link) is received by to the object (the ingress Interface or Link) though a single link. To represent a bi-directional connection, the linkTo statement should be defined in both directions (both Interfaces pointing to each other). A linkTo property always involves an external connection, between devices, not connections within a device (for that, see switchedTo). The subject and object Interface must be on the same layer.
 * The linkTo is a subProperty of the connectedTo property and defines that the subject and object Interface are directly connected to each other on their layer. There is no intermediate connection point on this layer forwarding the data.
 * 
 * @author isart
 *
 */
public class LinkTo {
	/**
	 * ConnectionPoint or BroadcastSegment
	 */
	TransportNetworkElement src;
	
	/**
	 * ConnectionPoint or BroadcastSegment
	 */
	TransportNetworkElement dst;

}
