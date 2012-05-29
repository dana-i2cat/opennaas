package org.opennaas.core.resources.descriptor.network;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class NetworkTopology {

	@Id
	@GeneratedValue
	private long				id;

	@Basic
	private String				location;

	@OneToMany(cascade = CascadeType.ALL)
	private List<NetworkDomain>	networkDomains;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Device>		devices;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Layer>			layers;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Interface>		interfaces;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlElement(name = "NetworkDomain", namespace = "http://www.science.uva.nl/research/sne/ndl/domain#")
	public List<NetworkDomain> getNetworkDomains() {
		return networkDomains;
	}

	public void setNetworkDomains(List<NetworkDomain> networkDomains) {
		this.networkDomains = networkDomains;
	}

	@XmlElement(name = "Device", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public List<Device> getDevices() {
		return devices;
	}

	@XmlElement(name = "Layer", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public List<Layer> getLayers() {
		return layers;
	}

	public void setLayers(List<Layer> layers) {
		this.layers = layers;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	@XmlElement(name = "Interface", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public String toString() {
		return "RDF [Location=" + location + ", NetworkDomain=" + networkDomains + ", devices=" + devices
				+ ", interfaces=" + interfaces + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((devices == null) ? 0 : devices.hashCode());
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((networkDomains == null) ? 0 : networkDomains.hashCode());
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
		NetworkTopology other = (NetworkTopology) obj;
		if (devices == null) {
			if (other.devices != null)
				return false;
		} else if (!devices.equals(other.devices))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (networkDomains == null) {
			if (other.networkDomains != null)
				return false;
		} else if (!networkDomains.equals(other.networkDomains))
			return false;
		return true;
	}

}
