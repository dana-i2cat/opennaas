package org.opennaas.extensions.quantum.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.opennaas.extensions.bod.capability.l2bod.BoDLink;

@XmlAccessorType(XmlAccessType.FIELD)
public class AutobahnElement extends ResourceElement {

	private List<BoDLink>	links;

	public AutobahnElement() {
		links = new ArrayList<BoDLink>();
	}

	public List<BoDLink> getLinks() {
		return links;
	}

	public void setLinks(List<BoDLink> links) {
		this.links = links;
	}

	public void addLink(BoDLink link) {
		this.getLinks().add(link);
	}

	public void removeLink(BoDLink link) {
		this.getLinks().remove(link);
	}

}
