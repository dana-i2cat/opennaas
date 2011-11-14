package net.i2cat.mantychore.network.model.predicates;

import net.i2cat.mantychore.network.model.topology.TransportNetworkElement;

/**
 * The connectedTo ties Interfaces and Paths together. This property defines uni-directional tandem connections between Interfaces, or from Interfaces to Path and Paths to Interfaces. All data send out the subject Interface (the egress interface) is somehow received by to the object Interface (the ingress interface). To define a bi-directional connection with the connectedTo property it should be defined in both directions. A connectedTo property always involves external connection, between devices, not connections within a device (for that, see switchedTo). The subject and object Interface must be on the same layer.
 * 
 * @author isart
 *
 */
public class ConnectedTo {
	
	/**
	 * ConnectionPoint or Path
	 */
	TransportNetworkElement src;
	
	/**
	 * ConnectionPoint or Path
	 */
	TransportNetworkElement dst;

}
