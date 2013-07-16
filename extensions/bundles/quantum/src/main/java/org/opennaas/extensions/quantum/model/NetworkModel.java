package org.opennaas.extensions.quantum.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * The Resource class is used to link Quantum resource with the OpenNaaS resources it uses to provide the network.
 * 
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */

@XmlRootElement(name = "networkModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkModel {

	private String			quantumNetworkId;
	private List<Resource>	resources;

	public String getQuantumNetworkId() {
		return quantumNetworkId;
	}

	public void setQuantumNetworkId(String quantumNetworkId) {
		this.quantumNetworkId = quantumNetworkId;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public void addResource(Resource resource) {
		this.getResources().add(resource);
	}

	public void removeResource(Resource resource) {
		this.getResources().remove(resource);
	}

}
