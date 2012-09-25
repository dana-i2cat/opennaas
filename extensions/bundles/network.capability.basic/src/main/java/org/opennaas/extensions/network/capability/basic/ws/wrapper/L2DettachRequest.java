package org.opennaas.extensions.network.capability.basic.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.network.model.topology.Link;

@XmlRootElement
public class L2DettachRequest {

	private Link	link;

	/**
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(Link link) {
		this.link = link;
	}

}
