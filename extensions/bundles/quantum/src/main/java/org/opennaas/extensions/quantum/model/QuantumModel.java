package org.opennaas.extensions.quantum.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.quantum.extensions.l3.model.FloatingIP;
import org.opennaas.extensions.quantum.extensions.l3.model.Router;

public class QuantumModel implements IModel {

	private static final long	serialVersionUID	= -1626483965904441993L;

	// basic API v2 model objects
	@XmlIDREF
	@XmlAttribute
	private List<Network>		networks;
	@XmlIDREF
	@XmlAttribute
	private List<Port>			ports;
	@XmlIDREF
	@XmlAttribute
	private List<Subnet>		subnets;

	// L3 extension model objects
	@XmlIDREF
	@XmlAttribute
	private List<Router>		routers;
	@XmlIDREF
	@XmlAttribute
	private List<FloatingIP>	floatingIPs;

	// OpenNaaS mapping model
	@XmlIDREF
	@XmlAttribute
	private List<NetworkModel>	networksModel;

	public QuantumModel() {
		networks = new ArrayList<Network>();
		ports = new ArrayList<Port>();
		subnets = new ArrayList<Subnet>();
		routers = new ArrayList<Router>();
		floatingIPs = new ArrayList<FloatingIP>();
		networksModel = new ArrayList<NetworkModel>();
	}

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

	public List<Router> getRouters() {
		return routers;
	}

	public void setRouters(List<Router> routers) {
		this.routers = routers;
	}

	public List<FloatingIP> getFloatingIPs() {
		return floatingIPs;
	}

	public void setFloatingIPs(List<FloatingIP> floatingIPs) {
		this.floatingIPs = floatingIPs;
	}

	public List<NetworkModel> getNetworksModel() {
		return networksModel;
	}

	public void setNetworksModel(List<NetworkModel> networksModel) {
		this.networksModel = networksModel;
	}

	public void addNetworkModel(NetworkModel netModel) {
		this.getNetworksModel().add(netModel);
	}

	public void removeNetworkModel(NetworkModel netModel) {
		this.getNetworksModel().remove(netModel);
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
		if (floatingIPs == null) {
			if (other.floatingIPs != null)
				return false;
		} else if (!floatingIPs.equals(other.floatingIPs))
			return false;
		if (networks == null) {
			if (other.networks != null)
				return false;
		} else if (!networks.equals(other.networks))
			return false;
		if (networksModel == null) {
			if (other.networksModel != null)
				return false;
		} else if (!networksModel.equals(other.networksModel))
			return false;
		if (ports == null) {
			if (other.ports != null)
				return false;
		} else if (!ports.equals(other.ports))
			return false;
		if (routers == null) {
			if (other.routers != null)
				return false;
		} else if (!routers.equals(other.routers))
			return false;
		if (subnets == null) {
			if (other.subnets != null)
				return false;
		} else if (!subnets.equals(other.subnets))
			return false;
		return true;
	}

}
