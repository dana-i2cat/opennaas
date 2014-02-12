package org.opennaas.extensions.genericnetwork.model.circuit;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
@XmlRootElement(namespace = "opennaas.api")
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
