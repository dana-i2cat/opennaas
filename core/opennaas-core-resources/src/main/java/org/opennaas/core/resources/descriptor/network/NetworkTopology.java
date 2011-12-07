package org.opennaas.core.resources.descriptor.network;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name="RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class NetworkTopology {
	
	@Id
	@GeneratedValue
	private long 					id;
	
	@Basic
	private String Location;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Device> devices;
	
	@OneToMany(cascade = CascadeType.ALL)
	private	List<Interface> interfaces;
	
	public String getLocation() {
		return Location;
	}
	
	public void setLocation(String location) {
		Location = location;
	}
	
	@XmlElement(name = "Device", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public List<Device> getDevices() {
		return devices;
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
		return "RDF [Location=" + Location + ", devices=" + devices
				+ ", interfaces=" + interfaces + "]";
	}



	
	
	


}
