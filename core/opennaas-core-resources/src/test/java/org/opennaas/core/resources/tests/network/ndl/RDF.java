package org.opennaas.core.resources.tests.network.ndl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


//"http://www.science.uva.nl/research/sne/ndl#"
@XmlRootElement(name="RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class RDF {
	private String Location;
	private List<Device> devices;
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
