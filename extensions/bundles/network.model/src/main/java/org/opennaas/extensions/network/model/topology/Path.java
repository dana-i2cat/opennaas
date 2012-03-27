package org.opennaas.extensions.network.model.topology;

import java.util.ArrayList;
import java.util.List;

/**
 * A (collection of) network element(s) that can be represented as a tandem connection (ITU-T G.805 terminology) or as a path in a Graph (in Graph
 * theory). A path is always a connection at a single layer. A Path does not have to be end-to-end.
 *
 * @author isart
 *
 */
public class Path extends NetworkConnection {

	List<NetworkConnection>	pathSegments	= new ArrayList<NetworkConnection>();

	Interface				source;
	Interface				sink;

	/**
	 * The pathsegments property gives a serial partitioning of a Path object. This property specifies the links or sub paths of this path.
	 *
	 * @return list of segments (NetworkConnections) of this path
	 */
	public List<NetworkConnection> getPathSegments() {
		return pathSegments;
	}

	public void setPathSegments(List<NetworkConnection> pathSegments) {
		this.pathSegments = pathSegments;
	}

	public Interface getSource() {
		return source;
	}

	public void setSource(Interface source) {
		this.source = source;
	}

	public Interface getSink() {
		return sink;
	}

	public void setSink(Interface sink) {
		this.sink = sink;
	}
}
