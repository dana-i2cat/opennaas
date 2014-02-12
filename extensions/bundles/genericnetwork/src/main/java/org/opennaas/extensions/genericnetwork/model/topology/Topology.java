package org.opennaas.extensions.genericnetwork.model.topology;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class Topology {

	private Set<NetworkElement>	networkElements;

	private Set<Link>			links;

	/**
	 * @return the networkElements
	 */
	public Set<NetworkElement> getNetworkElements() {
		return networkElements;
	}

	/**
	 * @param networkElements
	 *            the networkElements to set
	 */
	public void setNetworkElements(Set<NetworkElement> networkElements) {
		this.networkElements = networkElements;
	}

	/**
	 * @return the links
	 */
	public Set<Link> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((networkElements == null) ? 0 : networkElements.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Topology other = (Topology) obj;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		if (networkElements == null) {
			if (other.networkElements != null)
				return false;
		} else if (!networkElements.equals(other.networkElements))
			return false;
		return true;
	}

}
