package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.predicates.ConnectedTo;

/**
 * A (collection of) network element(s) that can be represented as a tandem connection (ITU-T G.805 terminology) 
 * or as a path in a Graph (in Graph theory). A path is always a connection at a single layer. 
 * A Path does not have to be end-to-end.
 * 
 * @author isart
 *
 */
public class Path extends NetworkConnection {
	
	List<NetworkConnection> pathSegments;
	
	List<ConnectedTo> connectedTo;

	/**
	 * The pathsegments property gives a serial partitioning of a Path object.
	 * This property specifies the links or sub paths of this path.
	 * @return list of segments (NetworkConnections) of this path
	 */
	public List<NetworkConnection> getPathSegments() {
		return pathSegments;
	}

	public void setPathSegments(List<NetworkConnection> pathSegments) {
		this.pathSegments = pathSegments;
	}

	public List<ConnectedTo> getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(List<ConnectedTo> connectedTo) {
		this.connectedTo = connectedTo;
	}
}
