package org.opennaas.extensions.quantum.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

public class QuantumModel implements IModel {

	private static final long	serialVersionUID	= -1626483965904441993L;

	@XmlIDREF
	@XmlAttribute
	private List<Network>		networks;
	@XmlIDREF
	@XmlAttribute
	private List<Port>			ports;
	@XmlIDREF
	@XmlAttribute
	private List<Subnet>		subnets;

	public List<Network> getNetworks() {
		return networks;
	}

	public void setNetworks(List<Network> networks) {
		this.networks = networks;
	}

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuantumModel other = (QuantumModel) obj;
		if (!networks.equals(other.getNetworks()))
			return false;
		if (!ports.equals(other.getPorts()))
			return false;
		if (!subnets.equals(other.getSubnets()))
			return false;
		return true;
	}

}
