package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteSelectionInput {

	private String	srcIP;
	private String	dstIP;
	private String	tos;

	/**
	 * Default constructor. ONLY FOR JAXB. Not to be used manually.
	 */
	public RouteSelectionInput() {

	}

	public RouteSelectionInput(String srcIP, String dstIP, String tos) {
		super();
		this.srcIP = srcIP;
		this.dstIP = dstIP;
		this.tos = tos;
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
		result = prime * result + ((dstIP == null) ? 0 : dstIP.hashCode());
		result = prime * result + ((srcIP == null) ? 0 : srcIP.hashCode());
		result = prime * result + ((tos == null) ? 0 : tos.hashCode());
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
		RouteSelectionInput other = (RouteSelectionInput) obj;
		if (dstIP == null) {
			if (other.dstIP != null)
				return false;
		} else if (!dstIP.equals(other.dstIP))
			return false;
		if (srcIP == null) {
			if (other.srcIP != null)
				return false;
		} else if (!srcIP.equals(other.srcIP))
			return false;
		if (tos == null) {
			if (other.tos != null)
				return false;
		} else if (!tos.equals(other.tos))
			return false;
		return true;
	}
}
