package org.opennaas.core.resources.descriptor;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;

@Deprecated
@Embeddable
public class NetworkInfo {

	/* list of resources*/

	@OneToMany(cascade = CascadeType.ALL)
	List<ResourceId> resources;


	@XmlElement(name = "resources")
	public List<ResourceId> getResources() {
		return resources;
	}

	public void setResources(List<ResourceId> resources) {
		this.resources = resources;
	}
}
