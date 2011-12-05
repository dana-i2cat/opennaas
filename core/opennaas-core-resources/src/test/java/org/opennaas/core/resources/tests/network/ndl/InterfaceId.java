package org.opennaas.core.resources.tests.network.ndl;
import javax.xml.bind.annotation.XmlAttribute;

public class InterfaceId {
	


	private String resource;
    
	
	@XmlAttribute(name="resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

    @Override
	public String toString() {
		return "InterfaceId [resource=" + resource + "]";
	}
}
