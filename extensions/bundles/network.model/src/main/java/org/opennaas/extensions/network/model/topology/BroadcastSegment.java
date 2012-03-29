package org.opennaas.extensions.network.model.topology;

/**
 * A Broadcast Segment is a direct (not concatenated) connection between multiple Interfaces.
 * A Link can only connect two Interface, while a Broadcast Segment connects multiple interfaces.
 *
 * A Broadcast Segment can be unidirectional, bidirectional, point-to-point, point-to-multipoint, or multipoint-to-multipoint.
 * Data coming from a connected interface (Interface linkTo Broadcast Segment) will be forwarded to all other Interfaces (Broadcast Segment linkTo Interfaces).
 * Thus the received data will be forwarded to all Interfaces, with the exception of the Interface were the data originated.
 * @author isart
 *
 */
public class BroadcastSegment extends NetworkConnection {

}
