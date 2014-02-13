package org.opennaas.extensions.genericnetwork.model.topology;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class Port extends TopologyElement {

	/**
	 * A unique id identifying this port in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach port p_i in network elements of a topology
	 * 
	 * p_x.getId() != p_y.getId() when (x != y)
	 */
	@XmlAttribute(name = "id")
	@XmlID
	private String	id;

	/**
	 * Default constructor
	 */
	public Port() {
		super();
	}

	/**
	 * Copy constructor
	 * 
	 * @param port
	 *            Port to copy
	 */
	public Port(Port port) {
		this.id = port.id;
	}

	/**
	 * 
	 * A unique id identifying this port in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach port p_i in network elements of a topology
	 * 
	 * p_x.getId() != p_y.getId() when (x != y)
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * A unique id identifying this port in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach port p_i in network elements of a topology
	 * 
	 * p_x.getId() != p_y.getId() when (x != y)
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Port other = (Port) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
