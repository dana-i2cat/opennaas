package org.opennaas.extensions.genericnetwork.model.topology;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ Domain.class, Switch.class })
public class NetworkElement extends TopologyElement {

	/**
	 * A unique id identifying this networkelement in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach networkelement ne_i in a topology
	 * 
	 * ne_x.getId() != ne_y.getId() when (x != y)
	 */
	@XmlAttribute(name = "id")
	@XmlID
	protected String	id;

	@XmlElementWrapper(name = "ports")
	@XmlElement(name = "port")
	protected Set<Port>	ports;

	/**
	 * A unique id identifying this networkelement in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach networkelement ne_i in a topology
	 * 
	 * ne_x.getId() != ne_y.getId() when (x != y)
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * A unique id identifying this networkelement in the topology.
	 * 
	 * Unique refers to a topology, meaning that:
	 * 
	 * foreach networkelement ne_i in a topology
	 * 
	 * ne_x.getId() != ne_y.getId() when (x != y)
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the ports
	 */
	public Set<Port> getPorts() {
		return ports;
	}

	/**
	 * @param ports
	 *            the ports to set
	 */
	public void setPorts(Set<Port> ports) {
		this.ports = ports;
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
		result = prime * result + ((ports == null) ? 0 : ports.hashCode());
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
		NetworkElement other = (NetworkElement) obj;
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
		if (ports == null) {
			if (other.ports != null)
				return false;
		} else if (!ports.equals(other.ports))
			return false;
		return true;
	}

}
