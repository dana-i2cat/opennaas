package org.opennaas.extensions.sdnnetwork.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Route {

	private String					id;
	private List<NetworkConnection>	networkConnections;

	/**
	 * Default constructor
	 */
	public Route() {
		networkConnections = new ArrayList<NetworkConnection>();
	}

	/**
	 * Copy constructor
	 * 
	 * @param route
	 *            Route to copy
	 */
	public Route(Route route) {
		this.id = route.id;

		this.networkConnections = new ArrayList<NetworkConnection>(route.networkConnections.size());
		for (NetworkConnection networkConnection : route.networkConnections) {
			this.networkConnections.add(new NetworkConnection(networkConnection));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<NetworkConnection> getNetworkConnections() {
		return networkConnections;
	}

	public void setNetworkConnections(List<NetworkConnection> networkConnections) {
		this.networkConnections = networkConnections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((networkConnections == null) ? 0 : networkConnections.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (networkConnections == null) {
			if (other.networkConnections != null)
				return false;
		} else if (!networkConnections.equals(other.networkConnections))
			return false;
		return true;
	}

}
