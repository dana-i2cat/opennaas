package org.opennaas.extensions.network.model.topology;

import java.util.ArrayList;
import java.util.List;

/**
 * A network element(s) that can be represented as a connection point (ITU-T G.805 terminology) or as an incident of an edge on a vertex (in Graph
 * theory). This is a logical network interface, thus an interface at a specific layer.
 *
 * Connection points are always bidirectional. To represent a unidirectional interface, simply make the Link or Cross Connect unidirectional.
 *
 * @author isart
 *
 */
public class ConnectionPoint extends TransportNetworkElement {

	ConnectionPoint			serverInterface;
	List<ConnectionPoint>	clientInterfaces	= new ArrayList<ConnectionPoint>();

	public ConnectionPoint getServerInterface() {
		return serverInterface;
	}

	public void setServerInterface(ConnectionPoint serverInterface) {
		this.serverInterface = serverInterface;
	}

	public List<ConnectionPoint> getClientInterfaces() {
		return clientInterfaces;
	}

	public void setClientInterfaces(List<ConnectionPoint> clientInterfaces) {
		this.clientInterfaces = clientInterfaces;
	}
}
