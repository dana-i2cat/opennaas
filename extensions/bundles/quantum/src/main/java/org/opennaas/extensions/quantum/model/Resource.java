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
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {

	protected List<ResourceElement>	resourceElement;

	public List<ResourceElement> getResourceElement() {
		return resourceElement;
	}

	public void setResourceElement(List<ResourceElement> resourceElement) {
		this.resourceElement = resourceElement;
	}

	public void addResourceElement(ResourceElement resourceElement) {
		this.getResourceElement().add(resourceElement);
	}

	public void removeResourceElement(ResourceElement resourceElement) {
		this.getResourceElement().remove(resourceElement);
	}

}
