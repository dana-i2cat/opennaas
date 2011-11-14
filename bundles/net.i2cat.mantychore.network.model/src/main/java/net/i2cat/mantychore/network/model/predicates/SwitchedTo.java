package net.i2cat.mantychore.network.model.predicates;

import net.i2cat.mantychore.network.model.topology.TransportNetworkElement;

/**
 * The switchedTo property represents an internal uni-directional connection within a device or network. All data from the subject is forwarded to the object. A switchedTo property always involves internal connection, within devices, not connections between two devices (for that, see connectedTo). To define a bi-directional connection with the switchedTo property it should be defined in both directions.
 *
 * An Interface that is the object of two switchedTo statements represents a multicast abilty. An Interface that is the subject of two switchedTo statements represent either a selection or merger of data. In case of a circuit switched cross connect, the object Interface somehow picks one of the two signals (for example the Interface with the best signal-to-noise ratio of a 1:1 protected path), and puts that through, dropping traffic from the other subject Interface. In case of a packet switched cross connect, the object interface merges the packets.
 *
 * (In NDL 2.4 we distinguished between a circuitSwitchedTo, packetSwitchedTo and altSwitchedTo predicate. However, we felt that it is more appropriate to use a specific CrossConnect object for these circumstances. No specific predicates or subClasses are yet defined. We like to hear about your suggestions and experiences!)
 *
 * @author isart
 *
 */
public class SwitchedTo {
	
	/**
	 * ConnectionPoint or CrossConnect
	 */
	TransportNetworkElement src;
	
	/**
	 * ConnectionPoint or CrossConnect
	 */
	TransportNetworkElement dst;

}
